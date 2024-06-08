package org.example.mahjong.game;


import org.example.mahjong.player.Player;
import org.example.mahjong.tile.*;
import java.util.*;

/**
 * The MahjongGame class represents a game of Mahjong.
 * It extends the AbstractGame class and manages the game's state, players, and tile operations.
 */
public class MahjongGame extends AbstractGame {
    //A tilepile is the library of cards in the game,
    // which is generated by shuffling the order and dealing cards to the players one by one
    private LinkedList<Tile> tilepile;
    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;

    //An array to store players
    private Player[] players;
    //Whether current board of game is over or not
    private boolean isBoardOver;
    //The index of the current player's turn
    //When indexnum equals checknum, it indicates the time the current player played
    private int indexnum;
    //The index of the players currently being checked
    private int checknum;
    //Each game starts with a roll of the dice to determine which player will play first
    private int dicenum;
    //A temporary variable that keeps track of the cards the current player has drawn
    private Tile drawTile;
    //A temporary variable that keeps track of cards discarded by the current player
    private Tile discradTile;

    /**
     * Checks if the board is over.
     * @return true if the board is over, false otherwise
     */
    public boolean isBoardOver() {
        return isBoardOver;
    }

    /**
     * Gets the array of players.
     * @return the array of players
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Gets the current index number of the player.
     * @return the current index number
     */
    public int getIndexnum() {
        return indexnum;
    }

    /**
     * Gets the dice number.
     * @return the dice number
     */
    public int getDicenum() {
        return dicenum;
    }

    /**
     * Gets the tile pile.
     * @return the tile pile as a LinkedList
     */
    public LinkedList<Tile> getTilepile() {
        return tilepile;
    }

    /**
     * Gets the check number.
     * @return the check number
     */
    public int getChecknum() {
        return checknum;
    }

    /**
     * Gets the hands of all players.
     * @return a list of lists of tiles representing the hands of all players
     */
    public List<List<Tile>> getAllPlayersHands() {
        List<List<Tile>> allHands = new ArrayList<>();
        for (Player player : players) {
            allHands.add(player.getHand().getAllHandcard());
        }
        return allHands;
    }

    /**
     * Gets the discard piles of all players.
     * @return a list of lists of tiles representing the discard piles of all players
     */
    public List<List<Tile>> getAllPlayersDiscards() {
        List<List<Tile>> discards = new ArrayList<>();
        for (Player player : players) {
            discards.add(player.getHand().getDiscards());
        }
        return discards;
    }

    /**
     * Gets the tiles that have been shown by all players (chows, pungs, kongs).
     * @return a list of lists of tiles representing the shown tiles of all players
     */
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

    /**
     * Gets the decision conditions of all players.
     * @return a list of lists of booleans representing the decision conditions of all players
     */
    public List<List<Boolean>> getAllPlayersCondition() {
        List<List<Boolean>> allcondition = new ArrayList<>();
        for (Player player : players) {
            allcondition.add(player.getAllCondition());

        }
        return allcondition;
    }

    /**
     * Gets the scores of all players.
     * @return a list of integers representing the scores of all players
     */
    public List<Integer> getAllPlayersScores() {
        List<Integer> allscores = new ArrayList<>();
        for (Player player : players) {
            allscores.add(player.getScore());
        }
        return allscores;
    }

    /**
     * Gets the index numbers for the current player and check number.
     * @return a list of integers representing the index numbers
     */
    public List<Integer> getPlayerIndex() {
        List<Integer> indexs = new ArrayList<>();
        indexs.add(indexnum);
        indexs.add(checknum);
        return indexs;
    }

    /**
     * Gets the tile that is currently being discarded.
     * @return a list of lists of tiles representing the discarding tile for each player
     */
    public List<List<Tile>> getDiscardingTile() {
        List<List<Tile>> DiscardingTile = new ArrayList<>();
        for (Player player : players) {
            DiscardingTile.add(new ArrayList<>());
        }
        if (discradTile != null) {
            DiscardingTile.get(indexnum).add(discradTile);
        }
        return DiscardingTile;
    }

    /**
     * Creates the initial tile pile for the game.
     */
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

    /**
     * Creates the players for the game.
     */
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

    /**
     * Shuffles the tiles in the tile pile.
     */
    public void shuffleTiles() {
        Collections.shuffle(tilepile);
    }

    /**
     * Distributes the initial tiles to each player.
     */
    public void distributeInitialTiles() {
        for (int i = 0; i < 4; i++) {
            Player temp = players[i];
            for (int j = 0; j < 13; j++) {
                temp.getHand().addCard(tilepile.poll());
            }
            temp.getHand().sortAllCard();
        }
    }

