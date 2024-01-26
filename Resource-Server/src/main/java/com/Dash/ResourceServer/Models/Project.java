package com.Dash.ResourceServer.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


/** Little Tab rep Projects of User */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @JsonProperty("projectId")
    private String projectId;

    @JsonProperty("projectName")
    private String projectName;

    @JsonProperty("creationDate")
    private LocalDate creationDate;

    @JsonProperty("lastTimeModified")
    private LocalDate lastTimeModified;

    @JsonProperty("csvSheetLink")
    private String csvSheetLink;

    @JsonProperty("projectDescription")
    private String projectDescription;

    @JsonProperty("Widgets")
    private List<Widget> widgets;

}
