package org.example.mahjong.player;
import org.example.mahjong.tile.*;

import org.example.mahjong.tile.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class Hand {
    public List<Tile>[] gethandcard() {
        return handcard;
    }

    public List<Tile> getPungs() {
        return pungs;
    }

    public List<Tile> getChows() {
        return chows;
    }

    public List<Tile> getKongs() {
        return kongs;
    }

    public List<Tile> getDiscards() {
        return discards;
    }

    public int getPair() {
        return pair;
    }

    public int getTriple() {
        return triple;
    }

    public int getOthers() {
        return others;
    }

    public int getSequence() {
        return sequence;
    }

    public void clearHand() {
        handcard = new List[5];
        for (int i = 0; i < handcard.length; i++) {
            handcard[i] = new LinkedList<>();
        }
        chows.clear();
        pungs.clear();
        kongs.clear();
    }
    private List<Tile>[] handcard;
    private List<Tile> pungs; //碰的牌
    private List<Tile> chows; //吃的牌
    private List<Tile> kongs;// 杠的牌
    private List<Tile> discards; // 弃掉的牌
    private int pair = 0;
    private int triple = 0;
    private int others = 0; // 无关单张牌
    private int sequence = 0; // 顺子



    public Hand() {
        pungs = new ArrayList<>();
        chows = new ArrayList<>();
        kongs = new ArrayList<>();
        handcard = new List[5];
        for (int i = 0; i < 5; i++) {
            handcard[i] = new LinkedList<>();
        }
        discards = new ArrayList<>();
    }
    //for test
    public void printCard(){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < handcard[i].size(); j++) {
                System.out.print(handcard[i].get(j).toString()+" ");
            }
        }
    }
    //将牌的类型转换成索引
    public static int translateType(TileType type){
        switch (type) {
            case BAMBOO:
                return 0;
            case CHARACTER:
                return 1;
            case DOT:
                return 2;
            case DRAGON:
                return 3;
            case WIND:
                return 4;
            default:
                System.out.println("Non-existent tile, why did it happen?");
                return -1;
        }
    }

    private List<Tile>[] deepCopyHandcard(List<Tile>[] handcard) {
        List<Tile>[] copy = new List[handcard.length];
        for (int i = 0; i < handcard.length; i++) {
            copy[i] = new ArrayList<>(handcard[i]); // 浅拷贝每个列表
        }
        return copy;
    }
    public List<Tile>[] allCard(){
        List<Tile>[] allcard = deepCopyHandcard(handcard);
        for(Tile tile : chows){
            allcard[translateType(tile.getType())].add(tile);
        }
        for(Tile tile : pungs){
            allcard[translateType(tile.getType())].add(tile);
        }
        for(Tile tile : kongs){
            allcard[translateType(tile.getType())].add(tile);
        }
        for(int i = 0; i < allcard.length; i++){
            Collections.sort(allcard[i]);
        }
        return allcard;
    }

    //添加牌并返回它的索引
    public int addCard(Tile tile){
        int index = translateType(tile.getType());
        if(index != -1){
            handcard[translateType(tile.getType())].add(tile);
        }
        return index;
    }
    //弃牌阶段我觉得可以分为，当前玩家弃掉指定牌，然后系统先保留这张牌
    //如果有其他玩家需要操作，再将这张牌移动到别人的手牌中
    //如果没有玩家需要操作，然后再把这张牌放入弃牌堆
    public Tile discard(TileType type, int number){
        Tile tile = findTile(type, number);
        if(tile != null){
            handcard[translateType(type)].remove(tile);
        }else{
            System.out.println("错误输入，没有这张牌。");
        }
        return tile;
    }
    //这个方法需要牌的对象
    public Tile discard(Tile tile){
        handcard[translateType(tile.getType())].remove(tile);
        return tile;
    }
    public void addDiscards(Tile tile){
        discards.add(tile);
    }
    //根据牌型和值找到牌，如果没找到返回null, 因为可能在吃碰杠上也用上所以写出来
    public Tile findTile(TileType type, int number){
        int index = translateType(type);
        if(index != -1){
            for (int i = 0; i < handcard[index].size(); i++) {
                if(handcard[index].get(i).getNumber() == number){
                    return handcard[index].get(i);
                }
            }
        }
        return null;
    }
    //只排序指定索引的牌，可以在每次抽牌时调用，减少计算
    public void sortCard(int i){
        Collections.sort(handcard[i]);
    }
    public void sortAllCard(){
        for (int i = 0; i < 5; i++) {
            sortCard(i);
        }
    }

    public boolean canChow(Tile tile){
        TileType type = tile.getType();
        int typeindex = translateType(type);
        int number = tile.getNumber();
        int left = 0;
        int right = 0;
        for (Tile tile1 : handcard[typeindex]) {
            if(tile1.getNumber() == number - 1){
                left++;
            }
            if (tile1.getNumber() == number + 1){
                right++;
            }
        }
        if(left >= 1 && right >= 1){
            return true;
        }
        return false; // 这部分比较丑陋还得再想其他的好一点的办法
    }


    public boolean canPung(Tile tile){
        int count = 0;
        for (Tile t : handcard[translateType(tile.getType())]) {
            if (t.getNumber() == tile.getNumber()) {
                count++;
            }
        }
        return count == 2;
    }

    // 添加吃到明牌堆
    public void addToChow(Tile tile){
        chows.add(tile);
    }

    //添加碰到明牌堆
    public void addToPung(Tile tile) {
        pungs.add(tile);
    }
    public void executeChows(Tile tile){
        if (canChow(tile)) {
            addToChow(discard(tile.getType(), tile.getNumber() - 1));
            addToChow(tile);
            addToChow(discard(tile.getType(), tile.getNumber() + 1));
        }

    }
    public void executePung(Tile tile){
        if (canPung(tile)) {  // 移除两张相同的牌，并加入到碰的列表中
            addToPung(discard(tile.getType(), tile.getNumber()));
            addToPung(discard(tile.getType(), tile.getNumber()));
            addToPung(tile); // 添加第三张牌
        }
    }

    // 添加一个牌到明牌区的杠
    public void addToKongs(Tile tile) {
        kongs.add(tile);
    }

    // 检查是否可以杠
    public boolean canKong(Tile tile) {
        // 计算手牌中与tile相同的牌的数量
        //这里改了，不能用tile.equal, 那样比的是地址值
        int count = 0;
        for (Tile t : handcard[translateType(tile.getType())]) {
            if (t.getNumber() == tile.getNumber()) {
                count++;
            }
        }
        // 如果有三张相同的牌，则可以明杠；如果摸到第四张，则可以暗杠
        return count == 3;
    }

    // 执行杠操作
    public void executeKong(Tile tile) {
        if (canKong(tile)) {
            // 移除三张相同的牌，并加入到杠的列表中
            for (int i = 0; i < 3; i++) { // 移除三张
                addToKongs(discard(tile.getType(), tile.getNumber()));
            }
            //这里我也改了，我们不是要放入相同的牌，是把相同的值的牌放进去，所以再for循环里加吧
//            addToKongs(tile); // 加入到杠的列表中，此操作实际上要加入四张相同的牌
            addToKongs(tile); // 添加第四张牌
        }
    }


    public boolean isValidMahjong_Other(Tile drawnTile){
        List<Tile>[] copyofhandcard = deepCopyHandcard(handcard);
        copyofhandcard[translateType(drawnTile.getType())].add(drawnTile);
        return isValidMahjong(copyofhandcard);
    }

    public boolean isValidMahjong_Myself(){
        return isValidMahjong(handcard);
    }

    public boolean isValidMahjong(List<Tile>[] handcard) {
        pair = 0;
        triple = 0;
        sequence = 0;
        others = 0;
        for(int i = 0; i < handcard.length; i++){ //遍历五种类型的list
            int index = 0;
            while (index < handcard[i].size()) { //遍历每个list里的tile
                Tile currentTile = handcard[i].get(index);

                if (index + 1 < handcard[i].size()) {
                    Tile nextTile = handcard[i].get(index + 1);

                    if (index + 2 < handcard[i].size()) {
                        Tile nextNextTile = handcard[i].get(index + 2);
                        if (isTriplet(currentTile, nextTile, nextNextTile)) { //看是不是刻子
                            triple++;
                            index += 3; // 跳过刻子
                        } else if (isPair(currentTile, nextTile)) { //看是不是对子
                            pair++;
                            index += 2; // 跳过对子
                        } else if (isSequence(currentTile, nextTile, nextNextTile)) { // 看是不是顺子
                            sequence++;
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
        if(pair + (kongs.size()/2) == 7){ //七小对
            return true;
        } else if (pair == 1 && triple + sequence + ((chows.size()+pungs.size())/3) == 4) { //4个三个的，一个两个的，正常胡牌格式
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

    //print all the cards in the player's hand
    public void printCards() {
        System.out.println("玩家的手牌：");
        for (List<Tile> cardList : handcard) {
            for (Tile tile : cardList) {
                System.out.print(tile + " ");
            }
        }
        System.out.println();
        System.out.println("玩家的弃牌堆：");
        for (Tile tile : discards) {
            System.out.print(tile + " ");
        }
        System.out.println();
        System.out.println("玩家的明牌堆：");
        for (Tile tile : chows) {
            System.out.print(tile + " ");
        }
        for (Tile tile : pungs) {
            System.out.print(tile + " ");
        }
        for (Tile tile : kongs) {
            System.out.print(tile + " ");
        }
        System.out.println();
    }
    public void printCards_(){
        System.out.println("玩家的手牌：");
        for (List<Tile> cardList : handcard) {
            for (Tile tile : cardList) {
                System.out.print(tile + " ");
            }
        }
    }

    public List<Tile> getAllHandcard() {
        List<Tile> allHandCards = new ArrayList<>();
        allHandCards.addAll(handcard[0]);
        allHandCards.addAll(handcard[1]);
        allHandCards.addAll(handcard[2]);
        allHandCards.addAll(handcard[3]);
        allHandCards.addAll(handcard[4]);
        return allHandCards;
    }
}