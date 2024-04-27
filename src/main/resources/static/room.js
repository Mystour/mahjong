var socket = new SockJS('/room');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe('/topic/room', function (message) {
        var count = parseInt(message.body);
        console.log("Received WebSocket message: " + count); // Add this line to see the message in the browser console
        var progressBar = document.getElementById('progressBar');
        if (progressBar) {
            progressBar.style.width = (count / 4 * 100) + '%';
            progressBar.setAttribute('aria-valuenow', count.toString());
        }
    });
});
