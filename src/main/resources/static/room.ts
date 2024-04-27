declare var SockJS: any;
declare var Stomp: any;

let socket = new SockJS('/room');
let stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame: any) {
    stompClient.subscribe('/topic/room', function(message: any) {
        let count = parseInt(message.body);
        console.log("Received WebSocket message: " + count);  // Add this line to see the message in the browser console
        let progressBar: HTMLElement | null = document.getElementById('progressBar');
        if (progressBar) {
            progressBar.style.width = (count / 4 * 100) + '%';
            progressBar.setAttribute('aria-valuenow', count.toString());
        }
    });
});