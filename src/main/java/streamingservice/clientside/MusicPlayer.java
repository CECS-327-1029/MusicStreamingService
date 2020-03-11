package streamingservice.clientside;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer {

    enum PLAYER_MODE {PLAYING, PAUSED, NOT_STARTED, STOPPED}    // determines the mode the player is in at any moment
    private PLAYER_MODE playerMode = PLAYER_MODE.NOT_STARTED;   // initial mode of the player
    private final Object lock = new Object();                   // helps lock the thread that plays the music

    private Player player;
    private ProxyInterface proxy;

    private ArrayList<Tuple2<String, String>> queue;    // list of songs
    private int currentlyPlayingSong = -1;              // specifies which song is playing. -1 means no song is playing

    public MusicPlayer(ProxyInterface proxy) { this.proxy = proxy; }

    public void setQueue(ArrayList<Tuple2<String, String>> queue) { this.queue = queue; }

    // sets the player mode to STOPPED which in turn stops the music
    public void stop() {
        synchronized (lock) {
            playerMode = PLAYER_MODE.STOPPED;
            lock.notifyAll();
            currentlyPlayingSong = -1;
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

    public void play(Tuple2<String, String> song) throws IOException, JavaLayerException {
        if (song != null) {
            if (player != null) {
                stop();
                player.close();
                currentlyPlayingSong = -1;
            }

            InputStream ris = new CECS327RemoteInputStream(song.getValue0(), proxy);
            player = new Player(ris);
            currentlyPlayingSong = queue.indexOf(song);
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

    public String next() throws IOException, JavaLayerException {
        if (currentlyPlayingSong + 1 < queue.size()) {
            currentlyPlayingSong++;
            play(queue.get(currentlyPlayingSong));
            return queue.get(currentlyPlayingSong).getValue0();
        }
        return "";
    }

    public String previous() throws IOException, JavaLayerException {
        if (currentlyPlayingSong - 1 >= 0) {
            currentlyPlayingSong--;
            play(queue.get(currentlyPlayingSong));
            return queue.get(currentlyPlayingSong).getValue0();
        }
        return "";
    }

    public void repeat() throws IOException, JavaLayerException {
        stop();
        play(queue.get(currentlyPlayingSong));
    }

    public ArrayList<Tuple2<String, String>> shuffle(int playingSongIndex) {
        if (queue.size() != 0) {
            Tuple2<String, String> song = queue.remove(playingSongIndex == -1 ? 0 : playingSongIndex);
            Collections.shuffle(queue);
            queue.add(0, song);
            currentlyPlayingSong = 0;
        }
        return queue;
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
    }
}
