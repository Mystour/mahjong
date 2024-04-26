package org.example.mahjong.service;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.player.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class GameService {

    private final Map<String, MahjongGame> games = new HashMap<>();
    private final Map<String, Player> userPlayerMap = new HashMap<>();

    public String createGame(String port) {
        // 创建一个新的MahjongGame实例
        MahjongGame game = new MahjongGame();
        game.startGame();

        // 将MahjongGame实例添加到Map中
        games.put(port, game);

        // 返回游戏代码
        return port;
    }

    public boolean joinGame(String gameCode, String username) {
        // 从Map中获取对应的MahjongGame实例
        MahjongGame game = games.get(gameCode);

        // 如果找不到MahjongGame实例，返回false
        if (game == null) {
            return false;
        }

        // 从MahjongGame的players数组中取出一个Player实例
        // 这里我们假设MahjongGame的players数组是按照加入游戏的顺序填充的
        Player player = game.players[userPlayerMap.size()];

        // 将用户和Player实例关联起来
        userPlayerMap.put(username, player);

        // 返回true表示成功加入游戏
        return true;
    }

    public Player getPlayer(String username) {
        // 根据用户找到对应的Player实例
        return userPlayerMap.get(username);
    }
}