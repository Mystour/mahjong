package org.example.mahjong.tile;

public class DotTile extends Tile{
    public DotTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        switch (getNumber()){
            case 0:
                return "一筒";
            case 1:
                return "二筒";
            case 2:
                return "三筒";
            case 3:
                return "四筒";
            case 4:
                return "五筒";
            case 5:
                return "六筒";
            case 6:
                return "七筒";
            case 7:
                return "八筒";
            case 8:
                return "九筒";
            default:
                return "?";
        }

    }
}
