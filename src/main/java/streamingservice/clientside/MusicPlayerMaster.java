package streamingservice.clientside;

import javazoom.jl.decoder.JavaLayerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public final class MusicPlayerMaster {

    private final MusicPlayer musicPlayer;
    private ArrayList<Tuple2<String, String>> queue;
    private int currentlyPlayingIndex = 0;
    private Tuple2<String, String> currentlyPlaying;

    public MusicPlayerMaster(ProxyInterface proxy) {
        musicPlayer = new MusicPlayer(proxy);
    }

    public void setQueue(ArrayList<Tuple2<String, String>> songsInQueue) { queue = songsInQueue; }
    
    public void play(Tuple2<String, String> song) {
        try {
            currentlyPlaying = song;
            musicPlayer.play(currentlyPlaying);
        } catch (JavaLayerException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void pause() { musicPlayer.pause(); }

    public void resume() { musicPlayer.resume(); }

    public void stop() {
        currentlyPlaying = null;
        musicPlayer.stop();
    }

    public String next() {
        if (currentlyPlayingIndex + 1 < queue.size()) {
            stop();
            currentlyPlayingIndex++;
            play(queue.get(currentlyPlayingIndex));
            return currentlyPlaying.getValue0();
        }
        return "";
    }

    public String previous() {
        if (currentlyPlayingIndex - 1 >= 0) {
            stop();
            currentlyPlayingIndex--;
            play(queue.get(currentlyPlayingIndex));
            return currentlyPlaying.getValue0();
        }
        return "";
    }

    public void repeat() {
        stop();
        play(currentlyPlaying);
    }

    public ArrayList<Tuple2<String, String>> shuffle() {
        queue.remove(currentlyPlayingIndex);
        Collections.shuffle(queue);
        queue.add(0, currentlyPlaying);
        currentlyPlayingIndex = 0;
        return queue;
    }


}