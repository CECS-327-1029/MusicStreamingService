package streamingservice.UI.panels;

import streamingservice.UI.GUIManager;
import streamingservice.music.FileHandler;
import streamingservice.music.Playlist;
import streamingservice.music.Song;
import streamingservice.music.User;
import streamingservice.music.songinfo.Artist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.TreeMap;

public class UserProfile {

    private static final String SONGS = "Songs";
    private static final String ARTISTS = "Artists";
    private static final String GO_BACK_SYMBOL = "...";

    // values that will be used as search options besides the first element
    private static final String[] SEARCH_FILTERS = {"--- Search by ---", SONGS, ARTISTS};

    private String searchingBy; // keeps track of what is being searched

    private User user;      // tells what user is logged in
    private ArrayList<Song> songsToDisplay;  // holds all searched for songs that will be displayed to the user
    // HashMap to contains an searched artist and a list of their respective songs
    private TreeMap<Artist, ArrayList<Song>> artistsRespectiveSongs;


    // the user profile panel that holds the widgets to simulate a user's profile
    private JPanel userProfilePanel;
    // panel to hold widgets that allow a user to search for songs and artists
    private JPanel songListPanel;
    private JLabel userNameDisplayLabel;        // label to display a user's username
    private JComboBox<String> searchFilter;     // combobox so the user can search by either song or artist
    private JLabel songListLabel;               // label to display: Song List
    private JTextField searchTF;                // text field where the user can enter a keyword to search
    private JButton searchButton;               // user can click to search for songs or artists based on the search text field
    private JLabel numberOfItemsLabel;          // tells the user how many items were found
    private JButton logOutButton;               // user clicks this button to log out

    // panel that holds widgets that will display a list of artists or songs
    private JPanel songListDisplayPanel;
    private JList<String> listOfSearchedItems;          // holds a list of searched song names
    private DefaultListModel<String> listOfSearchItemsModel;     // list model to hold the jlist of songs

    // determine if the the user is viewing an searched artists or a particular artist's songs
    private boolean showingArtists = false;
    private Artist artistSelected;                      // used to know what artist's songs the user is viewing

    // panel to hold widgets that allow a user to create and display a user's playlist
    private JPanel playlistPanel;
    private JLabel playlistLabel;               // label to display: Your Playlists
    private JButton addPlaylistButton;          // button that will create a new playlist
    private JPanel playlistDisplayPanel;        // panel that holds widgets that will display all user's playlists
    private JList<String> listOfPlaylists;         // holds a list of a user's playlist names
    private DefaultListModel<String> playlistListModel; // list model to hold the jlist of playlists

    // panel that contains button to manage a song
    private JPanel musicPlayerPanel;
    private JButton repeatButton;
    private JButton previousButton;
    private JButton playButton;
    private JButton nextButton;
    private JButton shuffleButton;
    private JButton showPlaylistsBtn;
    private JButton removePlaylistBtn;

    private boolean lookingAtPlaylistList;      // determines if the user is looking at a playlist
    private int songToRemoveIndex;              // index of the song that will be removed from a playlist
    private int playListUserisIn;               // determines which playlist the user is in

    // reference to the frame the user profile is int
    JFrame mainFrame;

