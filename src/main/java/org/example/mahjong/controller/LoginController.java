package org.example.mahjong.controller;

import org.example.mahjong.player.PlayerInfo;
import org.example.mahjong.repository.PlayerRepository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for handling login and registration requests. (Login is in WebSecurityConfig)
 */
@Controller
public class LoginController {
    static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor with dependency injection for PlayerRepository and PasswordEncoder.
     *
     * @param playerRepository Repository for handling player data.
     * @param passwordEncoder Encoder for hashing passwords.
     */
    public LoginController(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * Handles POST requests to register a new user.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param email The email of the new user.
     * @param redirectAttributes Attributes for a redirect scenario.
     * @return Redirect to the login page if registration is successful, otherwise redirect back to the registration page.
     */
    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String password, @RequestParam String email, RedirectAttributes redirectAttributes) {
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setUsername(username);
        playerInfo.setPassword(passwordEncoder.encode(password));
        playerInfo.setEmail(email);
        try {
            PlayerInfo savedPlayer = playerRepository.save(playerInfo);
            if (savedPlayer.getId() == null) {
                logger.error("Failed to save player");
                redirectAttributes.addFlashAttribute("error", "Registration failed");
                return "redirect:/register";
            } else {
                // Add a flash message
                redirectAttributes.addFlashAttribute("message", "Registration successful!");
                return "redirect:/login";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Registration failed");
            return "redirect:/register";
        }
    }
}
