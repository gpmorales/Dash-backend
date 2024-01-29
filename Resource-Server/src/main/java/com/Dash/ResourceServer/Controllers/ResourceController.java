package com.Dash.ResourceServer.Controllers;

import lombok.extern.slf4j.Slf4j;
import com.Dash.ResourceServer.Models.Widget;
import com.Dash.ResourceServer.Models.Project;
import com.Dash.ResourceServer.Services.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/resources/api")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }


    // TODO
    /** Will grab the file from this path user/{userid}/user.json, which contains the projects of that user and corresponding links/details in JSON format */
    @GetMapping(value = "/all-projects/{userId}")
    public List<Project> getUserProjects(@PathVariable String userId) {
        try {

            return resourceService.getProjectsBelongingTo(userId);

        } catch (Exception e) {
            log.warn(e.getMessage());
            return null;
        }
    }



    // TODO
    /** Create a new project (dashboard with widgets) given a project name, a CSV file and */
    @PostMapping(value = "/create-project", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public HttpStatus addProject(@Valid @RequestPart("project") Project project,
                                     @RequestPart("data") byte[] csvByteArray,
                                     BindingResult bindingResult) {
        try {

            if (bindingResult.hasErrors()) {
                log.warn("PROJECT MISSING DETAILS");
                return HttpStatus.BAD_REQUEST;
            }

            return resourceService.generateProject(project, csvByteArray);

        } catch (Exception e) {
            log.warn(e.getMessage());
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }




    // TODO
    /** Will grab the file from this path user/{userid}/user.json , which contains the projects of that user and corresponding links/details in JSON format
     @GetMapping(value = "/load")
     public Project loadProject(@RequestParam("link") String projectLink) {
     try {

     log.warn("GET PROJECT FROM USER - HIT !!!!");

     return resourceService.getProject(projectLink);

     } catch (Exception e) {
     return new Project();
     }
     }*/

}
