package org.example.mahjong.dto.message;

public class OperateMessage extends AbsMessage {
    private String who;
    private int remained;
    private OperationType operationType;

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

    public OperationType getOperationType() {
        return this.operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.Operate;
    }
}
