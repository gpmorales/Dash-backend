package com.Dash.Dashboard.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;


/** Little Tab rep Projects of User */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("projectName")
    private String projectName;

    @JsonProperty("csvSheetLink")
    private String csvSheetLink;

    @JsonProperty("projectDescription")
    private String projectDescription;

    @JsonProperty("widgets")
    private List<Widget> widgets;

}
