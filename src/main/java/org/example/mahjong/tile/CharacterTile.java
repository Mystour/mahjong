package org.example.mahjong.tile;

public class CharacterTile extends Tile {
    public CharacterTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        switch (getNumber()){
            case 0:
                return "一万";
            case 1:
                return "二万";
            case 2:
                return "三万";
            case 3:
                return "四万";
            case 4:
                return "五万";
            case 5:
                return "六万";
            case 6:
                return "七万";
            case 7:
                return "八万";
            case 8:
                return "九万";
            default:
                return "?";
        }

    }
}
