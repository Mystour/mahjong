package org.example.mahjong.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mahjong.dto.GameProgress;
import org.example.mahjong.dto.RoomProgress;
import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.game.Room;
import org.example.mahjong.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Service for handling game-related operations.
 */
@Service
public class GameService {

    private final Map<String, Room> roomMap = new HashMap<>();
    private final Map<String, Player> userMap = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * Constructor with dependency injection for ObjectMapper.
     *
     * @param objectMapper Mapper for JSON processing.
     */
    public GameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Gets the room associated with the specified room code.
     *
     * @param roomCode The code of the room.
     * @return The Room object.
     */
    public Room getRoom(String roomCode) {
        return roomMap.get(roomCode);
    }

    /**
     * Creates a new room with the specified room code and adds the specified user to the room.
     *
     * @param roomCode The code of the room to create.
     * @param username The username of the user creating the room.
     * @return The room code if creation is successful, otherwise null.
     */
    public String createRoom(String roomCode, String username) {
        if (roomMap.containsKey(roomCode)) {
            return null;
        }

        Room room = new Room(roomCode);
        room.getUsers().add(username);
        roomMap.put(roomCode, room);

        // Calculate progress and send WebSocket message
        sendRoomProgress(roomCode);

        return roomCode;
    }

    /**
     * Adds the specified user to the specified room.
     *
     * @param roomCode The code of the room to join.
     * @param username The username of the user joining the room.
     * @return True if the user successfully joins the room, otherwise false.
     */
    public boolean joinRoom(String roomCode, String username) {
        Room room = roomMap.get(roomCode);
        if (room == null) {
            return false;
        }

        room.addUser(username);

        if (room.getUsers().size() == 4) {
            MahjongGame game = new MahjongGame();
            game.startGame();
            game.currentPlayerDraw();
            room.setGame(game);

            // Store the mapping between users and player instances in userMap
            for (String user : room.getUsers()) {
                Player player = game.getPlayers()[room.getUsers().indexOf(user)];
                userMap.put(user, player);
                logger.info("Redirecting user {} to /game/{}", user, roomCode);
            }
        }

        // Calculate progress and send WebSocket message
        sendRoomProgress(roomCode);

        return true;
    }

    /**
     * Gets the player associated with the specified username in the specified room.
     *
     * @param roomCode The code of the room.
     * @param username The username of the player.
     * @return The Player object.
     */
    public Player getPlayer(String roomCode, String username) {
        Room room = roomMap.get(roomCode);
        if (room == null || room.getGame() == null) {
            return null;
        }

        // Find the Player instance associated with the user
        return userMap.get(username);
    }

    /**
     * Gets the username associated with the specified player.
     *
     * @param player The Player object.
     * @return The username of the player.
     */
    public String getUserName(Player player) {
        for (Map.Entry<String, Player> entry : userMap.entrySet()) {
            String key = entry.getKey();
            Player value = entry.getValue();
            if (value == player) {
                return key;
            }
        }
        return "";
    }

    /**
     * Gets the game associated with the specified room code.
     *
     * @param roomCode The code of the room.
     * @return The MahjongGame object.
     */
    public MahjongGame getGame(String roomCode) {
        Room room = roomMap.get(roomCode);
        return room == null ? null : room.getGame();
    }

    /**
     * Calculates the progress of the specified room.
     *
     * @param roomCode The code of the room.
     * @return The number of users in the room.
     */
    public int calculateProgress(String roomCode) {
        Room room = roomMap.get(roomCode);
        return room == null ? 0 : room.getUsers().size();
    }

    /**
     * Sends the progress of the specified room via WebSocket.
     *
     * @param roomCode The code of the room.
     */
    public void sendRoomProgress(String roomCode) {
        int progress = calculateProgress(roomCode);
        RoomProgress roomProgress = new RoomProgress(roomCode, progress);
        template.convertAndSend("/topic/room", roomProgress);
        logger.info("Progress of room {}: {}", roomCode, progress);
    }

    /**
     * Starts a countdown for game progress in the specified room.
     *
     * @param roomCode The code of the room.
     */
    public void startGameProgressCountdown(String roomCode) {
        System.out.println("startGameProgressCountdown");

        final int[] progress = {10};
        Timer timer = new Timer();
        TimerTask countdown = new TimerTask() {
            public void run() {
                if (progress[0] > 0) {
                    GameProgress gameProgress = new GameProgress(roomCode, progress[0]);
                    template.convertAndSend("/topic/game", gameProgress);
                    logger.info("Progress of game {}: {}", roomCode, progress[0]);
                    progress[0]--;
                    System.out.println(progress[0]);
                } else {
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(countdown, 0, 1000);
    }

    /**
     * Handles the draw tile message sent from a client.
     *
     * @param message The JSON message containing roomCode, userName, and tile.
     * @return True if the message is successfully processed, otherwise false.
     */
    public boolean handleDrawTileMessage(String message) {
        try {
            // Parse JSON string into a Map object
            Map<String, String> messageMap = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {});

            // Get roomCode, userName, and tile from the Map
            String roomCode = messageMap.get("roomCode");
            String userName = messageMap.get("userName");
            String tile = messageMap.get("tile");

            // Get the game instance and process the tile information
            MahjongGame game = getGame(roomCode);

            if (game != null) {
                game.receiveTileFromMessage(tile);
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            // Handle JSON parsing exception
            e.printStackTrace();
        }
        return false;
    }
}