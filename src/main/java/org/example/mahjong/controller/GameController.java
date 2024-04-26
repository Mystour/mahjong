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

    @PostMapping("/createRoom")
    public String createRoom(@RequestParam String roomCode, @ModelAttribute("username") String username, RedirectAttributes redirectAttributes) {
        String roomCodeGet = gameService.createRoom(roomCode, username);
        if (roomCodeGet != null) {
            redirectAttributes.addFlashAttribute("message", "Room created with code: " + roomCodeGet);
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create room with code: " + roomCodeGet);
        }
        return "redirect:/welcome";
    }

    @PostMapping("/joinRoom")
    public String joinRoom(@RequestParam String roomCode, @ModelAttribute("username") String username, RedirectAttributes redirectAttributes) {
        boolean success = gameService.joinRoom(roomCode, username);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Joined room with code: " + roomCode);
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join room with code: " + roomCode);
        }
        return "redirect:/welcome";
    }
}