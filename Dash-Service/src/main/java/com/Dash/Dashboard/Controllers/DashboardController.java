package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Models.Project;
import com.Dash.Dashboard.Services.DashboardService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.multipart.MultipartFile;

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
    // oidcUser -> injected after authentication with OAuth2 server (AuthenticationPrincipal)


    // TODO
    /** On Startup, provide all the user's projects on their home dashboard */
    @GetMapping
    public ResponseEntity<List<Project>> loadDashboard(//@RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                         //OAuth2AuthorizedClient authorizedClient) {
    ){
        try {

            final Optional<List<Project>> projectList = dashboardService.loadAllProjects();//authorizedClient);

            if (projectList.isPresent() && !projectList.get().isEmpty()) {
                return ResponseEntity.ok().header("Content-Type", "application/json").
                        body(projectList.get());
            }

            return ResponseEntity.ok().header("Content-Type", "application/json").build();

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    // TODO
    /** Client creates a new project, args include name of project, description, and some auto-gen fields */
    @PostMapping(value = "/create-project", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> createProject(@RequestParam("projectName") String projectName,
                                                @RequestParam("projectDescription") String projectDescription,
                                                @RequestBody MultipartFile csvFile) {
                                                //@RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                //OAuth2AuthorizedClient authorizedClient) { // TODO - UNCOMMENT
        try {

            // Sanitize the form
            if (projectName.isEmpty()) {
                return new ResponseEntity<>("Project must have a name", HttpStatus.BAD_REQUEST);
            } else if (projectDescription.isEmpty()) {
                return new ResponseEntity<>("Project must have a description", HttpStatus.BAD_REQUEST);
            }

            final Optional<String> projectLink = dashboardService.createProject(projectName, projectDescription, csvFile);
            //, authorizedClient);

            if (projectLink.isPresent() && !projectLink.get().isEmpty()) {
                return new ResponseEntity<>(projectLink.get(), HttpStatus.OK);
            }
            if (projectLink.isPresent()) {
                return new ResponseEntity<>("You do not sufficient credits to start a new project ... ", HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>("Could not create project at the moment ... ", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>("Something went wrong ... ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





    // TODO -> do we provide the csv sheet data in the Project object or just the link
    /** When client selects a project tab, load the project -> widgets (graphs/plots) and the csv data for that project */
    /*
    @GetMapping(value = "/workspace")
    public ResponseEntity<MultipartFile> loadProject(@NotEmpty @RequestParam String csvDataLink,
                                                     @RegisteredOAuth2AuthorizedClient("resource-access-client")
                                                     OAuth2AuthorizedClient authorizedClient) {
        try {

            final Optional<MultipartFile> projectData = dashboardService.getProject(csvDataLink, authorizedClient);


            if (projectData.isPresent() && projectData.get().getBytes().length > 0) {

                log.warn(projectData.get().getBytes().length + "");

                return ResponseEntity.ok().header("Content-Type", "application/multi-part").
                        body(projectData.get());
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            log.warn(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */


}
