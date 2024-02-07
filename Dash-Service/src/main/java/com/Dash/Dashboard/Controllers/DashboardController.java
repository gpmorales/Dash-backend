package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Models.Project;
import com.Dash.Dashboard.Services.DashboardService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/my-dashboard")
public class DashboardController {

    final private DashboardService dashboardService;

    @Autowired
    DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    // Client -> injected with authorization details to make calls to my Resource server
    // oidcUser -> injected after authentication with OAuth2 server (AuthenticationPrincipal) (OPTIONAL)


    // TODO
    /** On Startup, provide all the user's projects on their home dashboard */
    @GetMapping
    public ResponseEntity<List<Project>> loadDashboard(){//@RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                       //OAuth2AuthorizedClient authorizedClientConfig) { // TODO - UNCOMMENT
        try {

            //log.warn(authorizedClientConfig.getPrincipalName());

            final Optional<List<Project>> projectList = dashboardService.loadAllProjects(null);//authorizedClientConfig);

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



    // TODO
    /** Client creates a new project, args include name of project, description, and some auto-gen fields */
    @PostMapping(value = "/create-project", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Project> createProject(@RequestPart("project-name") String projectName,
                                                 @RequestPart("project-description") String projectDescription,
                                                 @RequestPart("csv-file") MultipartFile csvFile) {
                                                 //@RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                 //OAuth2AuthorizedClient authorizedClientConfig) { // TODO - UNCOMMENT
        try {

            //log.warn(authorizedClient.getPrincipalName());

            // Ensure request can be made by user
            if (!dashboardService.userHasEnoughCredits("userId")) {
                log.warn("You do not sufficient credits to create a new project ... ");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            final Optional<Project> generatedProjectConfig = dashboardService.createProject(projectName, projectDescription, csvFile); //, authorizedClientConfig);

            if (generatedProjectConfig.isPresent() && !generatedProjectConfig.get().getWidgets().isEmpty()) {
                return new ResponseEntity<>(generatedProjectConfig.get(), HttpStatus.OK);
            }

            final String foo = "hello";

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
