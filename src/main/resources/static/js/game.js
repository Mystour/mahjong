"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
// @ts-ignore
var jquery_1 = require("jquery");
var Player = /** @class */ (function () {
    function Player(id, cards) {
        this.id = id;
        this.cards = cards;
    }
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
(0, jquery_1.default)(document).ready(function () {
    jquery_1.default.ajax({
        url: '/getAllPlayersHandCards',
        type: 'GET',
        success: function (data) {
            for (var i = 0; i < data.length; i++) {
                var player = new Player('player' + (i + 1), data[i]);
                player.updateHandWithImages();
            }
        }
    });
});
