package org.example.mahjong.player;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.score.Scorable;

public class Player implements Playable, Scorable {
    public boolean isbanker;
    public Hand hand;
    public MahjongGame game;
    public Player(MahjongGame game) {
        hand = new Hand();
        this.game = game;
    }

    @Override
    public void drawTile() {
        //这里套了好多哈哈，不知道怎么简化QWQ
        hand.sortCard(hand.addCard(game.dealOneCard()));
    }

    @Override
    public void discardTile() {}

    @Override
    public void declareChow() {}

    @Override
    public void declarePung() {}

    @Override
    public void declareKong() {}

    @Override
    public void declareMahjong() {}

    @Override
    public void calculateScore() {}

    // Displays the player's hand
    public void displayHand() {
        System.out.println("Player's Hand:");
        hand.printCards();
    }

    public boolean getIsBanker() {
        return isbanker;
    }

    public void setIsBanker(boolean isbanker) {
        this.isbanker = isbanker;
    }
}