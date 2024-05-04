package org.example.mahjong.controller;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.player.Hand;
import org.example.mahjong.service.GameService;
import org.example.mahjong.tile.Tile;
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
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GameController {

    private final GameService gameService;

    private Hand hand = new Hand();

    @Autowired
    public GameController(GameService gameService, Hand hand) {
        this.gameService = gameService;
        this.hand = hand;
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
                String url = "http://" + serverIp + ":" + serverPort;  // 没弄SSL证书，所以是http而不是https
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

    @GetMapping("/game/{roomCode}/{playerID}")
    public String gameWithPlayerID(@PathVariable String roomCode, @PathVariable int playerID, Model model) {
        model.addAttribute("roomCode", roomCode);
        model.addAttribute("playerID", playerID);
        return "game";
    }

    @GetMapping("/getAllPlayersHandCards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersHandCards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            // Handle the case where the game has not started yet
            return null;
        }
        return game.getAllPlayersHands().stream()
                .map(playerHandCards -> playerHandCards.stream()
                    .map(Tile::getImageUrl)
                    .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllPlayersHandCards")
    @ResponseBody
    public ResponseEntity<String> getAllPlayersHandCardsWithoutRoomCode() {
        return ResponseEntity.badRequest().body("Room code is required.");
    }
}