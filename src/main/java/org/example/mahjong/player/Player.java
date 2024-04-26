package org.example.mahjong.player;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.score.Scorable;
import org.example.mahjong.score.ScoringSystem;
import org.example.mahjong.tile.Tile;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Player implements Playable, Scorable {
    private ScoringSystem scoringSystem;
    public boolean isbanker;
    public Hand hand;
    public MahjongGame game;
    public Player(MahjongGame game) {
        hand = new Hand();
        this.game = game;
        this.scoringSystem = new ScoringSystem(hand.handcard);
    }

    @Override
    public void drawTile() {
        //这里套了好多哈哈，不知道怎么简化QWQ
        hand.sortCard(hand.addCard(game.dealOneCard()));
    }

    @Override
    public Tile discardTile() {
        // 显示玩家当前的手牌
        System.out.println("当前手牌: ");
        for (int i = 0; i < this.hand.handcard.length; i++) {
            if (this.hand.handcard[i] != null && !this.hand.handcard[i].isEmpty()) {
                System.out.println("类型 " + i + ": " + this.hand.handcard[i]);
            } else {
                System.out.println("类型 " + i + ": 空");
            }
        }

        // 请求玩家选择要丢弃的牌
        Scanner scanner = new Scanner(System.in);
        int typeIndex = -1, tileIndex = -1;
        do {
            System.out.print("请选择要丢弃的牌的类型索引: ");
            typeIndex = scanner.nextInt();
            if (typeIndex >= 0 && typeIndex < this.hand.handcard.length && !this.hand.handcard[typeIndex].isEmpty()) {
                System.out.println("你选择的类型是 " + typeIndex + "，有以下牌: " + this.hand.handcard[typeIndex]);
                System.out.print("请选择要丢弃的牌的索引: ");
                tileIndex = scanner.nextInt();
                if (tileIndex < 0 || tileIndex >= this.hand.handcard[typeIndex].size()) {
                    System.out.println("无效的牌索引，请重新选择。");
                    tileIndex = -1; // Reset tile index for reselection
                }
            } else {
                System.out.println("无效的类型索引或者所选类型没有牌，请重新选择。");
                typeIndex = -1; // Reset type index for reselection
            }
        } while (typeIndex == -1 || tileIndex == -1);

        Tile tileToDiscard = this.hand.handcard[typeIndex].remove(tileIndex);
        System.out.println("玩家丢弃的牌是: " + tileToDiscard);

        return tileToDiscard;
    }





    @Override
    public void declareChow() {}

    @Override
    public void declarePung() {}

    @Override
    public void declareKong(Tile tile) {
        if (hand.canKong(tile)) {
            hand.executeKong(tile);
            // 补一张牌，通常是杠后从牌堆中摸取
            drawTileFromPile();
        }
    }
    private void drawTileFromPile() {
        Tile newTile = game.dealOneCard();
        if (newTile != null) {
            hand.addCard(newTile); // 假设addCard已经处理了排序的逻辑
        }
    }

    public void reactToDiscard(Tile discardedTile) {
        // 这里是示例逻辑，根据游戏规则调整
        System.out.println("考虑是否对丢弃的牌做出反应。");
        // 根据具体逻辑实现决策过程...
    }


    @Override
    public void declareMahjong() {}

    @Override
    public int calculateScore() {
        return scoringSystem.getScore();
    }

    // 根据当前情况做出决策
    public void makeDecision(Tile drawnTile) {
        System.out.println("考虑是否杠、碰、胡等操作。");
        // 检查是否可以吃、碰或杠
        // TODO
        if (hand.canKong(drawnTile)) {
            // 如果可以杠，则执行杠操作
            declareKong(drawnTile);
        } else {
            // TODO:如果不杠，根据其他条件做决策
            System.out.println("Player chooses not to Kong.");
        }

        // 检查是否可以胡牌
        if (hand.isValidMahjong()) {
            // 如果可以胡牌，根据具体规则做出决策
            System.out.println("Player has a winning hand.");
        }

        // 做出打牌决策
        discardTile();
    }

    // Displays the player's hand
    public void displayHand() {
        System.out.println("Player's Hand:");
        hand.printCards();
    }

    // 清理手牌的方法
    public void clearHand() {
        hand.handcard = new List[5];
        for (int i = 0; i < hand.handcard.length; i++) {
            hand.handcard[i] = new LinkedList<>();
        }
        hand.chowsAndPungs.clear();
        hand.kongs.clear();
    }



    public boolean getIsBanker() {
        return isbanker;
    }

    public void setIsBanker(boolean isbanker) {
        this.isbanker = isbanker;
    }
}