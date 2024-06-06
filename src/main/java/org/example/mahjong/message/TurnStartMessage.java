package org.example.mahjong.message;

public class TurnStartMessage {
    private String username;
    private int time;

    public TurnStartMessage(String username, int time) {
        this.username = username;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}