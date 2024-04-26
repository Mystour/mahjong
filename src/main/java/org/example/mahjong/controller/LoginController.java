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

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        PlayerInfo playerInfo = playerRepository.findByUsername(username);
        if (playerInfo != null && passwordEncoder.matches(password, playerInfo.getPassword())) {
            // 添加一次性消息
            redirectAttributes.addFlashAttribute("message", "Login successful!");
            logger.info("User " + username + " logged in");
            // 登录成功，重定向到主页
            return "redirect:/welcome";
        } else {
            // 登录失败，添加错误消息并重定向到登录页面
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
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
                System.out.println("PlayerInfo was not saved to the database");
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