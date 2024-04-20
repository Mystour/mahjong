package org.example.mahjong.controller;

import org.example.mahjong.player.Player;
import org.example.mahjong.repository.PlayerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

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

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password) {
        // 在这里处理登录请求
        Player player = playerRepository.findByUsername(username);
        if (player != null && passwordEncoder.matches(password, player.getPassword())) {
            // 登录成功，重定向到主页
            return "redirect:/";
        } else {
            // 登录失败，返回错误消息
            return "login?error";
        }
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        // 在这里处理注册请求
        Player player = new Player();
        player.setUsername(username);
        player.setPassword(passwordEncoder.encode(password));
        player.setEmail(email);
        playerRepository.save(player);
        return "redirect:/login";
    }
}