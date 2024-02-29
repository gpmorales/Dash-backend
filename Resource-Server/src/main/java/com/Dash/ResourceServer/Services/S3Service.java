package com.Dash.ResourceServer.Services;

import com.Dash.ResourceServer.Models.Project;
import com.Dash.ResourceServer.Models.Widget;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class S3Service {

    @Value("${application.bucket}")
    private String bucket;

    private final static String S3URL = "https://dash-analytics-test.s3.amazonaws.com/";

    private final AmazonS3 amazonS3Client;

    @Autowired
    S3Service(@Qualifier("s3Client") AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }


    /**
     * @param userId
     * @return List<Project>
     */
    public List<Project> getProjectsBelongingTo(String userId) {

        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request().withBucketName(bucket).withPrefix(userId + "/");

        ListObjectsV2Result listObjectsResponse = amazonS3Client.listObjectsV2(listObjectsRequest);

        List<String> projectConfigs = listObjectsResponse.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey).filter(key -> key.endsWith(".json")).toList();

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



    /**
     *
     * @param projectConfig
     * @param csvFile
     */
    //@Async
    public void uploadToS3(Project projectConfig, MultipartFile csvFile) {

        //log.warn("RUNS ASYNC");
        try {

            final String jsonString = (new ObjectMapper()).writeValueAsString(projectConfig);
            final byte[] csvByteArray = csvFile.getBytes();

            try (final InputStream csvStream = new ByteArrayInputStream(csvByteArray);
                 final InputStream jsonStream = new ByteArrayInputStream(jsonString.getBytes())) {

                // Upload CSV
                final String csvFileLocation = projectConfig.getCsvSheetLink();
                ObjectMetadata csvMetaData = new ObjectMetadata();
                csvMetaData.setContentLength(csvByteArray.length);
                amazonS3Client.putObject(new PutObjectRequest(bucket, csvFileLocation, csvStream, csvMetaData));

                // Upload Json
                final String jsonFileLocation = csvFileLocation.replace(".csv", ".json");
                ObjectMetadata jsonMetaData = new ObjectMetadata();
                jsonMetaData.setContentLength(jsonString.getBytes().length);
                amazonS3Client.putObject(new PutObjectRequest(bucket, jsonFileLocation, jsonStream, jsonMetaData));

            } catch (IOException e) {
                log.warn(e.getMessage());
            }

        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }




    // TODO --> ASYNC???)
    // UPDATE Project.json config file by adding/deleting/or modifying its widgets
    /**
     * @param projectLink
     * @param widget
     * @return
     */

    @Async
    public Void updateProject(String projectLink, Widget widget) {

        // Let's get the parsed JSON object (Project)
        // So that we can modify its widgets
        // Retrieve Project
        return null;
    }


}
