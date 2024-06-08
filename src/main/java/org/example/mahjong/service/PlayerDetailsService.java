package org.example.mahjong.service;

import org.example.mahjong.player.PlayerInfo;
import org.example.mahjong.repository.PlayerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/**
 * Service for loading user-specific data.
 */
@Service
public class PlayerDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    /**
     * Constructor with dependency injection for PlayerRepository.
     *
     * @param playerRepository Repository for handling player data.
     */
    public PlayerDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Loads the user by username.
     *
     * @param username The username of the user to load.
     * @return A fully populated UserDetails object.
     * @throws UsernameNotFoundException if the user could not be found.
     */
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
