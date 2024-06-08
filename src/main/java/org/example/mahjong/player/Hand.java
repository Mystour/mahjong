package org.example.mahjong.player;
import org.example.mahjong.tile.*;

import org.example.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Hand class represents a player's hand in a tile-based game.
 * It manages the tiles in the player's hand, as well as the tiles that have been discarded or involved in special operations.
 * The class supports operations like adding and discarding tiles, checking for possible moves like chow, pung, and kong,
 * and verifying if the hand is in a winning state.
 */

public class Hand {

    //Tiles in the player's hand are placed in a List with a specific index,
    //and tiles of each type are stored in an array
    private List<Tile>[] handcard;
    //Tiles that the player has made special operations on are stored
    //in the specified list, including discards, chow, pung, and kong piles
    private final List<Tile> discards;
    private final List<Tile> pungs;
    private final List<Tile> chows;
    private final List<Tile> kongs;

    //Pair refers to two tiles that have the same class and the same value
    private int pair = 0;
    //Triple refers to three tiles that have the same class and the same value
    private int triple = 0;
    //Sequence refers to three tiles that have the same class and the adjacent value
    private int sequence = 0;
    //Others refers to tile not belong to pair, triple and sequence
    private int others = 0;


    /**
     * Constructor for the Hand class.
     * Initializes the lists for storing tiles and special operation piles.
     *
     */
    public Hand() {
        pungs = new ArrayList<>();
        chows = new ArrayList<>();
        kongs = new ArrayList<>();
        handcard = new List[5];
        for (int i = 0; i < 5; i++) {
            handcard[i] = new LinkedList<>();
        }
        discards = new ArrayList<>();
    }


    /**
     * Clears the hand, resetting all tiles and special operation piles.
     */
    public void clearHand() {
        handcard = new List[5];
        for (int i = 0; i < handcard.length; i++) {
            handcard[i] = new LinkedList<>();
        }
        chows.clear();
        pungs.clear();
        kongs.clear();
    }


    /**
     * Returns the list of tiles in the player's hand.
     * @return an array of lists containing tiles of each type
     */
    public List<Tile>[] gethandcard() {
        return handcard;
    }

    /**
     * Returns the list of kongs.
     * @return the list of kongs
     */
    public List<Tile> getKongs() {
        return kongs;
    }

    /**
     * Returns the list of pungs.
     * @return the list of pungs
     */
    public List<Tile> getPungs() {
        return pungs;
    }

    /**
     * Returns the list of chows.
     * @return the list of chows
     */
    public List<Tile> getChows() {
        return chows;
    }

    /**
     * Returns the list of discarded tiles.
     * @return the list of discarded tiles
     */
    public List<Tile> getDiscards() {
        return discards;
    }

    /**
     * Returns the count of pairs in the hand.
     * @return the count of pairs
     */
    public int getPair() {
        return pair;
    }

    /**
     * Returns the count of triples in the hand.
     * @return the count of triples
     */
    public int getTriple() {
        return triple;
    }

    /**
     * Returns a list of all tiles in the player's hand.
     * @return a combined list of all tiles in the hand
     */
    public List<Tile> getAllHandcard() {
        List<Tile> allHandCards = new ArrayList<>();
        allHandCards.addAll(handcard[0]);
        allHandCards.addAll(handcard[1]);
        allHandCards.addAll(handcard[2]);
        allHandCards.addAll(handcard[3]);
        allHandCards.addAll(handcard[4]);
        return allHandCards;
    }

    /**
     * Translates a tile type to an index for storing in the hand.
     * @param type the tile type
     * @return the corresponding index
     */
    public static int translateType(TileType type){
        return switch (type) {
            case BAMBOO -> 0;
            case CHARACTER -> 1;
            case DOT -> 2;
            case DRAGON -> 3;
            case WIND -> 4;
        };
    }

    /**
     * Creates a deep copy of the handcard array.
     * @param handcard the original handcard array
     * @return a deep copy of the handcard array
     */
    public List<Tile>[] deepCopyHandcard(List<Tile>[] handcard) {
        List<Tile>[] copy = new List[handcard.length];
        for (int i = 0; i < handcard.length; i++) {
            copy[i] = new ArrayList<>(handcard[i]); // 浅拷贝每个列表
        }

        return copy;
    }
    /**
     * Returns a combined list of all tiles including those in handcard, chows, pungs, and kongs.
     * @return a combined list of all tiles
     */
    public List<Tile>[] allCard(){
        List<Tile>[] allcard = deepCopyHandcard(handcard);
        for(Tile tile : chows){
            allcard[translateType(tile.getType())].add(tile);
        }
        for(Tile tile : pungs){
            allcard[translateType(tile.getType())].add(tile);
        }
        for(Tile tile : kongs){
            allcard[translateType(tile.getType())].add(tile);
        }
        for (List<Tile> tiles : allcard) {
            Collections.sort(tiles);
        }
        return allcard;
    }

