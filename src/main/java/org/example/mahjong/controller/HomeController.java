package org.example.mahjong.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

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