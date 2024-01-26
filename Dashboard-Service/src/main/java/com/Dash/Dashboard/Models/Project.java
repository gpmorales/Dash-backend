package com.Dash.Dashboard.Models;

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
    private String projectId; // Identifies sub - folder under User and where project.json + project.csv are located

    private String projectName; // Name to display on frontend

    private LocalDate creationDate; // For user -> frontend

    private LocalDate lastTimeModified; // For user -> frontend

    private String csvSheetLink; // For quick access to linked csv sheet

    private String projectDescription; // GPT prompt for AI generated graphs

    private List<Widget> widgets; // Graphs,plots,analytics to be displayed on front end

}
