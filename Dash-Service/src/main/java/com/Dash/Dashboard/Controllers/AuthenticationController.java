package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Services.AuthenticationService;
import com.Dash.Dashboard.Models.UserRegistrationRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.Valid;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    /**
     * @param userEmail
     * @return
     */
    @GetMapping(value = "/request-access")
    public ResponseEntity<String> getAccess(@RequestParam("email") String userEmail) {
        try {

            if (userEmail == null || userEmail.isEmpty()) {
                return new ResponseEntity<>("Request was empty", HttpStatus.BAD_REQUEST);
            }

            return authenticationService.sendActivationRequest(userEmail);

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong... "  + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // TODO -> Once you have received an activation key, activate user account
    @GetMapping(value = "/activate-account")
    public ResponseEntity<String> verifyRegistration(@RequestParam("key") String activationKey, @RequestParam("email") String userEmail) {
        try {

            if (activationKey.isEmpty()) {
                return new ResponseEntity<>("No activation key provided", HttpStatus.BAD_REQUEST);
            } else if (userEmail.isEmpty()) {
                return new ResponseEntity<>("No email was provided", HttpStatus.BAD_REQUEST);
            }

            return authenticationService.verifyActivationKey(activationKey, userEmail);

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong... " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // TODO --> After user has been verified via activation key
    @PostMapping(value = "/register-account", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequest registrationRequest,
                                               BindingResult bindingResult) {
        try {

            if (registrationRequest == null) {
                return new ResponseEntity<>("Request was empty", HttpStatus.BAD_REQUEST);
            }

            if (bindingResult.hasErrors()) {

                final String errorList = bindingResult.getAllErrors().toString();

                // TODO -> FRONTEND DISPLAY OF SPECIFIC INCORRECT FIELDS
                if (errorList.contains("password") || errorList.contains("email")) {
                    return new ResponseEntity<>("Password or Email fields are invalid ...", HttpStatus.BAD_REQUEST);
                }
                else if (errorList.contains("phoneNumber")) {
                    return new ResponseEntity<>("Invalid Phone-Number format ...", HttpStatus.BAD_REQUEST);
                }
                else {
                    return new ResponseEntity<>("Names must be between 2 - 20 characters ...", HttpStatus.BAD_REQUEST);
                }

            }

            return authenticationService.register(registrationRequest);

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong ... " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
