package org.example.mahjong.tile;

public class TileFactory {
    public static Tile createTile(TileType type, int number) {
        return switch (type) {
            case BAMBOO -> new BambooTile(type, number);
            case CHARACTER -> new CharacterTile(type, number);
            case DOT -> new DotTile(type, number);
            case DRAGON -> new DragonTile(type, number);
            case WIND -> new WindTile(type, number);
            default -> throw new IllegalArgumentException("Unknown tile type");
        };
    }
}
