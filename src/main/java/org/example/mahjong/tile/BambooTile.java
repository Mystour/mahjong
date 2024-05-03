package org.example.mahjong.tile;

public class BambooTile extends Tile {
    public BambooTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        switch (getNumber()){
            case 0:
                return "一条";
            case 1:
                return "二条";
            case 2:
                return "三条";
            case 3:
                return "四条";
            case 4:
                return "五条";
            case 5:
                return "六条";
            case 6:
                return "七条";
            case 7:
                return "八条";
            case 8:
                return "九条";
            default:
                return "?";
        }

    }
}