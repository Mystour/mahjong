package org.example.mahjong.dto;

import lombok.Data;
import org.example.mahjong.tile.Tile;

import java.util.List;

// PutEntity.java
@Data
public class PutEntity {
    private long idx;
    private List<Tile> hands;
    private List<Tile> pungs; // 碰的牌
    private List<Tile> chows; // 吃的牌
    private List<Tile> kongs;// 杠的牌
    private List<Tile> outs;
    private Tile put;
    private long remained;
    private List<Option> options;

    public long getIdx() {
        return idx;
    }

    public void setIdx(long value) {
        this.idx = value;
    }

    public List<Tile> getHands() {
        return hands;
    }

    public void setHands(List<Tile> value) {
        this.hands = value;
    }

    public List<Tile> getPungs() {
        return pungs;
    }

    public void setPungs(List<Tile> value) {
        this.pungs = value;
    }

    public List<Tile> getChows() {
        return chows;
    }

    public void setChows(List<Tile> value) {
        this.chows = value;
    }

    public List<Tile> getKongs() {
        return kongs;
    }

    public void setKongs(List<Tile> value) {
        this.kongs = value;
    }

    public List<Tile> getOuts() {
        return outs;
    }

    public void setOuts(List<Tile> value) {
        this.outs = value;
    }

    public Tile getPut() {
        return put;
    }

    public void setPut(Tile value) {
        this.put = value;
    }

    public long getRemained() {
        return remained;
    }

    public void setRemained(long value) {
        this.remained = value;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> value) {
        this.options = value;
    }
}