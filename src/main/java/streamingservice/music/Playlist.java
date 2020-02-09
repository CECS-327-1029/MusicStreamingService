package streamingservice.music;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Playlist {

    private UUID id;
    private String playlistName;
    private int size;
    private ArrayList<Song> songs;

    // If this is uncommented, the following exception is raised
    // Exception in thread "AWT-EventQueue-0" java.lang.reflect.InaccessibleObjectException:
    // Unable to make field private final java.time.LocalDate java.time.LocalDateTime.date accessible:
    // module java.base does not "opens java.time" to module com.google.gson

    // When issue is figured out uncomment this and corresponding lines below
    //private LocalDateTime playlistCreationDateTime;


    public Playlist() { }

    public Playlist(String playlistName) {
        //playlistCreationDateTime = LocalDateTime.now();
        id = UUID.randomUUID();
        songs = new ArrayList<>();
        this.playlistName = playlistName;
    }

    public boolean addSong(Song song) {
        songs.add(song);
        size++;
        return true;
    }

    public int getSize() { return size; }

    public ArrayList<Song> getSongs() {
        return songs;
    }

//    public LocalDateTime getPlaylistCreationDateTime() {
//        return playlistCreationDateTime;
//    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getId() {
        return id.toString();
    }

}