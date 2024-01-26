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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/Dash/**").permitAll() // Public access
            .antMatchers("/dashboard/**").authenticated() // Secured endpoint for oauth2 clients only
            .antMatchers("/swagger-ui.html").authenticated() // TODO - remove
            .and()
            .oauth2Login(oauth2login -> oauth2login.loginPage("/oauth2/authorization/api-client-oidc"))
            .oauth2Client(Customizer.withDefaults());

        return http.build();
    }


}
