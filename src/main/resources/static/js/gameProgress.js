// Establish a SockJS connection to the /room endpoint
var socket = new SockJS('/room');

// Create a STOMP client over the SockJS connection
var stompClient = Stomp.over(socket);

// Connect to the STOMP server
stompClient.connect({}, function (options) {
    // Subscribe to the /topic/game topic
    stompClient.subscribe('/topic/game', function (message) {
        // Parse the incoming message
        let data = JSON.parse(message.body);
        let roomCode = data.roomCode;
        let count = data.count;
        console.log("Received game progress message: " + count);

        // Store the progress count in localStorage
        localStorage.setItem(roomCode, count.toString());

        // Get the current room code from the URL
        let path = window.location.pathname;
        let parts = path.split('/');
        let currentRoomCode = parts[parts.length - 1];

        // Update the progress bar if the current room code matches the room code from the message
        if (currentRoomCode === roomCode) {
            updateProgressBar(count);
        }
    });
});

/**
 * Updates the progress bar based on the count.
 *
 * @param {number} count - The current progress count.
 */
function updateProgressBar(count) {
    let progressBar = document.getElementById('progressBar');
    if (progressBar) {
        progressBar.style.width = (count * 10) + '%';
        progressBar.setAttribute('aria-valuenow', count.toString());
    }
}

/**
 * Function to run on window load. Retrieves the room code from the URL,
 * fetches the progress count from localStorage, and updates the progress bar.
 */
window.onload = function() {
    let path = window.location.pathname;
    let parts = path.split('/');
    let roomCode = parts[parts.length - 1];
    let count = parseInt(localStorage.getItem(roomCode) || '0');
    updateProgressBar(count);
};

/**
 * Updates the playing player's name on the UI.
 *
 * @param {string} playingPlayer - JSON string containing the playing player's details.
 */
window.updatePlayerNames = function(playingPlayer) {
    let jsonObject = JSON.parse(playingPlayer);
    let username = jsonObject.username;
    document.getElementById('playingPlayer').textContent = 'Playing player: ' + (username || 'None');
}

/**
 * Sends a request to skip the current player's action.
 */
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

/**
 * Sends a request to start the game progress countdown.
 */
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

