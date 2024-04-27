package org.example.mahjong.controller;

import org.example.mahjong.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class RoomController {

    private final GameService gameService;

    @Autowired
    public RoomController(GameService gameService) {
        this.gameService = gameService;
    }

    @MessageMapping("/room")
    @SendTo("/topic/room")
    public String sendRoomCount(String roomCode) {
        int count = gameService.calculateProgress(roomCode);
        return String.valueOf(count);
    }
}