    public UserProfile(JFrame mainFrame, CardLayout screenTransitionCardLayout, JPanel rootPanel) {
        this.mainFrame = mainFrame;

        artistsRespectiveSongs = new TreeMap<>();       // will hold all artists and their corresponding songs
        songsToDisplay = new ArrayList<>();             // hold the list songs that were found when searched

        // sets search to have values to search by
        searchFilter.setModel(new DefaultComboBoxModel<>(SEARCH_FILTERS));
        searchFilter.addItemListener(e -> searchingBy = e.getItem().toString());

        lookingAtPlaylistList = true;

        // following allows for viewing the songs found
        listOfSearchItemsModel = new DefaultListModel<>();
        listOfSearchedItems = new JList<>(listOfSearchItemsModel);
        listOfSearchedItems.setSelectionBackground(Color.RED);
        listOfSearchedItems.setFont(new Font("Ayuthaya", Font.PLAIN, 16));
        listOfSearchedItems.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // allows us to scroll through the display of songs
        // set the size of the scrollpane to prevent resizing when songs title are very long
        JScrollPane songListScrollPane = new JScrollPane(listOfSearchedItems);
        Dimension dimension = new Dimension(200, 200);
        songListScrollPane.setMaximumSize(dimension);
        songListScrollPane.setMinimumSize(dimension);
        songListScrollPane.setPreferredSize(dimension);
        songListDisplayPanel.add(songListScrollPane);

        playlistListModel = new DefaultListModel<>();
        listOfPlaylists = new JList<>(playlistListModel);
        listOfPlaylists.setSelectionBackground(Color.RED);
        listOfPlaylists.setFont(new Font("Ayuthaya", Font.PLAIN, 16));
        listOfPlaylists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // allows us to scroll through the display of the user's playlists
        // set the size of the scrollpane to prevent resizing when songs title are very long
        JScrollPane playlistListScrollPane = new JScrollPane(listOfPlaylists);
        playlistListScrollPane.setMaximumSize(dimension);
        playlistListScrollPane.setMinimumSize(dimension);
        playlistListScrollPane.setPreferredSize(dimension);
        playlistDisplayPanel.add(playlistListScrollPane);

        // display the playlists of the user
        showPlaylistsBtn.addActionListener(e -> {
            displayUserPlayLists();
            lookingAtPlaylistList = true;
        });


        // will search by the option given in the searchFiler combo box
        searchButton.addActionListener(e -> {
            if (searchingBy != null) {  // textField not empty
                if (searchingBy.equals(SONGS)) { // search by songs
                    // transition to the card that will display the songs
                    songsToDisplay = FileHandler.searchForSongs(searchTF.getText());    // finds songs based on the input
                    displaySongs(); // display the songs to the user
                    numberOfItemsLabel.setText(songsToDisplay.size() + " song(s)");
                    numberOfItemsLabel.setVisible(true);
                }
                else if (searchingBy.equals(ARTISTS)) {  // search by artists
                    // transition to the card that will display the artists
                    artistsRespectiveSongs = FileHandler.searchForArtists(searchTF.getText());
                    displayArtistAndTheirSongs(); // display the artists to the user
                    numberOfItemsLabel.setText(artistsRespectiveSongs.size() + " artist(s)");
                    numberOfItemsLabel.setVisible(true);
                }
            }
        });

        //popup menu to remove song from a playlist
        final JPopupMenu songToRemovePopupMenu = new JPopupMenu();
        JMenuItem removeFromPlaylistOption = new JMenuItem("Remove from playlist");
        songToRemovePopupMenu.add(removeFromPlaylistOption);


        //show songs of playlists
        listOfPlaylists.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e){

                if(lookingAtPlaylistList){
                    playListUserisIn = ((JList) e.getSource()).locationToIndex(e.getPoint());
                }

                //popup menu to remove song from playlist
                if(SwingUtilities.isRightMouseButton(e) && !listOfPlaylists.isSelectionEmpty()
                        && !lookingAtPlaylistList
                        && listOfPlaylists.locationToIndex(e.getPoint()) == listOfPlaylists.getSelectedIndex()){
                    songToRemovePopupMenu.show(listOfPlaylists,e.getX(),e.getY());

                    //saves the index of the song to remove
                    songToRemoveIndex = ((JList) e.getSource()).locationToIndex(e.getPoint());
                }

                int index = ((JList) e.getSource()).locationToIndex(e.getPoint());

                //user double clicks on a playlist
                if(e.getClickCount() == 2){
                    //clicking on the dots to go back
                    if(!lookingAtPlaylistList && index == 0){
                        lookingAtPlaylistList = true;
                        playlistListModel.clear();
                        displayUserPlayLists();
                    }
                    //play the song that the user clicks
                    else if(!lookingAtPlaylistList && index > 0){
                        //TODO: PLAYS SONG
                    }
                    else if(lookingAtPlaylistList){
                        //displays the songs of the playlist
                        playlistListModel.clear();
                        playlistListModel.addElement(GO_BACK_SYMBOL);
                        user.getPlaylists().get(index).getSongs().forEach(song -> playlistListModel.addElement(song.getSong().getTitle()));
                        listOfPlaylists.setModel(playlistListModel);
                        lookingAtPlaylistList = false;
                    }
                }
            }
        });

        removeFromPlaylistOption.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                //removes the song that the user clicks on from the playlist
                user.getPlaylists().get(playListUserisIn).getSongs().remove(songToRemoveIndex - 1);
                //updates the user json file
                FileHandler.updateUserPlaylist(user);

                //updates the model
                displayUserPlayLists();
                lookingAtPlaylistList = true;
            }
        });

        //popup menu to add song to playlist
        final JPopupMenu songPopupMenu = new JPopupMenu();
        JMenuItem addToPlaylistOption = new JMenuItem("Add to playlist");
        songPopupMenu.add(addToPlaylistOption);

        addToPlaylistOption.addActionListener(actionEvent -> {
            //gets the name of the playlist to add
            String playlist = showPlaylistList();

            //gets the song to add to playlist

            // this will get the song that was chosen by the user depending if they are looking at the songs searched
            // for or an artist's songs
            Song songClicked = searchingBy.equals(SONGS) ?
                               songsToDisplay.get(listOfSearchedItems.getSelectedIndex()) :
                               artistsRespectiveSongs.get(artistSelected).get(listOfSearchedItems.getSelectedIndex()-1);

            //saves the index of the playlist to add song to
            int indexToAdd = user.getIndexOfPlaylist(playlist);

            //if the playlist is found then the song will be added to the playlist
            if(indexToAdd != -1){
                user.getPlaylists().get(indexToAdd).addSong(songClicked);
                //updates the user profile
                FileHandler.updateUserPlaylist(user);
            }

            //updates the model
            displayUserPlayLists();
            lookingAtPlaylistList = true;
        });

        //when a user clicks on the remove button, the user will decide which playlist to remove from their profile
        removePlaylistBtn.addActionListener(e ->{
            //gets the name of the playlist to delete
            String playlist = showPlaylistList();

            //saves the index of the playlist to remove
            int indexToRemove = user.getIndexOfPlaylist(playlist);

            //if the playlist is found then the playlist will be deleted from the user
            if(indexToRemove != -1){
                user.removePlaylist(indexToRemove);
                //updates the user profile
                FileHandler.updateUserPlaylist(user);
            }

            //updates the model
            displayUserPlayLists();
            lookingAtPlaylistList = true;
        });

        // if an item from the card that shows the list of songs search was double clicked,
        // then the song should begin playing
        listOfSearchedItems.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                //popup menu to add song to playlist
                if(SwingUtilities.isRightMouseButton(e) && !listOfSearchedItems.isSelectionEmpty()
                        && !showingArtists
                        && listOfSearchedItems.locationToIndex(e.getPoint()) == listOfSearchedItems.getSelectedIndex()){
                    songPopupMenu.show(listOfSearchedItems,e.getX(),e.getY());
                }

                if (e.getClickCount() == 2) {   // captures a mouse double-click

                    if (searchingBy.equals(SONGS)) {
                        playSongWhenSelected();
                    } else {
                        // occurs when a user clicks on an artist or an artist's song
                        // get the index of the item clicked on
                        int index = ((JList) e.getSource()).locationToIndex(e.getPoint());
                        // if we are displaying the songs from a certain artist and the user wants to go
                        // back to view all artists searched...
                        if (!showingArtists && index == 0) {
                            // clear the model that contains the list of an artist's songs
                            // and populate it with artist's
                            listOfSearchItemsModel.clear();
                            artistsRespectiveSongs.keySet().forEach(artist -> listOfSearchItemsModel.addElement(artist.getName()));
                            listOfSearchedItems.setModel(listOfSearchItemsModel);
                            numberOfItemsLabel.setText(artistsRespectiveSongs.size() + " artist(s)");
                            // showingArtists determines if we are viewing the names of artists or a particular artist's songs
                            showingArtists = true;
                            artistSelected = null;
                        }
                        // if a user double clicks on an artist's song, play the song
                        else if (!showingArtists && index > 0) {
                            playSongWhenSelected();
                        } else if (showingArtists) {    // display the name of songs by artistSelected
                            showingArtists = false; // set false to show that we'll display and artist's songs
                            // artistSelected holds the name of the artist that was chosen to view their songs
                            artistSelected = (Artist) artistsRespectiveSongs.keySet().toArray()[index];
                            // get the songs of this particular artist
                            ArrayList<Song> songs = artistsRespectiveSongs.get(artistsRespectiveSongs.keySet().toArray()[index]);
                            // clear the model that contains a list of artists and populate it with
                            // the songs from the artist selected
                            numberOfItemsLabel.setText(songs.size() + " song(s)");
                            listOfSearchItemsModel.clear();
                            listOfSearchItemsModel.addElement(GO_BACK_SYMBOL);  // this is an element to go back to view a list of artists
                            songs.forEach(k -> listOfSearchItemsModel.addElement(k.getSong().getTitle()));
                            listOfSearchedItems.setModel(listOfSearchItemsModel);
                        }
                    }
                }
            }
        });

        // when the user clicks on the playButton, a selected song will play
        playButton.addActionListener(e -> playSongWhenSelected());

        logOutButton.addActionListener(e -> {
            clearAll();
            screenTransitionCardLayout.show(rootPanel, GUIManager.LOG_IN);
        });

        //when a user clicks on the add button, the user creates a new playlist in their profile
        addPlaylistButton.addActionListener(e -> {
            showPlaylistsPanel();
        } );

    }

    /************************************************************************************************/
    /************************************************************************************************/

    private void showPlaylistsPanel(){
        //pop up menu prompts the user to insert a name for the new playlist created
        String playlistName = JOptionPane.showInputDialog(getUserProfilePanel(), "Name of Playlist", null);

        //if statement will check if the user insert a name for the playlist or if the new playlist was canceled
        //if the user provides a name for the new playlist, a playlist called playlistName will be saved
        if(playlistName != null){
            if (!playlistName.trim().equals("")) {
                //creates a new playlist named playlistName
                Playlist newPlayList = new Playlist(playlistName);

                //the new playlist is added to the user
                user.addPlaylist(newPlayList);
                FileHandler.updateUserPlaylist(user);

                //updates the model
                displayUserPlayLists();
                lookingAtPlaylistList = true;
            }
        }
    }

    public JPanel getUserProfilePanel() { return userProfilePanel; }

    public void setUser(User user) {
        this.user = user;
        userNameDisplayLabel.setText(user.getUserName());
    }

    /**
     * Will display the title, artist, and album information on the top of the window
     * whenever a song is selected from their respective list. The corresponding song will
     * also start playing.
     */
    private void playSongWhenSelected() {
        Song songChosen = null;
        // if searching by "Songs" and a song is selected get the song
        if (listOfSearchedItems.getSelectedIndex() != -1 && searchingBy.equals(SONGS)) {
            songChosen = songsToDisplay.get(listOfSearchedItems.getSelectedIndex());
        }

        // if searching by "Artists" and a song is selected, and we are viewing a list of an artist's songs, get the song
        if (listOfSearchedItems.getSelectedIndex() > 0 && !showingArtists && searchingBy.equals(ARTISTS)) {
            songChosen = artistsRespectiveSongs.get(artistSelected).get(listOfSearchedItems.getSelectedIndex() - 1);
        }

        if (songChosen != null) {  // if a song is chosen, display the song on the top of the window
            String title = songChosen.getSong().getTitle();
            String album = songChosen.getRelease().getName();
            String artist = songChosen.getArtist().getName();

            mainFrame.setTitle(title + " by " + album + " from " + artist);
            //player.play();
        }
    }

    /**
     * Will display the songs found to the user by adding them to the Jlist that is used to display the songs.
     */
    private void displaySongs() {
        listOfSearchItemsModel.clear();
        songsToDisplay.forEach(song -> listOfSearchItemsModel.addElement(song.getSong().getTitle()));
        listOfSearchedItems.setModel(listOfSearchItemsModel);
    }

    private void displayUserPlayLists(){
        playlistListModel.clear();
        user.getPlaylists().forEach(playlist -> playlistListModel.addElement(playlist.getPlaylistName()));
        listOfPlaylists.setModel(playlistListModel);
    }

    /**
     * Will display the artists found to the user by adding them to the Jlist that is used to display the artists.
     */
    private void displayArtistAndTheirSongs() {
        showingArtists = true;
        listOfSearchItemsModel.clear();
        artistsRespectiveSongs.keySet().forEach(artist -> listOfSearchItemsModel.addElement(artist.getName()));
        listOfSearchedItems.setModel(listOfSearchItemsModel);
    }

    /**
     * Clears all the necessary labels, lists, hashmaps, models, and user's so as to simulate a user logging out.
     */
    private void clearAll() {
        user = null;
        songsToDisplay = null;
        userNameDisplayLabel.setText("");
        searchTF.setText("");
        searchFilter.setModel(new DefaultComboBoxModel<>(SEARCH_FILTERS));
        listOfSearchedItems.removeAll();
        listOfSearchItemsModel.removeAllElements();
        showingArtists = false;
        artistSelected = null;
        artistsRespectiveSongs = null;
        listOfSearchedItems.removeAll();
        listOfSearchItemsModel.removeAllElements();
        numberOfItemsLabel.setText("");
        numberOfItemsLabel.setVisible(false);
        mainFrame.setTitle("");
    }

    private String showPlaylistList(){

        JFrame frame = new JFrame();
        frame.setAlwaysOnTop(true);

        Object[] options = new Object[user.getPlaylists().size()];

        for(int i = 0; i < user.getPlaylists().size(); i++){
            options[i] = user.getPlaylists().get(i).getPlaylistName();
        }

        //passing `frame` instead of `null` as first parameter
        Object selectionObject = JOptionPane.showInputDialog(frame, "Choose playlist", "Menu", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        return selectionObject.toString();
    }

}