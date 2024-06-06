var stompClient = null;
var socket = new SockJS('/room');
var roomCode;
var username;

//这是一些建立连接，保持心跳和定义的东西
function connect() {
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setTimeout(()=>{
            console.log('Connected: ' + frame);

            // 启动心跳定时器
            var heartbeatInterval = setInterval(function() {
                console.log('Sending heartbeat');
                stompClient.send("/app/heartbeat", {}, "");
            }, 10000); // 每隔10秒发送一次心跳消息

            // 监听连接关闭事件，在连接关闭时清除心跳定时器并重新连接
            stompClient.onclose = function() {
                console.log('WebSocket connection closed');
                clearInterval(heartbeatInterval);
                reconnect();
            };
            stompClient.disconnect = function() {
                console.log('WebSocket connection closed');
                clearInterval(heartbeatInterval);
                reconnect();
            };

            // 订阅 '/game/room' 主题
            stompClient.subscribe('/game/room/' + roomCode, function (message) {
                console.log('Message received: ' + message.body);
                // 收到消息后更新所有玩家的手牌
                updateAllPlayersHandCards();
            });

            // 订阅 '/topic/roomDataChanged' 主题，用于接收服务器端推送的数据变化通知
            stompClient.subscribe('/topic/roomDataChanged/' + roomCode, function (message) {
                // 当接收到服务器端的消息时，执行更新所有玩家手牌数据的函数
                console.log('Message received: ' + message.body);
                updateAllPlayersHandCards();
            });
            stompClient.subscribe('/topic/drawTileFeedback/' + roomCode, function (message) {
                console.log('Message received: ' + message.body);

            });
        },50)
    }, function (error) {
        console.log('Error in connecting WebSocket: ' + error);
        // 连接断开时触发重新连接
        reconnect();
    });
}

function reconnect() {
    setTimeout(function () {
        console.log('Reconnecting to WebSocket...');
        connect();
    }, 5000); // 5秒后重新连接
}

connect(); // 初始化时建立连接

var Player = /** @class */ (function () {
    function Player(id, username, cards, discards, showcards, discardingTile, condition, isturn, ischecking) {
        //大部分信息都存在player类里了方便调用
        this.id = id;
        this.username = username; // 添加这一行
        this.cards = cards;
        this.discards = discards;
        this.showcards = showcards;
        this.discardingTile = discardingTile;
        this.condition = condition;
        this.canChow = this.condition[0];
        this.canPung = this.condition[1];
        this.canKong = this.condition[2];
        this.canMahjong = this.condition[3];
        this.hasMahjong = this.condition[4];
        this.isturn = isturn;
        this.ischecking = ischecking;
    }
    return Player;
}());

// Player.prototype.updateGameProgress = function() {
//     // 调用全局函数来更新进度条和显示的玩家名
//     window.updateProgressBarAndPlayerNames(this.username);
// };

//以下perform_xxx都是按钮，可以改一下按钮的格式和位置
Player.prototype.perform_discard = function (handDiv, selectedImgObj) {
    // 创建按钮
    var button = document.createElement('button');
    button.innerHTML = 'Discard';
    var subscription = null;

    // 设置按钮点击事件
    button.onclick = function() {
        if (selectedImgObj.img) {
            button.disabled = true;
            var imageName = selectedImgObj.img.src.split("/").pop().split(".")[0];
            console.log('Clicked on card with src:', selectedImgObj.img.src);
            console.log('Sending discard message with tile:', imageName);
            // 注意消息的目的地，应该与服务器端一致
            stompClient.send("/app/drawTile/" + roomCode, {}, JSON.stringify({ 'roomCode': roomCode, 'userName': username, 'tile': imageName }));

        } else {
            button.disabled = false;
            console.log('No card selected.');
        }

        // startGameProgressCountdown();
    };
    handDiv.appendChild(button);
};
Player.prototype.perform_chow = function (handDiv) {
    var button = document.createElement('button');
    button.innerHTML = 'Chow';
    console.log('Chow');
    button.addEventListener('click', function() {
        button.disabled = true;
        fetch('/otherPlayerChow/' + roomCode, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
            })
            .catch(error => {
                button.disabled = false;
                console.error('Error:', error);
            });
    });
    handDiv.appendChild(button);
};
Player.prototype.perform_pung = function (handDiv) {
    var button = document.createElement('button');
    button.innerHTML = 'Pung';
    console.log('Pung');
    button.addEventListener('click', function() {
        button.disabled = true;
        fetch('/otherPlayerPung/' + roomCode, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
            })
            .catch(error => {
                button.disabled = false;
                console.error('Error:', error);
            });
    });
    handDiv.appendChild(button);
};
Player.prototype.perform_kong = function (handDiv,_this) {
    var button = document.createElement('button');
    button.innerHTML = 'Kong';
    console.log('Kong');
    if(_this.isturn){
        button.addEventListener('click', function() {
            button.disabled = true;
            fetch('/currentPlayerKong/' + roomCode, {
                method: 'GET'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                })
                .catch(error => {
                    button.disabled = false;
                    console.error('Error:', error);
                });
        });
    }else if (_this.ischecking){
        button.addEventListener('click', function() {
            button.disabled = true;
            fetch('/otherPlayerKong/' + roomCode, {
                method: 'GET'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                })
                .catch(error => {
                    button.disabled = false;
                    console.error('Error:', error);
                });
        });
    }
    handDiv.appendChild(button);
};
Player.prototype.perform_mahjong = function (handDiv,_this) {
    var button = document.createElement('button');
    button.innerHTML = 'Mahjong';
    if(_this.isturn){
        button.addEventListener('click', function() {
            button.disabled = true;
            fetch('/currentPlayerMahjong/' + roomCode, {
                method: 'GET'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                })
                .catch(error => {
                    button.disabled = false;
                    console.error('Error:', error);
                });
        });
    }else if (_this.ischecking){
        button.addEventListener('click', function() {
            button.disabled = true;
            fetch('/otherPlayerMahjong/' + roomCode, {
                method: 'GET'
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                })
                .catch(error => {
                    button.disabled = false;
                    console.error('Error:', error);
                });
        });
    }

    handDiv.appendChild(button);
};

