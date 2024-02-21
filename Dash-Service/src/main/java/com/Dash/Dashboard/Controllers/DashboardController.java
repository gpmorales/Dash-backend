package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Models.Project;
import com.Dash.Dashboard.Services.DashboardService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;

import java.util.*;

import static com.Dash.Dashboard.Services.DashboardService.isPresent;


@Slf4j
@RestController
@RequestMapping("/my-dashboard")
//@CrossOrigin
public class DashboardController {

    final private DashboardService dashboardService;

    final private ApplicationEventPublisher loginEventPublisher;

    @Autowired
    DashboardController(DashboardService dashboardService, ApplicationEventPublisher loginEventPublisher) {
        this.dashboardService = dashboardService;
        this.loginEventPublisher = loginEventPublisher;
    }


    // Client -> injected with authorization details to make calls to my Resource server
    // oidcUser -> injected after authentication with OAuth2 server (AuthenticationPrincipal) (OPTIONAL)

    @Autowired
    private OAuth2AuthorizedClientManager authorizedClientManager;
    // TODO
    /** On Startup, provide all the user's projects on their home dashboard */
    @GetMapping
    public ResponseEntity<List<Project>> loadDashboardForGoogleClient(@RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                                      OAuth2AuthorizedClient authorizedClient,
                                                                      @AuthenticationPrincipal OidcUser oidcUser) {
        try {

            /*
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("resource-access-client")
                    .principal(new AnonymousAuthenticationToken("key","resource-service-account", AuthorityUtils.createAuthorityList("ROLE_USER")))
                    .build();

            OAuth2AuthorizedClient resourceClient = this.authorizedClientManager.authorize(authorizeRequest);
            */

            final Optional<List<Project>> projectList;

            log.warn(authorizedClient.getAccessToken().getTokenValue());

            if (oidcUser == null) {
                // TODO publish event to create user OR ensure user email doesnt alr exist in our IN-HOUSE-USER DB
                log.warn("Github");
                log.warn(authorizedClient.getClientRegistration().getScopes().toString());
                projectList = dashboardService.loadAllProjects(authorizedClient, "");
            }

            else if (isPresent(oidcUser.getEmail())) {
                // TODO publish event to create user OR ensure user email doesnt alr exist in our IN-HOUSE-USER DB
                //loginEventPublisher.publishEvent(new OAuthUserLoginEvent(oidcUser));
                log.warn("Google");
                log.warn(oidcUser.getEmail());
                log.warn(authorizedClient.getClientRegistration().getScopes().toString());
                projectList = dashboardService.loadAllProjects(authorizedClient, oidcUser.getEmail());
            } else {
                log.warn("DASH-OIDC");
                log.warn(authorizedClient.getPrincipalName());
                log.warn(authorizedClient.getClientRegistration().getScopes().toString());
                projectList = dashboardService.loadAllProjects(authorizedClient, authorizedClient.getPrincipalName());
            }

            if (projectList.isPresent() && !projectList.get().isEmpty()) {
                return ResponseEntity.ok().header("Content-Type", "application/json").
                        body(projectList.get());
            }

            return ResponseEntity.ok().header("Content-Type", "application/json").build();

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    // TODO ---> HOW DO WE HAVE CLIENT INJECTED AUTO? *****************
    /** Client creates a new project, args include name of project, description, and some auto-gen fields */
    @PostMapping(value = "/create-project", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Project> createProject(@RequestPart("project-name") String projectName,
                                                 @RequestPart("project-description") String projectDescription,
                                                 @RequestPart("csv-file") MultipartFile csvFile) {
                                                 //@RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                 //OAuth2AuthorizedClient authorizedClientConfig) { // TODO - UNCOMMENT
        try {
            //log.warn(authorizedClient.getPrincipalName());

            // TODO
            if (true) return new ResponseEntity<>(new Project(), HttpStatus.OK);

            // Ensure request can be made by user
            if (!dashboardService.userHasEnoughCredits("userId")) {
                log.warn("You do not sufficient credits to create a new project ... ");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            final Optional<Project> generatedProjectConfig = dashboardService.createProject(projectName, projectDescription, csvFile); //, authorizedClientConfig);

            if (generatedProjectConfig.isPresent() && !generatedProjectConfig.get().getWidgets().isEmpty()) {
                return new ResponseEntity<>(generatedProjectConfig.get(), HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
