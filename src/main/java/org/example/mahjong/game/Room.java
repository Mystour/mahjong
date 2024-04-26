package org.example.mahjong.game;

import org.example.mahjong.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String roomCode;
    private final List<String> users = new ArrayList<>();
    private MahjongGame game;

    public Room(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public List<String> getUsers() {
        return users;
    }

    public MahjongGame getGame() {
        return game;
    }

    public void setGame(MahjongGame game) {
        this.game = game;
    }
}