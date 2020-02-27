package streamingservice.clientside;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import streamingservice.serverside.Song;
import streamingservice.serverside.User;
import streamingservice.serverside.songinfo.Artist;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class MainUI {

    // set the width of the jframe window
    private static final int FRAME_WIDTH = 900;
    private static final int FRAME_HEIGHT = 600;

    // set the path to play the song
    private static final String SONG_TO_PLAY = "resources" + System.getProperty("file.separator") + "imperial.mp3";

    // set the path to files containing all user and song information
    private static final String MUSIC_FILE_PATH = "resources" + System.getProperty("file.separator") + "music.json";
    private static final String USER_FILE_PATH = "resources" + System.getProperty("file.separator") + "users.json";

    // values that will be used as search options besides the first element
    private static final String[] SEARCH_FILTERS = {"--- Search by ---", "Songs", "Artists"};

    private Gson gson;  // to read a json file

    private List<User> allUsers;        // holds all users in the user.json file
    private List<Song> allSongs;        // holds all songs in the music.json file
    private List<Song> songsOnDisplay;  // holds all searched for songs that will be displayed to the user

    // layout that will be used to switch between the log-in, create account, and user profile screens
    private CardLayout screenTransitionCardLayout;
    // layout that will be used to switch between showing a list of artists searched for and a list of songs
    // of a particular artist chosen from the list of artists searched for
    private CardLayout artistsAndTheirSongsCardLayout;
    // layout that will be used to switch between showing the user a list of songs/artist or an
    // image to show that there have been no searches found
    private CardLayout searchedItemsAndImageCardLayout;

    // frame to display everything
    private JFrame mainFrame;

    private JPanel root;    // the panel that holds everything

    // the log-in panel that holds widgets to simulate a log in screen
    private JPanel logInPanel;
    private JButton createAccountBtn;       // user clicks this button to go to the create account window
    private JLabel newUserLabel;            // label above the create account button to ask if they're new
    private JLabel usernameLabel;           // label used to show that they need a username to log-in
    private JTextField usernameInput;       // text field where a returning user can enter their username
    private JButton loginButton;            // user clicks this button to be redirected to their profile
    private JLabel usernameNotFoundLabel;   // label that tells the user that their input/username was not found

    // the create account panel that holds widgets to simulate a create account screen
    private JPanel createAccountPanel;
    private JLabel createAccountTitleLabel; // label to display the title "Create Account"
    // following labels "instructs" the user to enter the appropriate information to their right
    private JLabel userNameLabel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel reEmailLabel;
    // following text fields accepts information from the new user
    private JTextField userNameTF;
    private JTextField firstNameTF;
    private JTextField lastNameTF;
    private JTextField emailTF;
    private JTextField reEmailTF;
    // following labels display error messages
    private JLabel userNameErrorLabel;
    private JLabel firstNameErrorLabel;
    private JLabel lastNameErrorLabel;
    private JLabel emailErrorLabel;
    private JLabel reEmailErrorLabel;
    private JButton submitButton;      // review the information given and create an account for the user

    // the user profile panel that holds the widgets to simulate a user's profile
    private JPanel userProfilePanel;
    // panel to hold widgets that allow a user to search for songs and artists
    private JPanel songListPanel;
    private JLabel userNameDisplayLabel;        // label to display a user's username
    private JComboBox<String> searchFilter;     // combobox so the user can search by either song or artist
    private JLabel songListLabel;               // label to display: Song List
    private JTextField searchTF;                // text field where the user can enter a keyword to search
    private JButton searchButton;               // user can click to search for songs or artists based on the search text field
    // panel that holds widgets that will display a list of artists or songs
    private JPanel songListDisplayPanel;
    private JList<String> listOfSearchedSongs;          // holds a list of searched song names
    private DefaultListModel<String> songListModel;     // list model to hold the jlist of songs
    // determine if the the user is viewing an searched artists or a particular artist's songs
    private boolean showingArtists = false;
    private String artistSelected;                      // used to know what artist's songs the user is viewing
    // HashMap to contains an searched artist and a list of their respective songs
    private HashMap<String, ArrayList<Song>> artistsRespectiveSongs;
    private JList<String> listOfSearchedArtists;        // holds a list of searched artist's names
    private DefaultListModel<String> artistModel;       // list model to hold the jlist of artists

    // panel to hold widgets that allow a user to create and display a user's playlist
    private JPanel playlistPanel;
    private JLabel playlistLabel;               // label to display: Your Playlists
    private JButton addPlaylistButton;          // button that will create a new playlist
    private JPanel playlistDisplayPanel;        // panel that holds widgets that will display all user's playlists
    private JList<String> playlistList;         // holds a list of a user's playlist names
    private DefaultListModel<String> playlistModel; // list model to hold the jlist of playlists

    // panel that contains button to manage a song
    private JPanel musicPlayerPanel;
    private JButton repeatButton;
    private JButton previousButton;
    private JButton playButton;
    private JButton nextButton;
    private JButton shuffleButton;

    public MainUI() throws IOException {


        // screenTransitionCardLayout will allow for screen transitions
        screenTransitionCardLayout = new CardLayout();
        root.setLayout(screenTransitionCardLayout);
        root.add(logInPanel, "Log In");
        root.add(createAccountPanel, "Create Account");
        root.add(userProfilePanel, "User View");
        screenTransitionCardLayout.show(root, "Log In");

        // sets search to have values to search by
        searchFilter.setModel(new DefaultComboBoxModel<>(SEARCH_FILTERS));

        // hold the list songs that were found when searched
        songsOnDisplay = new ArrayList<>();
        // following allows for viewing the songs found
        songListModel = new DefaultListModel<>();
        listOfSearchedSongs = new JList<>(songListModel);
        listOfSearchedSongs.setSelectionBackground(Color.red);
        listOfSearchedSongs.setFont(new Font("Ayuthaya", Font.PLAIN, 16));
        listOfSearchedSongs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // cardholder to transition between displaying the songs and artists
        artistsAndTheirSongsCardLayout = new CardLayout();
        songListDisplayPanel.setLayout(artistsAndTheirSongsCardLayout);    // add the layout to the panel

        // allows us to scroll through the display of songs
        songListDisplayPanel.add(new JScrollPane(listOfSearchedSongs), "Display Songs");

        // will hold all artists and their corresponding songs
        artistsRespectiveSongs = new HashMap<>();
        // following allows for viewing the artists found
        artistModel = new DefaultListModel<>();
        listOfSearchedArtists = new JList<>(artistModel);
        listOfSearchedArtists.setSelectionBackground(Color.red);
        listOfSearchedArtists.setFont(new Font("Ayuthaya", Font.PLAIN,16));
        listOfSearchedArtists.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // allows us to scroll through the display of artists
        songListDisplayPanel.add(new JScrollPane(listOfSearchedArtists), "Artist's Songs");

        // button is in the log in screen.
        // moves the user to the screen where they will create an account
        createAccountBtn.addActionListener(e -> screenTransitionCardLayout.show(root, "Create Account"));

        // checks if the user is in the system when the log in button is clicked
        loginButton.addActionListener(e ->{
            // if the user is in the system show them their profile
            if(validateUsername(usernameInput.getText())){
                screenTransitionCardLayout.show(root, "User View");
                userNameDisplayLabel.setText(usernameInput.getText());
            }else{
                // show the user that they weren't found
                usernameInput.setText("");
                usernameNotFoundLabel.setVisible(true);
            }
        });

        // button is in the create account screen
        // check if the user has entered the necessary and valid information
        submitButton.addActionListener(e -> {
            if (checkIfAllEntriesFilled() && areAllNecessaryEntriesValid()) {
                createAccount();    // write their information to the users.json file
                userNameDisplayLabel.setText(userNameTF.getText()); // displays the users name
                screenTransitionCardLayout.show(root, "User View"); // go to user's profile
            }
        });

        // button is in the user view screen
        // will search by the option given in the searchFiler combo box
        searchButton.addActionListener(e -> {
            if (!searchTF.getText().trim().toLowerCase().equals("")) {  // textField not empty
                String selectedItem = Objects.requireNonNull(searchFilter.getSelectedItem()).toString();
                if (selectedItem.equals("Songs")) { // search by songs
                    // transition to the card that will display the songs
                    artistsAndTheirSongsCardLayout.show(songListDisplayPanel, "Display Songs");
                    searchBySongs();
                }
                else if (selectedItem.equals("Artists")) {  // search by artists
                    // transition to the card that will display the artists
                    artistsAndTheirSongsCardLayout.show(songListDisplayPanel, "Artist's Songs");
                    searchByArtists();
                }
            }
        });

        // if an item from the card that shows the list of songs search was double clicked,
        // then the song should begin playing
        listOfSearchedSongs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {   // captures a mouse double-click
                    playSongWhenSelected();
                }
            }
        });

        listOfSearchedArtists.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) { // captures a mouse double-click
                    // get the index of the item clicked on
                    int index = list.locationToIndex(evt.getPoint());
                    // if we are displaying the songs from a certain artist and the user wants to go
                    // back to view all artists searched...
                    if (!showingArtists && index == 0) {
                        // clear the model that contains the list of an artist's songs
                        // and populate it with artist's
                        artistModel.clear();
                        artistsRespectiveSongs.keySet().forEach(k -> artistModel.addElement(k));
                        listOfSearchedArtists.setModel(artistModel);
                        // showingArtists determines if we are viewing the names of artists or a particular artist's songs
                        showingArtists = true;
                        artistSelected = null;
                    }
                    // if a user double clicks on an artist's song, play the song
                    else if (!showingArtists && index > 0) {
                        playSongWhenSelected();
                    } else if (showingArtists) {
                        // display the name of songs by artistSelected
                        showingArtists = false; // set false to show that we'll display and artist's songs
                        // artistSelected holds the name of the artist that was chosen to view their songs
                        artistSelected = (String) artistsRespectiveSongs.keySet().toArray()[index];
                        // get the songs of this particular artist
                        ArrayList<Song> songs = artistsRespectiveSongs.get(artistsRespectiveSongs.keySet().toArray()[index]);
                        // clear the model that contains a list of artists and populate it with
                        // the songs from the artist selected
                        artistModel.clear();
                        artistModel.addElement("...");  // this is an element to go back to view a list of artists
                        songs.forEach(k -> artistModel.addElement(k.getSong().getTitle()));
                        listOfSearchedArtists.setModel(artistModel);
                    }
                }
            }
        });

        // when the user clicks on the playButton, a selected song will play
        playButton.addActionListener(e -> playSongWhenSelected());

        // sets up the frame that will display our screens
        mainFrame = new JFrame();
        mainFrame.add(root);
        mainFrame.pack();
        mainFrame.setFont(new Font("Courier", Font.BOLD, 20));
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        // load all the users and songs in the system
        gson = new Gson();
        allUsers = gson.fromJson(Files.newBufferedReader(Paths.get(USER_FILE_PATH)),
                new TypeToken<List<User>>() {}.getType());
        allSongs = gson.fromJson(Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH)),
                new TypeToken<List<Song>>() {}.getType());
    }

    /**
     * Will display the title, artist, and album information on the top of the window
     * whenever a song is selected from their respective list. The corresponding song will
     * also start playing.
     */
    private void playSongWhenSelected() {
        String view = Objects.requireNonNull(searchFilter.getSelectedItem()).toString();
        Song songChosen = null;
        // if searching by "Songs" and a song is selected get the song
        if (listOfSearchedSongs.getSelectedIndex() != -1 && view.equals("Songs")) {
            songChosen = songsOnDisplay.get(listOfSearchedSongs.getSelectedIndex());
        }

        // if searching by "Artists" and a song is selected, and we are viewing a list of an artist's songs, get the song
        if (listOfSearchedArtists.getSelectedIndex() > 0 && !showingArtists && view.equals("Artists")) {
            songChosen = artistsRespectiveSongs.get(artistSelected).get(listOfSearchedArtists.getSelectedIndex() - 1);
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
     * Searches for a song based on the keyword given from the user in the search text field. If a song title
     * contains the sequence of characters in the given text, then it will be added to the list of songs that
     * will be displayed to the user.
     */
    private void searchBySongs() {
        songListModel.clear();
        songsOnDisplay.clear();
        for (Song song : allSongs) {
            String title = song.getSong().getTitle();
            if (title.toLowerCase().contains(searchTF.getText().trim().toLowerCase())) {
                songListModel.addElement(title);
                songsOnDisplay.add(song);
            }
        }
        listOfSearchedSongs.setModel(songListModel);
    }

    /**
     * Searches for an artist whose name contains the sequence of characters given by the user in the
     * search text field. If an artist is found, then add their name to the list that will be displayed
     * to the user. The artist is also stored in a HashMap with their name as their key and an ArrayList of their
     * corresponding songs.
     */
    private void searchByArtists() {
        showingArtists = true;
        artistModel.clear();
        artistsRespectiveSongs.clear();
        for (Song song : allSongs) {
            Artist artist = song.getArtist();

            if (artist.getName().toLowerCase().contains(searchTF.getText().trim().toLowerCase())) {
                if (!artistsRespectiveSongs.containsKey(artist.getName())) {
                    artistsRespectiveSongs.put(artist.getName(), new ArrayList<>());
                }
                artistsRespectiveSongs.get(artist.getName()).add(song);
            }
        }
        for (String str : artistsRespectiveSongs.keySet()) { artistModel.addElement(str); }
        listOfSearchedArtists.setModel(artistModel);
    }

    /**
     * When a user creates a new account and all fields are valid, then we'll save the information given
     * to the users.json file.
     */
    private void createAccount() {
        User newUser = new User(firstNameTF.getText(), lastNameTF.getText(), emailTF.getText(), userNameTF.getText());
        if (allUsers == null) {
            allUsers = new ArrayList<>();
        }
        allUsers.add(newUser);
        try {
            // write the updated list to the file.
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            gson.toJson(allUsers, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if all the entries in the create account are valid.
     * If the entry is missing, then alert the user that the field is
     * needed. If at least one error label error is visible to the user,
     * then return false to signify that an entry is not filled; otherwise,
     * return true to represent that all entries are filled.
     *
     * @return true if all entries are filled in or false if at least one is empty
     */
    private boolean checkIfAllEntriesFilled() {
        if (userNameTF.getText().trim().toLowerCase().equals("")) {
            userNameErrorLabel.setVisible(true);
            userNameErrorLabel.setText("User Name Needed");
        } else {
            userNameErrorLabel.setText("");
            userNameErrorLabel.setVisible(false);
        }

        if (firstNameTF.getText().trim().equals("")) {
            firstNameErrorLabel.setVisible(true);
            firstNameErrorLabel.setText("First Name Needed");
        } else {
            firstNameErrorLabel.setText("");
            firstNameErrorLabel.setVisible(false);
        }

        if (lastNameTF.getText().trim().equals("")) {
            lastNameErrorLabel.setVisible(true);
            lastNameErrorLabel.setText("Last Name Needed");
        } else {
            lastNameErrorLabel.setText("");
            lastNameErrorLabel.setVisible(false);
        }

        if (emailTF.getText().trim().toLowerCase().equals("")) {
            emailErrorLabel.setVisible(true);
            emailErrorLabel.setText("Email Needed");
        } else {
            emailErrorLabel.setText("");
            emailErrorLabel.setVisible(false);
        }

        if (reEmailTF.getText().trim().toLowerCase().equals("")) {
            reEmailErrorLabel.setVisible(true);
            reEmailErrorLabel.setText("Email Re-entry Needed");
        } else {
            reEmailErrorLabel.setText("");
            reEmailErrorLabel.setVisible(false);
        }

        JLabel[] errorLabels = {userNameErrorLabel, firstNameErrorLabel, lastNameErrorLabel,
                emailErrorLabel, reEmailErrorLabel};
        boolean anyVisible = false;
        int i = 0;
        while (!anyVisible && i < errorLabels.length) {
            anyVisible = errorLabels[i++].isVisible();
        }
        return !anyVisible;
    }

    /**
     * Checks to see if the user entered all necessary entries and that they are free
     * to use their given entry.
     * @return true if the user can use the fields chosen, false if they can't
     */
    private boolean areAllNecessaryEntriesValid() {
        return isUserNameFreeToUse() && areEmailsTheSame() && isEmailFreeToUse();
    }

    /**
     * Determines if the user name given by the new user is available to use.
     * @return true if the wanted user name if free to use, false if the user name is taken
     */
    private boolean isUserNameFreeToUse() {
        boolean isFreeToUse = true;
        if (allUsers != null) {
            for (int i = 0; i < allUsers.size() && isFreeToUse; i++) {
                if (allUsers.get(i).getUserName().equals(userNameTF.getText())) {
                    isFreeToUse = false;
                }
            }
        }
        if (!isFreeToUse) {
            // tell the user name is in use
            userNameErrorLabel.setText("User Name already In Use");
            userNameErrorLabel.setVisible(true);
        } else {
            userNameErrorLabel.setText("");
            userNameErrorLabel.setVisible(false);
        }
        return isFreeToUse;
    }

    /**
     * Determines if the email given by the new user is available to use.
     * @return true if the user's email if free to use, false if the email is taken
     */
    private boolean isEmailFreeToUse() {
        boolean isFreeToUse = true;
        if (allUsers != null) {
            for (int i = 0; i < allUsers.size() && isFreeToUse; i++) {
                if (allUsers.get(i).getEmail().equals(userNameTF.getText())) {
                    isFreeToUse = false;
                }
            }
        }
        if (isFreeToUse) {
            // tell user the email is in use
            emailErrorLabel.setText("Email already In Use");
            emailErrorLabel.setVisible(true);
        } else {
            emailErrorLabel.setText("");
            emailErrorLabel.setVisible(false);
        }
        return isFreeToUse;
    }

    /**
     * Determines if the two emails required are the same one. If they are different,
     * then tell the user they don't match.
     * @return true if the given emails are the same, false otherwise
     */
    private boolean areEmailsTheSame() {
        boolean isSame = emailTF.getText().equals(reEmailTF.getText());
        if (!isSame) {
            reEmailErrorLabel.setText("Emails are different");
            reEmailErrorLabel.setVisible(true);
        } else {
            reEmailErrorLabel.setText("");
            reEmailErrorLabel.setVisible(false);
        }
        return isSame;
    }

    /**
     * Searches through the users.json file to determine if the given user name
     * already belongs to another account. If the name exists in the system, then
     * return false; otherwise, return true to signify that the name is open for use.
     * @param username the user name wished to use by the new user
     * @return true if the username is free to use, false otherwise
     */
    private boolean validateUsername(String username){
        boolean ifFound = false;

        JsonParser jsonParser = new JsonParser();

        try(FileReader reader = new FileReader(USER_FILE_PATH)){
            //Read Json file
            Object obj = jsonParser.parse(reader);

            JsonArray userList = (JsonArray) obj;

            /*checks to see if the json file is empty. If the json file is not empty then the method
             * will iterate through all the json file to find the userName that the user input */
            if(userList.size() != 0){
                //iterate through the file
                for(int i = 0; i < userList.size(); i++){
                    JsonObject userObject = (JsonObject) userList.get(i);

                    //is saving the userName of userObject
                    String userNameIs = userObject.get("userName").toString();
                    //removing the "" from the string
                    userNameIs = userNameIs.replace("\"", "");

                    //checks to see if the names are the same
                    if(username.equals(userNameIs)){
                        ifFound = true;
                    }
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return ifFound;
    }


}