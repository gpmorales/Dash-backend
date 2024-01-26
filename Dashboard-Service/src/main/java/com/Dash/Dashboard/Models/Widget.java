package com.Dash.Dashboard.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Widget {

    private String widgetName;

    private Integer bottomLeftXPosition;

    private Integer bottomLeftYPosition;

    private Integer topRightXPosition;

    private Integer topRightYPosition;

}
