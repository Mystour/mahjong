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

    @Autowired
    public GameController(SimpMessagingTemplate simpMessagingTemplate, GameService gameService, SimpMessagingTemplate template) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.gameService = gameService;
        this.template = template;
    }


    @ModelAttribute("username")
    public String getUsername(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails.getUsername();
    }

    @GetMapping("/api/username")
    @ResponseBody
    public String getCurrentUsername(@AuthenticationPrincipal UserDetails userDetails) {
        return getUsername(userDetails);
    }

    @GetMapping("/api/roomUsers/{roomCode}")
    @ResponseBody
    public List<String> getRoomUsers(@PathVariable String roomCode) {
        Room room = gameService.getRoom(roomCode);
        if (room == null) {
            return new ArrayList<>();
        }
        return room.getUsers();
    }

    @PostMapping("/createRoom")
    public String createRoom(@RequestParam String roomCode, @ModelAttribute("username") String username,
                             RedirectAttributes redirectAttributes) {
        String roomCodeGet = gameService.createRoom(roomCode, username);
        if (roomCodeGet != null) {
            try {
                String serverIp = InetAddress.getLocalHost().getHostAddress();
                String serverPort = "8080"; // 你的应用的端口号
                String url = "http://" + serverIp + ":" + serverPort; // 没弄SSL证书，所以是http而不是https
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

    @PostMapping("/joinRoom")
    public String joinRoom(@RequestParam String roomCode, @ModelAttribute("username") String username,
                           RedirectAttributes redirectAttributes) {
        boolean success = gameService.joinRoom(roomCode, username);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Joined room with code: " + roomCode);
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to join room with code: " + roomCode);
        }
        // if game has started, redirect to game page, else redirect to welcome page
        MahjongGame game = gameService.getGame(roomCode);
        if (game != null) {
            return "redirect:/game/" + roomCode;
        } else {
            return "redirect:/welcome/" + roomCode;
        }
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


    @GetMapping("/getAllPlayersHandCards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersHandCards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getAllPlayersHands().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllPlayersDiscards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersDiscards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getAllPlayersDiscards().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @GetMapping("/getAllPlayersShowedCards/{roomCode}")
    @ResponseBody
    public List<List<String>> getAllPlayersShowedCards(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getAllPlayersShowedCards().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
    @GetMapping("/getDiscardingTile/{roomCode}")
    @ResponseBody
    public List<List<String>> getDiscardingTile(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getDiscardingTile().stream()
                .map(playerHandCards -> playerHandCards.stream()
                        .map(Tile::getImageUrl)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    @MessageMapping("/drawTile/{roomCode}")
    public void handleDrawTileMessage(@Payload String message, @DestinationVariable String roomCode) {
        System.out.println(message);
        boolean success = gameService.handleDrawTileMessage(message);
        String feedback = success ? "Message received and processed successfully." : "Message processing failed.";

        // 发送反馈消息到主题
        simpMessagingTemplate.convertAndSend("/topic/drawTileFeedback/" + roomCode, feedback);
        // 正确使用房间代码调用 handleRoomDataChanged 方法
        handleRoomDataChanged(roomCode, feedback);
    }

    @MessageMapping("/roomDataChanged/{roomCode}")
    @SendTo("/topic/roomDataChanged/{roomCode}")
    public void handleRoomDataChanged(@DestinationVariable String roomCode, String message) {
        // 在这里编写通知浏览器数据已修改的逻辑
        System.out.println("Room data has changed in room: " + roomCode);
        // 这里可以向浏览器发送通知消息，告知数据已修改
        simpMessagingTemplate.convertAndSend("/topic/roomDataChanged/" + roomCode, message);
    }
    @GetMapping("/endGame/{roomCode}")
    @ResponseBody
    public void endGame(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.endGame();
        handleRoomDataChanged(roomCode,"currentPlayerDraw");
    }

    @GetMapping("/getAllPlayersCondition/{roomCode}")
    @ResponseBody
    public List<List<Boolean>> getAllPlayersCondition(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getAllPlayersCondition();
    }

    @GetMapping("/getPlayerIndex/{roomCode}")
    @ResponseBody
    public List<Integer> getPlayerIndex(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return null;
        }
        return game.getPlayerIndex();
    }

    @GetMapping("/getAllPlayersScores/{roomCode}")
    @ResponseBody
    public List<Integer> getAllPlayersScores(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            // Handle the case where the game has not started yet
            return null;
        }
        return game.getAllPlayersScores();
    }

    @GetMapping("/currentPlayerDraw/{roomCode}")
    @ResponseBody
    public void currentPlayerDraw(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.currentPlayerDraw();
        handleRoomDataChanged(roomCode,"currentPlayerDraw");
    }
    @GetMapping("/currentPlayerKong/{roomCode}")
    @ResponseBody
    public void currentPlayerKong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.currentPlayerKong();
        handleRoomDataChanged(roomCode,"currentPlayerKong");
    }
    @GetMapping("/currentPlayerMahjong/{roomCode}")
    @ResponseBody
    public void currentPlayerMahjong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.currentPlayerMahjong();
        handleRoomDataChanged(roomCode,"currentPlayerMahjong");
    }

    @GetMapping("/isdiscardOver/{roomCode}")
    @ResponseBody
    public void discardOver(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        if (game != null) {
            game.isdiscardOver();
        }
        handleRoomDataChanged(roomCode,"discardOver");
    }
    @GetMapping("/otherPlayerSkip/{roomCode}")
    @ResponseBody
    public void otherPlayerSkip(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerSkip();
        if(game.isdiscardOver()){
            game.currentPlayerDraw();
        }
        handleRoomDataChanged(roomCode,"otherPlayerSkip");

    }
    @GetMapping("/otherPlayerChow/{roomCode}")
    @ResponseBody
    public void otherPlayerChow(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerChow();
        handleRoomDataChanged(roomCode,"otherPlayerChow");
    }
    @GetMapping("/otherPlayerPung/{roomCode}")
    @ResponseBody
    public void otherPlayerPung(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerPung();
        handleRoomDataChanged(roomCode,"otherPlayerPung");
    }
    @GetMapping("/otherPlayerKong/{roomCode}")
    @ResponseBody
    public void otherPlayerKong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);

        game.otherPlayerKong();
        handleRoomDataChanged(roomCode,"otherPlayerKong");
    }

    @GetMapping("/otherPlayerMahjong/{roomCode}")
    @ResponseBody
    public void otherPlayerMahjong(@PathVariable String roomCode) {
        MahjongGame game = gameService.getGame(roomCode);
        game.otherPlayerMahjong();
        handleRoomDataChanged(roomCode,"otherPlayerMahjong");
    }



    // for game progress
    @GetMapping(value = "/startGameProgressCountdown/{roomCode}")
    @ResponseBody
    public void startGameProgressCountdown(@PathVariable("roomCode") String roomCode) {
        gameService.startGameProgressCountdown(roomCode);
    }


    @MessageMapping("/updateCurrentPlayer/{roomCode}")
    public void updateCurrentPlayer(@DestinationVariable String roomCode, String username) {
        System.out.println("Received message: " + username);
        // 将 username 发送到 "/topic/currentPlayer/{roomCode}"
        template.convertAndSend("/topic/currentPlayer/" + roomCode, username);
    }

}