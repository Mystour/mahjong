package org.example.mahjong.score;
import java.util.*;
import org.example.mahjong.tile.*;

public class ScoringSystem<T> {
    public List<Tile>[] hand;
    public int score;
    public ScoringSystem(List<Tile>[] hand) {
        this.hand = hand;
        this.score = 1;

    }
    public int getScore(List<Tile>[] hand) {
        if(uniformTile(hand)){
            score = score * 2;
        } else if (OneDragon(hand)) {
            score = score * 2;
        } else if (SevenPairs(hand)) {
            score = score * 2;
        } else if (AllTriple(hand)) {
            score = score * 2;
        }

        return score;
    }


    // Check QingYiSe
    public boolean uniformTile(List<Tile>[] hand){


        return false;
    }

    public boolean OneDragon(List<Tile>[] hand){

        return false;
    }

    public boolean SevenPairs(List<Tile>[] hand){

        return false;
    }

    public boolean AllTriple(List<Tile>[] hand){

        return false;
    }



















}