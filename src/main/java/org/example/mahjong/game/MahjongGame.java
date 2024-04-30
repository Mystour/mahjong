package org.example.mahjong.game;

import org.example.mahjong.player.Player;
import org.example.mahjong.tile.BambooTile;
import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

import java.util.*;

public class MahjongGame extends AbstractGame {

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
        game.playRound();
    }
    public void checkWinCondition() {}
    @Override
    public void startGame() {
        creatPlayers();
        creatTilePile();
        shuffleTiles();
        distributeInitialTiles();
    }

    public void playRound() {

        startGame();

        boolean roundEnded = false;
        while (!tilepile.isEmpty() && !roundEnded) {
            // 四个玩家轮流摸牌和做出决策
            for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
                Player player = players[playerIndex];
                System.out.println("轮到玩家 " + (playerIndex + 1) + " 行动。");
                System.out.println("轮到玩家行动");

                // 玩家摸牌
                Tile drawnTile = dealOneCard();
                if (drawnTile != null) {
                    System.out.println("摸到的牌是：" + drawnTile);
                    player.drawTile();
                } else {
                    System.out.println("牌堆中没有牌了。");
                    roundEnded = true;
                    break;
                }

                // 让玩家做出决策
                player.makeDecision(drawnTile);


                // TODO:这部分还没完成，但是吃碰杠的逻辑在前面写好了 检查其他玩家是否可以对这张牌做出反应
//                for (Player otherPlayer : new Player[]{p1, p2, p3, p4}) {
//                    if (otherPlayer != player) {
//                        otherPlayer.reactToDiscard(discardedTile);
//                    }
//                }
            }
        }

        // 结束回合，可能是因为牌摸完了，或者有玩家胡牌
        endRound();
    }

    public void endRound() {
        // 清理所有玩家的手牌
        for (Player player : players) {
            player.clearHand();
        }


        resetGameState();

        // 准备新一轮游戏
        setupNewRound();
    }

    // 重置游戏状态的方法
    private void resetGameState() {
        // 清空牌堆
        tilepile.clear();

        // TODO:可以在这里添加其他重置逻辑，如重置玩家状态、计分板等
    }

    // 设置新回合
    private void setupNewRound() {
        // 重新创建牌堆
        creatTilePile();

        // 洗牌
        shuffleTiles();

        // 分牌
        distributeInitialTiles();
    }


    public List<List<Tile>> getAllPlayersHands() {
        List<List<Tile>> allHands = new ArrayList<>();
        for (Player player : players) {
            allHands.add(player.hand.getHandcard());
        }
        return allHands;
    }

    @Override
    public void endGame() {}


}