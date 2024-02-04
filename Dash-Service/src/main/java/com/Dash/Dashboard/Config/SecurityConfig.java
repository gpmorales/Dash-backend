package com.Dash.Dashboard.Config;

import org.springframework.context.annotation.Bean;

import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;


@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/auth/**").permitAll() // Public access
            .antMatchers("/swagger-ui.html").permitAll() // TODO - REMOVE IN THE FUTURE
            .antMatchers("/my-dashboard/**").permitAll() // Secured endpoint for authenticated users only
            //.antMatchers("/my-dashboard/**").authenticated() // Secured endpoint for authenticated users only
            .and()
            .oauth2Login(oauth2login -> oauth2login.
                    loginPage("/oauth2/authorization/dash-oidc-client"). // standard URL /oauth2/authorization/{client-name} which UPON HITTING STARTS THE OAUTH2.0 FLOW
                    defaultSuccessUrl("/my-dashboard") // Where to redirect after successful authentication
            )
            .oauth2Client(Customizer.withDefaults());

        return http.build();
    }


}
