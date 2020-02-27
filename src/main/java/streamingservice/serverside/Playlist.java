package streamingservice.serverside;


import java.util.ArrayList;
import java.util.UUID;

public class Playlist {

    private UUID id;
    private String playlistName;
    private int size;
    private ArrayList<Tuple2<String, String>> songs;

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

    public void addSong(Tuple2<String, String> song) {
        songs.add(song);
        size++;
    }

    public void removeSong(int index) {
        songs.remove(index);
        size--;
    }

    public int getSize() { return songs.size(); }

    public ArrayList<Tuple2<String, String>> getSongs() {
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