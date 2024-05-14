package org.example.mahjong.score;

import org.example.mahjong.player.Hand;
import org.example.mahjong.tile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoringSystemTest {

    private Hand hand;
    private ScoringSystem scoringSystem;

    @BeforeEach
    void setUp() {
        hand = new Hand();
        scoringSystem = new ScoringSystem(hand);
    }

    @Test
    void calculateScore() {

    }

    @Test
    void uniformTile() {
        List<Tile> bamboos = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            bamboos.add(new BambooTile(TileType.BAMBOO, i));
        }
        for (Tile tile : bamboos) {
            hand.addCard(tile);
        }
        scoringSystem = new ScoringSystem(hand);
        assertEquals(2, scoringSystem.uniformTile());

        hand.clearHand();
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 2));
        scoringSystem = new ScoringSystem(hand);
        assertEquals(1, scoringSystem.uniformTile());
    }

    @Test
    void oneDragon() {
        List<Tile> bamboos = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            bamboos.add(new BambooTile(TileType.BAMBOO, i));
        }
        for (Tile tile : bamboos) {
            hand.addCard(tile);
        }
        scoringSystem = new ScoringSystem(hand);
        assertEquals(2, scoringSystem.OneDragon());

        hand.clearHand();
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 3));
        scoringSystem = new ScoringSystem(hand);
        assertEquals(1, scoringSystem.OneDragon());
    }

    @Test
    void isPairSequence() {
        Tile tile1 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile2 = new BambooTile(TileType.BAMBOO, 2);
        Tile tile3 = new BambooTile(TileType.BAMBOO, 3);

        assertTrue(ScoringSystem.isPairSequence(tile1, tile2));
        assertFalse(ScoringSystem.isPairSequence(tile1, tile3));
    }

    @Test
    void sevenPairs() {
        for (int i = 0; i <= 6; i++) {
            hand.addCard(new BambooTile(TileType.BAMBOO, i));
            hand.addCard(new BambooTile(TileType.BAMBOO, i));
        }
        scoringSystem = new ScoringSystem(hand);
        hand.isValidMahjong(hand.gethandcard());
        assertEquals(2, scoringSystem.SevenPairs());

        hand.clearHand();
        for (int i = 0; i <= 4; i++) {
            hand.addCard(new BambooTile(TileType.BAMBOO, i));
            hand.addCard(new BambooTile(TileType.BAMBOO, i));
        }
        for(int i = 0; i <= 3;i++){
            hand.addToKongs(new BambooTile(TileType.BAMBOO, 7));
        }

        scoringSystem = new ScoringSystem(hand);
        hand.isValidMahjong(hand.gethandcard());
        assertEquals(4, scoringSystem.SevenPairs());

        hand.clearHand();
        for (int i = 1; i <= 5; i++) {
            hand.addCard(new BambooTile(TileType.BAMBOO, i));
            hand.addCard(new BambooTile(TileType.BAMBOO, i));
        }
        scoringSystem = new ScoringSystem(hand);
        hand.isValidMahjong(hand.gethandcard());
        assertEquals(1, scoringSystem.SevenPairs());
    }

    @Test
    void allTriple() {
        for(int i = 0; i < 3;i++){
            hand.addCard(new BambooTile(TileType.BAMBOO, 7));
            hand.addCard(new BambooTile(TileType.BAMBOO, 2));
            hand.addToPung(new BambooTile(TileType.BAMBOO, 4));
            hand.addCard(new CharacterTile(TileType.CHARACTER, 2));
        }


        hand.addCard(new WindTile(TileType.WIND, 1));
        hand.addCard(new WindTile(TileType.WIND, 1));
        hand.sortAllCard();
        hand.isValidMahjong(hand.gethandcard());
        scoringSystem = new ScoringSystem(hand);
        assertEquals(2, scoringSystem.AllTriple());

        hand.clearHand();
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 2));
        hand.addCard(new BambooTile(TileType.BAMBOO, 3));
        hand.addCard(new BambooTile(TileType.BAMBOO, 4));
        hand.addCard(new BambooTile(TileType.BAMBOO, 5));
        hand.addCard(new BambooTile(TileType.BAMBOO, 6));
        hand.addCard(new BambooTile(TileType.BAMBOO, 7));
        hand.addCard(new BambooTile(TileType.BAMBOO, 8));
        hand.addCard(new BambooTile(TileType.BAMBOO, 9));
        hand.isValidMahjong(hand.gethandcard());

        scoringSystem = new ScoringSystem(hand);
        assertEquals(1, scoringSystem.AllTriple());
    }
}

