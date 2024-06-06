var socket = new SockJS('/room');
var stompClient = Stomp.over(socket);

stompClient.connect({}, function() {
    stompClient.subscribe('/topic/game', function(message) {
        let data = JSON.parse(message.body);
        let roomCode = data.roomCode;
        let count = data.count;
        console.log("Received WebSocket message: " + count);
        localStorage.setItem(roomCode, count.toString());

        let path = window.location.pathname;
        let parts = path.split('/');
        let currentRoomCode = parts[parts.length - 1];
        if (currentRoomCode === roomCode) {
            updateProgressBar(count);
        }
    });
});

function updateProgressBar(count) {
    let progressBar = document.getElementById('progressBar');
    if (progressBar) {
        progressBar.style.width = (count * 10) + '%';
        progressBar.setAttribute('aria-valuenow', count.toString());
    }
}

window.onload = function() {
    let path = window.location.pathname;
    let parts = path.split('/');
    let roomCode = parts[parts.length - 1];
    let count = parseInt(localStorage.getItem(roomCode) || '0');
    updateProgressBar(count);
};

window.updatePlayerNames = function(playingPlayer) {
    document.getElementById('playingPlayer').textContent = 'Playing player: ' + (playingPlayer || 'None');
}

// function autoSkip() {
//     let timer = setInterval(function() {
//         let progressBar = document.getElementById('progressBar');
//         progressBar.value -= 1;
//         if (progressBar.value <= 0) {
//             skipAction();
//             clearInterval(timer);
//         }
//     }, 1000);
// }

window.skipAction = function() {
    let path = window.location.pathname;
    let parts = path.split('/');
    let roomCode = parts[parts.length - 1];

    fetch('/otherPlayerSkip/' + roomCode, {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

window.startGameProgressCountdown = function() {
    let path = window.location.pathname;
    let parts = path.split('/');
    let roomCode = parts[parts.length - 1];

    fetch('/startGameProgressCountdown/' + roomCode, {
        method: 'GET'
    })
        .catch(error => {
            console.error('Error:', error);
        });
    console.log('Game progress countdown started');
};
