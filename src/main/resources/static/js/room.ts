declare var SockJS: any;
declare var Stomp: any;

let socket = new SockJS('/room');
let stompClient = Stomp.over(socket);

stompClient.connect({}, function() {
    stompClient.subscribe('/topic/room', function(message: any) {
        let data = JSON.parse(message.body);
        let roomCode = data.roomCode;
        let count = data.count;
        console.log("Received WebSocket message: " + count);  // Add this line to see the message in the browser console
        localStorage.setItem(roomCode, count.toString());  // Store the count in localStorage with roomCode as the key

        // If it is the current room, update the progress bar
        let path = window.location.pathname;  // Get the path from the URL
        let parts = path.split('/');  // Split the path into parts
        let currentRoomCode = parts[parts.length - 1];  // Get the last part of the path, which is the roomCode
        if (currentRoomCode === roomCode) {
            updateProgressBar(count);
        }
    });
});

// Function to update the progress bar
function updateProgressBar(count: number) {
    let progressBar: HTMLElement | null = document.getElementById('progressBar');
    if (progressBar) {
        progressBar.style.width = (count / 4 * 100) + '%';
        progressBar.setAttribute('aria-valuenow', count.toString());
    }
}

// When the page loads, get the count from localStorage and update the progress bar
window.onload = function() {
    let path = window.location.pathname;  // Get the path from the URL
    let parts = path.split('/');  // Split the path into parts
    let roomCode = parts[parts.length - 1];  // Get the last part of the path, which is the roomCode
    let count = parseInt(localStorage.getItem(roomCode) || '0');
    updateProgressBar(count);
};