package streamingservice.music;

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
    private int numberOfPlaylists;


    public User(String firstName, String lastName, String email, String userName) {
        id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.userName = userName;
    }

    public User(String firstName, String lastName, String email, String userName, List<Playlist> playlists) {
        this(firstName, lastName, email, userName);
        this.playlists = playlists;
        numberOfPlaylists = playlists.size();
    }

    public String getId() { return id.toString(); }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

    public String getUserName() { return userName; }

    public List<Playlist> getPlaylists() { return playlists; }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
        this.numberOfPlaylists = playlists.size();
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        numberOfPlaylists++;
    }
}