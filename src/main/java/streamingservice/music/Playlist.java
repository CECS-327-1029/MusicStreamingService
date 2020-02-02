package streamingservice.music;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class Playlist {

    private UUID id;
    private String playlistName;
    private int size;
    private ArrayList<Song> songs;
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