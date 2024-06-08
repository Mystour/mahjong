package org.example.mahjong.tile;

public class DragonTile extends Tile {
    public DragonTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        return switch (getNumber()) {
            case 0 -> "红中";
            case 1 -> "发财";
            case 2 -> "白板";
            default -> "?";
        };

    }
}
