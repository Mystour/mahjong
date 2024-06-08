package org.example.mahjong.player;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.score.Scorable;
import org.example.mahjong.score.ScoringSystem;
import org.example.mahjong.tile.CharacterTile;
import org.example.mahjong.tile.Tile;
import org.example.mahjong.tile.TileType;

import java.util.ArrayList;
import java.util.List;

/**
 * The Player class represents a player in a Mahjong game.
 * It implements the Playable and Scorable interfaces, providing methods for gameplay actions and scoring.
 */
public class Player implements Playable, Scorable {

    //The following four boolean values keep track of whether the player is allowed to
    //take an action and are recalculated when a change is sent
    private boolean canChow;
    private boolean canPung;
    private boolean canKong;
    private boolean canMahjong;
    private boolean hasMahjong;

    //Other classes that the player owns and belongs to
    private Hand hand;
    private final MahjongGame game;
    private ScoringSystem scoringSystem;

    //The number of scores the player has earned
    private int score;
    //Whether the player is a banker or not in current board of game
    private boolean isbanker;


    /**
     * Gets the current score of the player.
     * @return the current score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets whether the player is the banker.
     * @param isbanker true if the player is the banker, false otherwise
     */
    public void setIsbanker(boolean isbanker) {
        this.isbanker = isbanker;
    }

    /**
     * Checks if the player is the banker.
     * @return true if the player is the banker, false otherwise
     */
    public boolean isBanker() {
        return isbanker;
    }

    /**
     * Checks if the player can perform a chow.
     * @return true if the player can chow, false otherwise
     */
    public boolean isCanChow() {
        return canChow;
    }

    /**
     * Checks if the player can perform a pung.
     * @return true if the player can pung, false otherwise
     */
    public boolean isCanPung() {
        return canPung;
    }

    /**
     * Checks if the player can perform a kong.
     * @return true if the player can kong, false otherwise
     */
    public boolean isCanKong() {
        return canKong;
    }

    /**
     * Checks if the player can declare Mahjong.
     * @return true if the player can declare Mahjong, false otherwise
     */
    public boolean isCanMahjong() {
        return canMahjong;
    }

    /**
     * Checks if the player has declared Mahjong.
     * @return true if the player has declared Mahjong, false otherwise
     */
    public boolean isHasMahjong() {
        return hasMahjong;
    }

    /**
     * Gets the player's hand.
     * @return the hand of the player
     */
    public Hand getHand() {
        return hand;
    }

    /**
     * Gets the Mahjong game instance.
     * @return the Mahjong game instance
     */
    public MahjongGame getGame() {
        return game;
    }

    /**
     * Gets a list of all condition booleans related to the player's possible performance.
     * @return a list of booleans representing the player's conditions
     */
    public List<Boolean> getAllCondition() {
        List<Boolean> allcondition = new ArrayList<>();
        allcondition.add(canChow);
        allcondition.add(canPung);
        allcondition.add(canKong);
        allcondition.add(canMahjong);
        allcondition.add(hasMahjong);
        return allcondition;
    }

    /**
     * Constructs a Player with the specified Mahjong game instance.
     * Initializes the player's hand and scoring system.
     * @param game the Mahjong game instance
     */
    public Player(MahjongGame game) {
        hand = new Hand();
        this.game = game;
        this.scoringSystem = new ScoringSystem(hand);
    }

    /**
     * Resets the player's state for a new game.
     */
    public void playAgain(){
        hand = new Hand();
        scoringSystem = new ScoringSystem(hand);
        canChow = false;
        canPung = false;
        canKong = false;
        canMahjong = false;
        hasMahjong = false;
        isbanker = false;
    }

    /**
     * Draws a tile from the game and updates the player's hand and condition.
     * @return the drawn tile
     */
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

    /**
     * Discards a tile from the player's hand.
     * @param tile the tile to be discarded
     * @return the discarded tile
     */
    @Override
    public Tile discardTile(Tile tile) {
        return hand.discard(tile);
    }

    /**
     * Discards a tile of the specified type and number.
     * @param tileType the type of the tile
     * @param number the number of the tile
     * @return the discarded tile
     */
    public Tile discardTile(TileType tileType, int number) {
        return hand.discard(tileType, number);
    }

    /**
     * Puts a discarded tile into the discard pile.
     * @param discardedTile the tile to be put in the discard pile
     */
    public void putInDiscardPile(Tile discardedTile) {
        hand.excuteDiscard(discardedTile);
    }

    /**
     * Declares a chow with the specified tile.
     * @param tile the tile to declare chow with
     */
    @Override
    public void declareChow(Tile tile) {
        if (hand.canChow(tile)) {
            hand.executeChows(tile);
        }
    }

    /**
     * Declares a pung with the specified tile.
     * @param tile the tile to declare pung with
     */
    @Override
    public void declarePung(Tile tile) {
        if (hand.canPung(tile)) {
            hand.executePung(tile);
        }
    }

    /**
     * Declares a kong with the specified tile and draws a new tile.
     * @param tile the tile to declare kong with
     */
    @Override
    public void declareKong(Tile tile) {
        if (hand.canKong(tile)) {
            hand.executeKong(tile);
            drawTile();
        }
    }

    /**
     * Declares Mahjong with the specified tile.
     * @param tile the tile to declare Mahjong with
     */
    @Override
    public void declareMahjong(Tile tile) {
        if (canMahjong) {
            hand.sortCard(hand.addCard(tile));
            hasMahjong = true;
            calculateScore();
        }
    }

    /**
     * Calculates the player's score based on the current hand.
     * @return the score for the current hand
     */
    @Override
    public int calculateScore() {
        int nowscore = scoringSystem.calculateScore();
        score += nowscore;
        return nowscore;
    }

    /**
     * Checks and updates the player's decision conditions based on the drawn tile.
     * @param drawnTile the tile that was drawn
     */
    public void checkDecisionCondition(Tile drawnTile) {
        canChow = hand.canChow(drawnTile);
        canPung = hand.canPung(drawnTile);
        canKong = hand.canKong(drawnTile);
        canMahjong = hand.isValidMahjong_Other(drawnTile);
    }

}