package org.example.mahjong.tile;

public class DotTile extends Tile{
    public DotTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        return switch (getNumber()) {
            case 0 -> "一筒";
            case 1 -> "二筒";
            case 2 -> "三筒";
            case 3 -> "四筒";
            case 4 -> "五筒";
            case 5 -> "六筒";
            case 6 -> "七筒";
            case 7 -> "八筒";
            case 8 -> "九筒";
            default -> "?";
        };

    }
}
