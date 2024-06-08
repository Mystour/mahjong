package org.example.mahjong.message;

/**
 * Class representing a message indicating the start of a player's turn.
 */
public class TurnStartMessage {
    private String username;
    private int time;

    /**
     * Constructor to initialize the TurnStartMessage object.
     *
     * @param username The username of the player whose turn is starting.
     * @param time The time allocated for the turn.
     */
    public TurnStartMessage(String username, int time) {
        this.username = username;
        this.time = time;
    }

    /**
     * Gets the username of the player whose turn is starting.
     *
     * @return The username of the player.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the player whose turn is starting.
     *
     * @param username The username of the player.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the time allocated for the turn.
     *
     * @return The time allocated for the turn.
     */
    public int getTime() {
        return time;
    }

    /**
     * Sets the time allocated for the turn.
     *
     * @param time The time allocated for the turn.
     */
    public void setTime(int time) {
        this.time = time;
    }
}
