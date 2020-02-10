package streamingservice.music;

import java.io.*;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;

public class MusicPlayer {

    // determines the mode the player is in at any moment
    enum PLAYER_MODE {PLAYING, PAUSED, NOT_STARTED, STOPPED}

    // initial mode of the player
    private PLAYER_MODE playerMode = PLAYER_MODE.NOT_STARTED;

    private Player player;

    private boolean hasFinished = false;

    // helps lock the thread that plays the music
    private final Object lock = new Object();

    private JButton next;

    public MusicPlayer(JButton next) {
        this.next = next;
    }

    // sets the player mode to STOPPED which in turn stops the music
    public void stop() {
        synchronized (lock) {
            playerMode = PLAYER_MODE.STOPPED;
            lock.notifyAll();
        }
    }

    // sets the player mode to PAUSED which in turn pauses the music
    public void pause() {
        synchronized (lock) {
            if (playerMode == PLAYER_MODE.PLAYING) {
                playerMode = PLAYER_MODE.PAUSED;
            }
        }
    }

    public void play(Song song) throws IOException, JavaLayerException {
        if (player != null) {
            stop();
            player.close();
        }
        InputStream is = new CECS327InputStream(FileHandler.getSongPath(song));
        player = new Player(is);
        synchronized (lock) {

            if (playerMode == PLAYER_MODE.NOT_STARTED || playerMode == PLAYER_MODE.STOPPED) {
                Thread thread = new Thread(this::playInternal);
                thread.setDaemon(true);
                thread.setPriority(Thread.MAX_PRIORITY);
                playerMode = PLAYER_MODE.PLAYING;
                thread.start();
            }
        }
    }

    // sets the player mode to PLAYING which in turn resumes the music
    // only when the player was in PAUSED mode
    public void resume() {
        synchronized (lock) {
            if (playerMode == PLAYER_MODE.PAUSED) {
                playerMode = PLAYER_MODE.PLAYING;
                lock.notifyAll();
            }
        }
    }

    // will loop until the last until the last frame is played
    private void playInternal() {
        while (playerMode != PLAYER_MODE.STOPPED) {
            try {
                if (!player.play(1)) {
                    break;
                }
            } catch (JavaLayerException e) {
                break;
            }
            synchronized (lock) {
                // will make the thread wait while the music is paused
                while (playerMode == PLAYER_MODE.PAUSED) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
        hasFinished = true;
        next.doClick();
        System.out.println(hasFinished);
    }

    private boolean isSongDone() { return hasFinished; }

}
