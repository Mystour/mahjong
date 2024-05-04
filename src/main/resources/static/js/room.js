var socket = new SockJS('/room');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function () {
    stompClient.subscribe('/topic/room', function (message) {
        var data = JSON.parse(message.body);
        var roomCode = data.roomCode;
        var count = data.count;
        console.log("Received WebSocket message: " + count); // Add this line to see the message in the browser console
        localStorage.setItem(roomCode, count.toString()); // Store the count in localStorage with roomCode as the key
        // If it is the current room, update the progress bar
        var path = window.location.pathname; // Get the path from the URL
        var parts = path.split('/'); // Split the path into parts
        var currentRoomCode = parts[parts.length - 1]; // Get the last part of the path, which is the roomCode
        if (currentRoomCode === roomCode) {
            updateProgressBar(count);

            // If the count is 4, redirect to the results page
            if (count === 4) {
                window.location.href = '/game/' + roomCode;
            }
        }
    });
});
// Function to update the progress bar
function updateProgressBar(count) {
    var progressBar = document.getElementById('progressBar');
    if (progressBar) {
        progressBar.style.width = (count / 4 * 100) + '%';
        progressBar.setAttribute('aria-valuenow', count.toString());
    }
}
// When the page loads, get the count from localStorage and update the progress bar
window.onload = function () {
    var path = window.location.pathname; // Get the path from the URL
    var parts = path.split('/'); // Split the path into parts
    var roomCode = parts[parts.length - 1]; // Get the last part of the path, which is the roomCode
    var count = parseInt(localStorage.getItem(roomCode) || '0');
    updateProgressBar(count);
};
