package org.example.mahjong.dto;

import lombok.Getter;

/**
 * Class representing the progress of activities in a specific room.
 */
@Getter
public class RoomProgress {
    private final String roomCode;
    private int count;

    /**
     * Constructor to initialize the RoomProgress object.
     *
     * @param roomCode The code of the room.
     * @param count The current progress count.
     */
    public RoomProgress(String roomCode, int count) {
        this.roomCode = roomCode;
        this.count = count;
    }
}
