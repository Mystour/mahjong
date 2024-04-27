package org.example.mahjong.player;

import org.example.mahjong.tile.Tile;

public interface Playable {
    void drawTile();
    Tile discardTile();
    void declareChow();
    void declarePung();
    void declareKong(Tile tile);
    void declareMahjong();
}