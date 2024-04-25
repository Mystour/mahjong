package org.example.mahjong.player;

import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

public interface Playable {
    void drawTile();
    Tile discardTile(TileType type, int number);
    void declareChow();
    void declarePung();
    void declareKong();
    void declareMahjong();
}