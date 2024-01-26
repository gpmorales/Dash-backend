package com.Dash.Dashboard.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

// Holds the payload of the POST request send by a user or someone trying to sign in
// Will contain fields like username & password etc.


// TODO ----> DO WE NEED THIS?
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ComponentScan
public class UserLoginRequest {

    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;

}
