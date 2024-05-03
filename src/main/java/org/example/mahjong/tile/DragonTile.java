package org.example.mahjong.tile;

public class DragonTile extends Tile {
    public DragonTile(TileType type, int number) {
        super(type, number);
    }
    @Override
    public String toString() {
        switch (getNumber()){
            case 0:
                return "红中";
            case 1:
                return "发财";
            case 2:
                return "白板";
            default:
                return "?";
        }

    }
}
