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
                            for (let i = 0; i < 4; i++) {
                                let player = new Player('player'+(i + 1), data[(currentUserIndex + i) % 4]);
                                player.updateHandWithImages();
                            }
                        }
                    });
                }
            });
        }
    });
});

