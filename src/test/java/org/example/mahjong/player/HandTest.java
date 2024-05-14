package org.example.mahjong.player;

import org.example.mahjong.tile.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import java.util.List;

public class HandTest {
    private Hand hand;

    @BeforeEach
    public void setUp() {
        hand = new Hand();
    }

    @Test
    public void testInitialization() {
        List<Tile>[] handcard = hand.gethandcard();
        assertNotNull(handcard);
        assertEquals(5, handcard.length);

        for (List<Tile> tiles : handcard) {
            assertNotNull(tiles);
            assertTrue(tiles.isEmpty());
        }

        assertTrue(hand.getPungs().isEmpty());
        assertTrue(hand.getChows().isEmpty());
        assertTrue(hand.getKongs().isEmpty());
        assertTrue(hand.getDiscards().isEmpty());
    }

    @Test
    public void testDeepCopyHandcard() {
        BambooTile tile1 = new BambooTile(TileType.BAMBOO, 1);
        BambooTile tile2 = new BambooTile(TileType.BAMBOO, 2);
        hand.addCard(tile1);
        hand.addCard(tile2);

        List<Tile>[] originalHandcard = hand.gethandcard();
        List<Tile>[] copiedHandcard = hand.deepCopyHandcard(originalHandcard);

        assertNotSame(originalHandcard, copiedHandcard);

        for (int i = 0; i < originalHandcard.length; i++) {
            assertNotSame(originalHandcard[i], copiedHandcard[i]);
        }

        for (int i = 0; i < originalHandcard.length; i++) {
            assertEquals(originalHandcard[i], copiedHandcard[i]);
        }
    }

