package com.Dash.Dashboard.Services;

import com.Dash.Dashboard.Entites.Role;
import com.Dash.Dashboard.Entites.User;
import com.Dash.Dashboard.Entites.VerificationToken;
import com.Dash.Dashboard.Models.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClientException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class AuthenticationService {

    // Dependency injections done by constructor (all private and final fields)
    private final MongoTemplate userDAO;
    private final MongoTemplate verificationDAO;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    AuthenticationService(@Qualifier("userMongoTemplate") MongoTemplate userDAO, @Qualifier("verificationMongoTemplate") MongoTemplate verificationDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.verificationDAO = verificationDAO;
        this.passwordEncoder = passwordEncoder;
    }


    public ResponseEntity<String> sendActivationRequest(String email) throws WebClientException {

        // Check if user has already enabled
        Optional<User> user = Optional.ofNullable(
                userDAO.findOne(new Query(Criteria.where("email").is(email)), User.class)
        );

        if (user.isPresent() && user.get().isEnabled()) {
            return new ResponseEntity<>("An account with this email is already in use", HttpStatus.FORBIDDEN);
        }

        // In the case where the user has already sent an activation request to this email account, replace activationKey
        else if (user.isPresent()) {
            final String activationKey = generateNewVerificationToken(user.get().getId());
            sendVerificationEmail(email, activationKey);
            return new ResponseEntity<>("New Activation Key was sent", HttpStatus.CREATED);
        }

        // Otherwise this is a completely new user (email has not been used)
        final User tempUser = User.builder().email(email).enabled(false).build();
        userDAO.insert(tempUser);

        // Generate an activation key that is linked with this user ONLY
        final String activationKey = UUID.randomUUID().toString();
        final VerificationToken verificationToken = new VerificationToken(tempUser.getId(), activationKey);

        verificationDAO.save(verificationToken);

        return sendVerificationEmail(email, activationKey);
    }




    public ResponseEntity<String> verifyActivationKey(String activationKey, String providedEmail) {

        // Ensure that our user email exists in our database
        Optional<User> user = Optional.ofNullable(
                userDAO.findOne(new Query(Criteria.where("email").is(providedEmail)), User.class)
        );

        if (user.isEmpty()) {
            return new ResponseEntity<>("No Account is associated with this email", HttpStatus.UNAUTHORIZED);
        } else if (user.get().isEnabled()) {
            return new ResponseEntity<>("Account has already been activated", HttpStatus.UNAUTHORIZED);
        }


        // Search for verification token with associated activation key
        Optional<VerificationToken> verificationToken = Optional.ofNullable(
                verificationDAO.findOne(new Query(Criteria.where("activationKey").is(activationKey)), VerificationToken.class)
        );

        if (verificationToken.isEmpty()) return new ResponseEntity<>("Activation Key does not exist", HttpStatus.FORBIDDEN);


        // If verification object exists (someone's email successfully registered and key associated with that email), ensure that the user email (linked userId) from the verificationToken matches the given userEmail
        final String userId = verificationToken.get().getUserId();

        Optional<User> linkedUser = Optional.ofNullable(userDAO.findById(userId, User.class));

        if (linkedUser.isEmpty()) {
            return new ResponseEntity<>("Activation Key does not belong to this user", HttpStatus.FORBIDDEN);
        }

        // Otherwise the activation key is linked to a user so check if it is the same user attempting verification
        if (linkedUser.get().getEmail().equals(providedEmail) && !hasExpired(verificationToken.get())) {
            if (activateAccount(userId)) {
                return new ResponseEntity<>("Account successfully activated!", HttpStatus.CREATED);
            }
            else return new ResponseEntity<>("Account could not be activated at the moment", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Activation Key expired!", HttpStatus.BAD_REQUEST);
    }




    public ResponseEntity<String> register(UserRegistrationRequest registrationRequest) throws WebClientException {

        // Ensure account has been activated (from emailed activation key)
        final Query oldUser = new Query(Criteria.where("email").is(registrationRequest.getEmail()));

        Optional<User> user = Optional.ofNullable(userDAO.findOne(oldUser, User.class));

        if (user.isPresent() && !user.get().isEnabled()) {
            return new ResponseEntity<>("An account associated with this email has not been activated yet", HttpStatus.UNAUTHORIZED);
        } else if (user.isEmpty()) {
            return new ResponseEntity<>("The provided email is not associated with an account", HttpStatus.UNAUTHORIZED);
        }

        // Build verified customer
        final Update registeredUser = new Update()
                    .set("firstName", registrationRequest.getFirstName())
                    .set("lastName", registrationRequest.getLastName())
                    .set("password", passwordEncoder.encode(registrationRequest.getPassword()))
                    .set("phoneNumber", registrationRequest.getPhoneNumber())
                    .set("role", Role.USER);

        // Update customer
        userDAO.updateFirst(oldUser, registeredUser, User.class);

        // Send dashboard request
        return new ResponseEntity<>("Successfully Registered!", HttpStatus.CREATED);
    }



    /**
     *
     * Utility Methods
     *
     */

    private boolean hasExpired(VerificationToken verificationToken) {
        final Calendar cal = Calendar.getInstance();

        if ((verificationToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
            return verificationDAO.remove(verificationToken).wasAcknowledged()
                    && userDAO.remove(new Query(Criteria.where("id").is(verificationToken.getUserId())), User.class).wasAcknowledged();
        }

        return false;
    }


    private boolean activateAccount(String userId) throws WebClientException {
        final Query oldUser = new Query(Criteria.where("id").is(userId));
        final Update activatedUser = Update.update("enabled", true);
        return userDAO.updateFirst(oldUser, activatedUser, User.class).wasAcknowledged();
    }


    // From request-access
    private String generateNewVerificationToken(String userId) {
        final String activationKey = UUID.randomUUID().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 10);

        final Query query = new Query(Criteria.where("userId").is(userId));

        final Update update = new Update()
                .set("activationKey", activationKey)
                .set("expirationDate", new Date(calendar.getTime().getTime()));

        verificationDAO.updateFirst(query, update, VerificationToken.class);
        return activationKey;
    }


    // TODO
    private ResponseEntity<String> sendVerificationEmail(String email, String activationToken) throws WebClientException {
        String url = ""; //getApplicationUrl() + "/verifyRegistration?token= + token;
        log.info("Click the link to verify your account: {}", url);

        if (email != null) {
            return new ResponseEntity<>("Activation key was successfully sent to {email}", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Activation key could not be sent at the moment", HttpStatus.BAD_REQUEST);
        }
    }

}
