package org.example.mahjong.player;

import org.example.mahjong.tile.Tile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Hand {
    public List<Tile>[] handcard;

    public Hand() {
        handcard = new List[5];
        for (int i = 0; i < 5; i++) {
            handcard[i] = new LinkedList<>();
        }
    }
    //for test
    public void printCard(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < handcard[i].size(); j++) {
                System.out.print(handcard[i].get(j).toString()+" ");
            }
        }
    }
    public int addCard(Tile tile){
        switch (tile.getType()) {
            case BAMBOO:
                handcard[0].add(tile);
                return 0;
            case CHARACTER:
                handcard[1].add(tile);
                return 1;
            case DOT:
                handcard[2].add(tile);
                return 2;
            case DRAGON:
                handcard[3].add(tile);
                return 3;
            case WIND:
                handcard[4].add(tile);
                return 4;
            default:
                System.out.println("Non-existent tile, why did it happen?");
                return -1;
        }
    }
    public void sortCard(int i){
        Collections.sort(handcard[i]);
    }
    public void sortAllCard(){
        for (int i = 0; i < 5; i++) {
            sortCard(i);
        }
    }
    public boolean isValidMahjong() {
        return false;
    }
}