package org.example.mahjong.tile;

public class CharacterTile extends Tile {
    public CharacterTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        return switch (getNumber()) {
            case 0 -> "一万";
            case 1 -> "二万";
            case 2 -> "三万";
            case 3 -> "四万";
            case 4 -> "五万";
            case 5 -> "六万";
            case 6 -> "七万";
            case 7 -> "八万";
            case 8 -> "九万";
            default -> "?";
        };

    }
}
