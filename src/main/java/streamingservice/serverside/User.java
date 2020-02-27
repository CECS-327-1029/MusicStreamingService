package streamingservice.serverside;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String userName;
    private List<Playlist> playlists = new ArrayList<>();
    private ArrayList<Tuple2<String, String>> queuedSongs = new ArrayList<>();

    private int numberOfPlaylists;

    public User() {}

    public User(String firstName, String lastName, String email, String userName) {
        id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
        this.numberOfPlaylists = 0;
    }

    public User(String firstName, String lastName, String email, String userName, List<Playlist> playlists) {
        this(firstName, lastName, email, userName);
        this.playlists = playlists;
        this.numberOfPlaylists = playlists.size();
    }

    public UUID getId() { return id; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

    public String getUserName() { return userName; }

    public List<Playlist> getPlaylists() { return playlists; }

    public int getNumberOfPlaylists() { return numberOfPlaylists; }

    public ArrayList<Tuple2<String, String>> getQueuedSongs() {
        return queuedSongs;
    }

    //returns the index of the playlist in arraylist playlists
    public int getIndexOfPlaylist(String playlistName){
        int returnIndex = -1;

        for(int i = 0; i < playlists.size(); i++){
            if(playlists.get(i).getPlaylistName().equals(playlistName)){
                returnIndex = i;
            }
        }
        return returnIndex;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        this.numberOfPlaylists = playlists.size();
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        numberOfPlaylists++;
    }

    public void removePlaylist(int index){
        playlists.remove(index);
        numberOfPlaylists--;
    }

    public void addSongsToQueue(Tuple2<String, String> song) { queuedSongs.add(song); }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}