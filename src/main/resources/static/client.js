var socket = new WebSocket("ws://localhost:8080/game");

socket.onmessage = function(event) {
    // 处理从服务器接收到的消息
    // 更新游戏界面
}

// 添加事件监听器，当用户进行操作时，发送用户的操作到服务器
document.getElementById("myButton").addEventListener("click", function() {
    var message = {
        action: "myAction",
        data: "myData"
    };
    socket.send(JSON.stringify(message));
});