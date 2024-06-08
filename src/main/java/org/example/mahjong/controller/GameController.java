package org.example.mahjong.controller;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.game.Room;
import org.example.mahjong.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.example.mahjong.tile.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class GameController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameService gameService;
    public final SimpMessagingTemplate template;

    /**
     * Constructor with dependency injection.
     * @param simpMessagingTemplate SimpMessagingTemplate for WebSocket messaging.
     * @param gameService GameService for handling game logic.
     * @param template SimpMessagingTemplate for WebSocket messaging.
     */
    @Autowired
    public GameController(SimpMessagingTemplate simpMessagingTemplate, GameService gameService, SimpMessagingTemplate template) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameService = gameService;
        this.template = template;
    }

    /**
     * Get the username of the currently authenticated user.
     * @param userDetails UserDetails object containing user information.
     * @return The username of the authenticated user.
     */
    @ModelAttribute("username")
    public String getUsername(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getUsername();
    }

    /**
     * Endpoint to get the current username.
     * @param userDetails UserDetails object containing user information.
     * @return The username of the authenticated user.
     */
    @GetMapping("/api/username")
    @ResponseBody
    public String getCurrentUsername(@AuthenticationPrincipal UserDetails userDetails) {
        return getUsername(userDetails);
    }

    /**
     * Endpoint to get the list of users in a specific room.
     * @param roomCode The code of the room.
     * @return List of usernames in the room.
     */
    @GetMapping("/api/roomUsers/{roomCode}")
    @ResponseBody
    public List<String> getRoomUsers(@PathVariable String roomCode) {
        Room room = gameService.getRoom(roomCode);
        if (room == null) {
            return new ArrayList<>();
        }
        return room.getUsers();
    }

    /**
     * Endpoint to create a new room.
     * @param roomCode The code of the room to be created.
     * @param username The username of the room creator.
     * @param redirectAttributes RedirectAttributes for flash messages.
     * @return Redirect to the welcome page of the created room.
     */
    @PostMapping("/createRoom")
    public String createRoom(@RequestParam String roomCode, @ModelAttribute("username") String username,
                             RedirectAttributes redirectAttributes) {
        String roomCodeGet = gameService.createRoom(roomCode, username);
        if (roomCodeGet != null) {
            try {
                String serverIp = InetAddress.getLocalHost().getHostAddress();
                String serverPort = "8080"; // Your application's port number
                String url = "http://" + serverIp + ":" + serverPort; // Not using SSL certificate, hence http instead of https
                redirectAttributes.addFlashAttribute("message",
                        "Room created with code: " + roomCode + ". Others can join at: " + url);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Failed to get server IP address.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create room with code: " + roomCode);
        }
        return "redirect:/welcome/" + roomCode;
    }

    /**
     * Endpoint to join an existing room.
     * @param roomCode The code of the room to join.
     * @param username The username of the player joining the room.
     * @param redirectAttributes RedirectAttributes for flash messages.
     * @return Redirect to the game page if the game has started, otherwise redirect to the welcome page.
     */
    @PostMapping("/joinRoom")
    public String joinRoom(@RequestParam String roomCode, @ModelAttribute("username") String username,
                           RedirectAttributes redirectAttributes) {
        boolean success = gameService.joinRoom(roomCode, username);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Joined room with code: " + roomCode);
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join room with code: " + roomCode);
        }
        MahjongGame game = gameService.getGame(roomCode);
        if (game != null) {
            return "redirect:/game/" + roomCode;
        } else {
            return "redirect:/welcome/" + roomCode;
        }
    }

    /**
     * Endpoint to display the welcome page for a specific room.
     * @param roomCode The code of the room.
     * @param model The Model object to pass data to the view.
     * @return The welcome view.
     */
    @GetMapping("/welcome/{roomCode}")
    public String welcomeToRoom(@PathVariable String roomCode, Model model) {
        model.addAttribute("welcomeMessage", "Welcome to room: " + roomCode);
        return "welcome";
    }

    /**
     * Endpoint to display the general welcome page.
     * @param model The Model object to pass data to the view.
     * @return The welcome view.
     */
    @GetMapping("/welcome")
    public String welcome(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to mahjong game");
        return "welcome";
    }

    /**
     * Endpoint to display the game page for a specific room.
     * @param roomCode The code of the room.
     * @param model The Model object to pass data to the view.
     * @return The game view.
     */
    @GetMapping("/game/{roomCode}")
    public String game(@PathVariable String roomCode, Model model) {
        model.addAttribute("roomCode", roomCode);
        return "game";
    }

    /**
     * Endpoint to get the hand cards of all players in a specific room.
     * @param roomCode The code of the room.
     * @return List of lists of image URLs of the hand cards of all players.
     */
    @GetMapping("/getAllPlayersHandCards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersHandCards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        return game.getAllPlayersHands().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }



    /**
     * Endpoint to get the discard pile of all players in a specific room.
     * @param roomCode The code of the room.
     * @return List of lists of image URLs of the discard pile of all players.
     */
    @GetMapping("/getAllPlayersDiscards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersDiscards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        return game.getAllPlayersDiscards().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Endpoint to get the exposed cards of all players in a specific room.
     * @param roomCode The code of the room.
     * @return List of lists of image URLs of the exposed cards of all players.
     */
    @GetMapping("/getAllPlayersShowedCards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersShowedCards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        return game.getAllPlayersShowedCards().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Endpoint to get the tiles currently being discarded in a specific room.
     * @param roomCode The code of the room.
     * @return List of lists of image URLs of the tiles currently being discarded.
     */
    @GetMapping("/getDiscardingTile/{roomCode}")
    @ResponseBody
    public List<List<String>> getDiscardingTile(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        return game.getDiscardingTile().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    /**
     * Handles draw tile messages sent to the server.
     * @param message The message payload.
     * @param roomCode The code of the room.
     */
    @MessageMapping("/drawTile/{roomCode}")
    public void handleDrawTileMessage(@Payload String message, @DestinationVariable String roomCode) {
        System.out.println(message);
        boolean success = gameService.handleDrawTileMessage(message);
        String feedback = success ? "Message received and processed successfully." : "Message processing failed.";
        simpMessagingTemplate.convertAndSend("/topic/drawTileFeedback/" + roomCode, feedback);
        handleRoomDataChanged(roomCode, feedback);
    }

    /**
     * Handles room data change messages sent to the server.
     * @param roomCode The code of the room.
     * @param message The message payload.
     */
    @MessageMapping("/roomDataChanged/{roomCode}")
    @SendTo("/topic/roomDataChanged/{roomCode}")
    public void handleRoomDataChanged(@DestinationVariable String roomCode, String message) {
        System.out.println("Room data has changed in room: " + roomCode);
        simpMessagingTemplate.convertAndSend("/topic/roomDataChanged/" + roomCode, message);
    }

    /**
     * Endpoint to end the game in a specific room.
     * @param roomCode The code of the room.
     */
    @GetMapping("/endGame/{roomCode}")
    @ResponseBody
    public void endGame(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.endGame();
        handleRoomDataChanged(roomCode, "endGame");
    }

    /**
     * Endpoint to get the condition of all players in a specific room.
     * @param roomCode The code of the room.
     * @return List of lists of booleans representing the condition of all players.
     */
    @GetMapping("/getAllPlayersCondition/{roomCode}")
    @ResponseBody
    public List<List<Boolean>> getAllPlayersCondition(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getAllPlayersCondition();
    }

    /**
     * Endpoint to get the indices of all players in a specific room.
     * @param roomCode The code of the room.
     * @return List of integers representing the indices of all players.
     */
    @GetMapping("/getPlayerIndex/{roomCode}")
    @ResponseBody
    public List<Integer> getPlayerIndex(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getPlayerIndex();
    }

    /**
     * Endpoint to get the scores of all players in a specific room.
     * @param roomCode The code of the room.
     * @return List of integers representing the scores of all players.
     */
    @GetMapping("/getAllPlayersScores/{roomCode}")
    @ResponseBody
    public List<Integer> getAllPlayersScores(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        return game.getAllPlayersScores();
    }

    /**
     * Endpoint to trigger the current player to draw a tile.
     * @param roomCode The code of the room.
     */
    @GetMapping("/currentPlayerDraw/{roomCode}")
    @ResponseBody
    public void currentPlayerDraw(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.currentPlayerDraw();
        handleRoomDataChanged(roomCode, "currentPlayerDraw");
    }

    /**
     * Endpoint to trigger the current player to perform a kong.
     * @param roomCode The code of the room.
     */
    @GetMapping("/currentPlayerKong/{roomCode}")
    @ResponseBody
    public void currentPlayerKong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.currentPlayerKong();
        handleRoomDataChanged(roomCode, "currentPlayerKong");
    }

    /**
     * Endpoint to trigger the current player to declare mahjong.
     * @param roomCode The code of the room.
     */
    @GetMapping("/currentPlayerMahjong/{roomCode}")
    @ResponseBody
    public void currentPlayerMahjong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.currentPlayerMahjong();
        handleRoomDataChanged(roomCode, "currentPlayerMahjong");
    }

    /**
     * Endpoint to check if the discard phase is over.
     * @param roomCode The code of the room.
     */
    @GetMapping("/isdiscardOver/{roomCode}")
    @ResponseBody
    public void discardOver(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.isdiscardOver();
        handleRoomDataChanged(roomCode, "discardOver");
    }

    /**
     * Endpoint to trigger a skip action for other players.
     * @param roomCode The code of the room.
     */
    @GetMapping("/otherPlayerSkip/{roomCode}")
    @ResponseBody
    public void otherPlayerSkip(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerSkip();
        if (game.isdiscardOver()) {
            game.currentPlayerDraw();
        }
        handleRoomDataChanged(roomCode, "otherPlayerSkip");
    }

    /**
     * Endpoint to trigger a chow action for other players.
     * @param roomCode The code of the room.
     */
    @GetMapping("/otherPlayerChow/{roomCode}")
    @ResponseBody
    public void otherPlayerChow(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerChow();
        handleRoomDataChanged(roomCode, "otherPlayerChow");
    }

    /**
     * Endpoint to trigger a pung action for other players.
     * @param roomCode The code of the room.
     */
    @GetMapping("/otherPlayerPung/{roomCode}")
    @ResponseBody
    public void otherPlayerPung(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerPung();
        handleRoomDataChanged(roomCode, "otherPlayerPung");
    }

    /**
     * Endpoint to trigger a kong action for other players.
     * @param roomCode The code of the room.
     */
    @GetMapping("/otherPlayerKong/{roomCode}")
    @ResponseBody
    public void otherPlayerKong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerKong();
        handleRoomDataChanged(roomCode, "otherPlayerKong");
    }

    /**
     * Endpoint to trigger a mahjong declaration for other players.
     * @param roomCode The code of the room.
     */
    @GetMapping("/otherPlayerMahjong/{roomCode}")
    @ResponseBody
    public void otherPlayerMahjong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerMahjong();
        handleRoomDataChanged(roomCode, "otherPlayerMahjong");
    }

    /**
     * Endpoint to start the game progress countdown for a specific room.
     * @param roomCode The code of the room.
     */
    @GetMapping(value = "/startGameProgressCountdown/{roomCode}")
    @ResponseBody
    public void startGameProgressCountdown(@PathVariable("roomCode") String roomCode) {
        gameService.startGameProgressCountdown(roomCode);
    }

    /**
     * Handles updating the current player message sent to the server.
     * @param roomCode The code of the room.
     * @param username The username of the current player.
     */
    @MessageMapping("/updateCurrentPlayer/{roomCode}")
    public void updateCurrentPlayer(@DestinationVariable String roomCode, String username) {
        System.out.println("Received message: " + username);
        template.convertAndSend("/topic/currentPlayer/" + roomCode, username);
    }
}
