package org.example.mahjong.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This class is a Spring MVC controller that handles the home page requests.
 */
@Controller
public class HomeController {

    /**
     * Handles the root URL ("/") GET requests.
     *
     * @return A redirect to either the welcome page or the login page depending on the user's authentication status.
     */
    @GetMapping("/")
    public String home() {
        // Check if the user is authenticated
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            // If the user is authenticated, return the welcome page
            return "redirect:/welcome";
        } else {
            // If the user is not authenticated, redirect them to the login page
            return "redirect:/login";
        }
    }
}
