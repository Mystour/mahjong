declare var $: any;
declare var SockJS: any;
declare var Stomp: any;

class Player {
    constructor(public id: string, public cards: string[]) {}

    updateHandWithImages() {
        let handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';
        for (let i = 0; i < this.cards.length; i++) {
            let img = document.createElement('img');
            img.src = this.cards[i];
            handDiv.appendChild(img);
        }
    }
}

$(function() {
    const roomCode = window.location.pathname.split('/')[2];
    const playerID: number = parseInt(window.location.pathname.split('/')[3]);

    $.ajax({
        url: '/getAllPlayersHandCards/' + roomCode + '/' + playerID,  // Add room code and playerID to the URL
        type: 'GET',
        success: function(data: any[]) {
            // Rotate the array so that the current player is at the first position
            const rotatedData = data.slice(playerID - 1).concat(data.slice(0, playerID - 1));

            const directions = ['south', 'east', 'north', 'west'];  // Change the order of directions to counter-clockwise
            for (let i = 0; i < rotatedData.length; i++) {
                let player = new Player('player' + (i + 1), rotatedData[i]);
                player.updateHandWithImages();
                // Add the direction class to the player's element
                let playerDiv = document.getElementById(player.id);
                playerDiv.classList.add(directions[i % 4]);
            }
        }
    });

    // Connect to the WebSocket server
    const socket = new SockJS('/room');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function() {
        // Subscribe to '/user/queue/redirect', which is where we send messages from the server
        stompClient.subscribe('/user/queue/redirect', function(message: { body: string; }) {
            // When a message is received, redirect to the new URL
            window.location.href = message.body;
            // log the message in the console
            console.log("Received WebSocket message: " + message.body);
        });
    });
});