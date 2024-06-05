package org.example.mahjong.repository;

import org.example.mahjong.player.PlayerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<PlayerInfo, Long> {
    PlayerInfo findByUsername(String username);
}