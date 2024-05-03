package org.example.mahjong.tile;

public class WindTile extends Tile {
    public WindTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        switch (getNumber()){
            case 0:
                return "东风";
            case 1:
                return "南风";
            case 2:
                return "西风";
            case 3:
                return "北风";
            default:
                return "?";
        }

    }
}