    /**
     * Adds a tile to the hand.
     * @param tile the tile to be added
     * @return the index of the tile type
     */
    public int addCard(Tile tile){
        int index = translateType(tile.getType());
        if(index != -1){
            handcard[translateType(tile.getType())].add(tile);
        }
        return index;
    }

    /**
     * Finds a tile of a specific type and number in the hand.
     * @param type the tile type
     * @param number the tile number
     * @return the found tile, or null if not found
     */
    public Tile findTile(TileType type, int number){
        int index = translateType(type);
        if(index != -1){
            for (int i = 0; i < handcard[index].size(); i++) {
                if(handcard[index].get(i).getNumber() == number){
                    return handcard[index].get(i);
                }
            }
        }
        return null;
    }

    /**
     * Sorts the tiles of a specific type in the hand.
     * @param i the index of the tile type to be sorted
     */
    public void sortCard(int i){
        Collections.sort(handcard[i]);
    }

    /**
     * Sorts all tiles in the hand.
     */
    public void sortAllCard(){
        for (int i = 0; i < 5; i++) {
            sortCard(i);
        }
    }
    /**
     * Discards a tile of a specific type and number.
     * @param type the tile type
     * @param number the tile number
     * @return the discarded tile, or null if not found
     */
    public Tile discard(TileType type, int number){
        Tile tile = findTile(type, number);
        if(tile != null){
            handcard[translateType(type)].remove(tile);
        }else{
            System.out.println("Non-existent tile, what happen?");
        }
        return tile;
    }

    /**
     * Discards a specific tile.
     * @param tile the tile to be discarded
     * @return the discarded tile
     */
    protected Tile discard(Tile tile){
        handcard[translateType(tile.getType())].remove(tile);
        return tile;
    }

    /**
     * Adds a tile to the list of discards.
     * @param tile the tile to be added
     */
    public void excuteDiscard(Tile tile){
        discards.add(tile);
    }


    /**
     * Checks if a specific tile can form a chow with the tiles in the hand.
     * @param tile the tile to be checked
     * @return true if a chow can be formed, false otherwise
     */
    public boolean canChow(Tile tile){
        TileType type = tile.getType();
        if(type == TileType.WIND || type == TileType.DRAGON){
            return false;
        }
        int typeindex = translateType(type);
        int number = tile.getNumber();
        int left = 0;
        int right = 0;
        for (Tile tile1 : handcard[typeindex]) {
            if(tile1.getNumber() == number - 1){
                left++;
            }
            if (tile1.getNumber() == number + 1){
                right++;
            }
        }
        return left >= 1 && right >= 1;
    }

    /**
     * Checks if a specific tile can form a pung with the tiles in the hand.
     * @param tile the tile to be checked
     * @return true if a pung can be formed, false otherwise
     */
    public boolean canPung(Tile tile){
        int count = 0;
        for (Tile t : handcard[translateType(tile.getType())]) {
            if (t.getNumber() == tile.getNumber()) {
                count++;
            }
        }
        return count == 2;
    }
    /**
     * Checks if a specific tile can form a kong with the tiles in the hand.
     * @param tile the tile to be checked
     * @return true if a kong can be formed, false otherwise
     */
    public boolean canKong(Tile tile) {
        int count = 0;
        for (Tile t : handcard[translateType(tile.getType())]) {
            if (t.getNumber() == tile.getNumber()) {
                count++;
            }
        }
        return count == 3;
    }

    /**
     * Adds a tile to the list of chows.
     * @param tile the tile to be added
     */
    public void addToChow(Tile tile){
        chows.add(tile);
    }

