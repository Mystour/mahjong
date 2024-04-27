var socket = new SockJS('/room');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe('/topic/room', function (message) {
        var count = parseInt(message.body);
        console.log("Received WebSocket message: " + count); // Add this line to see the message in the browser console
        localStorage.setItem('playerCount', count.toString()); // Store the count in localStorage
        updateProgressBar(count);
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
    var count = parseInt(localStorage.getItem('playerCount') || '0');
    updateProgressBar(count);
};
