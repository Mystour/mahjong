package org.example.mahjong.dto;

import lombok.Getter;

/**
 * Class representing the progress of a game in a specific room.
 */
@Getter
public class GameProgress {
    private final String roomCode;
    private int count;

    /**
     * Constructor to initialize the GameProgress object.
     *
     * @param roomCode The code of the room.
     * @param count The current progress count.
     */
    public GameProgress(String roomCode, int count) {
        this.roomCode = roomCode;
        this.count = count;
    }
}
