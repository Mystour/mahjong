var Player = /** @class */ (function () {
    function Player(id, cards) {
        this.id = id;
        this.cards = cards;
    }
    Player.prototype.updateHand = function () {
        var handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';
        for (var i = 0; i < this.cards.length; i++) {
            var cardDiv = document.createElement('div');
            cardDiv.className = 'card';
            if (this.id === 'player3') {
                // 如果是下面的玩家，显示明牌
                cardDiv.textContent = this.cards[i];
            }
            else {
                // 其他玩家，显示暗牌
                cardDiv.textContent = '*';
            }
            handDiv.appendChild(cardDiv);
        }
    };
    return Player;
}());
// 假设这是从服务器获取的玩家数据
var players = [
    new Player('player1', ['*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*']),
    new Player('player2', ['*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*']),
    new Player('player3', ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13']),
    new Player('player4', ['*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*', '*'])
];
// 更新所有玩家的手牌
for (var _i = 0, players_1 = players; _i < players_1.length; _i++) {
    var player = players_1[_i];
    player.updateHand();
}
