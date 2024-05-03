package org.example.mahjong.player;

import org.example.mahjong.tile.Tile;

public interface Playable {
    Tile drawTile();
    Tile discardTile();
    void declareChow(Tile tile);
    void declarePung(Tile tile);
    void declareKong(Tile tile);
    void declareMahjong(Tile tile);
}