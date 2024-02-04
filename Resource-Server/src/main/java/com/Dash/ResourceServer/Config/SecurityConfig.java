package com.Dash.ResourceServer.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /*
        http.authorizeRequests().mvcMatchers("/resources/**")
                .access("hasAuthority('SCOPE_api.read')")
                .and()
                .oauth2ResourceServer()
                .jwt();
         */

        http
        .authorizeRequests()
        .mvcMatchers("/resources/**").permitAll() // Allow anyone to access this endpoint without any permission
        .and()
        .csrf().disable() // Disable CSRF for simplicity (you may want to enable it based on your application requirements)
        .httpBasic(); // Use HTTP Basic authentication

        return http.build();
    }

}
