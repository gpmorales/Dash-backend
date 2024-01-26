package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Models.Project;
import com.Dash.Dashboard.Services.DashboardService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/dashboard")
public class DashboardController {


    final private DashboardService dashboardService;

    @Autowired
    DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }


    // TODO
    /** On Startup, provide all the user's projects */
    @GetMapping
    public ResponseEntity<List<Project>> loadAllProjects(@RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
                                                         OAuth2AuthorizedClient client, @AuthenticationPrincipal OidcUser oidcUser) {
        try {

            final Optional<List<Project>> projectList = dashboardService.loadAllProjects(oidcUser, client);

            if (projectList.isPresent() && !projectList.get().isEmpty()) {
                return ResponseEntity.ok().header("Content-Type", "application/json").
                        body(projectList.get());
            }

            return ResponseEntity.ok().header("Content-Type", "application/json").build();

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // TODO -> do we provide the csv sheet data in the Project object or just the link
    /** When client selects a project tab, load the project -> widgets (graphs/plots) and the csv data for that project */
    @GetMapping(value = "/workspace/{projectId}")
    public ResponseEntity<Project> loadProject(@PathVariable String projectId,
                                               @RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
                                               OAuth2AuthorizedClient client, @AuthenticationPrincipal OidcUser oidcUser) {
        try {

            final Optional<Project> project = dashboardService.getProject(projectId, oidcUser, client);

            if (available(project) && project.isPresent()) {
                return ResponseEntity.ok().header("Content-Type", "application/json").body(project.get());
            } else if (!available(project)) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    // TODO **********************
    // client -> injected with authorization details to make calls to my Resource server
    // oidcUser -> injected after authentication with OAuth2 server

    /** Client creates a new project, simple form with name of project and some auto-gen fields */
    @PostMapping(value = "/create-project", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> createProject(@RequestParam("projectName") String projectName,
                                                @RequestParam("projectDescription") String projectDescription,
                                                @RequestPart MultipartFile csvSheet,
                                                @RegisteredOAuth2AuthorizedClient("api-client-authorization-code")
                                                OAuth2AuthorizedClient client, @AuthenticationPrincipal OidcUser oidcUser) {

        if (true) {
            try {
                log.warn(client.toString());
                return ResponseEntity.ok(csvSheet.getBytes().length);
            } catch (IOException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        try {
            // Sanitize the form
            if (projectName.isEmpty()) {
                return new ResponseEntity<>("Project must have a name", HttpStatus.BAD_REQUEST);
            } else if (projectDescription.isEmpty()) {
                return new ResponseEntity<>("Project must have a description", HttpStatus.BAD_REQUEST);
            }

            final Optional<HttpStatus> projectUploadStatus = dashboardService.createProject(projectName, projectDescription, csvSheet, oidcUser, client);

            if (projectUploadStatus.isPresent() && projectUploadStatus.get().is2xxSuccessful()) {
                return new ResponseEntity<>("Success", HttpStatus.CREATED);
            }

            return new ResponseEntity<>("Could not create Project at this moment", projectUploadStatus.orElse(HttpStatus.INTERNAL_SERVER_ERROR));

        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong ... ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    private static boolean available(Object T) { return T != null; }
}
