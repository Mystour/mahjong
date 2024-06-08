export class Player {
    /**
     * Constructor to initialize a Player object.
     * @param {string} roomcode - The room code.
     * @param {string} id - The player's ID.
     * @param {string} username - The player's username.
     * @param {Array} cards - The player's hand cards.
     * @param {Array} discards - The player's discarded cards.
     * @param {Array} showcards - The player's exposed cards.
     * @param {Array} discardingTile - The player's currently discarding tile.
     * @param {Array} condition - The player's action conditions.
     * @param {boolean} isturn - Whether it is the player's turn.
     * @param {boolean} ischecking - Whether the player is checking.
     */
    constructor(roomcode, id, username, cards, discards, showcards, discardingTile, condition, isturn, ischecking) {
        this.roomcode = roomcode;
        this.id = id;
        this.username = username;
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
        this.isturn = isturn;
        this.ischecking = ischecking;
    }

    /**
     * Sends the player's username to the server.
     */
    sendUsernameToServer() {
        if (stompClient && stompClient.connected) {
            console.log({ 'username': this.username });
            const message = JSON.stringify({ 'username': this.username });
            stompClient.send("/app/updateCurrentPlayer/" + this.roomcode, {}, message);
        } else {
            console.error("WebSocket connection is not established.");
        }
    }

    /**
     * Creates a button element with specified text and click event.
     * @param {HTMLElement} handDiv - The container element for the button.
     * @param {string} text - The button text.
     * @param {Function} onClick - The click event handler.
     */
    static createButton(handDiv, text, onClick) {
        const button = document.createElement('button');
        button.innerHTML = text;
        setButtonStyle(button);
        button.addEventListener('click', onClick);
        handDiv.appendChild(button);
    }

    /**
     * Creates and attaches a discard button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     * @param {Object} selectedImgObj - The object holding the selected image.
     */
    perform_discard(handDiv, selectedImgObj) {
        Player.createButton(handDiv, 'Discard', () => {
            if (selectedImgObj.img) {
                const imageName = selectedImgObj.img.src.split("/").pop().split(".")[0];
                stompClient.send("/app/drawTile/" + this.roomcode, {}, JSON.stringify({ 'roomCode': this.roomcode, 'userName': this.username, 'tile': imageName }));
            } else {
                console.log('No card selected.');
            }
        });
    }

    /**
     * Creates and attaches a chow button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     */
    perform_chow(handDiv) {
        Player.createButton(handDiv, 'Chow', () => {
            fetch('/otherPlayerChow/' + this.roomcode, {
                method: 'GET'
            }).catch(error => console.error('Error:', error));
        });
    }

    /**
     * Creates and attaches a pung button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     */
    perform_pung(handDiv) {
        Player.createButton(handDiv, 'Pung', () => {
            fetch('/otherPlayerPung/' + this.roomcode, {
                method: 'GET'
            }).catch(error => console.error('Error:', error));
        });
    }

    /**
     * Creates and attaches a kong button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     */
    perform_kong(handDiv) {
        const url = this.isturn ? '/currentPlayerKong/' + this.roomcode : '/otherPlayerKong/' + this.roomcode;
        Player.createButton(handDiv, 'Kong', () => {
            fetch(url, { method: 'GET' }).catch(error => console.error('Error:', error));
        });
    }

    /**
     * Creates and attaches a mahjong button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     */
    perform_mahjong(handDiv) {
        const url = this.isturn ? '/currentPlayerMahjong/' + this.roomcode : '/otherPlayerMahjong/' + this.roomcode;
        Player.createButton(handDiv, 'Mahjong', () => {
            fetch(url, { method: 'GET' }).catch(error => console.error('Error:', error));
        });
    }

    /**
     * Creates and attaches a skip button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     */
    perform_skip(handDiv) {
        Player.createButton(handDiv, 'Skip', () => skipAction());
    }

    /**
     * Creates and attaches a play again button to the specified element.
     * @param {HTMLElement} handDiv - The container element for the button.
     */
    perform_playagain(handDiv) {
        Player.createButton(handDiv, 'Play Again', () => {
            fetch('/endGame/' + this.roomcode, { method: 'GET' }).catch(error => console.error('Error:', error));
        });
    }

    /**
     * Updates the player's hand with images, highlighting the player's own cards.
     */
    async updateHandWithImagesSelf() {
        if (!Array.isArray(this.cards)) {
            console.error('Player cards is not an array:', this.cards);
            return;
        }

        const handDiv = document.getElementById('player1');
        handDiv.innerHTML = '';
        const selectedImgObj = { img: null };

        // Draw the player's currently discarding tile
        for (let i = 0; i < this.discardingTile.length; i++) {
            let img = document.createElement('img');
            img.src = this.discardingTile[i];
            img.classList.add('card', 'small-card1');
            img.style.transform = 'translateY(-40px) translateX(40px)';
            handDiv.appendChild(img);
        }

        // Draw the player's hand cards
        for (let i = 0; i < this.cards.length; i++) {
            let img = document.createElement('img');
            img.src = this.cards[i];
            img.classList.add('card', 'small-card');
            img.addEventListener('click', function (event) {
                if (selectedImgObj.img === img) {
                    img.style.transform = 'translateY(0)';
                    selectedImgObj.img = null;
                } else {
                    if (selectedImgObj.img) {
                        selectedImgObj.img.style.transform = 'translateY(0)';
                    }
                    img.style.transform = 'translateY(-20px)';
                    selectedImgObj.img = img;
                }
            });
            handDiv.appendChild(img);
        }

        // Update the discard pile
        const discardPileDiv = document.getElementById('discardPile1');
        discardPileDiv.innerHTML = '';
        for (let i = 0; i < this.discards.length; i++) {
            let img = document.createElement('img');
            img.src = this.discards[i];
            img.classList.add('card', 'small-card1');
            discardPileDiv.appendChild(img);
        }

        // Draw the player's exposed cards
        for (let i = 0; i < this.showcards.length; i++) {
            let img = document.createElement('img');
            img.src = this.showcards[i];
            img.classList.add('card', 'small-card2');
            handDiv.appendChild(img);
        }

        // Add action buttons based on the player's state
        if (!this.hasMahjong) {
            if (this.isturn && this.ischecking) {
                this.perform_discard(handDiv, selectedImgObj);
                if (this.canKong) {
                    this.perform_kong(handDiv);
                }
                if (this.canMahjong) {
                    this.perform_mahjong(handDiv);
                }
                const triangle = document.createElement('div');
                triangle.classList.add('triangle');
                handDiv.appendChild(triangle);
            } else if (this.ischecking) {
                this.perform_skip(handDiv);
                if (this.canChow) {
                    this.perform_chow(handDiv);
                }
                if (this.canPung) {
                    this.perform_pung(handDiv);
                }
                if (this.canKong) {
                    this.perform_kong(handDiv);
                }
                if (this.canMahjong) {
                    this.perform_mahjong(handDiv);
                }
                const circle = document.createElement('div');
                circle.classList.add('circle');
                handDiv.appendChild(circle);
            }
        } else {
            this.perform_playagain(handDiv);
        }
        if (this.ischecking) {
            this.sendUsernameToServer();
        }
    }

    /**
     * Updates the player's hand with images for other players, displaying back-facing or side-facing cards.
     */
    updateHandWithImagesOther() {
        if (!Array.isArray(this.cards)) {
            console.error('Player cards is not an array:', this.cards);
            return;
        }

        const handDiv = document.getElementById(this.id);
        handDiv.innerHTML = '';

        // Determine the image to use based on the player's direction
        let cardImage;
        switch (this.id) {
            case 'player2':
                cardImage = '/images/TileType2/east.png';
                break;
            case 'player3':
                cardImage = '/images/TileType2/north.png';
                break;
            case 'player4':
                cardImage = '/images/TileType2/west.png';
                break;
            default:
                console.error('Invalid player id:', this.id);
                return;
        }

        // Draw the player's currently discarding tile
        for (let i = 0; i < this.discardingTile.length; i++) {
            let img = document.createElement('img');
            img.src = this.discardingTile[i];
            img.classList.add('card', 'small-card1');
            if (this.id === 'player2') {
                img.style.transform = 'translateY(-40px) translateX(-40px) rotate(15deg)';
            } else if (this.id === 'player3') {
                img.style.transform = 'translateY(-40px) translateX(40px)';
            } else {
                img.style.transform = 'translateY(-40px) translateX(40px) rotate(-15deg)';
            }
            handDiv.appendChild(img);
        }

        // Draw the player's hand cards
        for (let i = 0; i < this.cards.length; i++) {
            let img = document.createElement('img');
            img.src = cardImage;
            if (this.id === 'player2') {
                img.classList.add('card', 'small-card-side');
                img.style.transform = 'rotate(105deg)';
            } else if (this.id === 'player3') {
                img.classList.add('card', 'small-card-back');
                img.style.transform = 'rotate(180deg)';
            } else {
                img.classList.add('card', 'small-card-side');
                img.style.transform = 'rotate(-105deg)';
            }
            handDiv.appendChild(img);
        }

        // Update the discard pile
        const discardPileDiv = document.getElementById('discardPile' + this.id.split('player')[1]);
        discardPileDiv.innerHTML = '';
        for (let i = 0; i < this.discards.length; i++) {
            let img = document.createElement('img');
            img.src = this.discards[i];
            img.classList.add('card', 'small-card1');
            discardPileDiv.appendChild(img);
        }

        // Draw the player's exposed cards
        for (let i = 0; i < this.showcards.length; i++) {
            let img = document.createElement('img');
            img.src = this.showcards[i];
            img.classList.add('card', 'small-card2');
            handDiv.appendChild(img);
        }

        // Add indicators for the player's turn or checking state
        if (this.isturn) {
            const triangle = document.createElement('div');
            triangle.classList.add('triangle');
            handDiv.appendChild(triangle);
        } else if (this.ischecking) {
            const circle = document.createElement('div');
            circle.classList.add('circle');
            handDiv.appendChild(circle);
        }
    }
}