    /**
     * Adds a tile to the list of pungs.
     * @param tile the tile to be added
     */
    public void addToPung(Tile tile) {
        pungs.add(tile);
    }
    /**
     * Adds a tile to the list of kongs.
     * @param tile the tile to be added
     */
    public void addToKongs(Tile tile) {
        kongs.add(tile);
    }
    /**
     * Executes the action of forming a pung with a specific tile.
     * @param tile the tile to form a pung with
     */
    public void executePung(Tile tile){
        if (canPung(tile)) {
            addToPung(discard(tile.getType(), tile.getNumber()));
            addToPung(discard(tile.getType(), tile.getNumber()));
            addToPung(tile);
        }
    }
    /**
     * Executes the action of forming a chow with a specific tile.
     * @param tile the tile to form a chow with
     */
    public void executeChows(Tile tile){
        if (canChow(tile)) {
            addToChow(discard(tile.getType(), tile.getNumber() - 1));
            addToChow(tile);
            addToChow(discard(tile.getType(), tile.getNumber() + 1));
        }

    }
    /**
     * Executes the action of forming a kong with a specific tile.
     * @param tile the tile to form a kong with
     */
    public void executeKong(Tile tile) {
        if (canKong(tile)) {
            for (int i = 0; i < 3; i++) { // 移除三张
                addToKongs(discard(tile.getType(), tile.getNumber()));
            }
            addToKongs(tile);
        }
    }

    /**
     * Checks if the hand is in a valid Mahjong state when a tile is drawn.
     * @param drawnTile the tile that was drawn
     * @return true if the hand is valid, false otherwise
     */
    public boolean isValidMahjong_Other(Tile drawnTile){
        List<Tile>[] copyofhandcard = deepCopyHandcard(handcard);
        copyofhandcard[translateType(drawnTile.getType())].add(drawnTile);
        Collections.sort(copyofhandcard[translateType(drawnTile.getType())]);
        return isValidMahjong(copyofhandcard);
    }
    /**
     * Checks if the hand is in a valid Mahjong state for the player.
     * @return true if the hand is valid, false otherwise
     */
    public boolean isValidMahjong_Myself(){
        return isValidMahjong(handcard);
    }
    /**
     * Checks if the hand is in a valid Mahjong state.
     * @param handcard the hand to be checked
     * @return true if the hand is valid, false otherwise
     */
    public boolean isValidMahjong(List<Tile>[] handcard) {
        pair = 0;
        triple = 0;
        sequence = 0;
        others = 0;
        for (List<Tile> tiles : handcard) { // Iterate through the five types of lists
            int index = 0;
            while (index < tiles.size()) { // Iterate through each tile in the list
                Tile currentTile = tiles.get(index);

                if (index + 1 < tiles.size()) {
                    Tile nextTile = tiles.get(index + 1);

                    if (index + 2 < tiles.size()) {
                        Tile nextNextTile = tiles.get(index + 2);
                        if (isTriplet(currentTile, nextTile, nextNextTile)) { // Check if it's a triplet
                            triple++;
                            index += 3; // Skip the triplet
                        } else if (isPair(currentTile, nextTile)) { // Check if it's a pair
                            pair++;
                            index += 2; // Skip the pair
                        } else if (isSequence(currentTile, nextTile, nextNextTile)) { // Check if it's a sequence
                            sequence++;
                            index += 3; // Skip the sequence
                        } else {
                            others++;
                            index++;
                        }
                    } else {
                        // Two tiles left at the end
                        if (isPair(currentTile, nextTile)) {
                            pair++;
                        } else {
                            others += 2;
                        }
                        index += 2;
                    }
                } else {
                    // One tile left at the end
                    others++;
                    index++;
                }
            }
        }
        // Check for valid winning hand
        // Normal winning hand format
        if (pair + (kongs.size() / 2) == 7) { // Seven pairs
            return true;
        } else return pair == 1 && triple + sequence + ((chows.size() + pungs.size()) / 3) == 4;

    }
    /**
     * Checks if two tiles form a pair.
     * @param tile1 the first tile
     * @param tile2 the second tile
     * @return true if the tiles form a pair, false otherwise
     */
    public static boolean isPair(Tile tile1, Tile tile2) {
        return tile1.compareTo(tile2) == 0;
    }
    /**
     * Checks if three tiles form a triplet.
     * @param tile1 the first tile
     * @param tile2 the second tile
     * @param tile3 the third tile
     * @return true if the tiles form a triplet, false otherwise
     */
    public static boolean isTriplet(Tile tile1, Tile tile2, Tile tile3) {
        return tile1.compareTo(tile2) == 0 && tile2.compareTo(tile3) == 0;
    }
    /**
     * Checks if three tiles form a sequence.
     * @param tile1 the first tile
     * @param tile2 the second tile
     * @param tile3 the third tile
     * @return true if the tiles form a sequence, false otherwise
     */
    public static boolean isSequence(Tile tile1, Tile tile2, Tile tile3) {
        return tile2.getNumber() - tile1.getNumber() == 1 && tile3.getNumber() - tile2.getNumber() == 1;

    }

}