package org.example.mahjong.controller;

import org.example.mahjong.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @ModelAttribute("username")
    public String getUsername(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getUsername();
    }

    @PostMapping("/createGame")
    public String createGame(@RequestParam String gameInput, RedirectAttributes redirectAttributes) {
        String gameCode = gameService.createGame(gameInput);
        redirectAttributes.addFlashAttribute("message", "Game created with code: " + gameCode);
        return "redirect:/welcome";
    }

    @PostMapping("/joinGame")
    public String joinGame(@RequestParam String gameCode, @ModelAttribute("username") String username, RedirectAttributes redirectAttributes) {
        boolean success = gameService.joinGame(gameCode, username);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Joined game with code: " + gameCode);
            return "redirect:/game";
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join game with code: " + gameCode);
            return "redirect:/welcome";
        }
    }
}