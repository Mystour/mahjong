document.addEventListener('DOMContentLoaded', function() {
    var playMusicButton = document.getElementById('playMusicButton');
    if (playMusicButton) {
        playMusicButton.addEventListener('click', function() {
            var audioElement = document.getElementById('bgMusic');
            if (audioElement) {
                audioElement.play();
            }
        });
    }
});