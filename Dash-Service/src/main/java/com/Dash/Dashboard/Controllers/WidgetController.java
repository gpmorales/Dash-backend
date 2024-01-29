package com.Dash.Dashboard.Controllers;

import com.Dash.Dashboard.Models.Widget;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/my-dashboard/workspace")
public class WidgetController {

    // TODO - use webFlux client for ASYNC CALLS

    /** While working on a project, allow user to add new Widget */
    @PostMapping("/{projectLink}")
    public ResponseEntity<Object> addWidget(@NotEmpty @PathVariable String projectLink,
                                              @RequestBody Widget widget) {

        try {

            final String projectJsonLink = sanitize(projectLink);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (Exception e) {

        }
        return null;
    }



    // TODO
    /** While working on a project, allow user to update widget */
    @PutMapping("/{projectLink}")
    public ResponseEntity<Object> updateWidget(@NotEmpty @PathVariable String projectLink,
                                              @RequestBody Widget widget) {

        try {
            final String projectJsonLink = sanitize(projectLink);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (Exception e) {

        }
        return null;
    }



    // TODO
    /** While working on a project, allow user to delete a Widget */
    @DeleteMapping("/{projectLink}")
    public ResponseEntity<Object> deleteWidget(@NotEmpty @PathVariable String projectLink) {

        try {
            final String projectJsonLink = sanitize(projectLink);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);

        } catch (Exception e) {

        }
        return null;
    }




    // TODO
    private static String sanitize(String link) {
        return link.strip();
    }

}
