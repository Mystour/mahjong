package org.example.mahjong.repository;

import org.example.mahjong.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    // 这里可以定义一些自定义的数据库操作方法，如果需要的话
}