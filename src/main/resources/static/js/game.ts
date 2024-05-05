declare var $: any;

class Player {
    constructor(public id: string, public cards: string[]) {}

    updateHandWithImages() {
        if (!Array.isArray(this.cards)) {
            console.error('Player cards is not an array:', this.cards);
            return;
        }
        let handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';
        for (let i = 0; i < this.cards.length; i++) {
            let img = document.createElement('img');
            img.src = this.cards[i];
            img.classList.add('card', 'small-card');  // Add classes to the img element
            handDiv.appendChild(img);
        }
    }
}

$(function() {
    const roomCode = window.location.pathname.split('/')[2];
    let username: string;

    // First, get the current username
    $.ajax({
        url: '/api/username',
        type: 'GET',
        success: function(data: string) {
            username = data;

            // Then, get all players' usernames
            $.ajax({
                url: '/api/roomUsers/' + roomCode,
                type: 'GET',
                success: function(usernames: string[]) {
                    // Find the index of the current user in the returned data
                    let currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);

                    // Then, get all players' hand cards
                    $.ajax({
                        url: '/getAllPlayersHandCards/' + roomCode,
                        type: 'GET',
                        success: function(data: any[]) {
                            const directions = ['south', 'east', 'north', 'west'];  // Change the order of directions to counter-clockwise

                            for (let i = 0; i < 4; i++) {
                                // Calculate the direction based on the index of the current user
                                let direction = directions[i]
                                console.log('Direction:', direction);

                                let player = new Player('player' + ((currentUserIndex + i) % 4 + 1), data[(currentUserIndex + i) % 4]);
                                player.updateHandWithImages();
                                // Add the direction class to the player's element
                                let playerDiv = document.getElementById(player.id);
                                playerDiv.classList.add('hand', direction);  // Add classes to the player's element
                            }
                        }
                    });
                }
            });
        }
    });
});