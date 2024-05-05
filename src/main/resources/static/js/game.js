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
$(function () {
    var roomCode = window.location.pathname.split('/')[2];
    var username;
    // First, get the current username
    $.ajax({
        url: '/api/username',
        type: 'GET',
        success: function (data) {
            username = data;
            // Then, get all players' usernames
            $.ajax({
                url: '/api/roomUsers/' + roomCode,
                type: 'GET',
                success: function (usernames) {
                    // Find the index of the current user in the returned data
                    var currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);
                    // Then, get all players' hand cards
                    $.ajax({
                        url: '/getAllPlayersHandCards/' + roomCode,
                        type: 'GET',
                        success: function (data) {
                            var directions = ['south', 'east', 'north', 'west']; // Change the order of directions to counter-clockwise
                            for (var i = 0; i < 4; i++) {
                                // Calculate the direction based on the index of the current user
                                var direction = directions[i];
                                console.log('Direction:', direction);
                                var player = new Player('player' + ((currentUserIndex + i) % 4 + 1), data[(currentUserIndex + i) % 4]);
                                player.updateHandWithImages();
                                // Add the direction class to the player's element
                                var playerDiv = document.getElementById(player.id);
                                playerDiv.classList.add('hand', direction); // Add classes to the player's element
                            }
                        }
                    });
                }
            });
        }
    });
});
