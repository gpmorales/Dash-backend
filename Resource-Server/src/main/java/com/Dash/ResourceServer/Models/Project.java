package com.Dash.ResourceServer.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;


/** Little Tab rep Projects of User */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @NotNull
    @NotEmpty
    @JsonProperty("projectId")
    private String projectId;

    @NotNull
    @NotEmpty
    @JsonProperty("projectName")
    private String projectName;

    @NotNull
    @NotEmpty
    @JsonProperty("csvSheetLink")
    private String csvSheetLink;

    @NotNull
    @NotEmpty
    @JsonProperty("projectDescription")
    private String projectDescription;

    @NotNull
    @JsonProperty("lastModified")
    private String lastModified;

    @NotNull
    @JsonProperty("widgets")
    private List<Widget> widgets;

}
