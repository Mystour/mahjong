package org.example.mahjong.service;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.player.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final Map<String, MahjongGame> games = new HashMap<>();
    private final Set<String> rooms = new HashSet<>();
    private final Map<String, Player> userPlayerMap = new HashMap<>();
    private final List<String> waitingUsers = new ArrayList<>();

    public String createRoom(String roomCode, String username) {
        // 如果房间号已经存在，就返回null
        if (rooms.contains(roomCode)) {
            return null;
        }

        // 将房间号添加到房间列表中
        rooms.add(roomCode);

        // 将用户添加到等待列表中
        waitingUsers.add(username);

        // 返回房间号
        return roomCode;
    }

    public boolean joinRoom(String roomCode, String username) {
        // 将用户添加到等待列表中
        waitingUsers.add(username);

        // 如果等待列表中的用户数量达到4，就创建一个新的游戏并开始游戏
        if (waitingUsers.size() == 4) {
            MahjongGame game = new MahjongGame();
            game.startGame();
            games.put(roomCode, game);

            // 将用户和Player实例关联起来
            for (int i = 0; i < 4; i++) {
                userPlayerMap.put(waitingUsers.get(i), game.players[i]);
            }

            // 清空等待列表
            waitingUsers.clear();

            // 返回true表示成功加入房间并开始游戏
            return true;
        }

        // 如果等待列表中的用户数量还没有达到4，就返回false表示还没有开始游戏
        return false;
    }

    public Player getPlayer(String username) {
        // 根据用户找到对应的Player实例
        return userPlayerMap.get(username);
    }
}