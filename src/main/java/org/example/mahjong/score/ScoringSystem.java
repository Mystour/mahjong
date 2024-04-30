package org.example.mahjong.score;
import java.util.*;

import org.example.mahjong.player.Hand;
import org.example.mahjong.tile.*;

public class ScoringSystem implements Scorable{
    public List<Tile>[] handcard; // 手牌
    public List<Tile>[] allCard; // 手牌+杠吃碰的牌
    public int score;
    public Hand hand;
    public ScoringSystem(List<Tile>[] handcard) {
        this.handcard = handcard.clone();
        this.score = 1;
        hand = new Hand();

        //将名牌区的牌和手牌区的合在一起，方便判断清一色和一条龙
    }
    public int calculateScore() {
        return score * SevenPairs() * uniformTile() * AllTriple() * OneDragon();
    }
    public int uniformTile(){ // 检查是不是清一色
        int count = 0; //计算有几种类型的牌在手牌里
        for(int i = 0; i < allCard.length; i++){
            if(!allCard[i].isEmpty()){
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
            while(index < allCard[i].size()){
                Tile nextTile = allCard[i].get(index);
                Tile currentTile = allCard[i].get(count);
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

        int kongs = (hand.kongs.size())/4;

        if (hand.pair == 7 && kongs == 0) {
            return 2; // 普通七小对
        } else if (hand.pair + kongs * 2 == 7) {
            return 4; // 带杠的七小对
        }
        return 0;
    }



    public int AllTriple(){ // 检查是不是都是由刻字和对子形成的胡
        //这个部分需要改，不好获取名牌堆里的碰的牌的数量
        if(hand.pair == 1 && hand.triple + hand.pungs.size()/3== 4){
            return 2;
        }
        return 1;
    }



}