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

    //以下boolean值可以用在gui页面的判断中
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
    //这个我觉得应该放在游戏里，属于轮次类的东西，应该交给系统判断
    public Tile discardTile() {
        // 显示玩家当前的手牌
        System.out.println("当前手牌: ");
        for (int i = 0; i < this.hand.getHandcard().length; i++) {
            if (this.hand.getHandcard()[i] != null && !this.hand.getHandcard()[i].isEmpty()) {
                System.out.println("类型 " + i + ": " + this.hand.getHandcard()[i]);
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
            if (typeIndex >= 0 && typeIndex < this.hand.getHandcard().length && !this.hand.getHandcard()[typeIndex].isEmpty()) {
                System.out.println("你选择的类型是 " + typeIndex + "，有以下牌: " + this.hand.getHandcard()[typeIndex]);
                System.out.print("请选择要丢弃的牌的索引: ");
                tileIndex = scanner.nextInt();
                if (tileIndex < 0 || tileIndex >= this.hand.getHandcard()[typeIndex].size()) {
                    System.out.println("无效的牌索引，请重新选择。");
                    tileIndex = -1; // Reset tile index for reselection
                }
            } else {
                System.out.println("无效的类型索引或者所选类型没有牌，请重新选择。");
                typeIndex = -1; // Reset type index for reselection
            }
        } while (typeIndex == -1 || tileIndex == -1);

        Tile tileToDiscard = this.hand.getHandcard()[typeIndex].remove(tileIndex);
        System.out.println("玩家丢弃的牌是: " + tileToDiscard);

        return tileToDiscard;
    }


    //这个给gui界面做的
    public Tile discardTile(Tile tile){
        return hand.discard(tile);
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



    public void reactToDiscard(Tile discardedTile) {
        // 这里是示例逻辑，根据游戏规则调整
        System.out.println("考虑是否对丢弃的牌做出反应。");
        // 根据具体逻辑实现决策过程...
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


    // 根据当前情况做出决策
    // 点炮的情况还没做好
    // 吃碰杠都是发生在别人的回合结束阶段，这种写法是直接进行吃碰杠了
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
        if (hand.isValidMahjong_Other(drawnTile)) {
            // 如果可以胡牌，根据具体规则做出决策
            System.out.println("Player has a winning hand.");
        }

        // 做出打牌决策
        discardTile();
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

    // 清理手牌的方法




    public boolean getIsBanker() {
        return isbanker;
    }

    public void setIsBanker(boolean isbanker) {
        this.isbanker = isbanker;
    }
}