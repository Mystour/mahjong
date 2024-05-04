"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var socket_io_client_1 = require("socket.io-client");
var Player = /** @class */ (function () {
    function Player(id, cards) {
        this.id = id;
        this.cards = cards;
    }
    // 更新玩家手中的牌
    Player.prototype.updateHandWithImages = function () {
        var handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';
        for (var i = 0; i < this.cards.length; i++) {
            var img = document.createElement('img');
            img.src = this.cards[i];
            handDiv.appendChild(img);
        }
    };
    return Player;
}());
$(function () {
    var roomCode = window.location.pathname.split('/')[2];
    var playerID = parseInt(window.location.pathname.split('/')[3]);
    // 发送GET请求，获取所有玩家的手牌
    $.ajax({
        url: '/getAllPlayersHandCards/' + roomCode + '/' + playerID,
        type: 'GET',
        success: function (data) {
            var rotatedData = data.slice(playerID - 1).concat(data.slice(0, playerID - 1));
            var directions = ['south', 'east', 'north', 'west'];
            for (var i = 0; i < rotatedData.length; i++) {
                var player = new Player('player' + (i + 1), rotatedData[i]);
                player.updateHandWithImages();
                var playerDiv = document.getElementById(player.id);
                playerDiv.classList.add(directions[i % 4]);
            }
        }
    });
    // 连接到WebSocket服务器
    var socket = (0, socket_io_client_1.default)('/room');
    // 监听'redirect'事件，这是我们从服务器发送消息的地方
    socket.on('redirect', function (url) {
        // 当收到消息时，跳转到新的URL
        window.location.href = url;
    });
});
