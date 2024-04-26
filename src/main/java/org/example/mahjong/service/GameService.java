package org.example.mahjong.service;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.game.Room;
import org.example.mahjong.player.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {

    private final Map<String, Room> roomMap = new HashMap<>();
    private final Map<String, Player> userMap= new HashMap<>();

    public String createRoom(String roomCode, String username) {
        if (roomMap.containsKey(roomCode)) {
            return null;
        }

        Room room = new Room(roomCode);
        room.getUsers().add(username);
        roomMap.put(roomCode, room);

        return roomCode;
    }

    public boolean joinRoom(String roomCode, String username) {
        Room room = roomMap.get(roomCode);
        if (room == null) {
            return false;
        }

        room.getUsers().add(username);

        if (room.getUsers().size() == 4) {
            MahjongGame game = new MahjongGame();
            game.startGame();
            room.setGame(game);

            // 将用户与玩家实例的映射关系存储在userMap中
            for (String user : room.getUsers()) {
                Player player = game.players[room.getUsers().indexOf(user)];
                userMap.put(user, player);
            }

            return true;
        }

        return false;
    }

    public Player getPlayer(String roomCode, String username) {
        Room room = roomMap.get(roomCode);
        if (room == null || room.getGame() == null) {
            return null;
        }

        // 根据用户找到对应的Player实例
        return userMap.get(username);
    }
}