package org.example.mahjong.player;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.score.Scorable;
import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

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

    //现在弃牌是需要牌的类型和值的，弃牌也是弃掉第一张匹配的牌
    //但是在游戏里是根据鼠标选中一张牌的，按道理是选中一个对象，
    //所以我重写了一个方法，但是这个方法没有检验tile是不是null，因为不知道到时候应该怎么实现
    //在hand里也写过，这里返回Tile是为了让系统判断是否其他玩家需要然后给它们
    //如果不需要再放入该玩家弃牌堆
    @Override
    public Tile discardTile(TileType type, int number) {
        Tile tile = hand.discard(type, number);
        if (tile == null){
            System.out.println("没有这张牌无法丢弃");
            return null;
        }
        return tile;
    }
    public Tile discardTile(Tile tile) {
        return hand.discard(tile);
    }

    public void discardIntoPile(Tile tile){
        hand.addDiscards(tile);
    }

    //吃碰杠都是对系统中存有的牌进行操作，所以需要的变量都是Tile类型的
    //还有这边觉得，吃碰杠需要主动选择和被动判断
    //由系统调用hand里的确认吃碰杠的方法
    //以下player的方法都是主动触发的
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