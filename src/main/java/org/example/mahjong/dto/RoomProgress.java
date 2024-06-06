package org.example.mahjong.dto;

import lombok.Getter;

@Getter
public class RoomProgress {
    private final String roomCode;
    private int count;

    public RoomProgress(String roomCode, int count) {
        this.roomCode = roomCode;
        this.count = count;
    }
}