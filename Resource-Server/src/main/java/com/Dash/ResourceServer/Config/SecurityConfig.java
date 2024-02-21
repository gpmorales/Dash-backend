package com.Dash.ResourceServer.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Map;


@Slf4j
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().mvcMatchers("/resources/**")
                .access("hasAuthority('SCOPE_api.read')")
                .and()
                .oauth2ResourceServer()
                .jwt();

        return http.build();
    }



    /*
    void _securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests((authorizeRequests) ->
                        authorizeRequests.mvcMatchers("/resources/**").authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer ->

                        oauth2ResourceServer.authenticationManagerResolver(request -> {

                            String token = new DefaultBearerTokenResolver().resolve(request);

                            log.warn("TOKEN -> " + token);

                            if (tokenLooksLikeJwt(token)) {
                                log.warn("JWT");
                                JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider(jwtDecoder());
                                return new ProviderManager(jwtProvider);
                            } else {
                                log.warn("OPAQUE");
                                OpaqueTokenAuthenticationProvider opaqueProvider = new OpaqueTokenAuthenticationProvider(opaqueTokenIntrospector());
                                return new ProviderManager(opaqueProvider);
                            }

                        })
                );

        return http.build();
    }

    private boolean tokenLooksLikeJwt(String token) {
        try {
            // Split the token into parts
            String[] parts = token.split("\\.");
            // JWT tokens should have 3 parts: header, payload, and signature
            if (parts.length == 3) {
                // Decode the header part of the token
                String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
                Map<String, Object> header = new ObjectMapper().readValue(headerJson, Map.class);
                log.warn(header.get("alg").toString());
                return header.containsKey("alg");
            }
        } catch (Exception e) {
            // In case of any exception (e.g., invalid Base64, invalid JSON), it's not a valid JWT header
            return false;
        }
        // If it doesn't meet the conditions above, it's likely not a JWT
        return false;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Configure and return a JwtDecoder for your custom dash-oidc-client
        String issuerUri = "http://auth-server:9000";
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        // Configure and return an OpaqueTokenIntrospector for Google OAuth2 client tokens
        return new GoogleTokenIntrospector("https://oauth2.googleapis.com/tokeninfo");
    }

    */

}