Player.prototype.perform_skip = function (handDiv) {
    var button = document.createElement('button');
    button.innerHTML = 'Skip';
    button.addEventListener('click', function() {
        // 禁用按钮，防止多次点击
        button.disabled = true;

        skipAction();
    });
    handDiv.appendChild(button);
};

Player.prototype.perform_playagain = function (handDiv) {
    var button = document.createElement('button');
    button.innerHTML = 'playagain';
    button.addEventListener('click', function() {
        // 禁用按钮，防止多次点击
        button.disabled = true;
        fetch('/endGame/' + roomCode, {
            method: 'GET'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
            })
            .catch(error => {
                // 如果发生错误，可以选择重新启用按钮，用户可以再次尝试
                button.disabled = false;
                console.error('Error:', error);
            });
    });
    handDiv.appendChild(button);
};
//更新图片的方法分为了当前界面的主玩家和别的玩家，只有主玩家的界面才能创建按钮，牌能点击
Player.prototype.updateHandWithImages_self = async function () {
    if (!Array.isArray(this.cards)) {
        console.error('Player cards is not an array:', this.cards);
        return;
    }

    var handDiv = document.getElementById('player1');
    handDiv.innerHTML = '';
    var _this = this; // 保存当前上下文
    let selectedImgObj = {img: null};// 用于存储当前选中的图片
    //这是画当前玩家刚刚出的牌，因为不会摆直接和手牌一起放了，大小和位置不一样
    for (let i = 0; i < _this.discardingTile.length; i++) {
        let img = document.createElement('img');
        img.src = _this.discardingTile[i];
        img.classList.add('card', 'small-card1'); // Add classes to the img element
        img.style.transform = 'translateY(-40px) translateX(40px)';
        handDiv.appendChild(img);
    }
    //这是画手牌的地方，被选择牌的变化情况可以改，但是不要改selectedImgObj这种判断，选中的牌会在弃牌时用上
    for (let i = 0; i < _this.cards.length; i++) {
        let img = document.createElement('img');
        img.src = this.cards[i];
        img.classList.add('card', 'small-card'); // Add classes to the img element
        img.addEventListener('click', function (event) {
            console.log('Clicked on card with src:', img);
            if (selectedImgObj.img === img) { // 如果当前点击的图片已经被选中
                img.style.transform = 'translateY(0)';
                selectedImgObj.img = null;
            } else {
                if (selectedImgObj.img) { // 如果有其他图片被选中，则取消它的选中状态
                    selectedImgObj.img.style.transform = 'translateY(0)';
                }
                img.style.transform = 'translateY(-20px)';
                selectedImgObj.img = img; // 更新选中的图片
            }
        });
        handDiv.appendChild(img);
    }
    // 以下是弃牌和明牌，都堆在手牌后面了，大小不一样

    //更新弃牌堆
    var discardPileDiv = document.getElementById('discardPile1'); // 把player1换成1
    discardPileDiv.innerHTML = '';
    for (let i = 0; i < _this.discards.length; i++) {
        let img = document.createElement('img');
        img.src = _this.discards[i];
        img.classList.add('card', 'small-card1'); // Add classes to the img element
        discardPileDiv.appendChild(img);
    }

    for (let i = 0; i < _this.showcards.length; i++) {
        let img = document.createElement('img');
        img.src = _this.showcards[i];
        img.classList.add('card', 'small-card2'); // Add classes to the img element
        handDiv.appendChild(img);
    }
    //以下是判断，只有当前玩家能进行某些操作时才会给出按钮
    if (!_this.hasMahjong) {
        if (_this.isturn && _this.ischecking) {
            _this.perform_discard(handDiv, selectedImgObj);
            if (_this.canKong) {
                _this.perform_kong(handDiv, _this);
            }
            if (_this.canMahjong) {
                _this.perform_mahjong(handDiv, _this);
            }
            var triangle = document.createElement('div');
            triangle.classList.add('triangle');
            handDiv.appendChild(triangle);
        } else if (_this.ischecking) {
            _this.perform_skip(handDiv);
            if (_this.canChow) {
                _this.perform_chow(handDiv);
            }
            if (_this.canPung) {
                _this.perform_pung(handDiv);
            }
            if (_this.canKong) {
                _this.perform_kong(handDiv, _this);
            }
            if (_this.canMahjong) {
                _this.perform_mahjong(handDiv, _this);
            }
            var circle = document.createElement('div');
            circle.classList.add('circle');
            handDiv.appendChild(circle);
        }
    } else {
        _this.perform_playagain(handDiv, _this);
        //本来是玩家胜利后可以选择看积分板，但是"/api/roomUsers/{roomCode}"方法会让服务器报错？？
        //await createTable(handDiv, _this.usernames, _this.scores);
    }

};
Player.prototype.updateHandWithImages_other = function () {
    if (!Array.isArray(this.cards)) {
        console.error('Player cards is not an array:', this.cards);
        return;
    }

    var handDiv = document.getElementById(this.id);
    handDiv.innerHTML = '';
    var _this = this;

    // 根据玩家的方位来决定显示哪一张图片
    var cardImage;
    switch (this.id) {
        case 'player2':
            cardImage = '/images/TileType2/east.png';
            break;
        case 'player3':
            cardImage = '/images/TileType2/north.png';
            break;
        case 'player4':
            cardImage = '/images/TileType2/west.png';
            break;
        default:
            console.error('Invalid player id:', this.id);
            return;
    }

    for (let i = 0; i < _this.discardingTile.length; i++) {
        let img = document.createElement('img');
        img.src = _this.discardingTile[i];
        img.classList.add('card', 'small-card1'); // Add classes to the img element
        if (this.id === 'player2') {
            img.style.transform = 'translateY(-40px) translateX(-40px) rotate(15deg)';
        } else if (this.id === 'player3') {
            img.style.transform = 'translateY(-40px) translateX(40px)';
        } else {
            img.style.transform = 'translateY(-40px) translateX(40px)  rotate(-15deg)';
        }
        handDiv.appendChild(img);
    }
    for (let i = 0; i < _this.cards.length; i++) {
        let img = document.createElement('img');
        img.src = cardImage; // 使用预定的图片
        if (this.id === 'player2') {
            img.classList.add('card', 'small-card-side'); // Add classes to the img element
            img.style.transform = 'rotate(105deg)';
        } else if (this.id === 'player3') {
            img.classList.add('card', 'small-card-back'); // Add classes to the img element
            img.style.transform = 'rotate(180deg)';
        } else {
            img.classList.add('card', 'small-card-side'); // Add classes to the img element
            img.style.transform = 'rotate(-105deg)';
        }
        img.addEventListener('click', function (event) {
            console.log('Clicked on card with src:', img);
        });
        handDiv.appendChild(img);
    }

    // 更新弃牌堆
    var discardPileDiv = document.getElementById('discardPile' + this.id.split('player')[1]); // 把playeri换成i
    discardPileDiv.innerHTML = '';
    for (let i = 0; i < _this.discards.length; i++) {
        let img = document.createElement('img');
        img.src = _this.discards[i];
        img.classList.add('card', 'small-card1'); // Add classes to the img element

        discardPileDiv.appendChild(img);
    }

    for (let i = 0; i < this.showcards.length; i++) {
        let img = document.createElement('img');
        img.src = this.showcards[i];
        img.classList.add('card', 'small-card2'); // Add classes to the img element
        handDiv.appendChild(img);
    }
    if(_this.isturn){
        var triangle = document.createElement('div');
        triangle.classList.add('triangle');
        handDiv.appendChild(triangle);
    }else if(_this.ischecking){
        var circle = document.createElement('div');
        circle.classList.add('circle');
        handDiv.appendChild(circle);
    }
};

