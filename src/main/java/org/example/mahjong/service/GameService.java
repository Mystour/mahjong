package org.example.mahjong.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
public class GameService {

    private final Map<String, Room> roomMap = new HashMap<>();
    private final Map<String, Player> userMap = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate template;

    public GameService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Room getRoom(String roomCode) {
        return roomMap.get(roomCode);
    }

    public String createRoom(String roomCode, String username) {
        if (roomMap.containsKey(roomCode)) {
            return null;
        }

        Room room = new Room(roomCode);
        room.getUsers().add(username);
        roomMap.put(roomCode, room);

        // Calculate progress and send WebSocket message
        sendProgress(roomCode);

        return roomCode;
    }

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

            // 将用户与玩家实例的映射关系存储在userMap中
            for (String user : room.getUsers()) {
                Player player = game.getPlayers()[room.getUsers().indexOf(user)];
                userMap.put(user, player);
                logger.info("Redirecting user {} to /game/{}", user, roomCode);
            }
        }

        // Calculate progress and send WebSocket message
        sendProgress(roomCode);

        return true;
    }

    public Player getPlayer(String roomCode, String username) {
        Room room = roomMap.get(roomCode);
        if (room == null || room.getGame() == null) {
            return null;
        }

        // 根据用户找到对应的Player实例
        return userMap.get(username);
    }

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

    public MahjongGame getGame(String roomCode) {
        Room room = roomMap.get(roomCode);
        return room == null ? null : room.getGame();
    }

    public int calculateProgress(String roomCode) {
        Room room = roomMap.get(roomCode);
        return room == null ? 0 : room.getUsers().size();
    }

    public void sendProgress(String roomCode) {
        int progress = calculateProgress(roomCode);
        RoomProgress roomProgress = new RoomProgress(roomCode, progress);
        template.convertAndSend("/topic/room", roomProgress);
        logger.info("Progress of room {}: {}", roomCode, progress);
    }
    public boolean handleDrawTileMessage(String message) {
        try {
            // 解析 JSON 字符串为一个 Map 对象
            Map<String, String> messageMap = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {});

            // 从 Map 中获取 roomCode、userName 和 tile 属性
            String roomCode = messageMap.get("roomCode");
            String userName = messageMap.get("userName");
            String tile = messageMap.get("tile");
            // 获取游戏实例并处理瓦牌信息
            MahjongGame game = getGame(roomCode);

            if (game != null) {
                game.receiveTileFromMessage(tile);
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            // JSON 解析失败，处理异常
            e.printStackTrace();
        }
        return false;
    }




}