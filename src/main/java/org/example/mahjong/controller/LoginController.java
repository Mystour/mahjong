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
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String password, @RequestParam String email, RedirectAttributes redirectAttributes) {
        // 在这里处理注册请求
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
                // 添加一次性消息
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