package com.Dash.Dashboard.Services;

import com.Dash.Dashboard.Models.Project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;


@Service
@Slf4j
public class DashboardService {

    private final WebClient webClient;

    @Autowired
    DashboardService(WebClient webClient) {
        this.webClient = webClient;
    }


    /**
     *
     * @param client
     * @param oidcUser
     * @return
     * @throws WebClientResponseException
     */
    public Optional<List<Project>> loadAllProjects(OidcUser oidcUser, OAuth2AuthorizedClient client) throws WebClientResponseException {

        // Extract username from authorized client
        final String userId = oidcUser.getName();

        // Encode url with username
        final String resourceUrl = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/resources/api/all-projects/{userId}")
                    .buildAndExpand(userId).toUriString();

        // Call Resource Server
        final List<Project> userProjects = this.webClient.get().uri(resourceUrl)
                                        .attributes(oauth2AuthorizedClient(client))
                                        .retrieve()
                                        .bodyToMono(new ParameterizedTypeReference<List<Project>>() {})
                                        .block();

        return Optional.ofNullable(userProjects);
    }



    /**
     *
     * @param projectId
     * @param oidcUser
     * @param client
     * @throws WebClientResponseException
     */
    public Optional<Project> getProject(String projectId, OidcUser oidcUser, OAuth2AuthorizedClient client) throws WebClientResponseException {

        // Extract userid from authorized client
        final String userId = oidcUser.getName();

        // Encode url with username
        final String resourceUrl = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/resources/api/load/{userId}/{projectId}")
                .buildAndExpand(userId, projectId).toUriString();

        // Call Resource Server
        return this.webClient.get().uri(resourceUrl)
                .attributes(oauth2AuthorizedClient(client))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Optional<Project>>() {})
                .block();
    }




    // TODO >>>>>>>>>>>>>>>>>>>>>>>>>
    public Optional<HttpStatus> createProject(String projectName, String projectDescription, MultipartFile csvSheet,
                                    OidcUser oidcUser, OAuth2AuthorizedClient client) throws WebClientResponseException {

        // Extract userid from authorized client & prepare project
        final String userId = oidcUser.getName();

        // TODO NOTE -- projectId will be the name of project folder For now under that user!!!
        final String projectId = UUID.randomUUID().toString();

        final Project project = Project.builder().
                projectId(projectId).
                projectName(projectName).
                creationDate(LocalDate.now()).
                csvSheetLink(projectId.concat("/").concat(projectId.concat(".csv"))).
                projectDescription(projectDescription).
                build();


        // Encode url with username
        final String createProjectUrl = UriComponentsBuilder.fromUriString("http://127.0.0.1:8081/resources/api/create-project/{userId}")
                .buildAndExpand(userId).toUriString();


        // TODO ************************************************************************************
        // MAKE CALL TO UPLOAD CSV SHEET / CREATE PROJECT JSON
        // Create project folder + project json file

        final HttpStatus responseStatus = this.webClient.post().uri(createProjectUrl)
            .body(project, Project.class)
            .attributes(oauth2AuthorizedClient(client))
            .retrieve()
            .bodyToMono(HttpStatus.class)
            .block();

        return Optional.ofNullable(responseStatus);
    }


}
