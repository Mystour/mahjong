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
    for (let i = 0; i < this.cards.length; i++) { // 注意这里使用 let 而非 var
        var img = document.createElement('img');
        img.src = this.cards[i];
        img.classList.add('card', 'small-card');
        img.addEventListener('click', () => { // 使用箭头函数以保持 this 上下文正确
            this.handleCardClick(img.src);
        });
        handDiv.appendChild(img);
    }
};


// Player.prototype.handleCardClick = function (cardSrc) {
//     // 处理卡片点击事件的逻辑
//     console.log('Clicked on card with src:', cardSrc);
//     // 移除卡片从手牌
//     img.remove(); // 从 DOM 中移除这张卡片
//     this.cards.splice(index, 1); // 从卡片数组中移除这张卡片
//
//     // 将卡片添加到弃牌堆
//     var discardPile = document.getElementById('discardPile');
//     var newImg = img.cloneNode(true); // 克隆 img 元素
//     discardPile.appendChild(newImg);
//     // 这里可以执行任何你想要的操作，例如向服务器发送消息等
//
//     //发送出牌的请求，目前没找到出牌的接口
//     // $.ajax({
//     //     url: '/getAllPlayersHandCards/' + roomCode,
//     //     type: 'GET',
//     //     success: function (data) {
//
//     //     }
//     // });
// };

Player.prototype.handleCardClick = function (cardSrc) {
    console.log('Clicked on card with src:', cardSrc);

    // 找到当前点击的卡牌元素
    var clickedCard = document.querySelector(`img[src="${cardSrc}"]`);

    // 发送一个出牌请求到服务器
    $.ajax({
        url: '/playCard',
        type: 'POST',
        data: { cardSrc: cardSrc },
        success: function (response) {
            console.log('Server responded:', response);
            // 服务器确认出牌有效后，将卡牌移动到弃牌堆
            var discardPile = document.getElementById('discard-pile');
            discardPile.appendChild(clickedCard); // 将点击的卡牌移动到弃牌堆
        },
        error: function (error) {
            console.error('Error sending card:', error);
        }
    });
};



$(function () {
    var roomCode = window.location.pathname.split('/')[2];
    var username;
    var currentUserIndex;

    // 定义一个函数，用于获取并更新所有玩家手牌数据
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

    // 定义一个函数，用于获取当前用户的索引
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
                        setInterval(updateAllPlayersHandCards, 5000);
                    }
                });
            }
        });
    }

    // 页面加载后立即执行一次获取当前用户索引的函数
    getCurrentUserIndex();

});
