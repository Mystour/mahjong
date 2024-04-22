package org.example.mahjong.player;
import java.util.*;
import org.example.mahjong.tile.*;

public class Hand {
    public List<Tile>[] handcard;
    public int count; // 明牌区的计数器，负责计杠，碰，吃的次数

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
        for(int i = 0; i < handcard.length; i++){ //遍历五种类型的list
            int index = 0;
            while (index < handcard[i].size()) { //遍历每个list里的tile
                Tile currentTile = handcard[i].get(index);

                if (index + 1 < handcard[i].size()) {
                    Tile nextTile = handcard[i].get(index + 1);

                    if (index + 2 < handcard[i].size()) {
                        Tile nextNextTile = handcard[i].get(index + 2);
                        if (isPair(currentTile, nextTile)) { //看是不是对子
                            pair++;
                            index += 2; // 跳过对子
                        } else if (isTriplet(currentTile, nextTile, nextNextTile)) { //看是不是刻子
                            triple++;
                            index += 3; // 跳过刻子
                        } else if (isSequence(currentTile, nextTile, nextNextTile)) { // 看是不是顺子
                            triple++;
                            index += 3; // 跳过顺子
                        } else {
                            others++;
                            index++;
                        }
                    } else {
                        //末尾剩两张
                        if (isPair(currentTile, nextTile)) {
                            pair++;
                        } else {
                            others += 2;
                        }
                        index += 2;
                    }
                } else {
                    //末尾剩一张
                    others++;
                    index++;
                }
            }
        }
        //判断有没有胡牌
        if(pair == 7){ //七小对
            return true;
        } else if (pair == 1 && triple + count == 4) { //4个三个的，一个两个的，正常胡牌格式
            return true;
        }else{
            return false;
        }

    }
    //判断是否为对子
    public static boolean isPair(Tile tile1, Tile tile2) {
        return tile1.compareTo(tile2) == 0;
    }
    //判断是否为刻子
    public static boolean isTriplet(Tile tile1, Tile tile2, Tile tile3) {
        return tile1.compareTo(tile2) == 0 && tile2.compareTo(tile3) == 0;
    }
    //判断是否为顺子
    public static boolean isSequence(Tile tile1, Tile tile2, Tile tile3) {
        return tile2.getNumber() - tile1.getNumber() == 1 && tile3.getNumber() - tile2.getNumber() == 1;

    }
}