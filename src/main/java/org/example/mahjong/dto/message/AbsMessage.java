package org.example.mahjong.dto.message;

public abstract class AbsMessage {
    protected MessageType messageType;

    public AbsMessage() {
        this.messageType = getMessageType();
    }

    public abstract MessageType getMessageType();
}
