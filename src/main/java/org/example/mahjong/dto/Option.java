package org.example.mahjong.dto;

import lombok.Data;
import org.example.mahjong.dto.message.OperationType;
import org.example.mahjong.tile.Tile;

import java.util.List;

@Data
public class Option {
    private OperationType operationType;
    private List<Tile> tiles;

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType value) {
        this.operationType = value;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> value) {
        this.tiles = value;
    }
}
