package com.Dash.ResourceServer.Services;

import lombok.extern.slf4j.Slf4j;
import com.amazonaws.util.IOUtils;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.AmazonS3;

import com.Dash.ResourceServer.Models.Widget;
import com.Dash.ResourceServer.Models.Project;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.*;



@Slf4j
@Service
public class ResourceService {

    // TODO CONNECT TO S3
    @Value("${application.bucket}")
    private String bucket;

    private final static String S3URL = "https://dash-analytics-test.s3.amazonaws.com/";

    private final AmazonS3 amazonS3Client;

    @Autowired
    ResourceService(@Qualifier("s3Client") AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }


    // TODO
    // Connect to S3 and query for all projects belonging to this userDetails
    public List<Project> getProjectsBelongingTo(String userId) {

        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucket).withPrefix(userId + "/");

        ListObjectsV2Result listObjectsResponse = amazonS3Client.listObjectsV2(listObjectsRequest);

        List<String> projectConfigs = listObjectsResponse.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .filter(key -> key.endsWith(".json"))
                .toList();


        final List<Project> userProjects = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        for (String projectConfigLink : projectConfigs) {

            S3Object projectConfigObj = amazonS3Client.getObject(bucket, projectConfigLink);

            try {
                final byte[] jsonData = IOUtils.toByteArray(projectConfigObj.getObjectContent());
                final Project projectConfig = objectMapper.readValue(jsonData, Project.class);
                projectConfig.setLastModified(projectConfigObj.getObjectMetadata().getLastModified().toString());
                userProjects.add(projectConfig);
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }

        return userProjects;
    }



    public HttpStatus generateProject(Project project, byte[] csvFile) {

        if (csvFile.length == 0) {
            return HttpStatus.BAD_REQUEST;
        }

        try (final InputStream csvStream = new ByteArrayInputStream(csvFile)) {

            // Upload CSV
            final String csvFileLocation = project.getCsvSheetLink();

            final ObjectMetadata csvMetadata = new ObjectMetadata();
            csvMetadata.setContentLength(csvFile.length);

            amazonS3Client.putObject(new PutObjectRequest(bucket, csvFileLocation, csvStream, csvMetadata));


            // Upload Json
            project.setWidgets(List.of(new Widget("plot 1", 10, 10, 50, 50), new Widget("plot 2", 60, 10, 100, 50)));

            String jsonString = (new ObjectMapper()).writeValueAsString(project);
            final InputStream jsonStream = new ByteArrayInputStream(jsonString.getBytes());
            final ObjectMetadata jsonMetadata = new ObjectMetadata();
            jsonMetadata.setContentLength(jsonString.getBytes().length);

            if (jsonStream.available() == 0) {
                return HttpStatus.BAD_REQUEST;
            }

            final String jsonFileLocation = csvFileLocation.replace(".csv", ".json");

            amazonS3Client.putObject(new PutObjectRequest(bucket, jsonFileLocation, jsonStream, jsonMetadata));

            return HttpStatus.CREATED;

        } catch (IOException e) {
            log.warn(e.getMessage());
            return HttpStatus.BAD_REQUEST;
        }

    }




    /*
    // TODO
    public Project getProject(String projectLink) {
        return new Project("3433", "Lurie's Studies Analytics Display", "csv.link", "pr description",
                List.of(new Widget("plot 1", 10, 10, 50, 50), new Widget("plot 2", 60, 10, 100, 50))
        );
    }
    */

}
