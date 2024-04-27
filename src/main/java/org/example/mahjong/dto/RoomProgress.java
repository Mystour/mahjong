package org.example.mahjong.dto;

public class RoomProgress {
    private String roomCode;
    private int count;

    public RoomProgress(String roomCode, int count) {
        this.roomCode = roomCode;
        this.count = count;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public int getCount() {
        return count;
    }
}