package org.example.mahjong.controller;

import org.example.mahjong.game.MahjongGame;
import org.example.mahjong.game.Room;
import org.example.mahjong.service.GameService;
import org.example.mahjong.sound.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.example.mahjong.player.*;
import org.example.mahjong.tile.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.example.mahjong.dto.*;
import org.example.mahjong.dto.message.DetermineMessage;
import org.example.mahjong.dto.message.OperateMessage;
import org.example.mahjong.dto.message.OperationType;
import org.example.mahjong.dto.message.PutMessage;
import org.example.mahjong.dto.message.SkipMessage;
import org.example.mahjong.dto.message.TakeMessage;


    @Controller
    public class GameController {

        private final GameService gameService;

        @Autowired
        public SimpMessagingTemplate template;

        @Autowired
        public GameController(GameService gameService) {
            this.gameService = gameService;
        }

        @ModelAttribute("username")
        public String getUsername(@AuthenticationPrincipal UserDetails userDetails) {
            return userDetails.getUsername();
        }

        @GetMapping("/api/username")
        @ResponseBody
        public String getCurrentUsername(@AuthenticationPrincipal UserDetails userDetails) {
            return getUsername(userDetails);
        }

        @GetMapping("/api/roomUsers/{roomCode}")
        @ResponseBody
        public List<String> getRoomUsers(@PathVariable String roomCode) {
            Room room = gameService.getRoom(roomCode);
            if (room == null) {
                return new ArrayList<>();
            }
            return room.getUsers();
        }

        @PostMapping("/createRoom")
        public String createRoom(@RequestParam String roomCode, @ModelAttribute("username") String username,
                                 RedirectAttributes redirectAttributes) {
            String roomCodeGet = gameService.createRoom(roomCode, username);
            if (roomCodeGet != null) {
                try {
                    String serverIp = InetAddress.getLocalHost().getHostAddress();
                    String serverPort = "8080"; // 你的应用的端口号
                    String url = "http://" + serverIp + ":" + serverPort; // 没弄SSL证书，所以是http而不是https
                    redirectAttributes.addFlashAttribute("message",
                            "Room created with code: " + roomCode + ". Others can join at: " + url);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    redirectAttributes.addFlashAttribute("error", "Failed to get server IP address.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to create room with code: " + roomCode);
            }
            return "redirect:/welcome/" + roomCode;
        }

        @PostMapping("/joinRoom")
        public String joinRoom(@RequestParam String roomCode, @ModelAttribute("username") String username,
                               RedirectAttributes redirectAttributes) {
            boolean success = gameService.joinRoom(roomCode, username);
            if (success) {
                redirectAttributes.addFlashAttribute("message", "Joined room with code: " + roomCode);
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to join room with code: " + roomCode);
            }
            // if game has started, redirect to game page, else redirect to welcome page
            MahjongGame game = gameService.getGame(roomCode);
            if (game != null) {
                return "redirect:/game/" + roomCode;
            } else {
                return "redirect:/welcome/" + roomCode;
            }
        }

        @GetMapping("/welcome/{roomCode}")
        public String welcomeToRoom(@PathVariable String roomCode, Model model) {
            model.addAttribute("welcomeMessage", "Welcome to room: " + roomCode);
            return "welcome";
        }

        @GetMapping("/welcome")
        public String welcome(Model model) {
            model.addAttribute("welcomeMessage", "Welcome to mahjong game");
            return "welcome";
        }

        @GetMapping("/game/{roomCode}")
        public String game(@PathVariable String roomCode, Model model) {
            model.addAttribute("roomCode", roomCode);
            SoundService soundService = SoundService.getInstance();
            soundService.playMusic();
            return "game";
        }

        @GetMapping("/getAllPlayersHandCards/{roomCode}")
        @ResponseBody
        public List<List<String>> getAllPlayersHandCards(@PathVariable String roomCode) {
            MahjongGame game = gameService.getGame(roomCode);
            if (game == null) {
                // Handle the case where the game has not started yet
                return null;
            }
            return game.getAllPlayersHands().stream()
                    .map(playerHandCards -> playerHandCards.stream()
                            .map(Tile::getImageUrl)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList());
        }
    @PostMapping("/getRoudInfo")
    @ResponseBody
    public ResultData<RoudInfoEntity> getRoudInfo(@RequestParam String roomCode) {
        RoudInfoEntity roudInfoEntity = new RoudInfoEntity();
        MahjongGame game = gameService.getGame(roomCode);
        if (game == null) {
            return ResultData.fail(-1, "game not found");
        }
        Player[] players = game.getPlayers();
        List<PlayerEntity> playerEntitys = new ArrayList<>();
        for (Player item : players) {
            PlayerEntity playerEntity = new PlayerEntity();
            playerEntity.setUserName(roomCode);
            String userName = gameService.getUserName(item);
            playerEntity.setUserName(userName);
            if (item.isBanker()) {
                playerEntity.setAlias("庄家");
                roudInfoEntity.setOwn(playerEntity);
            }
            playerEntity.setAlias("闲家");
            playerEntitys.add(playerEntity);
        }
        roudInfoEntity.setPlayers(playerEntitys);
        return ResultData.success(roudInfoEntity);
    }

    @PostMapping("/take")
    @ResponseBody
    public ResultData<TakeEntity> take(@RequestParam String roomCode, @RequestParam String userName) {
        Player player = gameService.getPlayer(roomCode, userName);
        if (player == null) {
            return ResultData.fail(-1, "player is not found");
        }
        Tile tile = player.drawTile();
        TakeEntity takeEntity = new TakeEntity();
        takeEntity.setTake(tile);
        ArrayList<Option> raceTypes = new ArrayList<>();
        if (!player.isCanKong()) {
            Option operate = new Option();
            operate.setOperationType(OperationType.Kong);
            List<Tile> operateTiles = new ArrayList<>();
            operateTiles.add(tile);
            operate.setTiles(operateTiles);
            raceTypes.add(operate);
        }

        if (player.isCanMahjong()) {
            Option operate = new Option();
            operate.setOperationType(OperationType.Hu);
            List<Tile> operateTiles = new ArrayList<>();
            operateTiles.add(tile);
            operate.setTiles(operateTiles);
            raceTypes.add(operate);
        }

        List<Tile> chows = player.getHand().getChows();
        List<Tile> pungs = player.getHand().getPungs();
        List<Tile> kongs = player.getHand().getKongs();
        List<Tile> discards = player.getHand().getDiscards();
        List<Tile>[] handAllTypeCards = player.getHand().gethandcard();
        List<Tile> handCars = new ArrayList<>();
        for (List<Tile> item : handAllTypeCards) {
            handCars.addAll(item);
        }
        takeEntity.setChows(chows);
        takeEntity.setPungs(pungs);
        takeEntity.setKongs(kongs);
        takeEntity.setOuts(discards);
        takeEntity.setHands(handCars);

        TakeMessage msg = new TakeMessage();
        msg.setWho(userName);
        sendTake(roomCode, msg);

        return ResultData.success(takeEntity);
    }

    @PostMapping("/put")
    @ResponseBody
    public ResultData<PutEntity> put(@RequestParam String roomCode, @RequestParam String userName,
                                     @RequestParam Tile tile) {
        Player player = gameService.getPlayer(roomCode, userName);
        if (player == null) {
            return ResultData.fail(-1, "player is not found");
        }
        player.discardTile(tile);
        PutEntity puEntity = new PutEntity();
        puEntity.setPut(tile);
        List<Tile> chows = player.getHand().getChows();
        List<Tile> pungs = player.getHand().getPungs();
        List<Tile> kongs = player.getHand().getKongs();
        List<Tile> discards = player.getHand().getDiscards();
        List<Tile>[] handAllTypeCards = player.getHand().gethandcard();
        List<Tile> handCars = new ArrayList<>();
        for (List<Tile> item : handAllTypeCards) {
            handCars.addAll(item);
        }
        puEntity.setChows(chows);
        puEntity.setPungs(pungs);
        puEntity.setKongs(kongs);
        puEntity.setOuts(discards);
        puEntity.setHands(handCars);

        PutMessage msg = new PutMessage();
        msg.setWho(userName);
        msg.setTile(tile);
        sendPut(roomCode, msg);

        return ResultData.success(puEntity);
    }

    @PostMapping("/determine")
    @ResponseBody
    public ResultData<DetermineEntity> determine(@RequestParam String roomCode, @RequestParam String userName,
                                                 @RequestParam Tile tile) {
        Player player = gameService.getPlayer(roomCode, userName);
        if (player == null) {
            return ResultData.fail(-1, "player is not found");
        }
        DetermineEntity determineEntity = new DetermineEntity();
        List<Option> options = new ArrayList<>();

        player.checkDecisionCondition(tile);
        if (player.isCanChow()) {
            Option option = new Option();
            List<Tile> tiles = new ArrayList<>();
            tiles.add(tile);
            option.setTiles(tiles);
            option.setOperationType(OperationType.Chow);
            options.add(option);
        }
        if (player.isCanPung()) {
            Option option = new Option();
            List<Tile> tiles = new ArrayList<>();
            tiles.add(tile);
            option.setTiles(tiles);
            option.setOperationType(OperationType.Pung);
            options.add(option);
        }
        if (player.isCanKong()) {
            Option option = new Option();
            List<Tile> tiles = new ArrayList<>();
            tiles.add(tile);
            option.setTiles(tiles);
            option.setOperationType(OperationType.Kong);
            options.add(option);
        }
        if (player.isCanMahjong()) {
            Option option = new Option();
            List<Tile> tiles = new ArrayList<>();
            tiles.add(tile);
            option.setTiles(tiles);
            option.setOperationType(OperationType.Hu);
            options.add(option);
        }
        determineEntity.setOptions(options);
        return ResultData.success(determineEntity);
    }

    @PostMapping("/operate")
    @ResponseBody
    public ResultData<OperateEntity> operate(@RequestParam String roomCode, @RequestParam String userName,
                                             OperationType operationType,
                                             Tile[] tiles) {
        Player player = gameService.getPlayer(roomCode, userName);
        if (player == null) {
            return ResultData.fail(-1, "player is not found");
        }
        if (operationType == OperationType.Chow) {
            player.declareChow(tiles[0]);
        } else if (operationType == OperationType.Pung) {
            player.declarePung(tiles[0]);
        } else if (operationType == OperationType.Kong) {
            player.declareKong(tiles[0]);
        } else if (operationType == OperationType.Hu) {
            player.declareMahjong(tiles[0]);
        }
        OperateEntity operateEntity = new OperateEntity();
        List<Tile> chows = player.getHand().getChows();
        List<Tile> pungs = player.getHand().getPungs();
        List<Tile> kongs = player.getHand().getKongs();
        List<Tile> discards = player.getHand().getDiscards();
        List<Tile>[] handAllTypeCards = player.getHand().gethandcard();
        List<Tile> handCars = new ArrayList<>();
        for (List<Tile> item : handAllTypeCards) {
            handCars.addAll(item);
        }
        operateEntity.setChows(chows);
        operateEntity.setPungs(pungs);
        operateEntity.setKongs(kongs);
        operateEntity.setOuts(discards);
        operateEntity.setHands(handCars);
        operateEntity.setTargetTile(tiles[0]);

        OperateMessage msg = new OperateMessage();
        msg.setWho(userName);
        msg.setOperationType(operationType);

        sendOperate(roomCode, msg);
        return ResultData.success(operateEntity);
    }

    public void sendTake(String roomCode, TakeMessage msg) {
        template.convertAndSend("/game/room/" + roomCode, msg);
    }

    public void sendPut(String roomCode, PutMessage msg) {
        template.convertAndSend("/game/room/" + roomCode, msg);
    }

    public void sendDetermine(String roomCode, DetermineMessage msg) {
        template.convertAndSend("/game/room/" + roomCode, msg);
    }

    public void sendOperate(String roomCode, OperateMessage msg) {
        template.convertAndSend("/game/room/" + roomCode, msg);
    }

    public void sendSkip(String roomCode, SkipMessage msg) {
        template.convertAndSend("/game/room/" + roomCode, msg);
    }
}