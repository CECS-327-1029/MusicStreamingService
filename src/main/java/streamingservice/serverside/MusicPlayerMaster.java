package streamingservice.serverside;

import com.google.gson.JsonObject;
import javazoom.jl.decoder.JavaLayerException;
import streamingservice.clientside.MusicPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public final class MusicPlayerMaster {

    private static final MusicPlayer musicPlayer = new MusicPlayer();
    private static ArrayList<Tuple2<String, String>> queue;
    private static int currentlyPlayingIndex = 0;
    private static Tuple2<String, String> currentlyPlaying;

    public static void setQueue(ArrayList<Tuple2<String, String>> songsInQueue) { queue = songsInQueue; }
    
    public static void play(Tuple2<String, String> song) {
        try {
            currentlyPlaying = song;
            musicPlayer.play(currentlyPlaying);
        } catch (JavaLayerException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void pause() { musicPlayer.pause(); }

    public static void resume() { musicPlayer.resume(); }

    public static void stop() {
        currentlyPlaying = null;
        musicPlayer.stop();
    }

    public static String next() {
        if (currentlyPlayingIndex + 1 < queue.size()) {
            currentlyPlayingIndex++;
            play(queue.get(currentlyPlayingIndex));
            return FileHandler.getSongInfo(currentlyPlaying.getValue0());
        }
        return "";
    }

    public static String previous() {
        if (currentlyPlayingIndex - 1 >= 0) {
            currentlyPlayingIndex--;
            play(queue.get(currentlyPlayingIndex));
            return FileHandler.getSongInfo(currentlyPlaying.getValue0());
        }
        return "";
    }

    public static void repeat() {
        play(currentlyPlaying);
    }

    public static String shuffle() {
        queue.remove(currentlyPlayingIndex);
        Collections.shuffle(queue);
        queue.add(0, currentlyPlaying);
        currentlyPlayingIndex = 0;
        JsonObject object = new JsonObject();
        queue.forEach(song -> object.addProperty(song.getValue0(), song.getValue1()));
        return object.toString();
    }


}