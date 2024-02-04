package com.Dash.ResourceServer.Controllers;

import com.Dash.ResourceServer.Services.GPTService;
import com.Dash.ResourceServer.Services.S3Service;

import lombok.extern.slf4j.Slf4j;

import com.Dash.ResourceServer.Models.Project;
import com.Dash.ResourceServer.Models.Widget;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import java.util.*;


@Slf4j
@RestController
@RequestMapping("/resources/api")
public class ResourceController {

    private final S3Service resourceService;

    private final GPTService gptService;

    @Autowired
    ResourceController(S3Service resourceService, GPTService gptService) {
        this.resourceService = resourceService;
        this.gptService = gptService;
    }


    // TODO
    /** Will grab the file from this path user/{userid}/user.json, which contains the projects of that user and corresponding links/details in JSON format */
    @GetMapping(value = "/all-projects/{userId}")
    public List<Project> getUserProjects(@PathVariable String userId) {
        try {

            return resourceService.getProjectsBelongingTo(userId);

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }



    // TODO
    /** Create a new project (dashboard with widgets) given a project name, a CSV file and */
    @PostMapping(value = "/generate-project", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Optional<Project> addProject(@RequestPart("template-project") Project templateProject,
                                        @RequestPart("csv-data") byte[] csvByteArray,
                                        BindingResult bindingResult) {
        try {

            // TODO Create Config with GPT API
            //final String generatedProjectConfig = gptService.promptGptWith(project);
            final Optional<Project> generatedProjectConfig = gptService.promptGptWith(templateProject);

            if (generatedProjectConfig.isEmpty()) {
                log.error("GPT API COULD NOT GENERATE CONFIG");
                // TODO generate default widgets??
                List<Widget> defaultWidgets = new ArrayList<>(6);
                templateProject.setWidgets(defaultWidgets);
                resourceService.uploadToS3(templateProject, csvByteArray);
                return Optional.of(templateProject);
            }

            log.warn("SHOULD RUN FIRST");
            resourceService.uploadToS3(generatedProjectConfig.get(), csvByteArray);
            log.warn("SHOULD RUN SECOND");

            return generatedProjectConfig;

        } catch (Exception e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }


}
