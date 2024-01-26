package com.Dash.ResourceServer.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Widget {

    @JsonProperty("Name")
    private String widgetName;

    @JsonProperty("bottom-left-x-position")
    private Integer bottomLeftXPosition;

    @JsonProperty("bottom-left-y-position")
    private Integer bottomLeftYPosition;

    @JsonProperty("top-right-x-position")
    private Integer topRightXPosition;

    @JsonProperty("top-right-y-position")
    private Integer topRightYPosition;

}
