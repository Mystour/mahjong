package org.example.mahjong.game;

import org.example.mahjong.tile.BambooTile;
import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

import java.util.*;

public class MahjongGame extends AbstractGame {
    public LinkedList<Tile> tilepile;
    public void creatTilePile(){
        tilepile = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new BambooTile(TileType.BAMBOO,i));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new BambooTile(TileType.CHARACTER,i));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new BambooTile(TileType.DOT,i));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new BambooTile(TileType.DRAGON,i));
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new BambooTile(TileType.WIND,i));
            }
        }
    }
    public void shuffleTiles() {
        Collections.shuffle(tilepile);
    }
    public void distributeInitialTiles() {}
    public void checkWinCondition() {}
    public void endRound() {}
    @Override
    public void startGame() {
        creatTilePile();
        shuffleTiles();
    }

    @Override
    public void endGame() {}
}