// 更新进度条和显示的玩家名
// 定义为全局函数
window.updateProgressBarAndPlayerNames = function(playingPlayer) {
    // 更新进度条
    var progressBar = document.getElementById('progressBar');
    if (playingPlayer) {
        progressBar.value = 10;
    } else {
        progressBar.value = 0;
    }
    autoSkip(); // 自动执行skip操作

    // 更新显示的玩家名
    document.getElementById('playingPlayer').textContent = 'Playing player: ' + (playingPlayer || 'None');
}

// 自动执行skip操作
function autoSkip() {
    // 设置一个10秒的定时器
    var timer = setInterval(function() {
        var progressBar = document.getElementById('progressBar');
        progressBar.value -= 1;
        if (progressBar.value <= 0) {
            // 在这里执行skip操作
            skipAction();
            clearInterval(timer);
        }
    }, 1000); // 每隔1秒减少进度条的值

    // // 当玩家做出选择时，清除定时器
    // // 注意：你需要在玩家做出选择的代码中调用这个函数
    // function playerMadeChoice() {
    //     clearInterval(timer);
    // }
}

window.skipAction = function() {
    fetch('/otherPlayerSkip/' + roomCode, {
        method: 'GET'
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}