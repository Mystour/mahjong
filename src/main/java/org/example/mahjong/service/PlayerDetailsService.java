package org.example.mahjong.service;

import org.example.mahjong.player.Player;
import org.example.mahjong.player.PlayerInfo;
import org.example.mahjong.repository.PlayerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PlayerDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public PlayerDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PlayerInfo playerInfo = playerRepository.findByUsername(username);
        if (playerInfo == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return User.withUsername(playerInfo.getUsername())
                .password(playerInfo.getPassword())
                .roles("USER")
                .build();
    }
}