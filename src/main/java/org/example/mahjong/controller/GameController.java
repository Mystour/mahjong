package org.example.mahjong.controller;

import org.example.mahjong.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
            try {
                String serverIp = InetAddress.getLocalHost().getHostAddress();
                String serverPort = "8080"; // 你的应用的端口号
                String url = "https://" + serverIp + ":" + serverPort;
                redirectAttributes.addFlashAttribute("message", "Room created with code: " + roomCode + ". Others can join at: " + url);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Failed to get server IP address.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create room with code: " + roomCode);
        }
        return "redirect:/welcome/" + roomCode;
    }

    @PostMapping("/joinRoom")
    public String joinRoom(@RequestParam String roomCode, @ModelAttribute("username") String username, RedirectAttributes redirectAttributes) {
        boolean success = gameService.joinRoom(roomCode, username);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Joined room with code: " + roomCode);
            if (gameService.isGameStarted(roomCode)) {
                return "redirect:/game/" + roomCode;
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join room with code: " + roomCode);
        }
        return "redirect:/welcome/" + roomCode;
    }

    @GetMapping("/welcome/{roomCode}")
    public String welcomeToRoom(@PathVariable String roomCode, Model model) {
        model.addAttribute("welcomeMessage", "Welcome to room: " + roomCode);
        return "welcome";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to mahjong game");
        return "welcome";
    }

    @GetMapping("/game/{roomCode}")
    public String game(@PathVariable String roomCode, Model model) {
        model.addAttribute("roomCode", roomCode);
        return "game";
    }
}