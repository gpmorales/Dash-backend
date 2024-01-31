package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Models.Widget;
import com.Dash.Dashboard.Services.WidgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;

@RestController
@RequestMapping("/my-dashboard/workspace")
public class WidgetController {

    private final WidgetService widgetService;

    WidgetController(WidgetService widgetService) {
        this.widgetService = widgetService;
    }


    // TODO - use webFlux client for ASYNC CALLS

    /** While working on a project, allow user to add new Widget */
    @PostMapping("/{projectLink}")
    public ResponseEntity<Object> addWidget(@NotEmpty @PathVariable String projectLink,
                                            @RequestBody Widget widget) {
        try {

            final Optional<String> projectJsonLink = sanitize(projectLink);

            if (projectJsonLink.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            final Optional<Object> addedWidget = widgetService.addWidget(projectLink, widget);

            if (addedWidget.isPresent()) {

            }

            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (Exception e) {
            return null;
        }
    }



    // TODO
    /** While working on a project, allow user to update widget */
    @PutMapping("/{projectLink}")
    public ResponseEntity<Object> updateWidget(@NotEmpty @PathVariable String projectLink,
                                              @RequestBody Widget widget) {
        try {

            final Optional<String> projectJsonLink = sanitize(projectLink);

            if (projectJsonLink.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (Exception e) {
            return null;
        }
    }



    // TODO
    /** While working on a project, allow user to delete a Widget */
    @DeleteMapping("/{projectLink}")
    public ResponseEntity<Object> deleteWidget(@NotEmpty @PathVariable String projectLink) {
        try {

            final Optional<String> projectJsonLink = sanitize(projectLink);

            if (projectJsonLink.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (Exception e) {
            return null;
        }
    }




    // TODO
    private static Optional<String> sanitize(String link) {
        if (link.endsWith(".csv")) {
            return Optional.of(link.replace(".csv", ".json"));
        } else if (link.endsWith(".json")) {
            return Optional.of(link.concat(".json"));
        }
        return Optional.empty();
    }

}
