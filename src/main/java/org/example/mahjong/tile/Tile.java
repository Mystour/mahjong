package org.example.mahjong.tile;

public class Tile implements Comparable<Tile> {
    private TileType type;
    private int number;

    public Tile(TileType type, int number) {
        this.type = type;
        this.number = number;
    }

    public TileType getType() {
        return type;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public int compareTo(Tile other) {
        if (this.type != other.type) {
            return this.type.ordinal() - other.type.ordinal();
        } else {
            return Integer.compare(this.number, other.number);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tile other = (Tile) obj;
        return type == other.type && number == other.number;
    }

    @Override
    public int hashCode() {
        return 31 * type.ordinal() + number;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "type=" + type +
                ", number=" + number +
                '}';
    }
}

enum TileType {
    BAMBOO, CHARACTER, DOT, WIND, DRAGON, SEASON, FLOWER
}
