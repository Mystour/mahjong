// Establish a SockJS connection to the /room endpoint
var socket = new SockJS('/room');
// Create a STOMP client over the SockJS connection
var stompClient = Stomp.over(socket);
// Extract the room code from the URL
var roomCode = window.location.pathname.split('/')[2];

// Initialize arrays to store usernames and scores
var usernames = [];
var scores = [];

// Connect to the STOMP server
stompClient.connect({}, function (options) {
    // Subscribe to the /topic/roomDataChanged topic for the specified room
    stompClient.subscribe('/topic/roomDataChanged/' + roomCode, async function (message) {
        console.log('Message received: ' + message.body);

        // Fetch updated usernames and scores upon receiving a message
        try {
            usernames = await fetch(`/api/roomUsers/${roomCode}`).then(res => res.json());
            scores = await fetch(`/getAllPlayersScores/${roomCode}`).then(res => res.json());

            console.log('Updated usernames:', usernames);
            console.log('Updated scores:', scores);
        } catch (error) {
            console.error('Error fetching updated data:', error);
        }
    });
});

// Event listener for DOMContentLoaded to initialize UI elements and data
document.addEventListener("DOMContentLoaded", async function () {
    // Create a container for the toggle button
    const buttonContainer = document.createElement('div');
    buttonContainer.className = 'button-container';

    // Create the toggle button for showing/hiding the score table
    const toggleButton = document.createElement('button');
    toggleButton.textContent = 'Score Table';
    buttonContainer.appendChild(toggleButton);

    // Append the button container to the document body
    document.body.appendChild(buttonContainer);

    // Create a container for the score table
    const tableContainer = document.createElement('div');
    tableContainer.id = 'table-container';
    tableContainer.className = 'table-container hidden'; // Initially hidden
    document.body.appendChild(tableContainer);

    // Add an event listener to the toggle button to show/hide the score table
    toggleButton.addEventListener('click', function () {
        tableContainer.classList.toggle('hidden');
        if (!tableContainer.classList.contains('hidden')) {
            tableContainer.classList.add('visible'); // Add visible class when table is shown
            updateTable(tableContainer); // Update table content when shown
        } else {
            tableContainer.classList.remove('visible'); // Remove visible class when table is hidden
        }
    });

    // Fetch initial usernames and scores
    try {
        usernames = await fetch(`/api/roomUsers/${roomCode}`).then(res => res.json());
        scores = await fetch(`/getAllPlayersScores/${roomCode}`).then(res => res.json());

        console.log('Initial usernames:', usernames);
        console.log('Initial scores:', scores);
    } catch (error) {
        console.error('Error fetching initial data:', error);
    }
});

/**
 * Creates and populates a table with usernames and scores.
 *
 * @param {HTMLElement} container - The container element to append the table to.
 * @param {Array} usernames - The array of usernames.
 * @param {Array} scores - The array of scores.
 */
function createTable(container, usernames, scores) {
    const table = document.createElement('table');
    table.id = 'ScoreTable';
    table.className = 'score-table';

    // Create table header
    const headerRow = document.createElement('tr');
    const headers = ['Name', 'Score'];
    headers.forEach(headerText => {
        const header = document.createElement('th');
        header.textContent = headerText;
        headerRow.appendChild(header);
    });
    table.appendChild(headerRow);

    // Create table rows with usernames and scores
    for (let i = 0; i < Math.max(usernames.length, scores.length); i++) {
        const row = document.createElement('tr');
        headers.forEach((_, j) => {
            const cell = document.createElement('td');
            cell.textContent = j === 1 ? (scores[i] || "") : (usernames[i] || ""); // Handle undefined values
            row.appendChild(cell);
        });
        table.appendChild(row);
    }

    container.appendChild(table);
}

/**
 * Updates the score table with the latest usernames and scores.
 *
 * @param {HTMLElement} container - The container element to update.
 */
function updateTable(container) {
    container.innerHTML = ''; // Clear the container
    createTable(container, usernames, scores); // Recreate the table with updated data
}

// Define CSS styles for the UI elements
const style = `
  .table-container {
    position: absolute;
    top: 20%;
    right: 10%;
    padding: 10px;
    border-radius: 5px;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  }

  .table-container.hidden {
    display: none;
  }

  .table-container.visible {
    background-color: lightgray;
  }

  .score-table {
    margin: 0 auto;
    border-collapse: collapse;
  }

  .score-table th,
  .score-table td {
    padding: 8px;
    border: 1px solid black;
  }

  .button-container {
    position: absolute;
    top: 10px;
    right: 10px;
  }
`;

// Append the CSS styles to the document head
const styleTag = document.createElement('style');
styleTag.textContent = style;
document.head.appendChild(styleTag);

