// Initialize variables
var stompClient = null;
var socket = new SockJS('/room');
var roomCode;
var username;
var currentUserIndex;

// Import the Player class
import { Player } from './Player.js';

// Function to establish a WebSocket connection, maintain heartbeat, and define subscriptions
function connect() {
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setTimeout(() => {
            console.log('Connected: ' + frame);

            // Start a heartbeat interval to keep the connection alive
            var heartbeatInterval = setInterval(function() {
                console.log('Sending heartbeat');
                stompClient.send("/app/heartbeat", {}, "");
            }, 10000); // Send a heartbeat every 10 seconds

            // Listen for connection close event, clear the heartbeat interval, and reconnect
            stompClient.onclose = function() {
                console.log('WebSocket connection closed');
                clearInterval(heartbeatInterval);
                reconnect();
            };
            stompClient.disconnect = function() {
                console.log('WebSocket connection closed');
                clearInterval(heartbeatInterval);
                reconnect();
            };

            // Subscribe to the '/game/room' topic
            stompClient.subscribe('/game/room/' + roomCode, function (message) {
                console.log('Message received: ' + message.body);
                // Update all players' hand cards upon receiving a message
                updateAllPlayersHandCards();
            });

            // Subscribe to the '/topic/roomDataChanged' topic for server-pushed data change notifications
            stompClient.subscribe('/topic/roomDataChanged/' + roomCode, function (message) {
                console.log('Message received: ' + message.body);
                // Update all players' hand cards upon receiving a message
                updateAllPlayersHandCards();
            });

            // Subscribe to the '/topic/currentPlayer' topic
            stompClient.subscribe('/topic/currentPlayer/' + roomCode, function (message) {
                console.log('Message received: ' + message.body);
                // Update the displayed player name upon receiving a message
                let currentPlayingPlayer = message.body;
                updatePlayerNames(currentPlayingPlayer);
            });
        }, 50);
    }, function (error) {
        console.log('Error in connecting WebSocket: ' + error);
        // Trigger reconnection upon connection failure
        reconnect();
    });
}

// Function to reconnect to the WebSocket after a delay
function reconnect() {
    setTimeout(function () {
        console.log('Reconnecting to WebSocket...');
        connect();
    }, 5000); // Reconnect after 5 seconds
}

// Initialize connection
connect();

/**
 * Asynchronously updates all players' hand cards by fetching data from the server.
 */
async function updateAllPlayersHandCards() {
    try {
        // Fetch hand cards, discards, shown cards, and discarding tile from the server
        var cards = await $.ajax({ url: '/getAllPlayersHandCards/' + roomCode, type: 'GET' });
        var discards = await $.ajax({ url: '/getAllPlayersDiscards/' + roomCode, type: 'GET' });
        var showcards = await $.ajax({ url: '/getAllPlayersShowedCards/' + roomCode, type: 'GET' });
        var discardingTile = await $.ajax({ url: '/getDiscardingTile/' + roomCode, type: 'GET' });

        // Define directions for players
        var directions = ['south', 'east', 'north', 'west'];
        var condition = await $.ajax({ url: '/getAllPlayersCondition/' + roomCode, type: 'GET' });
        var index = await $.ajax({ url: '/getPlayerIndex/' + roomCode, type: 'GET' });

        // Loop through each player to update their hand cards
        for (var i = 0; i < 4; i++) {
            var direction = directions[i];
            console.log('Direction:', direction);

            var isturn = index[0] === (currentUserIndex + i) % 4;
            var ischecking = index[1] === (currentUserIndex + i) % 4;
            var username = await $.ajax({ url: '/api/username', type: 'GET' });

            // Create a new Player object and update their hand cards
            var player = new Player(roomCode, 'player' + (i + 1), username,
                cards[(currentUserIndex + i) % 4], discards[(currentUserIndex + i) % 4],
                showcards[(currentUserIndex + i) % 4], discardingTile[(currentUserIndex + i) % 4],
                condition[(currentUserIndex + i) % 4], isturn, ischecking);
            if (i === 0) {
                await player.updateHandWithImagesSelf();
            } else {
                player.updateHandWithImagesOther();
            }
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// jQuery function to execute when the DOM is ready
$(function () {
    roomCode = window.location.pathname.split('/')[2];
    console.log("roomcode: " + roomCode);
    $.ajax({
        url: '/api/username',
        type: 'GET',
        success: function (data) {
            username = data;
            // Fetch all usernames in the room
            $.ajax({
                url: '/api/roomUsers/' + roomCode,
                type: 'GET',
                success: function (usernames) {
                    currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);
                    // Update all players' hand cards
                    updateAllPlayersHandCards();
                }
            });
        }
    });
});
