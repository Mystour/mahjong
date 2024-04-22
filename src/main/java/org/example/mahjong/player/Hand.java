package org.example.mahjong.player;
import java.util.*;
import org.example.mahjong.tile.*;

public class Hand {
    public List<Tile>[] handcard;
    public int count;

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
        int pair = 0;
        int triple = 0;
        int others = 0;
        for(int i = 0; i < handcard.length; i++){
            List<Tile> TileType = handcard[i];
            int index = 0;
            while (index < handcard[i].size()) {
                Tile currentTile = handcard[i].get(index);

                if (index + 1 < handcard[i].size()) {
                    Tile nextTile = handcard[i].get(index + 1);

                    if (index + 2 < handcard[i].size()) {
                        Tile nextNextTile = handcard[i].get(index + 2);
                        if (isPair(currentTile, nextTile)) {
                            pair++;
                            index += 2; // 跳过对子
                        } else if (isTriplet(currentTile, nextTile, nextNextTile)) {
                            triple++;
                            index += 3; // 跳过刻子
                        } else if (isSequence(currentTile, nextTile, nextNextTile)) {
                            triple++;
                            index += 3; // 跳过顺子
                        } else {
                            others++;
                            index++;
                        }
                    } else {
                        if (isPair(currentTile, nextTile)) {
                            pair++;
                            System.out.println("对子：" + currentTile + ", " + nextTile);
                        } else {
                            others += 2;
                        }
                        index += 2;
                    }
                } else {
                    others++;
                    index++;
                }
            }
        }
        if(pair == 7){
            return true;
        } else if (pair == 1 && triple == 4) {
            return true;
        }else{
            return false;
        }

    }
    public static boolean isPair(Tile tile1, Tile tile2) {
        return tile1.compareTo(tile2) == 0;
    }

    public static boolean isTriplet(Tile tile1, Tile tile2, Tile tile3) {
        return tile1.compareTo(tile2) == 0 && tile2.compareTo(tile3) == 0;
    }
    // this part a bit uncertain
    public static boolean isSequence(Tile tile1, Tile tile2, Tile tile3) {
        return tile1.compareTo(tile2) == -1 && tile2.compareTo(tile3) == -1;

    }

    //print all the cards in the player's hand
    public void printCards() {
        for (List<Tile> cardList : handcard) {
            for (Tile card : cardList) {
                System.out.print(card + " ");
            }
            System.out.println();
        }
    }
}