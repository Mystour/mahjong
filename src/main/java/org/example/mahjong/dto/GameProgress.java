package org.example.mahjong.dto;

import lombok.Getter;

public class GameProgress {
    private final String roomCode;
    private int count;

    public GameProgress(String roomCode, int count) {
        this.roomCode = roomCode;
        this.count = count;
    }
}