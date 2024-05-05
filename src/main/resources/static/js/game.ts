declare var $: any;

class Player {
    constructor(public id: string, public cards: string[]) {}

    updateHandWithImages() {
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
    const playerID: number = parseInt(window.location.pathname.split('/')[3]);

    $.ajax({
        url: '/getAllPlayersHandCards/' + roomCode,
        type: 'GET',
        success: function(data: any[]) {
            const directions = ['south', 'east', 'north', 'west'];  // Change the order of directions to counter-clockwise

            for (let i = 0; i < data.length; i++) {
                // Calculate the direction based on the player's ID
                let direction = directions[(i - playerID + 4) % 4];  // Add 4 to ensure the result is positive

                let player = new Player('player' + (i + 1), data[i]);
                player.updateHandWithImages();
                // Add the direction class to the player's element
                let playerDiv = document.getElementById(player.id);
                playerDiv.classList.add('hand', direction);  // Add classes to the player's element
            }
        }
    });
});