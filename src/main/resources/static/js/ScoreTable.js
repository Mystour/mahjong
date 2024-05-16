declare var $: any;

$(function() {
    const roomCode = window.location.pathname.split('/')[2];
    let username: string;

    $.ajax({
        url: '/api/username',
        type: 'GET',
        success: function(data: string) {
            username = data;

            $.ajax({
                url: '/api/roomUsers/' + roomCode,
                type: 'GET',
                success: function(usernames: string[]) {
                    let currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);

                    $.ajax({
                        url: '/getAllPlayersScores/' + roomCode,
                        type: 'GET',
                        success: function(scores: any[]) {
                            createTable("table-container", username, scores)
                        }
                    });
                }
            });
        }
    });
});
function createTable(containerId, username: string[], scores: any[]) {
    var tableContainer = document.getElementById(containerId);

    var table = document.createElement("table");
    table.id = "ScoreTable";
    table.className = "score-table"; // 添加类名
    table.style.display = "none";

    var titleRow = document.createElement("tr");
    titleRow.className = "table-title";
    var titleCell = document.createElement("th");
    titleCell.colSpan = Number("2");
    titleCell.textContent = "Score Table";
    titleRow.appendChild(titleCell);
    table.appendChild(titleRow);

    var headerRow = document.createElement("tr");
    var headers = ["Name", "Score"];
    headers.forEach(function(headerText) {
        var header = document.createElement("th");
        header.textContent = headerText;
        headerRow.appendChild(header);
    });
    table.appendChild(headerRow);


    var numRows = 4;
    for (var i = 0; i < numRows; i++) {
        var row = document.createElement("tr");
        for (var j = 0; j < headers.length; j++) {
            var cell = document.createElement("td");
            if (j === 1) {
                cell.textContent = scores[i];
            } else {
                cell.textContent = username[i];
            }
            row.appendChild(cell);
        }
        table.appendChild(row);
    }


    tableContainer.appendChild(table);
}



// createTable("table-container");

var style = `
  .table-container {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
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

  .table-title {
    text-align: center;
    font-size: 24px;
    margin-bottom: 10px;
  }
`;
var styleTag = document.createElement("style");
styleTag.textContent = style;
document.head.appendChild(styleTag);

function toggleTable() {
    var table = document.getElementById("ScoreTable");
    if (table.style.display === "none") {
        table.style.display = "table"; // 如果表格被隐藏，则显示它
        document.querySelector(".table-title").style.display = "table-row";
        } else {
        table.style.display = "none"; // 如果表格可见，则隐藏它
        document.querySelector(".table-title").style.display = "none";
    }
}