    /**
     * Start a new game
     */
    @Override
    public void startGame() {
        creatPlayers();
        initializeGame();
    }

    /**
     * End current game and start another game
     */
    @Override
    public void endGame() {
        for (Player player : players) {
            player.playAgain();
        }
        drawTile = null;
        discradTile = null;
        initializeGame();
    }
    /**
     * Initialize a condition of new game
     */
    private void initializeGame(){
        creatTilePile();
        shuffleTiles();
        distributeInitialTiles();
        Random random = new Random();
        dicenum = random.nextInt(10) + 2;
        indexnum = dicenum % 4;
        checknum = indexnum;
        players[indexnum].setIsbanker(true);
    }

    /**
     * Deals one card from the tile pile.
     * @return the dealt tile
     */
    public Tile dealOneCard() {
        return tilepile.poll();
    }

    /**
     * The current player draws a tile.
     */
    public void currentPlayerDraw() {
        checkBoardOver();
        drawTile = players[indexnum].drawTile();


    }

    /**
     * The current player declares a kong.
     */
    public void currentPlayerKong() {
        if (drawTile != null) {
            players[indexnum].declareKong(drawTile);
            drawTile = null;
        }
    }

    /**
     * The current player declares Mahjong.
     */
    public void currentPlayerMahjong() {
        if (drawTile != null) {
            players[indexnum].declareMahjong(drawTile);
            drawTile = null;
        }
    }

    /**
     * Translates a tile type from a message string.
     * @param s the message string
     * @return the corresponding TileType
     */
    public static TileType transTypeFromMessage(String s) {
        String string = s.substring(0, s.length() - 1);
        return switch (string) {
            case "BAMBOO" -> TileType.BAMBOO;
            case "CHARACTER" -> TileType.CHARACTER;
            case "DOT" -> TileType.DOT;
            case "WIND" -> TileType.WIND;
            case "DRAGON" -> TileType.DRAGON;
            default -> null;
        };
    }

    /**
     * Translates a tile number from a message string.
     * @param s the message string
     * @return the corresponding tile number
     */
    public static int transNumFromMessage(String s) {
        char lastChar = s.charAt(s.length() - 1);
        return Integer.parseInt(String.valueOf(lastChar)) - 1;
    }

    /**
     * Receives a tile from a message string and processes it for the current player.
     * @param string the message string
     */
    public void receiveTileFromMessage(String string) {
        TileType type = transTypeFromMessage(string);
        int num = transNumFromMessage(string);
        Tile tile = players[indexnum].discardTile(type, num);
        if (tile != null) {
            discradTile = tile;
            for (int i = 0, index = (indexnum + 1) % 4; i < 3; i++, index = (index + 1) % 4) {
                players[index].checkDecisionCondition(tile);
            }
            checknum = (indexnum + 1) % 4;
        }
    }

    /**
     * Checks if the discard phase is over.
     * @return true if the discard phase is over, false otherwise
     */
    public boolean isdiscardOver() {
        if (checknum == indexnum && discradTile != null) {
            players[indexnum].putInDiscardPile(discradTile);
            discradTile = null;
            indexnum = (indexnum + 1) % 4;
            checknum = indexnum;
            return true;
        }
        return false;
    }

    /**
     * Skips the current player to the next player.
     */
    public void otherPlayerSkip() {
        checknum = (checknum + 1) % 4;
    }

    /**
     * The current player declares a chow.
     */
    public void otherPlayerChow() {
        players[checknum].declareChow(discradTile);
        discradTile = null;
        indexnum = checknum;
    }

    /**
     * The current player declares a pung.
     */
    public void otherPlayerPung() {
        players[checknum].declarePung(discradTile);
        discradTile = null;
        indexnum = checknum;
    }

    /**
     * The current player declares a kong.
     */
    public void otherPlayerKong() {
        players[checknum].declareKong(discradTile);
        discradTile = null;
        indexnum = checknum;
    }

    /**
     * The current player declares Mahjong.
     */
    public void otherPlayerMahjong() {
        players[checknum].declareMahjong(discradTile);
        discradTile = null;
        indexnum = checknum;
    }
    /**
     * If there are no tiles left in the pile, start the game again.
     */
    private void checkBoardOver(){
        isBoardOver = tilepile.size() <= 0;
        if(isBoardOver){
            endGame();
        }
    }
}