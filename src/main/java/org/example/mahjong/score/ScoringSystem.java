package org.example.mahjong.score;
import java.util.*;

import org.example.mahjong.player.Hand;
import org.example.mahjong.tile.*;

public class ScoringSystem implements Scorable{

    public List<Tile>[] allCard; // handcard + the chow pung kong card
    public int score;
    public Hand hand;
    public ScoringSystem(Hand hand) {
        this.score = 1;
        this.hand = hand;
        allCard = hand.allCard();

        //put all the card together can easily to calculate the score
    }


    public int calculateScore() {
        return score * SevenPairs() * uniformTile() * AllTriple() * OneDragon();
    }
    public int uniformTile(){ // check whether the tile is uniform
        int count = 0; 
        for(int i = 0; i < allCard.length; i++){
            if(!allCard[i].isEmpty()){
                count++;

            }
        }
        if(count == 1){
            return 2;
        }
        return 1;
    }

    public int OneDragon(){ 
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

    public int SevenPairs() {

        int kongs = (hand.getKongs().size())/4;

        if (hand.getPair() == 7) {
            return 2; // normal seven pair
        } else if (hand.getPair() + kongs * 2 == 7) {
            return 4; // seven pair contains kong
        }
        return 1;
    }



    public int AllTriple(){ // 

        if(hand.getPair() == 1 && hand.getTriple() + hand.getPungs().size()/3== 4){
            return 2;
        }
        return 1;
    }

}
