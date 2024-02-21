package com.Dash.ResourceServer.Config;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OAuth2IntrospectionAuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

public final class GoogleTokenIntrospector implements OpaqueTokenIntrospector {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String introspectionUri;

    public GoogleTokenIntrospector(String introspectionUri) {
        this.introspectionUri = introspectionUri;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        RequestEntity<?> requestEntity = buildRequest(token);
        try {
            ResponseEntity<Map<String, Object>> responseEntity = this.restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {});
            // TODO: Create and return OAuth2IntrospectionAuthenticatedPrincipal based on response...
            Map<String, Object> claims = responseEntity.getBody();

            if (claims == null || claims.isEmpty() || !Boolean.TRUE.equals(claims.get("active"))) {
                throw new BadOpaqueTokenException("Introspected token is inactive or invalid");
            }

            return new OAuth2IntrospectionAuthenticatedPrincipal(claims, Collections.emptyList());
        } catch (Exception ex) {
            throw new BadOpaqueTokenException(ex.getMessage(), ex);
        }
    }

    private RequestEntity<?> buildRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("access_token", token);
        return new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(introspectionUri));
    }
}
