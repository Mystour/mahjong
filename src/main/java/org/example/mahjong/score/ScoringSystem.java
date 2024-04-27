package org.example.mahjong.score;
import java.util.*;

import org.example.mahjong.player.Hand;
import org.example.mahjong.tile.*;

public class ScoringSystem<T> {
    public List<Tile>[] hand; // 需要把手牌和杠吃碰的名牌堆合在一起
    public int score;
    public ScoringSystem(List<Tile>[] hand) {
        this.hand = hand;
        this.score = 1;

    }
    public int getScore() {

        return score * SevenPairs() * uniformTile() * AllTriple() * OneDragon();
    }



    public int uniformTile(){ // 检查是不是清一色
        int count = 0; //计算有几种类型的牌在手牌里
        for(int i = 0; i < hand.length; i++){
            if(!hand[i].isEmpty()){
                count++;

            }
            i++;
        }
        if(count == 1){
            return 2;
        }
        return 1;
    }

    public int OneDragon(){ // 检查是不是一条龙
        for(int i = 0; i < 3; i++){
            int index = 1;
            int count = 0;
            while(index < hand[i].size()){
                Tile nextTile = hand[i].get(index);
                Tile currentTile = hand[i].get(count);
                if(isPairSequence(currentTile,nextTile)){
                    count = index;
                    index ++;
                }else{
                    index++;
                }
                if(count == 8){
                    return 2;
                }

            }
            i++;
        }
        return 1;
    }
    public static boolean isPairSequence(Tile tile1, Tile tile2) {

        return tile2.getNumber() - tile1.getNumber() == 1;
    }

    public int SevenPairs() { // 检查是不是七小对
        Hand a = new Hand();
        int pairs = 0;

        //用hashmap计算一下出现的数量
        Map<Tile, Integer> tileCounts = new HashMap<>();
        for (List<Tile> tileList : a.handcard) {
            for (Tile tile : tileList) {
                tileCounts.put(tile, tileCounts.getOrDefault(tile, 0) + 1);//获取这张牌当前的计数值 默认是0
            }
        }

        // 统计对子
        for (int count : tileCounts.values()) {
            if (count == 2) {
                pairs++;
            }
        }
        int kongs = a.kongs.size();

        if (pairs == 7 && kongs == 0) {
            return 2; // 普通七小对
        } else if (pairs + kongs*2 == 7) {
            return 4; // 带杠的七小对
        }
        return 0;
    }



    public int AllTriple(){ // 检查是不是都是由刻字和对子形成的胡
        Hand a = new Hand();
        if(a.pair == 1 && a.triple == 4){
            return 2;
        }
        return 1;
    }


}