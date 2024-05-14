package org.example.mahjong.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoudInfoEntity {
    private String roomId;
    private PlayerEntity own;
    private List<PlayerEntity> players;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String value) {
        this.roomId = value;
    }

    public PlayerEntity getOwn() {
        return own;
    }

    public void setOwn(PlayerEntity value) {
        this.own = value;
    }

    public List<PlayerEntity> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerEntity> value) {
        this.players = value;
    }
}
