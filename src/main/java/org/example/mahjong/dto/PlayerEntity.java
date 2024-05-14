package org.example.mahjong.dto;

import lombok.Data;

@Data
public class PlayerEntity {
    private int idx;
    private String userName;
    private String alias;
    private String avatar;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int value) {
        this.idx = value;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String value) {
        this.userName = value;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String value) {
        this.alias = value;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String value) {
        this.avatar = value;
    }
}
