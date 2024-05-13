package org.example.mahjong.player;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.score.Scorable;
import org.example.mahjong.score.ScoringSystem;
import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

import java.util.Scanner;

public class Player implements Playable, Scorable {
    private ScoringSystem scoringSystem;

    public void setIsbanker(boolean isbanker) {
        this.isbanker = isbanker;
    }

    private boolean isbanker;

    public ScoringSystem getScoringSystem() {
        return scoringSystem;
    }

    public boolean getIsbanker() {
        return isbanker;
    }


    public boolean isBanker() {
        return isbanker;
    }

    public boolean isCanChow() {
        return canChow;
    }

    public boolean isCanPung() {
        return canPung;
    }

    public boolean isCanKong() {
        return canKong;
    }

    public boolean isCanMahjong() {
        return canMahjong;
    }

    public Hand getHand() {
        return hand;
    }

    public MahjongGame getGame() {
        return game;
    }
    public boolean isHasMahjong() {
        return hasMahjong;
    }

    private boolean canChow;
    private boolean canPung;
    private boolean canKong;
    private boolean canMahjong;
    private boolean hasMahjong;
    private Hand hand;
    private MahjongGame game;

    public Player(MahjongGame game) {
        hand = new Hand();
        this.game = game;
        this.scoringSystem = new ScoringSystem(hand);
    }


    //抽牌意味着在自身回合，要检查杠和胡牌的条件
    @Override
    public Tile drawTile() {
        Tile temp = game.dealOneCard();
        canChow = false;
        canPung = false;
        canKong = hand.canKong(temp);
        hand.sortCard(hand.addCard(temp));
        canMahjong = hand.isValidMahjong_Myself();
        return temp;
    }

    @Override
    public Tile discardTile(Tile tile){
        return hand.discard(tile);
    }

    public Tile getTile(TileType tileType, int number){
        return hand.findTile(tileType, number);
    }

    public Tile discardTile(TileType tileType, int number){
        return hand.discard(tileType,number);
    }

    public void putInDiscardPile(Tile discardedTile){
        hand.addDiscards(discardedTile);
    }


    @Override
    public void declareChow(Tile tile) {
        if(hand.canChow(tile)){
            hand.executeChows(tile);
        }
    }

    @Override
    public void declarePung(Tile tile) {
        if (hand.canPung(tile)){
            hand.executePung(tile);
        }
    }

    @Override
    public void declareKong(Tile tile) {
        if (hand.canKong(tile)) {
            hand.executeKong(tile);
            // 补一张牌，通常是杠后从牌堆中摸取
            // 改成了drawtile方法，可以排序
            drawTile();
        }
    }




    //Mahjong好像就是胡的意思，所以这个方法应该是玩家胡牌
    @Override
    public void declareMahjong(Tile tile) {
        if(canMahjong){
            hand.addCard(tile);
            hasMahjong = true;
        }
    }

    @Override
    public int calculateScore() {
        return scoringSystem.calculateScore();
    }


    //在其他人的回合判断可以操作的条件
    public void checkDecisionCondition(Tile drawnTile){
        canChow = hand.canChow(drawnTile);
        canPung = hand.canPung(drawnTile);
        canKong = hand.canKong(drawnTile);
        canMahjong = hand.isValidMahjong_Other(drawnTile);
    }

    // Displays the player's hand
    public void displayHand() {
        hand.printCards();
    }





}