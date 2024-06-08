package org.example.mahjong.sound;

import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SoundService {
    private final Logger LOGGER = Logger.getLogger(SoundService.class.getName());
    private Clip musicClip;
    private static SoundService instance = null;

    private SoundService() {
        musicClip = initSound("/sound/11.wav");
    }

    public static SoundService getInstance() {
        if (instance == null) {
            instance = new SoundService();
        }
        return instance;
    }

    private Clip initSound(String path) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
            Clip sound = AudioSystem.getClip();
            sound.open(audioInputStream);
            return sound;
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            LOGGER.log(Level.SEVERE, "Error with initializing sound.", ex);
            return null;
        }
    }

    public void playMusic() {
        if (musicClip != null) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        }
    }

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
        }
    }

    public boolean isMusicRunning() {
        return musicClip != null && musicClip.isRunning();
    }
}


