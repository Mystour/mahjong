package org.example.mahjong.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.example.mahjong.player.Player;
import org.example.mahjong.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    // other methods
}