package com.Dash.Dashboard.Services;


import com.Dash.Dashboard.Entites.PasswordResetToken;
import com.Dash.Dashboard.Entites.User;
import com.Dash.Dashboard.Entites.VerificationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PasswordService {

    private final MongoTemplate passwordResetTokenDAO;

    private final MongoTemplate userDAO;

    @Autowired
    PasswordService(@Qualifier("passwordResetMongoTemplate") MongoTemplate passwordResetTokenDAO,
                    @Qualifier("userMongoTemplate") MongoTemplate userDAO) {

        this.passwordResetTokenDAO = passwordResetTokenDAO;
        this.userDAO = userDAO;

    }

    public ResponseEntity<String> initiatePasswordResetProcess(String userEmail) {
        Optional<User> user = Optional.ofNullable(
                userDAO.findOne(new Query(Criteria.where("email").is(userEmail)), User.class)
        );

        if (user.isEmpty()) {
            return new ResponseEntity<>("An account associated with this email has not been created ... " , HttpStatus.BAD_REQUEST);
        }

        // Check if user has a password reset token
        Optional<PasswordResetToken> linkedPasswordResetToken = Optional.ofNullable(
                passwordResetTokenDAO.findOne(new Query(Criteria.where("userId").is(user.get().getId())), PasswordResetToken.class)
        );

        final String resetPasswordKey;

        // Update the password reset token
        if (linkedPasswordResetToken.isPresent()) {
            final String resetPasswordLink = sendPasswordResetEmail(userEmail, regeneratePasswordResetToken(user.get().getId()));
            return new ResponseEntity<>("New Password Reset Key was sent with the following link : " + resetPasswordLink,
                    HttpStatus.OK);
        }

        // When an existing account that is enabled exists AND they have NOT REQUESTED to reset their password before, then
        if (user.get().isEnabled()) {
            resetPasswordKey = UUID.randomUUID().toString();

            passwordResetTokenDAO.insert(new PasswordResetToken(user.get().getId(), resetPasswordKey));

            final String resetPasswordLink = sendPasswordResetEmail(userEmail, resetPasswordKey);

            return new ResponseEntity<>(
                    "Email with password reset link has been sent with the following link: " + resetPasswordLink, HttpStatus.CREATED);
        }

        return new ResponseEntity<>("Account associated with this email has not been activated yet... " , HttpStatus.BAD_REQUEST);
    }



    public Optional<PasswordResetToken> verifyPasswordResetKey(String resetPasswordKey) {
        Optional<PasswordResetToken> passwordResetToken = Optional.ofNullable(
                passwordResetTokenDAO.findOne(new Query(Criteria.where("resetPasswordKey").is(resetPasswordKey)), PasswordResetToken.class)
        );

        if (passwordResetToken.isPresent() && !hasExpired(passwordResetToken.get())) {
            return passwordResetToken;
        }

        return Optional.empty();
    }


    public Optional<String> resetUserPassword(String resetPasswordKey, String userEmail, String newPassword) {
        return Optional.empty();
    }



    /**
     *
     *  Utility methods
     *
     */
    private String regeneratePasswordResetToken(String userId) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, 3); // TODO

        final String activationKey = UUID.randomUUID().toString();

        final Update update = new Update()
                .set("resetPasswordKey", activationKey)
                .set("expirationDate", new Date(calendar.getTime().getTime()));

        passwordResetTokenDAO.updateFirst(new Query(Criteria.where("userId").is(userId)), update, PasswordResetToken.class);

        return activationKey;
    }


    // TODO *** *** *** *** *** *** *** (ASYNC???)
    private String sendPasswordResetEmail(String email, String passwordResetKey) throws WebClientException {
        final String url = "www.dash.com/reset-password?token=" + passwordResetKey;

        if (email != null) {
            log.warn("Activation key was successfully sent to {email}");
            return url;
        } else {
            log.warn("Activation key could not be sent at the moment");
            return "";
        }
    }

    private boolean hasExpired(PasswordResetToken passwordResetToken) {
        final Calendar cal = Calendar.getInstance();
        return (passwordResetToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0;
    }

}
