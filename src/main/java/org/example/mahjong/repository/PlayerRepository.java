package org.example.mahjong.repository;

import org.example.mahjong.player.PlayerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<PlayerInfo, Long> {
    PlayerInfo findByUsername(String username);
    // 这里可以定义一些自定义的数据库操作方法，如果需要的话
}