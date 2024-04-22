package org.example.mahjong.game;

import org.example.mahjong.player.Player;
import org.example.mahjong.tile.BambooTile;
import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

import java.util.*;

public class MahjongGame extends AbstractGame {
//    public static void main(String[] args) {
//        MahjongGame game = new MahjongGame();
//        game.startGame();
//        System.out.println(game.tilepile.size());
//        game.p1.hand.printCard();
//        game.p1.drawTile();
//        System.out.println();
//        game.p1.hand.printCard();
//    }
    //为了方便，现在所有的变量我都用了public，但是应该有更好的办法
    public LinkedList<Tile> tilepile;
    public Player p1;
    public Player p2;
    public Player p3;
    public Player p4;
    public Player[] players;
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
    public void creatPlayers(){
        p1 = new Player(this);
        p2 = new Player(this);
        p3 = new Player(this);
        p4 = new Player(this);
        players = new Player[4];
        players[0] = p1;
        players[1] = p2;
        players[2] = p3;
        players[3] = p4;
    }
    public void shuffleTiles() {
        Collections.shuffle(tilepile);
    }
    public void distributeInitialTiles() {
        for (int i = 0; i < 4; i++) {
            Player temp = players[i];
            for (int j = 0; j < 13; j++) {
                temp.hand.addCard(tilepile.poll());
            }
            temp.hand.sortAllCard();
        }
    }
    public Tile dealOneCard(){
        return tilepile.poll();
    }

    public static void main(String[] args) {
        MahjongGame game = new MahjongGame();
        game.startGame();
        for (Player player : game.players) {
            player.displayHand();
        }
    }
    public void checkWinCondition() {}
    public void endRound() {}
    @Override
    public void startGame() {
        creatPlayers();
        creatTilePile();
        shuffleTiles();
        distributeInitialTiles();
    }

    @Override
    public void endGame() {}


}