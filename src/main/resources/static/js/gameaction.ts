declare var $: any;
function chowAction() {
    // Check if chow is possible
    $.ajax({
        url: '/isCanChow',
        type: 'GET',
        //data: { roomCode, userName: username },
        success: function(canChow: boolean) {
            if (canChow) {
                // If chow is possible
                $.ajax({
                    url: '/performChow',
                    type: 'GET',
                    //data: { roomCode, userName: username, data },
                    success: function() {
                        // 处理接下来的工作



                    }
                });
            } else {
                console.error('Player cards can not chow')
            }
        }
    });
}

function pungAction() {
    // Check if chow is possible
    $.ajax({
        url: '/isCanPung',
        type: 'GET',
        //data: { roomCode, userName: username },
        success: function(canPung: boolean) {
            if (canPung) {
                // If chow is possible
                $.ajax({
                    url: '/performPung',
                    type: 'GET',
                    //data: { roomCode, userName: username, data },
                    success: function() {
                        // 处理接下来的工作



                    }
                });
            } else {
                console.error('Player cards can not pung')
            }
        }
    });
}

function kongAction() {
    // Check if chow is possible
    $.ajax({
        url: '/isCanKong',
        type: 'GET',
        //data: { roomCode, userName: username },
        success: function(canKong: boolean) {
            if (canKong) {
                // If chow is possible
                $.ajax({
                    url: '/performKong',
                    type: 'GET',
                    //data: { roomCode, userName: username, data },
                    success: function() {
                        // 处理接下来的工作



                    }
                });
            } else {
                console.error('Player cards can not kong')
            }
        }
    });
}

