var socket = new SockJS('/room');
var stompClient = Stomp.over(socket);
var roomCode;

stompClient.connect({}, function () {
    stompClient.subscribe('/game/room', function (message) {
        updateAllPlayersHandCards();
    });
});

var Player = /** @class */ (function () {
    function Player(id, cards) {
        this.id = id;
        this.cards = cards;
    }
    Player.prototype.updateHandWithImages = function () {
        if (!Array.isArray(this.cards)) {
            console.error('Player cards is not an array:', this.cards);
            return;
        }
        var handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';
        for (var i = 0; i < this.cards.length; i++) {
            var img = document.createElement('img');
            img.src = this.cards[i];
            img.classList.add('card', 'small-card'); // Add classes to the img element
            handDiv.appendChild(img);
        }
    };
    return Player;
}());

Player.prototype.updateHandWithImages = function () {
    if (!Array.isArray(this.cards)) {
        console.error('Player cards is not an array:', this.cards);
        return;
    }
    var handDiv = document.getElementById(this.id);
    handDiv.innerHTML = '';
    var _this = this; // 保存当前上下文
    for (var i = 0; i < this.cards.length; i++) {
        (function() {
            var img = document.createElement('img');
            img.src = _this.cards[i];
            img.classList.add('card', 'small-card'); // Add classes to the img element
            img.addEventListener('click', function () {
                _this.handleCardClick(img.src); // 调用处理点击事件的方法
            });
            handDiv.appendChild(img);
        })();
    }
};

Player.prototype.handleCardClick = function (cardSrc) {
    // 处理卡片点击事件的逻辑
    console.log('Clicked on card with src:', cardSrc);
    // 这里可以执行任何你想要的操作，例如向服务器发送消息等



    //发送出牌的请求，目前没找到出牌的接口
    stompClient.send("/app/drawTile", {}, JSON.stringify({ 'roomCode': roomCode, 'userName': 'user1' }));
};

// 定义 updateAllPlayersHandCards 函数
function updateAllPlayersHandCards() {
    // 获取所有玩家的手牌数据
    $.ajax({
        url: '/getAllPlayersHandCards/' + roomCode,
        type: 'GET',
        success: function (data) {
            var directions = ['south', 'east', 'north', 'west']; // 将方向顺序改为逆时针
            for (var i = 0; i < 4; i++) {
                // 根据当前用户的索引计算方向
                var direction = directions[i];
                console.log('Direction:', direction);
                // 创建玩家对象并更新手牌数据
                var player = new Player('player' + (i + 1), data[(currentUserIndex + i) % 4]);
                player.updateHandWithImages();
            }
        }
    });
}

$(function () {
    roomCode = window.location.pathname.split('/')[2]; // 获取 roomCode
    getCurrentUserIndex(); // 调用获取当前用户索引的函数
});

// 定义 getCurrentUserIndex 函数
function getCurrentUserIndex() {
    // 获取当前用户名
    $.ajax({
        url: '/api/username',
        type: 'GET',
        success: function (data) {
            username = data;
            // 获取房间内所有玩家的用户名
            $.ajax({
                url: '/api/roomUsers/' + roomCode,
                type: 'GET',
                success: function (usernames) {
                    // 找到当前用户在返回的数据中的索引
                    currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);
                    // 获取并更新所有玩家的手牌数据
                    updateAllPlayersHandCards();
                    // 每隔一定时间间隔执行一次获取并更新所有玩家手牌数据的函数
                    // 间隔5秒钟执行一次
                    // setInterval(updateAllPlayersHandCards, 5000);
                }
            });
        }
    });
}
