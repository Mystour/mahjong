package org.example.mahjong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GameController {

    @GetMapping("/createGame")
    public String createGame() {
        // 在这里处理创建游戏的请求
        // ...
        return "createGame";
    }

    @PostMapping("/joinGame")
    public String joinGame(@RequestParam String gameCode, RedirectAttributes redirectAttributes) {
        // 在这里处理加入游戏的请求
        // ...
        return "joinGame";
    }
}