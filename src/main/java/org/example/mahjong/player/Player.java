package org.example.mahjong.player;

import org.example.mahjong.score.Scorable;

public class Player implements Playable, Scorable {
    private boolean isbanker;

    @Override
    public void drawTile() {}

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

    public boolean getIsBanker() {
        return isbanker;
    }

    public void setIsBanker(boolean isbanker) {
        this.isbanker = isbanker;
    }
}