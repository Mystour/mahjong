declare var $: any;

class Player {
    constructor(public id: string, public cards: string[]) {}

    updateHandWithImages() {
        if (!Array.isArray(this.cards)) {
            console.error('Player cards is not an array:', this.cards);
            return;
        }
        let handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';
        for (let i = 0; i < this.cards.length; i++) {
            let img = document.createElement('img');
            img.src = this.cards[i];
            img.classList.add('card', 'small-card');  // Add classes to the img element
            handDiv.appendChild(img);
        }
    }
}

$(function() {
    const roomCode = window.location.pathname.split('/')[2];
    let username: string;

    // First, get the current username
    $.ajax({
        url: '/api/username',
        type: 'GET',
        success: function(data: string) {
            username = data;

            // Then, get all players' usernames
            $.ajax({
                url: '/api/roomUsers/' + roomCode,
                type: 'GET',
                success: function(usernames: string[]) {
                    // Find the index of the current user in the returned data
                    let currentUserIndex = usernames.indexOf(username);
                    console.log('Current user ' + username + ' index:', currentUserIndex);

                    // Then, get all players' hand cards
                    $.ajax({
                        url: '/getAllPlayersHandCards/' + roomCode,
                        type: 'GET',
                        success: function(data: any[]) {
                            for (let i = 0; i < 4; i++) {
                                let player = new Player('player' +(i + 1), data[(currentUserIndex + i) % 4]);
                                player.updateHandWithImages();
                            }
                        }
                    });
                }
            });
        }
    });
});

function createTable(containerId) {
    var tableContainer = document.getElementById(containerId);

    // 创建表格元素
    var table = document.createElement("table");
    table.id = "ScoreTable";
    table.className = "score-table"; // 添加类名
    table.style.display = "none";

    // 创建标题行
    var titleRow = document.createElement("tr");
    titleRow.className = "table-title";
    var titleCell = document.createElement("th");
    titleCell.colSpan = Number("3");
    titleCell.textContent = "Score Table";
    titleRow.appendChild(titleCell);
    table.appendChild(titleRow);

    // 创建表头行
    var headerRow = document.createElement("tr");
    var headers = ["Name", "Banker", "Score"];
    headers.forEach(function(headerText) {
        var header = document.createElement("th");
        header.textContent = headerText;
        headerRow.appendChild(header);
    });
    table.appendChild(headerRow);

    // 创建表格数据行
    var numRows = 4;
    for (var i = 0; i < numRows; i++) {
        var row = document.createElement("tr");
        for (var j = 0; j < headers.length; j++) {
            var cell = document.createElement("td");
            if (j === 2) {
                cell.textContent = "0";
            } else {
                cell.textContent = "Player";
            }
            row.appendChild(cell);
        }
        table.appendChild(row);
    }


    tableContainer.appendChild(table);
}



createTable("table-container");

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
        (<HTMLElement>document.querySelector(".table-title")).style.display = "table-row";
    } else {
        table.style.display = "none"; // 如果表格可见，则隐藏它
        (<HTMLElement>document.querySelector(".table-title")).style.display = "none";
    }
}

