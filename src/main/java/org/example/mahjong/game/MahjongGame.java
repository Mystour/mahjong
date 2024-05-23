package org.example.mahjong.game;

import io.micrometer.common.KeyValues;
import org.example.mahjong.player.Player;
import org.example.mahjong.tile.*;

import java.io.IOException;
import java.util.*;

/**
 * 玩家在自己回合能进行的操作有问题
 * 关于吃操作，怎么控制玩家怎么吃
 * 比如玩家手里有一万，二万，四万，五万要吃三万这怎么判断
 * 注意，现在只能打一轮，就是一人胡牌后游戏结束
 *
 */
public class MahjongGame extends AbstractGame {



    private LinkedList<Tile> tilepile;
    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;

    public boolean isBoardOver() {
        return isBoardOver;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getIndexnum() {
        return indexnum;
    }

    public int getDicenum() {
        return dicenum;
    }

    public LinkedList<Tile> getTilepile() {
        return tilepile;
    }
    public int getChecknum() {
        return checknum;
    }
    private Player[] players;
    private boolean isBoardOver;
    private int indexnum;
    private int checknum;
    private int dicenum;


    public void creatTilePile() {
        tilepile = new LinkedList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new BambooTile(TileType.BAMBOO, i));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new CharacterTile(TileType.CHARACTER, i));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new DotTile(TileType.DOT, i));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new DragonTile(TileType.DRAGON, i));
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                tilepile.add(new WindTile(TileType.WIND, i));
            }
        }
    }

    public void creatPlayers() {
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
                temp.getHand().addCard(tilepile.poll());
            }
            temp.getHand().sortAllCard();
        }
    }

    public Tile dealOneCard() {
        return tilepile.poll();
    }

    public static void main(String[] args) {
        MahjongGame game = new MahjongGame();
        game.startGame();
        game.playOneBoard();
    }



    public static TileType transType(String s) {
        if (s.length() != 2) {
            return null;
        }
        String string = s.substring(1);
        if (string.equals("条")) {
            return TileType.BAMBOO;
        } else if (string.equals("万")) {
            return TileType.CHARACTER;
        } else if (string.equals("筒")) {
            return TileType.DOT;
        } else if (string.equals("风")) {
            return TileType.WIND;
        }
        if (s.equals("红中") || s.equals("发财") || s.equals("白板")) {
            return TileType.DRAGON;
        }
        return null;
    }

    public static int transNum(String s) {
        if (s.length() != 2) {
            return -1;
        }
        String string = s.substring(0, 1);
        if (string.equals("一") || string.equals("东") || s.equals("红中")) {
            return 0;
        } else if (string.equals("二") || string.equals("南") || s.equals("发财")) {
            return 1;
        } else if (string.equals("三") || string.equals("西") || s.equals("白板")) {
            return 2;
        } else if (string.equals("四") || string.equals("北")) {
            return 3;
        } else if (string.equals("五")) {
            return 4;
        } else if (string.equals("六")) {
            return 5;
        } else if (string.equals("七")) {
            return 6;
        } else if (string.equals("八")) {
            return 7;
        } else if (string.equals("九")) {
            return 8;
        } else {
            return -1;
        }
    }



    public Tile getCommandOfDiscard() {
        Tile tile = null;
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.print("请选择要丢弃的牌的名称: ");
            String command = scanner.nextLine(); // 等待用户输入命令
            TileType type = transType(command);
            int num = transNum(command);

            if (type == null || num == -1) {
                System.out.println("无效的牌名称，请重新选择。");
            } else {
                tile = players[indexnum].discardTile(type, num);
            }
        } while (tile == null);

        // scanner.close();
        return tile;

    }

    // 该阶段包括抽牌，不需要外部命令，此时一定知道玩家顺序
    public void beforeRound() {
        Tile temp = players[indexnum].drawTile();
        System.out.println("抽到的手牌是：" + temp.toString());
        printPlayerCard();
        if (askForOperationConfirmation(indexnum)) {
            if (players[indexnum].isCanMahjong()) {
                performMahjong(temp);
            }
            if (players[indexnum].isCanKong()) {
                performKong(temp);
            }
        }
    }

    // 该阶段包括吃碰杠的判断，需要外部命令，不包括判断，是直接进行操作
    public void insertRound(Tile discardtile) {
        players[indexnum].displayHand();
        if (players[indexnum].isCanMahjong()) {
            if (performMahjong(discardtile)) {
                players[indexnum].displayHand();
                return;
            }
        }
        if (players[indexnum].isCanChow()) {
            if (performChow(discardtile)) {
                players[indexnum].displayHand();
                return;
            }
        }
        if (players[indexnum].isCanPung()) {
            if (performPung(discardtile)) {
                players[indexnum].displayHand();
                return;
            }
        }
        if (players[indexnum].isCanKong()) {
            if (performKong(discardtile)) {
                players[indexnum].displayHand();
                return;
            }
        }

    }

    public boolean askForOperationConfirmation(int playerIndex) {
        Player player = players[playerIndex];
        if (!player.isCanChow() && !player.isCanKong() && !player.isCanPung() && !player.isCanMahjong()) {
            if(playerIndex != indexnum){
                Scanner scanner = new Scanner(System.in);
                System.out.print("玩家" + (playerIndex + 1) + "可以进行操作：");
                System.out.print(" 跳过");
                System.out.println();
                System.out.println("输入0表示玩家" + (playerIndex + 1) + "想要进行以上操作");
                int num = -1;
                try {
                    while (System.in.available() == 0) {
                        Thread.sleep(100); // 暂停100毫秒
                    }

                    num = Integer.parseInt(scanner.nextLine()); // 读取输入并尝试转换为整数
                } catch (NumberFormatException e) {
                    num = -1; // 如果输入无法转换为整数，将num设为-1，表示错误的输入
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // scanner.close();

                if (num == 0) {
                    return false;
                } else {
                    System.out.println("错误输入，请重新操作。");
                    askForOperationConfirmation(indexnum);
                }
            }else {
                return false;
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("玩家" + (playerIndex + 1) + "可以进行操作：");

            if (player.isCanChow()) {
                System.out.print("吃 ");
            }
            if (player.isCanPung()) {
                System.out.print("碰 ");
            }
            if (player.isCanKong()) {
                System.out.print("杠 ");
            }
            if (player.isCanMahjong()) {
                System.out.print("胡 ");
            }
            int num = -1;
            System.out.println();
            System.out.println("输入1表示玩家" + (playerIndex + 1) + "想要进行以上操作");
            System.out.println("输入0表示玩家" + (playerIndex + 1) + "放弃进行以上操作：");

            try {
                while (System.in.available() == 0) {
                    Thread.sleep(100); // 暂停100毫秒
                }

                num = Integer.parseInt(scanner.nextLine()); // 读取输入并尝试转换为整数
            } catch (NumberFormatException e) {
                num = -1; // 如果输入无法转换为整数，将num设为-1，表示错误的输入
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // scanner.close();

            if (num == 1) {
                return true;
            } else if (num == 0) {
                return false;
            } else {
                System.out.println("错误输入，请重新操作。");
                askForOperationConfirmation(indexnum);
            }
        }
        return false;
    }

    public boolean performChow(Tile tile) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("玩家" + (indexnum + 1) + "可以进行吃操作：");
        int num = -1;
        System.out.println("输入1表示玩家" + (indexnum + 1) + "想要进行以上操作");
        System.out.println("输入0表示玩家" + (indexnum + 1) + "放弃进行以上操作：");
        try {
            while (System.in.available() == 0) {
                Thread.sleep(100); // 暂停100毫秒
            }

            num = Integer.parseInt(scanner.nextLine()); // 读取输入并尝试转换为整数
        } catch (NumberFormatException e) {
            num = -1; // 如果输入无法转换为整数，将num设为-1，表示错误的输入
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // scanner.close();
        if (num == 1) {
            players[indexnum].declareChow(tile);
            return true;
        } else if (num == 0) {
            return false;
        } else {
            System.out.println("错误操作，请重新输入。");
            performChow(tile);
        }
        return false;
    }

    public boolean performPung(Tile tile) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("玩家" + (indexnum + 1) + "可以进行碰操作：");
        int num = -1;
        System.out.println("输入1表示玩家" + (indexnum + 1) + "想要进行以上操作");
        System.out.println("输入0表示玩家" + (indexnum + 1) + "放弃进行以上操作：");
        try {
            while (System.in.available() == 0) {
                Thread.sleep(100); // 暂停100毫秒
            }

            num = Integer.parseInt(scanner.nextLine()); // 读取输入并尝试转换为整数
        } catch (NumberFormatException e) {
            num = -1; // 如果输入无法转换为整数，将num设为-1，表示错误的输入
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // scanner.close();
        if (num == 1) {
            players[indexnum].declarePung(tile);
            return true;
        } else if (num == 0) {
            return false;
        } else {
            System.out.println("错误操作，请重新输入。");
            performPung(tile);
        }
        return false;
    }

    public boolean performKong(Tile tile) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("玩家" + (indexnum + 1) + "可以进行杠操作：");
        int num = -1;
        System.out.println("输入1表示玩家" + (indexnum + 1) + "想要进行以上操作");
        System.out.println("输入0表示玩家" + (indexnum + 1) + "放弃进行以上操作：");
        try {
            while (System.in.available() == 0) {
                Thread.sleep(100); // 暂停100毫秒
            }

            num = Integer.parseInt(scanner.nextLine()); // 读取输入并尝试转换为整数
        } catch (NumberFormatException e) {
            num = -1; // 如果输入无法转换为整数，将num设为-1，表示错误的输入
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // scanner.close();
        if (num == 1) {
            players[indexnum].declareKong(tile);
            return true;
        } else if (num == 0) {
            return false;
        } else {
            System.out.println("错误操作，请重新输入。");
            performKong(tile);
        }
        return false;
    }

    public boolean performMahjong(Tile tile) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("玩家" + (indexnum + 1) + "可以进行胡牌操作：");
        int num = -1;
        System.out.println("输入1表示玩家" + (indexnum + 1) + "想要进行以上操作");
        System.out.println("输入0表示玩家" + (indexnum + 1) + "放弃进行以上操作：");
        try {
            while (System.in.available() == 0) {
                Thread.sleep(100); // 暂停100毫秒
            }

            num = Integer.parseInt(scanner.nextLine()); // 读取输入并尝试转换为整数
        } catch (NumberFormatException e) {
            num = -1; // 如果输入无法转换为整数，将num设为-1，表示错误的输入
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // scanner.close();
        if (num == 1) {
            players[indexnum].declareMahjong(tile);
            isBoardOver = true;
            return true;
        } else if (num == 0) {
            return false;
        } else {
            System.out.println("错误操作，请重新输入。");
            performMahjong(tile);
        }
        return false;
    }


    // 该阶段包括弃牌，是在吃碰杠的判定之后的,需要关于弃掉牌名的外部命令
    public Tile onRound() {
        Tile temp = getCommandOfDiscard();
        System.out.println("玩家" + (indexnum + 1) + "出牌后: ");
        printPlayerCard();
        return temp;
////        Tile temp;
////        do {
////            temp = getCommandOfDiscard_js();
////        } while (temp == null);
//
//        System.out.println("玩家" + (indexnum + 1) + "出牌后: ");
//        printPlayerCard();
//        return temp;
    }

    // 系统接收弃牌，并且开始判断下一个出牌玩家
    // 返回flase说明是正常的下个玩家 -> beforeRound
    // 返回ture说明要变换出牌顺序 -> insertRound
    public boolean checkRound(Tile drawnTile) {
        System.out.println("判定中ing");
        for (int i = (indexnum + 1) % 4, index = 0; index < 3; i = (i + 1) % 4, index++) {
            checknum = i;
            players[i].checkDecisionCondition(drawnTile);
            if (askForOperationConfirmation(i)) {
                indexnum = i;
                return true;
            }
        }
        System.out.println("正常进入下一回合");
        players[indexnum].putInDiscardPile(drawnTile);
        System.out.println("玩家" + (indexnum + 1) + "最终手牌: ");
        printPlayerCard();
        indexnum = (indexnum + 1) % 4;
        checknum = indexnum;
        return false;
    }

    public void playOneBoard() {
        while (!isBoardOver) {
            System.out.println("现在是玩家" + (indexnum + 1) + "的回合");
            beforeRound();
            if (isBoardOver) {
                break;
            }
            Tile discardTile = onRound();
            while (checkRound(discardTile)) {
                insertRound(discardTile);
                if (isBoardOver) {
                    break;
                }
                discardTile = onRound();
            }
        }
        System.out.println("一局游戏结束，玩家" + (indexnum + 1) + "获胜");
        System.out.println("分数是" + players[indexnum].calculateScore());

    }

    public void endBoard() {
        // 清理所有玩家的手牌
        for (Player player : players) {
            player.getHand().clearHand();
        }

        resetGameState();

        // 准备新一轮游戏
        setupNewBoard();
    }

    // 重置游戏状态的方法
    private void resetGameState() {
        // 清空牌堆
        tilepile.clear();

    }

    public void printPlayerCard() {
        players[indexnum].displayHand();
    }

    // 设置新回合
    private void setupNewBoard() {
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
            allHands.add(player.getHand().getAllHandcard());
        }
        return allHands;
    }
    public List<List<Tile>> getAllPlayersDiscards() {
        List<List<Tile>> discards = new ArrayList<>();
        for (Player player : players) {
            discards.add(player.getHand().getDiscards());
        }
        return discards;
    }
    public List<List<Tile>> getAllPlayersShowedCards() {
        List<List<Tile>> showedcards = new ArrayList<>();
        for (Player player : players) {
            List<Tile> temp = new ArrayList<>();
            temp.addAll(player.getHand().getChows());
            temp.addAll(player.getHand().getPungs());
            temp.addAll(player.getHand().getKongs());
            showedcards.add(temp);
        }
        return showedcards;
    }
    public List<List<Boolean>> getAllPlayersCondition() {
        List<List<Boolean>> allcondition = new ArrayList<>();
        for (Player player : players) {
            allcondition.add(player.getAllCondition());
        }
        return allcondition;
    }
    public List<Integer> getAllPlayersScores(){
        List<Integer> allscores = new ArrayList<>();
        for (Player player : players)
            allscores.add(player.getScore());
        return allscores;
    }

    public List<Integer> getPlayerIndex(){
        List<Integer> indexs = new ArrayList<>();
        indexs.add(indexnum);
        indexs.add(checknum);
        return indexs;
    }
    public List<List<Tile>> getDiscardingTile() {
        List<List<Tile>> DiscardingTile = new ArrayList<>();
        for (Player player : players) {
            DiscardingTile.add(new ArrayList<>());
        }
        if(discradTile != null){
            DiscardingTile.get(indexnum).add(discradTile);
        }
        return DiscardingTile;
    }

    @Override
    public void startGame() {
        creatPlayers();
        creatTilePile();
        shuffleTiles();
        distributeInitialTiles();
        Random random = new Random();
        dicenum = random.nextInt(10) + 2;
        indexnum = dicenum % 4;
        checknum = indexnum;
        players[indexnum].setIsbanker(true);
//        players[indexnum].getHand().clearHand();
//        players[(indexnum + 1)%4].getHand().clearHand();
//        players[indexnum].getHand().addCard(new CharacterTile(TileType.CHARACTER,1));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,1));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,1));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,2));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,2));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,2));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,3));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,3));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,3));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,4));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,4));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,4));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,5));
//        players[(indexnum + 1)%4].getHand().addCard(new CharacterTile(TileType.CHARACTER,5));
    }


    @Override
    public void endGame() {
        for (Player player : players) {
            player.playAgain();
        }
        drawTile = null;
        discradTile = null;
        creatTilePile();
        shuffleTiles();
        distributeInitialTiles();
        Random random = new Random();
        dicenum = random.nextInt(10) + 2;
        indexnum = dicenum % 4;
        checknum = indexnum;
        players[indexnum].setIsbanker(true);


    }
    private Tile drawTile;
    //perform
    public void currentPlayerDraw(){
        drawTile = players[indexnum].drawTile();
    }
    //perform
    public void currentPlayerKong(){
        if(drawTile != null){
            players[indexnum].declareKong(drawTile);
            drawTile = null;
        }

    }

    public void currentPlayerMahjong(){
        if(drawTile != null){
            players[indexnum].declareMahjong(drawTile);
            drawTile = null;
        }
    }

    public static TileType transTypeFromMessage(String s) {
        String string = s.substring(0, s.length() - 1);
        System.out.println(string);
        if (string.equals("BAMBOO")) {
            return TileType.BAMBOO;
        } else if (string.equals("CHARACTER")) {
            return TileType.CHARACTER;
        } else if (string.equals("DOT")) {
            return TileType.DOT;
        } else if (string.equals("WIND")) {
            return TileType.WIND;
        } else if (string.equals("DRAGON")) {
            return TileType.DRAGON;
        }
        return null;
    }
    public static int transNumFromMessage(String s){
        char lastChar = s.charAt(s.length() - 1);
        System.out.println(Integer.parseInt(String.valueOf(lastChar)) - 1);
        // 将字符转换为字符串，然后再使用 Integer.parseInt() 方法将其转换为整数
        return Integer.parseInt(String.valueOf(lastChar)) - 1;
    }

    private Tile discradTile;

    public void receiveTileFromMessage(String string) {
        TileType type = transTypeFromMessage(string);
        int num = transNumFromMessage(string);
        Tile tile = players[indexnum].discardTile(type, num);
        if(tile != null){
            discradTile = tile;
            for (int i = 0, index = (indexnum + 1) % 4; i < 3; i++,index = (index + 1) % 4) {
                players[index].checkDecisionCondition(tile);
            }
            checknum = (indexnum + 1) % 4;
        }

    }
    public boolean isdiscardOver(){
        if(checknum == indexnum && discradTile != null){
            players[indexnum].putInDiscardPile(discradTile);
            discradTile = null;
            indexnum = (indexnum + 1) % 4;
            checknum = indexnum;
            return true;
        }
        return false;
    }
    public void otherPlayerSkip(){
        checknum = (checknum + 1) % 4;
    }
    public void otherPlayerChow(){
        players[checknum].declareChow(discradTile);
        discradTile = null;
        indexnum = checknum;
    }
    public void otherPlayerPung(){
        players[checknum].declarePung(discradTile);
        discradTile = null;
        indexnum = checknum;
    }
    public void otherPlayerKong(){
        players[checknum].declareKong(discradTile);
        discradTile = null;
        indexnum = checknum;
    }
    public void otherPlayerMahjong(){
        players[checknum].declareMahjong(discradTile);
        discradTile = null;
        indexnum = checknum;
    }

}