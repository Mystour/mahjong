
declare var $: any;

class Player {
    public canChow: boolean;
    public canPung: boolean;
    public canKong: boolean;
    public canMahjong: boolean;
    public hasMahjong: boolean;
    public isbanker: boolean;
    public isturn: boolean;
    public ischecking: boolean;
    usernames: string;

    constructor(
        public id: string,
        public cards: string[],
        public discards: string[],
        public showcards: string[],
        public discardingTile: string[],
        public condition: boolean[],
        isturn: boolean,
        ischecking: boolean,
        username: string,

    ) {
        this.id = id;
        this.cards = cards;
        this.discards = discards;
        this.showcards = showcards;
        this.discardingTile = discardingTile;
        this.condition = condition;

        this.canChow = this.condition[0];
        this.canPung = this.condition[1];
        this.canKong = this.condition[2];
        this.canMahjong = this.condition[3];
        this.hasMahjong = this.condition[4];
        this.isbanker = this.condition[5];
        this.isturn = isturn;
        this.ischecking = ischecking;
        this.usernames = usernames;
        this.scores = scores;
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

                            }
                        }
                    });
                }
            });
        }
    });
});

