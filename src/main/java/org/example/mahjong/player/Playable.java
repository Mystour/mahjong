package org.example.mahjong.player;

public interface Playable {
    void drawTile();
    void discardTile();
    void declareChow();
    void declarePung();
    void declareKong();
    void declareMahjong();
}