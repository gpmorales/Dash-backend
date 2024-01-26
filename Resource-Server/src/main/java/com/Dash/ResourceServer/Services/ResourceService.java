package com.Dash.ResourceServer.Services;

import lombok.extern.slf4j.Slf4j;
import com.amazonaws.services.s3.AmazonS3;
import com.Dash.ResourceServer.Models.Widget;
import com.Dash.ResourceServer.Models.Project;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@Slf4j
@Service
public class ResourceService {

    // TODO CONNECT TO S3
    @Value("${application.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3Client;

    @Autowired
    ResourceService(@Qualifier("s3Client") AmazonS3 amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }


    // TODO
    // Connect to S3 and query for all projects belonging to this userDetails
    public List<Project> getProjectsBelongingTo(String userId) {
        /*
        return List.of(
                new Project("3433", "Lurie's Studies Analytics Display", LocalDate.now().minusDays(4), LocalDate.now().minusDays(2), "csv.link", "pr description", List.of(new Widget("plot 1", 10, 10, 50, 50), new Widget("plot 2", 60, 10, 100, 50))),
                new Project("7890", "Kevin's Math 361 Project", LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), "another.csv.link", "pr description", List.of(new Widget("graph 1", 20, 20, 60, 60), new Widget("graph 2", 70, 20, 110, 60), new Widget("graph 3", 90, 20, 210, 90)))
        );
        */
        return new ArrayList<>();
    }



    // TODO
    public Optional<Project> getProject(String userId, String projectId) {
        /*
            return Optional.of(new Project("3433", "Lurie's Studies Analytics Display", LocalDate.now().minusDays(4), LocalDate.now().minusDays(2), "csv.link", "pr description",
                    List.of(new Widget("plot 1", 10, 10, 50, 50), new Widget("plot 2", 60, 10, 100, 50))
            ));
        */
        return Optional.empty();
    }


    // TODO
    public HttpStatus generateProject(String userId, Project project, MultipartFile csvSheet) {


        // TODO *************** Upload CSV SHEET

        Optional<File> csvData = convertMultiPartFile(csvSheet);

        if (csvData.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        final String csvFileLocation = userId.concat("/").concat(project.getCsvSheetLink());

        final PutObjectResult csvSheetUploadResult = amazonS3Client.putObject(new PutObjectRequest(bucket, csvFileLocation, csvData.get()));


        // TODO *************** Upload Json

        Optional<File> projectJson = convertProjectToJson(project);

        if (projectJson.isEmpty()) {
            return HttpStatus.BAD_REQUEST;
        }

        final String jsonFileLocation = csvFileLocation.replace(".csv", ".json");

        PutObjectResult jsonUploadResult = amazonS3Client.putObject(new PutObjectRequest(bucket, jsonFileLocation, projectJson.get()));


        // TODO
        log.warn("CSV PERSISTENCE RESULT " + csvSheetUploadResult.getMetadata().getLastModified());
        log.warn("JSON PERSISTENCE RESULT " + jsonUploadResult.getMetadata().getLastModified());

        return HttpStatus.CREATED;
    }



    private Optional<File> convertMultiPartFile(MultipartFile data) {
        final File convertedFile = new File(data.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(data.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }

        return Optional.of(convertedFile);
    }

    private Optional<File> convertProjectToJson(Project json) {
        return Optional.of(new File("as"));
    }


}
