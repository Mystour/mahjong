package org.example.mahjong.game;
import org.example.mahjong.player.Player;
import org.example.mahjong.tile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class MahjongGameTest {

    private MahjongGame mahjongGame;

    @BeforeEach
    public void setUp() {
        mahjongGame = new MahjongGame();
        mahjongGame.startGame();
    }

    @Test
    public void testInitialSetup() {
        // Verify that the players are created
        assertNotNull(mahjongGame.getPlayers());
        assertEquals(4, mahjongGame.getPlayers().length);

        // Verify that the tilepile is created and shuffled
        assertNotNull(mahjongGame.getTilepile());
        assertTrue(mahjongGame.getTilepile().size() > 0);

        // Verify that initial hands are dealt
        for (Player player : mahjongGame.getPlayers()) {
            assertEquals(13, player.getHand().getAllHandcard().size());
        }
    }

    @Test
    public void testDiceRollAndFirstPlayer() {
        // Verify the dice number is within the expected range
        assertTrue(mahjongGame.getDicenum() >= 2 && mahjongGame.getDicenum() <= 11);

        // Verify the first player's index
        assertEquals(mahjongGame.getDicenum() % 4, mahjongGame.getIndexnum());
    }

    @Test
    public void testDealOneCard() {
        int initialSize = mahjongGame.getTilepile().size();
        Tile dealtTile = mahjongGame.dealOneCard();

        // Verify a tile is dealt from the tilepile
        assertNotNull(dealtTile);
        assertEquals(initialSize - 1, mahjongGame.getTilepile().size());
    }

    @Test
    public void testGetAllPlayersHands() {
        List<List<Tile>> allHands = mahjongGame.getAllPlayersHands();
        assertEquals(4, allHands.size());

        for (List<Tile> hand : allHands) {
            assertEquals(13, hand.size());
        }
    }

    @Test
    public void testGetAllPlayersDiscards() {
        List<List<Tile>> allDiscards = mahjongGame.getAllPlayersDiscards();
        assertEquals(4, allDiscards.size());

        for (List<Tile> discards : allDiscards) {
            assertTrue(discards.isEmpty());
        }
    }

    @Test
    public void testGetAllPlayersShowedCards() {
        List<List<Tile>> showedCards = mahjongGame.getAllPlayersShowedCards();
        assertEquals(4, showedCards.size());

        for (List<Tile> showed : showedCards) {
            assertTrue(showed.isEmpty());
        }
    }

    @Test
    public void testGetAllPlayersCondition() {
        List<List<Boolean>> allConditions = mahjongGame.getAllPlayersCondition();
        assertEquals(4, allConditions.size());

        for (List<Boolean> conditions : allConditions) {
            assertEquals(5, conditions.size());
        }
    }

    @Test
    public void testGetAllPlayersScores() {
        List<Integer> scores = mahjongGame.getAllPlayersScores();
        assertEquals(4, scores.size());

        for (Integer score : scores) {
            assertNotNull(score);
        }
    }

    @Test
    public void testCurrentPlayerDraw() {
        int initialTilepileSize = mahjongGame.getTilepile().size();
        mahjongGame.currentPlayerDraw();

        // Verify that a tile is drawn and the tilepile size is reduced
        assertEquals(initialTilepileSize - 1, mahjongGame.getTilepile().size());
    }


    @Test
    public void testIsBoardOver() {
        // Verify the initial state of the board
        assertFalse(mahjongGame.isBoardOver());

        // Empty the tilepile
        while (mahjongGame.getTilepile().size() > 0) {
            mahjongGame.dealOneCard();
        }

        // Draw a tile to trigger the check
        mahjongGame.currentPlayerDraw();

        // Verify that the board is now over
        assertTrue(mahjongGame.isBoardOver());
    }
}
