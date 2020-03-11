package streamingservice.clientside.panels;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import streamingservice.clientside.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class UserProfile {

    private static final String GO_BACK_SYMBOL = "...";
    private static final Font FONT = new Font("Ayuthaya", Font.PLAIN, 18);
    private static final Color SELECTION_COLOR = Color.YELLOW;
    private static final Color SCROLLABLE_BACKGROUND = Color.decode("#0752CB");
    private static final Dimension CURRENTLY_PLAYING_DIMENSION = new Dimension(360, 520);
    private static final Dimension DIMENSION_200 = new Dimension(200, 200);
    private static final int MAX_AMOUNT_TO_DISPLAY = 25;

    private ProxyInterface proxy;
    private MusicPlayerMaster musicPlayerMaster;

    private String userId = "";
    // keeps track of what is being searched. Initial value is Songs
    private SEARCH_FILTER searchStrategy = SEARCH_FILTER.fromValue("Songs");

    /**
     * Holds the id and name of the searched up songs in a {@code org.javatuples.Tuple2}. These will be displayed to the user.
     */
    private ArrayList<Tuple2<String, String>> songs;

    /**
     * Holds the id and name of an album, artist, genre, or etc. in a in a {@code org.javatuples.Tuple2}. These will be displayed to the user
     * in which they can click on an element and view their songs which will be populated in the
     * {@code songs ArrayList}.
     */
    private ArrayList<Tuple2<String, String>> masterSearch;

    /**
     * Determines if the master searched items are being viewed or a selected master searched item is selected and their
     * respective songs are being viewed.
     */
    private boolean showMasterSearchedItems = true;

    private boolean showingListOfSongs = false;

    /**
     * Holds the id and name of the user's playlists.
     */
    private ArrayList<Tuple2<String, String>> playlists;

    /**
     * Holds the id and name of a playlist's songs.
     */
    private ArrayList<Tuple2<String, String>> songsInPlaylist;

    /**
     * Holds the id and name of the songs in the queue to play
     */
    private ArrayList<Tuple2<String, String>> songsInQueue;

    /**
     * Keeps track of the last selected song in either the searchedItemsJList, playlistJList, or the queueJList;
     */
    private Tuple2<String, String> lastSelectedSong;

    // Root Panel
    private JPanel userProfilePanel;

    // Song List Panel
    private JPanel songListPanel;
    private JLabel userNameDisplayLabel;
    private JComboBox<String> searchFilter;
    private JTextField searchTF;
    private JButton searchButton;               // button that searched for information based on the search filter
    private JLabel songListLabel;
    private JLabel numberOfItemsLabel;          // displays how many items were found
    private JButton displayMore;
    private boolean wasDisplayMorePressed = false;
    private int masterSearchIndex = -1;

    // Song List Display Panel. Holds the widgets that will display the songs, artists, etc.
    private JPanel songListDisplayPanel;
    private JList<String> searchedItemsJList;               // holds a list of searched song names
    private DefaultListModel<String> searchedItemsModel;    // list model to hold the JList of songs

    // Music Player Panel
    private JPanel musicPlayerPanel;
    private JButton nextButton;         // button that plays the next song
    private JButton repeatButton;       // button that repeats the currently playing song
    private JButton previousButton;     // button that plays the previous song
    private JButton playButton;         // button that plays a song
    private JButton resumeButton;             // button that resumes a song if it was paused
    private JButton pauseButton;        // button that pauses a song if it's playing
    private JButton stopButton;         // button that stops playing any song
    private JButton shuffleButton;      // button that shuffles the currently playing list

    // Playlist Panel
    private JPanel playlistPanel;
    private JButton addPlaylistButton;          // button that will create a new playlist
    private JButton removePlaylistBtn;          // button that will remove a playlist
    private JLabel playlistLabel;               // label to display: Your Playlists

    private JPanel playlistDisplayPanel;        // panel that holds widgets that will display all user's playlists
    private JList<String> playlistJList;        // holds a list of a user's playlist names
    private DefaultListModel<String> playlistModel;     // list model to hold the JList of playlists
    private int currentlyPlayingSongIndex = -1;             // the index of the song within the currently playing songs list
    private boolean lookingAtPlaylistList = true;      // determines if the user is looking at a playlist
    private int songToRemoveIndex;              // index of the song that will be removed from a playlist
    private int playListUserIsIn;               // determines which playlist the user is in

    // Currently Playing Panel
    private JPanel currentlyPlayingPanel;       // panel that will display the songs that will be played
    private JLabel currentlyPlayingLabel;       // label to display: Currently Playing
    private JList<String> queueJList;    // holds a list of currently playing songs
    private DefaultListModel<String> queueModel;   // list model to hold the JList of currently playing songs

    // reference to the frame the user profile is int
    private JFrame mainFrame;

    public UserProfile(JFrame mainFrame, ProxyInterface proxy) {
        this.mainFrame = mainFrame;
        this.proxy = proxy;
        this.musicPlayerMaster = new MusicPlayerMaster(proxy);

        songs = new ArrayList<>();
        masterSearch = new ArrayList<>();
        playlists = new ArrayList<>();
        songsInPlaylist = new ArrayList<>();
        songsInQueue = new ArrayList<>();

        setSearchFilter();

        // set the scrollable panes
        setSongListDisplayPanel();
        setPlaylistDisplayPanel();
        setCurrentlyPlayingPanel();

        // will search by the option given in the searchFiler combo box
        searchButton.addActionListener(e -> search(searchStrategy, searchTF.getText(), false, null, 0));

        displayMore.addActionListener(e -> displayMoreActionListener());

        // creates a new playlist
        addPlaylistButton.addActionListener(e -> createPlaylist());

        // delete an existing playlist
        removePlaylistBtn.addActionListener(e -> deletePlaylist());

        // start playing a song
        playButton.addActionListener(e -> playButtonActionListener());

        // pause a playing song
        pauseButton.addActionListener(e -> pauseButtonActionListener());

        // resume a paused song
        resumeButton.addActionListener(e -> resumeButtonActionListener());

        // stop a playing song
        stopButton.addActionListener(e -> stopButtonActionListener());

        // play the next song if available
        nextButton.addActionListener(e -> nextButtonActionListener());

        // play the previous song if available
        previousButton.addActionListener(e -> previousButtonActionListener());

        // repeat the song that's playing
        repeatButton.addActionListener(e -> repeatButtonActionListener());

        // shuggle the queue if not empty
        shuffleButton.addActionListener(e -> shuffleButtonActionListener());

        // popup menu in the searchedItemsJList
        final JPopupMenu songPopupMenu = new JPopupMenu();
        JMenuItem addToPlaylistOption = new JMenuItem("Add to playlist");
        JMenuItem addToCurrentlyPlaying = new JMenuItem("Add to Queue");
        songPopupMenu.add(addToPlaylistOption);
        songPopupMenu.add(addToCurrentlyPlaying);

        // popup menu in the playlistJList when viewing a playlist's songs
        final JPopupMenu songToRemovePopupMenu = new JPopupMenu();
        JMenuItem removeFromPlaylistOption = new JMenuItem("Remove from playlist");
        songToRemovePopupMenu.add(removeFromPlaylistOption);

        // popup menu in the playlistJList when viewing a user's playlist
        final JPopupMenu playlistPopupMenu = new JPopupMenu();
        JMenuItem startPlayingPlaylist = new JMenuItem("Start Playing");
        JMenuItem addPlaylistToQueue = new JMenuItem("Add Playlist to Queue");
        playlistPopupMenu.add(startPlayingPlaylist);
        playlistPopupMenu.add(addPlaylistToQueue);

        // popup menu in the queueJList
        final JPopupMenu queuePopupMenu = new JPopupMenu();
        JMenuItem removeFromQueue = new JMenuItem("Remove from Queue");
        queuePopupMenu.add(removeFromQueue);

        // popup menu in the queueJList to clear the queue
        final JPopupMenu clearQueuePopupMenu = new JPopupMenu();
        JMenuItem clearQueue = new JMenuItem("Clear the Queue");
        clearQueuePopupMenu.add(clearQueue);

        addToPlaylistOption.addActionListener(this::addToPlaylistOptionActionListener);
        addToCurrentlyPlaying.addActionListener(this::addToCurrentlyPlayingActionListener);
        removeFromPlaylistOption.addActionListener(this::removeFromPlaylistOptionActionListener);
        startPlayingPlaylist.addActionListener(this::startPlayingPlaylistActionListener);
        addPlaylistToQueue.addActionListener(this::addPlaylistToQueueActionListener);
        removeFromQueue.addActionListener(this::removeFromQueueActionListener);
        clearQueue.addActionListener(this::clearQueueActionListener);

        searchedItemsJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setSearchedItemsJListMouseListener(e, songPopupMenu);
            }
        });

        playlistJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setPlaylistJListMouseListener(e, songToRemovePopupMenu, playlistPopupMenu);
            }
        });

        queueJList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setQueueJListMouseListener(e, queuePopupMenu, clearQueuePopupMenu);
            }
        });
    }

    public void setUser(String userId) {
        this.userId = userId;
        proxy.setUserId(userId);
        JsonObject object = proxy.syncExecution("getUserName", userId);
        String username = (String) proxy.adjustOutput(object);
        userNameDisplayLabel.setText(username);
        displayPlaylists();
        displayCurrentlyPlayingSongs(false);
    }

    public JPanel getUserProfilePanel() { return userProfilePanel; }

    private void setSearchFilter() {
        // sets search to have values to search by
        searchFilter.setModel(new DefaultComboBoxModel<>(SEARCH_FILTER.toArray()));
        searchFilter.addItemListener(e -> searchStrategy = SEARCH_FILTER.fromValue(e.getItem().toString()));
        searchTF.addActionListener(e -> searchButton.doClick());
    }

    private void setSongListDisplayPanel() {
        // following allows for viewing the songs found
        searchedItemsModel = new DefaultListModel<>();
        searchedItemsJList = new JList<>(searchedItemsModel);
        searchedItemsJList.setSelectionBackground(SELECTION_COLOR);
        searchedItemsJList.setFont(FONT);
        searchedItemsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // allows us to scroll through the display of songs
        // set the size of the ScrollPane to prevent resizing when songs title are very long
        JScrollPane songListScrollPane = new JScrollPane(searchedItemsJList);
        songListScrollPane.setMaximumSize(DIMENSION_200);
        songListScrollPane.setMinimumSize(DIMENSION_200);
        songListScrollPane.setPreferredSize(DIMENSION_200);
        songListScrollPane.setBackground(SCROLLABLE_BACKGROUND);
        songListDisplayPanel.add(songListScrollPane);
    }

    private void setPlaylistDisplayPanel() {
        playlistModel = new DefaultListModel<>();
        playlistJList = new JList<>(playlistModel);
        playlistJList.setSelectionBackground(SELECTION_COLOR);
        playlistJList.setFont(FONT);
        playlistJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // allows us to scroll through the display of songs
        // set the size of the ScrollPane to prevent resizing when songs title are very long
        JScrollPane playlistListScrollPane = new JScrollPane(playlistJList);
        playlistListScrollPane.setMaximumSize(DIMENSION_200);
        playlistListScrollPane.setMinimumSize(DIMENSION_200);
        playlistListScrollPane.setPreferredSize(DIMENSION_200);
        playlistListScrollPane.setBackground(SCROLLABLE_BACKGROUND);
        playlistDisplayPanel.add(playlistListScrollPane);
    }

    private void setCurrentlyPlayingPanel() {
        // the following is used to display the songs that will be played one after another
        queueModel = new DefaultListModel<>();
        queueJList = new JList<>(queueModel);
        queueJList.setSelectionBackground(SELECTION_COLOR);
        queueJList.setFont(FONT);
        queueJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // allows us to scroll through the display of songs
        // set the size of the ScrollPane to prevent resizing when songs title are very long
        JScrollPane currentlyPlayingScrollPane = new JScrollPane(queueJList);
        currentlyPlayingScrollPane.setMaximumSize(CURRENTLY_PLAYING_DIMENSION);
        currentlyPlayingScrollPane.setMinimumSize(CURRENTLY_PLAYING_DIMENSION);
        currentlyPlayingScrollPane.setPreferredSize(CURRENTLY_PLAYING_DIMENSION);
        currentlyPlayingScrollPane.setBackground(SCROLLABLE_BACKGROUND);
        currentlyPlayingPanel.add(currentlyPlayingScrollPane);
    }

    private void setSearchedItemsJListMouseListener(MouseEvent mouseEvent, JPopupMenu songPopupMenu) {

        if (SwingUtilities.isRightMouseButton(mouseEvent) && !searchedItemsJList.isSelectionEmpty() && showingListOfSongs
                && searchedItemsJList.locationToIndex(mouseEvent.getPoint()) == searchedItemsJList.getSelectedIndex()) {
            displayPlaylists();
            songPopupMenu.show(searchedItemsJList, mouseEvent.getX(), mouseEvent.getY());
        }
        masterSearchIndex = ((JList) mouseEvent.getSource()).locationToIndex(mouseEvent.getPoint());

        if (searchStrategy == SEARCH_FILTER.SONGS) {
            lastSelectedSong = songs.get(searchedItemsJList.getSelectedIndex());
        } else if (!showMasterSearchedItems && masterSearchIndex > 0) {
            lastSelectedSong = songs.get(searchedItemsJList.getSelectedIndex() - 1);
        }

        if (mouseEvent.getClickCount() == 2) {  // when a user double-clicks a song

            if (searchStrategy == SEARCH_FILTER.SONGS) {
                playSelectedSong(songs.get(searchedItemsJList.getSelectedIndex()));
            } else {

                if (showMasterSearchedItems) {   // display songs from selected value
                   // search for songs with selected value's id
                   search(SEARCH_FILTER.SONGS, masterSearch.get(masterSearchIndex).getValue0(), true, searchStrategy, 0);
                   showMasterSearchedItems = false;
               } else if (masterSearchIndex == 0) { // showing a filter's songs -> go back
                   displayMasterSearch();
                    numberOfItemsLabel.setText("Showing " + masterSearch.size() + " " + searchStrategy.toString().toLowerCase() + "(s)");
                } else {
                   playSelectedSong(songs.get(searchedItemsJList.getSelectedIndex() - 1));
               }
            }
        }
    }

    private void setPlaylistJListMouseListener(MouseEvent mouseEvent, JPopupMenu songToRemovePopupMenu, JPopupMenu playlistPopupMenu) {

        if (lookingAtPlaylistList) {
            playListUserIsIn = ((JList) mouseEvent.getSource()).locationToIndex(mouseEvent.getPoint());
        }

        if (SwingUtilities.isRightMouseButton(mouseEvent) && lookingAtPlaylistList && playlistJList.getSelectedIndex() != -1) {
            playlistPopupMenu.show(playlistJList, mouseEvent.getX(), mouseEvent.getY());
        }

        if (SwingUtilities.isRightMouseButton(mouseEvent) && !playlistJList.isSelectionEmpty() && !lookingAtPlaylistList
            && playlistJList.locationToIndex(mouseEvent.getPoint()) == playlistJList.getSelectedIndex()) {
            songToRemovePopupMenu.show(playlistJList, mouseEvent.getX(), mouseEvent.getY());

            songToRemoveIndex = ((JList) mouseEvent.getSource()).locationToIndex(mouseEvent.getPoint());
        }

        int index = ((JList) mouseEvent.getSource()).locationToIndex(mouseEvent.getPoint());
        if (!lookingAtPlaylistList && index > 0) {
            lastSelectedSong = songsInPlaylist.get(playlistJList.getSelectedIndex() - 1);
        }

        if (mouseEvent.getClickCount() == 2) {
            if (lookingAtPlaylistList) {    // user double-clicks on a playlist -> show that playlist's songs
                displayPlaylistSongs(index);
            } else if (index == 0) {    // looking at a playlist's songs and the go back option is pressed
                lookingAtPlaylistList = true;
                displayPlaylists();
            } else {
                playSelectedSong(songsInPlaylist.get(playlistJList.getSelectedIndex() - 1));
            }
        }

    }

    private void setQueueJListMouseListener(MouseEvent mouseEvent, JPopupMenu queuePopupMenu, JPopupMenu clearQueuePopupMenu) {

        if (SwingUtilities.isRightMouseButton(mouseEvent) && queueJList.getSelectedIndex() == -1) {
            clearQueuePopupMenu.show(queueJList, mouseEvent.getX(), mouseEvent.getY());
        }

        if (SwingUtilities.isRightMouseButton(mouseEvent) && queueJList.getSelectedIndex() != -1) {
            queuePopupMenu.show(queueJList, mouseEvent.getX(), mouseEvent.getY());
        }

        int index = ((JList) mouseEvent.getSource()).locationToIndex(mouseEvent.getPoint());
        if (index != -1) {
            lastSelectedSong = songsInQueue.get(index);
            queueJList.setSelectedIndex(index);
        }
        if (mouseEvent.getClickCount() == 2) {
            if (index != -1) {
                JsonObject object = proxy.syncExecution("adjustQueue", userId, index, false);
                proxy.adjustOutput(object);
                displayCurrentlyPlayingSongs(true);
            }
        }
    }

    private void addToPlaylistOptionActionListener(ActionEvent actionEvent) {
        String chosenPlaylist = JOptionPane.showInputDialog("Enter playlist name");
        if (chosenPlaylist != null) {
            if (foundPlaylistFromInput(chosenPlaylist)) {
                Tuple2<String, String> songChosen = searchStrategy == SEARCH_FILTER.SONGS ?
                        songs.get(searchedItemsJList.getSelectedIndex()) : songs.get(searchedItemsJList.getSelectedIndex() - 1);

                int indexToAdd = getIndexOfPlaylist(chosenPlaylist);
                if (indexToAdd != -1) {
                    JsonObject object = proxy.syncExecution("isSongInPlaylist", userId, playlists.get(indexToAdd).getValue0(), songChosen.getValue0());
                    boolean isSongInPlaylist = (boolean) proxy.adjustOutput(object);
                    if (!isSongInPlaylist) {
                        object = proxy.syncExecution("updateUserPlaylists", userId, playlists.get(indexToAdd).getValue0(),
                                songChosen.getValue0(), songChosen.getValue1(), true);
                        proxy.adjustOutput(object);
                    }
                }
                displayPlaylists();
                lookingAtPlaylistList = true;
            }
        }
    }

    private void addToCurrentlyPlayingActionListener(ActionEvent actionEvent) {
        int chosenSongIndex = searchedItemsJList.getSelectedIndex();
        Tuple2<String, String> song = searchStrategy == SEARCH_FILTER.SONGS ?
                songs.get(chosenSongIndex) : songs.get(chosenSongIndex - 1);

        JsonObject object = proxy.syncExecution("addSongToQueue", userId, song.getValue0(), song.getValue1(), true);
        proxy.adjustOutput(object);
        displayCurrentlyPlayingSongs(songsInQueue.size() == 0);
    }

    private void removeFromPlaylistOptionActionListener(ActionEvent actionEvent) {
        JsonObject object = proxy.syncExecution("updateUserPlaylists", userId, playlists.get(playListUserIsIn).getValue0(),
                songsInPlaylist.get(songToRemoveIndex-1).getValue0(), songsInPlaylist.get(songToRemoveIndex-1).getValue0(), false);
        proxy.adjustOutput(object);
        displayPlaylists();
        lookingAtPlaylistList = true;
    }

    @SuppressWarnings("unchecked")
    private void startPlayingPlaylistActionListener(ActionEvent actionEvent) {
        queueModel.clear();
        songsInQueue.clear();
        JsonObject object = proxy.syncExecution("getPlaylistSongs", userId, playlists.get(playlistJList.getSelectedIndex() ).getValue0());
        ArrayList<Tuple2<String, String>> songs = (ArrayList<Tuple2<String, String>>) proxy.adjustOutput(object);
        if (songs != null) {
            songsInQueue.addAll(songs);
            object = proxy.syncExecution("clearQueue", userId);
            proxy.adjustOutput(object);
            songsInQueue.forEach(song -> {
                JsonObject object1 = proxy.syncExecution("addSongToQueue", userId, song.getValue0(), song.getValue1(), true);
                proxy.adjustOutput(object1);
            });
            displayCurrentlyPlayingSongs(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void addPlaylistToQueueActionListener(ActionEvent actionEvent) {
        JsonObject object = proxy.syncExecution("getPlaylistSongs", userId, playlists.get(playlistJList.getSelectedIndex() ).getValue0());
        ArrayList<Tuple2<String, String>> songs = (ArrayList<Tuple2<String, String>>) proxy.adjustOutput(object);
        if (songs != null) {
            songs.forEach(song -> {
                JsonObject object1 = proxy.syncExecution("addSongToQueue", userId, song.getValue0(), song.getValue1(), true);
                proxy.adjustOutput(object1);
            });
            displayCurrentlyPlayingSongs(true);
        }
    }

    private void removeFromQueueActionListener(ActionEvent actionEvent) {
        JsonObject object = proxy.syncExecution("adjustQueue", userId, queueJList.getSelectedIndex(), true);
        proxy.adjustOutput(object);
        displayCurrentlyPlayingSongs(false);
    }

    private void clearQueueActionListener(ActionEvent actionEvent) {
        JsonObject object = proxy.syncExecution("clearQueue", userId);
        proxy.adjustOutput(object);
        displayCurrentlyPlayingSongs(false);
    }

    private void playButtonActionListener() {
        if (songsInQueue.contains(lastSelectedSong)) {
            int index = songsInQueue.indexOf(lastSelectedSong);
            if (index != -1) {
                JsonObject object = proxy.syncExecution("adjustQueue", userId, index, false);
                proxy.adjustOutput(object);
                displayCurrentlyPlayingSongs(true);
            }
        } else if (lastSelectedSong != null) {
            playSelectedSong(lastSelectedSong);
        }
    }

    private void pauseButtonActionListener() {
        musicPlayerMaster.pause();
    }

    private void resumeButtonActionListener() {
        musicPlayerMaster.resume();
    }

    private void stopButtonActionListener() {
        musicPlayerMaster.stop();
        mainFrame.setTitle("");
    }

    private void nextButtonActionListener() {
        String songId = musicPlayerMaster.next();
        if (!songId.equals("")) {
            JsonObject object = proxy.syncExecution("getSongInfo", songId);
            mainFrame.setTitle((String) proxy.adjustOutput(object));
            queueJList.setSelectedIndex(++currentlyPlayingSongIndex);
        }
    }

    private void previousButtonActionListener() {
        String songId = musicPlayerMaster.previous();
        if (!songId.equals("")) {
            JsonObject object = proxy.syncExecution("getSongInfo", songId);
            mainFrame.setTitle((String) proxy.adjustOutput(object));
            queueJList.setSelectedIndex(--currentlyPlayingSongIndex);
        }
    }

    private void repeatButtonActionListener() {
        musicPlayerMaster.repeat();
    }

    @SuppressWarnings("unchecked")
    private void shuffleButtonActionListener() {
        JsonObject object = proxy.syncExecution("clearQueue", userId);
        proxy.adjustOutput(object);

        ArrayList<Tuple2<String, String>> returnValue = musicPlayerMaster.shuffle();
        if (returnValue != null) {
            returnValue.forEach(song -> {
                JsonObject object1 = proxy.syncExecution("addSongToQueue", userId, song.getValue0(), song.getValue1(), true);
                proxy.adjustOutput(object1);
            });
            displayCurrentlyPlayingSongs(false);
        }
    }

    @SuppressWarnings("unchecked")
    private void search(SEARCH_FILTER searchBy, String keyword, boolean searchByID, SEARCH_FILTER idFilter, int startIdx) {
        if (!keyword.trim().equals("")) {
            JsonObject object = proxy.syncExecution("getListOf", searchBy.toString(), keyword, searchByID,
                    idFilter != null ? idFilter.toString() : null, startIdx, MAX_AMOUNT_TO_DISPLAY);
            ArrayList<Tuple2<String, String>> searchedFor = (ArrayList<Tuple2<String, String>>) proxy.adjustOutput(object);

            if (searchBy == SEARCH_FILTER.SONGS) {
                if (!wasDisplayMorePressed) {
                    songs.clear();
                }
                songs.addAll(searchedFor);
                displaySongs();
                numberOfItemsLabel.setText("Showing " + songs.size() + " song(s)");
                numberOfItemsLabel.setVisible(true);
                displayMore.setVisible(true);

            } else {
                if (!wasDisplayMorePressed) { masterSearch.clear(); }
                masterSearch.addAll(searchedFor);
                displayMasterSearch();
                numberOfItemsLabel.setText("Showing " + masterSearch.size() + " " + searchBy.toString().toLowerCase() + "(s)");
                numberOfItemsLabel.setVisible(true);
                displayMore.setVisible(true);
            }
        }
    }

    private void displayMoreActionListener() {
        wasDisplayMorePressed = true;
        if (searchStrategy == SEARCH_FILTER.SONGS) {
            search(searchStrategy, searchTF.getText(), false, null, songs.size());
        } else {
            if (showMasterSearchedItems) {
                search(searchStrategy, searchTF.getText(), false, null, masterSearch.size());
            } else {
                search(SEARCH_FILTER.SONGS, masterSearch.get(masterSearchIndex).getValue0(), true,
                        searchStrategy, masterSearch.size());
            }
        }
    }

    private void displaySongs() {
        searchedItemsModel.clear();
        if (searchStrategy != SEARCH_FILTER.SONGS) {
            searchedItemsModel.addElement(GO_BACK_SYMBOL);
        }
        songs.forEach(pair -> searchedItemsModel.addElement(pair.getValue1()));
        searchedItemsJList.setModel(searchedItemsModel);
        showingListOfSongs = true;
        wasDisplayMorePressed = false;
    }

    private void displayMasterSearch() {
        searchedItemsModel.clear();
        masterSearch.forEach(pair -> searchedItemsModel.addElement(
                searchStrategy != SEARCH_FILTER.GENRE ? pair.getValue1() : pair.getValue0())
        );
        searchedItemsJList.setModel(searchedItemsModel);
        showMasterSearchedItems = true;
        showingListOfSongs = false;
        wasDisplayMorePressed = false;
    }

    @SuppressWarnings("unchecked")
    private void displayPlaylists() {
        JsonObject object = proxy.syncExecution("getUserPlaylists", userId);
        playlists = (ArrayList<Tuple2<String, String>>) proxy.adjustOutput(object);
        playlistModel.clear();
        if (playlists != null) {
            playlists.forEach(playlist -> playlistModel.addElement(playlist.getValue1()));
            playlistJList.setModel(playlistModel);
            lookingAtPlaylistList = true;
        }
    }

    @SuppressWarnings("unchecked")
    private void displayPlaylistSongs(int index) {
        JsonObject object = proxy.syncExecution("getPlaylistSongs", userId, playlists.get(index).getValue0());
        songsInPlaylist = (ArrayList<Tuple2<String, String>>) proxy.adjustOutput(object);
        if (songsInPlaylist != null) {
            playlistModel.clear();
            playlistModel.addElement(GO_BACK_SYMBOL);
            songsInPlaylist.forEach(song -> playlistModel.addElement(song.getValue1()));
            playlistJList.setModel(playlistModel);
            lookingAtPlaylistList = false;
        }
    }

    @SuppressWarnings("unchecked")
    private void displayCurrentlyPlayingSongs(boolean startPlayingFirst) {
        JsonObject object = proxy.syncExecution("getQueuedSongs", userId);
        songsInQueue = (ArrayList<Tuple2<String, String>>) proxy.adjustOutput(object);
        if (songsInQueue != null) {
            queueModel.clear();
            songsInQueue.forEach(song -> queueModel.addElement(song.getValue1()));
            queueJList.setModel(queueModel);

            musicPlayerMaster.setQueue(songsInQueue);
            queueJList.setSelectedIndex(0);
            if (startPlayingFirst) {
                musicPlayerMaster.play(songsInQueue.get(0));
                musicPlayerMaster.play(songsInQueue.get(0));
                object = proxy.syncExecution("getSongInfo", songsInQueue.get(0).getValue0());
                mainFrame.setTitle((String) proxy.adjustOutput(object));
                currentlyPlayingSongIndex = 0;
            }
        }
    }

    private String listToJsonString(ArrayList<Tuple2<String, String>> list) {

        JsonObject json = new JsonObject();
        list.forEach(song -> json.addProperty(song.getValue0(), song.getValue1()));
        return json.toString();
    }

    private String tupleToString(Tuple2<String, String> tuple2) {
        JsonObject json = new JsonObject();
        json.addProperty(tuple2.getValue0(), tuple2.getValue1());
        return json.toString();
    }

    private void playSelectedSong(Tuple2<String, String> songToPlay) {
        JsonObject object = proxy.syncExecution("addSongToQueue", userId, songToPlay.getValue0(), songToPlay.getValue1(), false);
        proxy.adjustOutput(object);
        displayCurrentlyPlayingSongs(true);
    }

    private boolean foundPlaylistFromInput(String input) {
        boolean found = false;
        for (int i = 0; i < playlists.size() && !found; i++) {
            found = playlists.get(i).getValue1().equals(input.trim());
        }
        return found;
    }

    private int getIndexOfPlaylist(String name) {
        int idx = -1;
        for (int i = 0; i < playlists.size() && idx == -1; i++) {
            if (playlists.get(i).getValue1().equals(name)) {
                idx = i;
            }
        }
        return idx;
    }

    private void createPlaylist(){
        //pop up menu prompts the user to insert a name for the new playlist created
        String playlistName = JOptionPane.showInputDialog(getUserProfilePanel(), "Name of Playlist", null);

        //if statement will check if the user insert a name for the playlist or if the new playlist was canceled
        //if the user provides a name for the new playlist, a playlist called playlistName will be saved
        if(playlistName != null){
            if (!playlistName.trim().equals("")) {
                JsonObject object = proxy.syncExecution("createPlaylist", userId, playlistName);
                proxy.adjustOutput(object);
                //updates the model
                displayPlaylists();
                lookingAtPlaylistList = true;
            }
        }
    }

    private void deletePlaylist() {
        String chosenPlaylist = JOptionPane.showInputDialog("Enter playlist name");
        if (chosenPlaylist != null) {
            JsonObject object = proxy.syncExecution("deletePlaylist", userId, chosenPlaylist);
            proxy.adjustOutput(object);
            displayPlaylists();
            lookingAtPlaylistList = true;
        }
    }


}