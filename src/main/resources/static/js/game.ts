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

$(document).ready(function() {
    $.ajax({
        url: '/getAllPlayersHandCards',
        type: 'GET',
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                let player = new Player('player' + (i + 1), data[i]);
                player.updateHandWithImages();
            }
        }
    });
});