//由于订阅机制，每次服务器的后端数据发生改动后，都会告诉客户端数据改动了，要客户端在更新所有玩家的手牌
async function updateAllPlayersHandCards() {
    try {
        // 获取所有玩家的手牌数据
        var cards = await $.ajax({
            url: '/getAllPlayersHandCards/' + roomCode,
            type: 'GET'
        });
        var discards = await $.ajax({
            url: '/getAllPlayersDiscards/' + roomCode,
            type: 'GET'
        });
        var showcards = await $.ajax({
            url: '/getAllPlayersShowedCards/' + roomCode,
            type: 'GET'
        });
        var discardingTile = await $.ajax({
            url: '/getDiscardingTile/' + roomCode,
            type: 'GET'
        });

        var directions = ['south', 'east', 'north', 'west']; // 将方向顺序改为逆时针
        var condition = await $.ajax({
            url: '/getAllPlayersCondition/' + roomCode,
            type: 'GET'
        });

        var index = await $.ajax({
            url: '/getPlayerIndex/' + roomCode,
            type: 'GET'
        });

        // var scores = await $.ajax({
        //     url: '/getAllPlayersScores/' + roomCode,
        //     type: 'GET',
        // });
        for (var i = 0; i < 4; i++) {
            // 根据当前用户的索引计算方向
            var direction = directions[i];
            console.log('Direction:', direction);
            // 创建玩家对象并更新手牌数据
            var isturn = index[0] === (currentUserIndex + i) % 4;
            var ischecking = index[1] === (currentUserIndex + i) % 4;
            var username = await $.ajax({
                url: '/api/username',
                type: 'GET',
            });
            var player  = new Player('player' + (i + 1), username, cards[(currentUserIndex + i) % 4], discards[(currentUserIndex + i) % 4],showcards[(currentUserIndex + i) % 4],discardingTile[(currentUserIndex + i) % 4], condition[(currentUserIndex + i) % 4], isturn, ischecking );
            if(i === 0){
                await player.updateHandWithImages_self();
                startGameProgressCountdown();
            } else {
                player.updateHandWithImages_other();
            }
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

$(function () {
    roomCode = window.location.pathname.split('/')[2];
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
                    currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);
                    // 获取并更新所有玩家的手牌数据
                    updateAllPlayersHandCards();
                }
            });
        }
    });
});
//以下是scoreTable文件里的东西我搬到这里来了

