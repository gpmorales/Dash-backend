package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Entites.PasswordResetToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import com.Dash.Dashboard.Services.PasswordService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/user")
public class PasswordController {

    private final PasswordService passwordService;

    @Autowired
    PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }


    /**
     * Prompt user to enter email account. User must be enabled account and if so, generate Password Reset Token and send link to this User
     * Finally send email with link to reset password that contains this Reset Token
     * @param userEmail
     * @return
     */
    @ResponseBody
    @PostMapping(value ="/forgot-password", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> forgotPassword(@RequestPart("email") String userEmail) {
        try {

            if (userEmail.isEmpty()) {
                return new ResponseEntity<>("Email field cannot be empty... ", HttpStatus.BAD_REQUEST);
            }

            return passwordService.initiatePasswordResetProcess(userEmail);

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong ... " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    /**
     *  This http endpoint will only be accessed via a URL sent via email
     *  This Endpoint's purpose is to serve the user a page where they can securely enter their new password.
     *  The presence and validity of the ticket is to ensure that the request to reset the password is legitimate.
     */
    @GetMapping(value ="/reset-password")
    public String verifyPasswordResetToken(@RequestParam("token") String passwordResetToken) {
        try {

            // No need to check if user email / account exists
            return passwordService.verifyPasswordResetKey(passwordResetToken).map(
                    s -> "redirect:https://www.dash-analytics.com/reset-password"
            ).orElseGet(() -> "redirect:https://www.dash-analytics.com/forgot-password");

        } catch (Exception e) {
            log.warn(e.getMessage());
            return "redirect:https://www.dash-analytics.com/oops";
        }
    }




    // TODO --- > FRONTEND CAN HANDLE SOME OF THIS LOGIC
    @ResponseBody
    @PostMapping(value = "/reset-password", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> resetPassword(@RequestPart("token") String passwordResetToken,
                                                @RequestPart("user-email") String userEmail,
                                                @RequestPart("password-reset") String passwordReset,
                                                @RequestPart("re-enter-password") String confirmedPasswordReset) {
        try {

            if (!passwordReset.equals(confirmedPasswordReset)) {
                return new ResponseEntity<>("Passwords do not match ..." , HttpStatus.BAD_REQUEST);
            }

            final Optional<String> redirectURI = passwordService.resetUserPassword(passwordResetToken, userEmail, confirmedPasswordReset);

            if (redirectURI.isPresent()) {
                return new ResponseEntity<>(redirectURI.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>("Request could not be complete at this time... " , HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (HttpServerErrorException | WebClientException e) {
            return new ResponseEntity<>("Something went wrong ... " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
