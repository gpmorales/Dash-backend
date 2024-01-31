package com.Dash.Dashboard.Services;

import com.Dash.Dashboard.Models.Project;

import com.Dash.Dashboard.Models.Widget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;


@Service
@Slf4j
public class DashboardService {

    private final static String S3URL = "s3://dash-analytics-test/";

    private final WebClient webClient;

    @Autowired
    DashboardService(WebClient webClient) {
        this.webClient = webClient;
    }


    /**
     *
     * @return
     * @throws WebClientResponseException
     */
    public Optional<List<Project>> loadAllProjects(){//OAuth2AuthorizedClient client) throws WebClientResponseException {

        // Extract username from authorized client
        final String userId = "user123@gmail.com"; //client.getPrincipalName();

        // Encode url with username
        final String resourceUrl = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/resources/api/all-projects/{userId}")
                    .buildAndExpand(userId).toUriString();

        // Call Resource Server
        final List<Project> userProjects = this.webClient.get().uri(resourceUrl)
                                        //.attributes(oauth2AuthorizedClient(client))
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<Project>>() {})
                                        .block();

        return Optional.ofNullable(userProjects);
    }



    /**
     *
     * @param projectName
     * @param projectDescription
     * @param csvFile
     * @return
     * @throws WebClientException
     * @throws IOException
     */
    public Optional<String> createProject(String projectName, String projectDescription, MultipartFile csvFile) throws WebClientException, IOException {
                                             // OAuth2AuthorizedClient client) throws WebClientResponseException {

        final String userId = "user123@gmail.com"; //client.getPrincipalName();

        // Check if User has enough credits to create new project
        if (!userHasEnoughCredits(userId)) {
            return Optional.of("");
        }

        final String projectId = UUID.randomUUID().toString();

        final String projectKey = userId.concat("/").concat("project-").concat(projectId);

        final Project project = Project.builder().projectId(projectId).projectName(projectName).
                csvSheetLink(projectKey.concat("/").concat(projectId.concat(".csv"))).
                projectDescription(projectDescription).widgets(new ArrayList<>()).build();


        final String createProjectUrl = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/resources/api/create-project").buildAndExpand(userId).toUriString();


        // Make HTTP request to upload CSV sheet + create Project JSON (create project folder + project json file)
        final HttpStatus responseStatus = this.webClient.post()
                .uri(createProjectUrl)
                .body(BodyInserters.fromMultipartData("project", project).with("data", csvFile.getBytes()))
                //.attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(HttpStatus.class)
                .block();


        final String projectConfigLink = S3URL.concat(projectKey.concat("/").concat(projectId.concat(".json")));

        if (responseStatus != null && responseStatus.equals(HttpStatus.CREATED)) {
            return Optional.of(projectConfigLink);
        }

        return Optional.empty();
    }




    private boolean userHasEnoughCredits(String userId) {
        return !userId.isEmpty();
    }






    /**
     *
     * @param csvDataLink
     * @param client
     * @throws WebClientResponseException
    public Optional<Project> getProject(String csvDataLink, OAuth2AuthorizedClient client) throws WebClientResponseException {

        // Encode url with username
        final String resourceUrl = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/resources/api/load/{csvDataLink}")
                .buildAndExpand(csvDataLink).toUriString();

        // Call Resource Server
        final Project project = this.webClient.get().uri(resourceUrl)
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(Project.class)
                .block();

        return Optional.ofNullable(project);
    }
     */


}
