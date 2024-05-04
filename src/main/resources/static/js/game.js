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
$(function () {
    var roomCode = window.location.pathname.split('/')[2];
    var playerID = parseInt(window.location.pathname.split('/')[3]);
    $.ajax({
        url: '/getAllPlayersHandCards/' + roomCode + '/' + playerID, // Add room code and playerID to the URL
        type: 'GET',
        success: function (data) {
            // Rotate the array so that the current player is at the first position
            var rotatedData = data.slice(playerID - 1).concat(data.slice(0, playerID - 1));
            var directions = ['south', 'east', 'north', 'west']; // Change the order of directions to counter-clockwise
            for (var i = 0; i < rotatedData.length; i++) {
                var player = new Player('player' + (i + 1), rotatedData[i]);
                player.updateHandWithImages();
                // Add the direction class to the player's element
                var playerDiv = document.getElementById(player.id);
                playerDiv.classList.add(directions[i % 4]);
            }
        }
    });
    // Connect to the WebSocket server
    var socket = new SockJS('/room');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        // Subscribe to '/user/queue/redirect', which is where we send messages from the server
        stompClient.subscribe('/user/queue/redirect', function (message) {
            // When a message is received, redirect to the new URL
            window.location.href = message.body;
            // log the message in the console
            console.log("Received WebSocket message: " + message.body);
        });
    });
});
