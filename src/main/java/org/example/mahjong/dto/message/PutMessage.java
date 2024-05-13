package org.example.mahjong.dto.message;

import org.example.mahjong.tile.Tile;

public class PutMessage extends AbsMessage {
    private String who;
    private int remained;
    private Tile tile;

    public String getWho() {
        return who;
    }

    public void setWho(String value) {
        this.who = value;
    }

    public int getRemained() {
        return remained;
    }

    public void setRemained(int value) {
        this.remained = value;
    }

    public Tile getTile() {
        return this.tile;
    }

    public void setTile(Tile value) {
        this.tile = value;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.Put;
    }
}
