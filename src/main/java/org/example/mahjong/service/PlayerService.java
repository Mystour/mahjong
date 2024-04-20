package org.example.mahjong.service;

import org.example.mahjong.player.PlayerInfo;
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

    public PlayerInfo savePlayer(PlayerInfo playerInfo) {
        return playerRepository.save(playerInfo);
    }

    // other methods
}