    @Test
    public void testAllCard() {
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 2));
        hand.addCard(new BambooTile(TileType.BAMBOO, 3));

        hand.addToChow(new DotTile(TileType.DOT, 4));
        hand.addToPung(new CharacterTile(TileType.CHARACTER, 5));
        hand.addToKongs(new WindTile(TileType.WIND, 6));

        List<Tile>[] allCards = hand.allCard();

        // Ensure that all cards are included in the combined list
        assertEquals(3, allCards[0].size());
        assertEquals(1, allCards[1].size());
        assertEquals(1, allCards[2].size());
        assertEquals(1, allCards[4].size());


        assertTrue(allCards[0].contains(new BambooTile(TileType.BAMBOO, 1)));
        assertTrue(allCards[0].contains(new BambooTile(TileType.BAMBOO, 2)));
        assertTrue(allCards[0].contains(new BambooTile(TileType.BAMBOO, 3)));
        assertTrue(allCards[1].contains(new CharacterTile(TileType.CHARACTER, 5)));
        assertTrue(allCards[2].contains(new DotTile(TileType.DOT, 4)));
        assertTrue(allCards[4].contains(new WindTile(TileType.WIND, 6)));

    }


    @Test
    public void testAddCard() {
        BambooTile tile = new BambooTile(TileType.BAMBOO, 1);
        int index = hand.addCard(tile);
        assertEquals(0, index);
        assertEquals(1, hand.gethandcard()[index].size());
        assertEquals(tile, hand.gethandcard()[index].get(0));
    }

    @Test
    public void testDiscard() {
        BambooTile tile = new BambooTile(TileType.BAMBOO, 1);
        hand.addCard(tile);
        BambooTile discardedTile = (BambooTile) hand.discard(TileType.BAMBOO, 1);
        assertEquals(tile, discardedTile);
        assertTrue(hand.gethandcard()[0].isEmpty());
    }

    @Test
    public void testCanChow() {
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 3));
        assertTrue(hand.canChow(new BambooTile(TileType.BAMBOO, 2)));
    }

    @Test
    public void testCanPung() {
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        assertTrue(hand.canPung(new BambooTile(TileType.BAMBOO, 1)));
    }

    @Test
    public void testCanKong() {
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        assertTrue(hand.canKong(new BambooTile(TileType.BAMBOO, 1)));
    }

    @Test
    public void testExecuteChows() {
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 3));
        BambooTile tile = new BambooTile(TileType.BAMBOO, 2);
        hand.executeChows(tile);
        assertTrue(hand.getChows().contains(tile));
    }

    @Test
    public void testExecutePung() {
        BambooTile tile1 = new BambooTile(TileType.BAMBOO, 1);
        BambooTile tile2 = new BambooTile(TileType.BAMBOO, 1);
        BambooTile tile3 = new BambooTile(TileType.BAMBOO, 1);
        hand.addCard(tile1);
        hand.addCard(tile2);
        hand.executePung(tile3);
        assertTrue(hand.getPungs().contains(tile3));
        assertEquals(3, hand.getPungs().size());
    }

    @Test
    public void testExecuteKong() {
        BambooTile tile1 = new BambooTile(TileType.BAMBOO, 1);
        BambooTile tile2 = new BambooTile(TileType.BAMBOO, 1);
        BambooTile tile3 = new BambooTile(TileType.BAMBOO, 1);
        BambooTile tile4 = new BambooTile(TileType.BAMBOO, 1);
        hand.addCard(tile1);
        hand.addCard(tile2);
        hand.addCard(tile3);
        hand.executeKong(tile4);
        assertTrue(hand.getKongs().contains(tile4));
        assertEquals(4, hand.getKongs().size());
    }

    @Test
    public void testIsValidMahjong() {
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 1));
        hand.addCard(new BambooTile(TileType.BAMBOO, 2));
        hand.addCard(new BambooTile(TileType.BAMBOO, 3));
        hand.addCard(new BambooTile(TileType.BAMBOO, 4));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 5));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 6));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 7));
        hand.addCard(new DotTile(TileType.DOT, 7));
        hand.addCard(new DotTile(TileType.DOT, 8));
        hand.addCard(new DotTile(TileType.DOT, 9));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 1));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 1));
        hand.addCard(new CharacterTile(TileType.CHARACTER, 1));
        hand.sortAllCard();
        assertTrue(hand.isValidMahjong(hand.gethandcard()));
    }

    @Test
    public void testIsPair() {
        Tile tile1 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile2 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile3 = new BambooTile(TileType.BAMBOO, 2);

        assertTrue(Hand.isPair(tile1, tile2));
        assertFalse(Hand.isPair(tile1, tile3));
    }

    @Test
    public void testIsTriplet() {
        Tile tile1 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile2 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile3 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile4 = new BambooTile(TileType.BAMBOO, 2);

        assertTrue(Hand.isTriplet(tile1, tile2, tile3));
        assertFalse(Hand.isTriplet(tile1, tile2, tile4));
    }

    @Test
    public void testIsSequence() {
        Tile tile1 = new BambooTile(TileType.BAMBOO, 1);
        Tile tile2 = new BambooTile(TileType.BAMBOO, 2);
        Tile tile3 = new BambooTile(TileType.BAMBOO, 3);
        Tile tile4 = new BambooTile(TileType.BAMBOO, 4);

        assertTrue(Hand.isSequence(tile1, tile2, tile3));
        assertFalse(Hand.isSequence(tile1, tile3, tile4));
        assertFalse(Hand.isSequence(tile1, tile2, tile4));
    }

    @Test
    public void testClearHand() {
        BambooTile tile = new BambooTile(TileType.BAMBOO, 1);
        hand.addCard(tile);
        hand.clearHand();
        for (List<Tile> tiles : hand.gethandcard()) {
            assertTrue(tiles.isEmpty());
        }
        assertTrue(hand.getChows().isEmpty());
        assertTrue(hand.getPungs().isEmpty());
        assertTrue(hand.getKongs().isEmpty());
        assertTrue(hand.getDiscards().isEmpty());
    }
}
