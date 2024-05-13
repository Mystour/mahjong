package org.example.mahjong.dto.message;

public class TakeMessage extends AbsMessage {
    private String who;
    private int remained;

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

    @Override
    public MessageType getMessageType() {
        return MessageType.Take;
    }

}
