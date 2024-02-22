package com.Dash.Dashboard.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;


@EnableWebSecurity
@Slf4j
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
            .antMatchers("/my-dashboard/**").authenticated() // Secured endpoints for authenticated users only
            .and()
            .oauth2Login(oauth2login -> oauth2login
                    .loginPage("http://localhost:3000") // TODO - HOME PAGE -> GUCCI
                    .defaultSuccessUrl("/my-dashboard") // Where to redirect after successful authentication
            )
            .oauth2Client(Customizer.withDefaults())
            .sessionManagement(sessionManagement ->
                sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Create session if required
                        .sessionFixation().newSession() // Protect against session fixation
            );
            /*
            // TODO => not way to end userSession???
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
            );*/

        return http.build();
    }


    /*
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/auth/**").permitAll()
                                .antMatchers("/swagger-ui.html").permitAll() // Consider securing this in production
                                .antMatchers("/my-dashboard/**").authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login.successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, javax.servlet.ServletException {
                                if (authentication instanceof OAuth2AuthenticationToken) {
                                    OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                                    String clientId = oauthToken.getAuthorizedClientRegistrationId();

                                    // Example logic to determine redirect URI based on the OAuth2 client
                                    String targetUrl = "/default-url";
                                    if ("google".equals(clientId)) {
                                        targetUrl = "/google-specific-url";
                                    } else if ("dash-oidc-client".equals(clientId)) {
                                        targetUrl = "/dash-specific-url";
                                    }

                                    // Redirect user to the determined URL
                                    response.sendRedirect(targetUrl);
                                }
                            }
                        })
                ).oauth2Client(Customizer.withDefaults());

        return http.build();
    }
    */

}
