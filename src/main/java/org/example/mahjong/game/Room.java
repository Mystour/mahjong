package org.example.mahjong.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a room where users can join and play a Mahjong game.
 */
public class Room {
    private final String roomCode;
    private final List<String> users = new ArrayList<>();
    private MahjongGame game;

    /**
     * Constructor to initialize the Room object.
     *
     * @param roomCode The code of the room.
     */
    public Room(String roomCode) {
        this.roomCode = roomCode;
    }

    /**
     * Gets the room code.
     *
     * @return The code of the room.
     */
    public String getRoomCode() {
        return roomCode;
    }

    /**
     * Gets the list of users in the room.
     *
     * @return The list of users.
     */
    public List<String> getUsers() {
        return users;
    }

    /**
     * Adds a user to the room.
     *
     * @param user The username of the user to add.
     */
    public void addUser(String user) {
        users.add(user);
    }

    /**
     * Gets the Mahjong game associated with the room.
     *
     * @return The Mahjong game.
     */
    public MahjongGame getGame() {
        return game;
    }

    /**
     * Sets the Mahjong game for the room.
     *
     * @param game The Mahjong game to set.
     */
    public void setGame(MahjongGame game) {
        this.game = game;
    }
}