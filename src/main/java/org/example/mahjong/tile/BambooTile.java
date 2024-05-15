package org.example.mahjong.tile;

public class BambooTile extends Tile {
    public BambooTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        return switch (getNumber()) {
            case 0 -> "一条";
            case 1 -> "二条";
            case 2 -> "三条";
            case 3 -> "四条";
            case 4 -> "五条";
            case 5 -> "六条";
            case 6 -> "七条";
            case 7 -> "八条";
            case 8 -> "九条";
            default -> "?";
        };

    }
}