function createTable(handDiv, usernames, scores) {

    var table = document.createElement("table");
    table.id = "ScoreTable";
    table.className = "score-table"; // 添加类名

    // 创建表格标题
    var titleRow = document.createElement("tr");
    titleRow.className = "table-title";
    var titleCell = document.createElement("th");
    titleCell.colSpan = 2;
    titleCell.textContent = "Score Table";
    titleRow.appendChild(titleCell);
    table.appendChild(titleRow);

    // 创建表头
    var headerRow = document.createElement("tr");
    var headers = ["Name", "Score"];
    headers.forEach(function(headerText) {
        var header = document.createElement("th");
        header.textContent = headerText;
        headerRow.appendChild(header);
    });
    table.appendChild(headerRow);

    // 创建表格内容
    for (var i = 0; i < Math.max(usernames.length, scores.length); i++) {
        var row = document.createElement("tr");
        for (var j = 0; j < headers.length; j++) {
            var cell = document.createElement("td");
            if (j === 1) {
                cell.textContent = scores[i] || ""; // 处理分数未定义的情况
            } else {
                cell.textContent = usernames[i] || ""; // 处理用户名未定义的情况
            }
            row.appendChild(cell);
        }
        table.appendChild(row);
    }

    // 将表格添加到容器中
    handDiv.appendChild(table);

    // 创建切换按钮
    var toggleButton = document.createElement("button");
    toggleButton.textContent = "Scores Table";
    toggleButton.onclick = toggleTable;
    handDiv.appendChild(toggleButton);

}

function toggleTable() {
    var table = document.getElementById("ScoreTable");
    if (table.classList.contains("hidden")) {
        table.classList.remove("hidden"); // 使用类名切换可见性
        document.querySelector(".table-title").classList.remove("hidden");
    } else {
        table.classList.add("hidden");
        document.querySelector(".table-title").classList.add("hidden");
    }
}