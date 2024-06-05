declare var $: any;

$(async function () {
    const roomCode = window.location.pathname.split('/')[2];
    var username = await $.ajax({
        url: '/api/username',
        type: 'GET',
    });
    var scores = await $.ajax({
        url: '/getAllPlayersScores/' + roomCode,
        type: 'GET',
    });
    createTable("table-container", username, scores);
});

function createTable(containerId: string, username: string[], scores: any[]) {
    var tableContainer = document.getElementById(containerId);

    var table = document.createElement("table");
    table.id = "ScoreTable";
    table.className = "score-table"; // 添加类名

    // 创建表格标题
    var titleRow = document.createElement("tr");
    titleRow.className = "table-title";
    var titleCell = document.createElement("th");
    titleCell.colSpan = 2;
    titleCell.textContent = "Score Table";
    titleRow.appendChild(titleCell);
    table.appendChild(titleRow);

    // 创建表头
    var headerRow = document.createElement("tr");
    var headers = ["Name", "Score"];
    headers.forEach(function(headerText) {
        var header = document.createElement("th");
        header.textContent = headerText;
        headerRow.appendChild(header);
    });
    table.appendChild(headerRow);

    // 创建表格内容
    for (var i = 0; i < Math.max(username.length, scores.length); i++) {
        var row = document.createElement("tr");
        for (var j = 0; j < headers.length; j++) {
            var cell = document.createElement("td");
            if (j === 1) {
                cell.textContent = scores[i] || ""; // 处理分数未定义的情况
            } else {
                cell.textContent = username[i] || ""; // 处理用户名未定义的情况
            }
            row.appendChild(cell);
        }
        table.appendChild(row);
    }

    // 将表格添加到容器中
    tableContainer.appendChild(table);

    // 创建切换按钮
    var toggleButton = document.createElement("button");
    toggleButton.textContent = "Toggle Table";
    toggleButton.onclick = toggleTable;
    tableContainer.appendChild(toggleButton);
}

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
    display: none; // 默认隐藏表格
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

  .hidden {
    display: none;
  }
`;

var styleTag = document.createElement("style");
styleTag.textContent = style;
document.head.appendChild(styleTag);

function toggleTable() {
    var table = document.getElementById("ScoreTable");
    if (table.classList.contains("hidden")) {
        table.classList.remove("hidden"); // 使用类名切换可见性
        document.querySelector(".table-title").classList.remove("hidden");
    } else {
        table.classList.add("hidden");
        document.querySelector(".table-title").classList.add("hidden");
    }
}
