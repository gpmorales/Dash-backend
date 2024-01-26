package com.Dash.ResourceServer.Controllers;

import com.Dash.ResourceServer.Models.Project;
import com.Dash.ResourceServer.Services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;


@RestController
@RequestMapping("/resources/api")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);


    // TODO
    /** Will grab the file from this path user/{userid}/user.json, which contains the projects of that user and corresponding links/details in JSON format */
    @GetMapping(value = "/all-projects/{userId}")
    public List<Project> getUserProjects(@PathVariable String userId) {
        try {

            logger.warn("GET ALL PROJECTS BELONGING TO USER - HIT !!!!");

            return resourceService.getProjectsBelongingTo(userId);

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    // TODO
    /** Will grab the file from this path user/{userid}/user.json , which contains the projects of that user and corresponding links/details in JSON format */
    @GetMapping(value = "/load/{userId}/{projectId}")
    public Optional<Project> loadProject(@PathVariable String userId, @PathVariable String projectId) {
        try {

            logger.warn("GET PROJECT FROM USER - HIT !!!!");

            return resourceService.getProject(projectId, userId);

        } catch (Exception e) {
            return Optional.empty();
        }
    }


    // TODO
    /** Create a new project (dashboard with widgets) given a project name, a CSV file and */
    @PostMapping(value = "/create-project/{userId}")
    public HttpStatus addProject(@Valid @RequestBody Project project,
                                 @RequestPart("data") MultipartFile csvSheet,
                                 @PathVariable String userId,
                                 BindingResult bindingResult) {
        try {

            if (bindingResult.hasErrors()) {
                logger.warn("PROJECT MISSING DETAILS");
                return HttpStatus.BAD_REQUEST;
            }

            logger.warn("ADDED PROJECT FROM USER - HIT !!!!");

            return resourceService.generateProject(userId, project, csvSheet);

        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }



}
