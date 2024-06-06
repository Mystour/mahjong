package org.example.mahjong.dto;

import lombok.Getter;

public class GameProgress {
    private final String roomCode;
    private int progress;

    public GameProgress(String roomCode, int progress) {
        this.roomCode = roomCode;
        this.progress = progress;
    }
}