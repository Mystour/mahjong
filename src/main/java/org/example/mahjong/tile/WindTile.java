package org.example.mahjong.tile;

public class WindTile extends Tile {
    public WindTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        return switch (getNumber()) {
            case 0 -> "东风";
            case 1 -> "南风";
            case 2 -> "西风";
            case 3 -> "北风";
            default -> "?";
        };

    }
}