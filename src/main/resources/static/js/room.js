"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var socket_io_client_1 = require("socket.io-client");
var socket = (0, socket_io_client_1.default)('/room'); // 连接到WebSocket服务器
// 监听'topic/room'事件，这是我们从服务器发送消息的地方
socket.on('topic/room', function (message) {
    var data = JSON.parse(message);
    var roomCode = data.roomCode;
    var count = data.count;
    console.log("Received WebSocket message: " + count);
    localStorage.setItem(roomCode, count.toString());
    var path = window.location.pathname;
    var parts = path.split('/');
    var currentRoomCode = parts[parts.length - 1];
    if (currentRoomCode === roomCode) {
        updateProgressBar(count);
    }
});
// 更新进度条的函数
function updateProgressBar(count) {
    var progressBar = document.getElementById('progressBar');
    if (progressBar) {
        progressBar.style.width = (count / 4 * 100) + '%';
        progressBar.setAttribute('aria-valuenow', count.toString());
    }
}
// 当页面加载时，从localStorage获取计数并更新进度条
window.onload = function () {
    var path = window.location.pathname;
    var parts = path.split('/');
    var roomCode = parts[parts.length - 1];
    var count = parseInt(localStorage.getItem(roomCode) || '0');
    updateProgressBar(count);
};
