package streamingservice.UI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import streamingservice.CECS327InputStream;
import streamingservice.music.Song;
import streamingservice.music.User;
import streamingservice.music.songinfo.Artist;

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

    private static final int FRAME_WIDTH = 900;
    private static final int FRAME_HEIGHT = 600;
    private static final String SONG_TO_PLAY = "resources" + System.getProperty("file.separator") + "imperial.mp3";
    private static final String MUSIC_FILE_PATH = "resources" + System.getProperty("file.separator") + "music.json";
    private static final String USER_FILE_PATH = "resources" + System.getProperty("file.separator") + "users.json";
    private static final String[] SEARCH_FILTERS = {"--- Search by ---", "Songs", "Artists"};

    private static final boolean PLAYING_SONG = false;

    private Gson gson;
    private List<User> allUsers;
    private List<Song> allSongs;
    private List<Song> songsOnDisplay;

    private CardLayout cardLayout;
    private CardLayout songArtistLayout;
    private JFrame mainFrame;

    private JPanel root;
    private JPanel LogIn;
    private JPanel AccountCreator;
    private JPanel UserView;
    private JLabel titleLabel;
    private JLabel userNameLabel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel reEmailLabel;
    private JTextField userNameTF;
    private JTextField firstNameTF;
    private JTextField lastNameTF;
    private JTextField emailTF;
    private JTextField reEmailTF;
    private JLabel userNameErrorLabel;
    private JLabel firstNameErrorLabel;
    private JLabel lastNameErrorLabel;
    private JLabel emailErrorLabel;
    private JLabel reEmailErrorLabel;
    private JButton submitButton;
    private JButton createAccountBtn;
    private JLabel userNameDisplayLabel;
    private JComboBox<String> searchFilter;
    private JLabel songListLabel;
    private JButton addPlaylistButton;
    private JList<String> playlistList;
    private JList<String> listOfSongs;
    private JButton repeatButton;
    private JButton previousButton;
    private JButton playButton;
    private JButton nextButton;
    private JButton shuffleButton;
    private JButton searchButton;
    private JPanel songListPanel;
    private JPanel playlistPanel;
    private JLabel playlistLabel;
    private JPanel musicPlayerPanel;
    private JTextField searchTF;
    private JPanel songListHolderPanel;
    private JPanel playlistHolderLabel;
    private DefaultListModel<String> listModel;

    private boolean showingArtists = false;
    private String artistSelected;
    private HashMap<String, ArrayList<Song>> artistSongs;
    private JList<String> artistList;
    private DefaultListModel<String> artistModel;

    public MainUI() throws IOException, JavaLayerException {

        InputStream stream = new CECS327InputStream(SONG_TO_PLAY);
        Player player = new Player(stream);

        cardLayout = new CardLayout();
        root.setLayout(cardLayout);
        root.add(LogIn, "Log In");
        root.add(AccountCreator, "Create Account");
        root.add(UserView, "User View");


        searchFilter.setModel(new DefaultComboBoxModel<>(SEARCH_FILTERS));

        songsOnDisplay = new ArrayList<>();

        listModel = new DefaultListModel<>();
        listOfSongs = new JList<>(listModel);
        listOfSongs.setSelectionBackground(Color.red);
        listOfSongs.setFont(new Font("Ayuthaya", Font.PLAIN,14));
        listOfSongs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        songArtistLayout = new CardLayout();
        songListHolderPanel.setLayout(songArtistLayout);

        songListHolderPanel.add(new JScrollPane(listOfSongs), "Display Songs");


        artistSongs = new HashMap<>();
        artistModel = new DefaultListModel<>();
        artistList = new JList<>(artistModel);
        artistList.setSelectionBackground(Color.red);
        artistList.setFont(new Font("Ayuthaya", Font.PLAIN,14));
        artistList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        songListHolderPanel.add(new JScrollPane(artistList), "Artist's Songs");

        cardLayout.show(root, "User View");

        createAccountBtn.addActionListener(e -> cardLayout.show(root, "Create Account"));


        submitButton.addActionListener(e -> {
            if (checkIfAllEntriesFilled() && areAllNecessaryEntriesValid()) {
                createAccount();
                userNameDisplayLabel.setText(userNameTF.getText()); // displays the users name
                cardLayout.show(root, "User View"); // go to user's profile
            }
        });

        searchButton.addActionListener(e -> {
            if (!searchTF.getText().trim().toLowerCase().equals("")) {  // textField not empty
                String selectedItem = Objects.requireNonNull(searchFilter.getSelectedItem()).toString();
                if (selectedItem.equals("Songs")) {
                    songArtistLayout.show(songListHolderPanel, "Display Songs");
                    searchBySongs();
                }
                else if (selectedItem.equals("Artists")) {
                    songArtistLayout.show(songListHolderPanel, "Artist's Songs");
                    searchByArtists();
                }
            }
        });

        listOfSongs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playSongWhenSelected();
                }
            }
        });

        artistList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    if (!showingArtists && index == 0) {
                        // display the artists names
                        artistModel.clear();
                        for (String str : artistSongs.keySet()) {
                            artistModel.addElement(str);
                        }
                        artistList.setModel(artistModel);
                        showingArtists = true;
                        artistSelected = null;
                    } else if (!showingArtists && index > 0) {
                        playSongWhenSelected();
                    } else if (showingArtists) {
                        // display the name of songs by artistSelected
                        artistModel.clear();
                        showingArtists = false;
                        artistSelected = (String) artistSongs.keySet().toArray()[index];
                        ArrayList<Song> songs = artistSongs.get(artistSongs.keySet().toArray()[index]);
                        artistModel.addElement("...");
                        songs.forEach(k -> artistModel.addElement(k.getSong().getTitle()));
                        artistList.setModel(artistModel);
                    }
                }
            }
        });

        playButton.addActionListener(e -> {
            playSongWhenSelected();
        });

        mainFrame = new JFrame();
        mainFrame.add(root);
        mainFrame.pack();
        mainFrame.setFont(new Font("Courier", Font.BOLD, 20));
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        gson = new Gson();
        Reader userReader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
        Reader musicReader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH));
        allUsers = gson.fromJson(userReader, new TypeToken<List<User>>() {}.getType());
        allSongs = gson.fromJson(musicReader, new TypeToken<List<Song>>() {}.getType());
    }

    private void playSongWhenSelected() {
        String view = Objects.requireNonNull(searchFilter.getSelectedItem()).toString();
        Song songChosen = null;
        if (listOfSongs.getSelectedIndex() != -1 && view.equals("Songs")) {
            songChosen = songsOnDisplay.get(listOfSongs.getSelectedIndex());
        }

        if (artistList.getSelectedIndex() > 0 && !showingArtists && view.equals("Artists")) {
            songChosen = artistSongs.get(artistSelected).get(artistList.getSelectedIndex() - 1);
        }

        if (songChosen  != null) {
            String title = songChosen.getSong().getTitle();
            String album = songChosen.getRelease().getName();
            String artist = songChosen.getArtist().getName();

            mainFrame.setTitle(title + " by " + album + " from " + artist);
        }
    }

    private void searchBySongs() {
        listModel.clear();
        songsOnDisplay.clear();
        for (Song song : allSongs) {
            String title = song.getSong().getTitle();
            if (title.toLowerCase().contains(searchTF.getText().trim().toLowerCase())) {
                listModel.addElement(title);
                songsOnDisplay.add(song);
            }
        }
        listOfSongs.setModel(listModel);
    }

    private void searchByArtists() {
        showingArtists = true;
        artistModel.clear();
        artistSongs.clear();
        for (Song song : allSongs) {
            Artist artist = song.getArtist();

            if (artist.getName().toLowerCase().contains(searchTF.getText().trim().toLowerCase())) {
                if (!artistSongs.containsKey(artist.getName())) {
                    artistSongs.put(artist.getName(), new ArrayList<>());
                }
                artistSongs.get(artist.getName()).add(song);
            }
        }

        for (String str : artistSongs.keySet()) { artistModel.addElement(str); }
        artistList.setModel(artistModel);
    }

    private void createAccount() {
        User newUser = new User(firstNameTF.getText(), lastNameTF.getText(), emailTF.getText(), userNameTF.getText());
        if (allUsers == null) {
            allUsers = new ArrayList<>();
        }
        allUsers.add(newUser);
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            gson.toJson(allUsers, writer);
            writer.close();
        } catch (IOException e) {
            showErrorWindow();
        }
    }

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

        JLabel[] labels = {userNameErrorLabel, firstNameErrorLabel, lastNameErrorLabel, emailErrorLabel, reEmailErrorLabel};
        boolean anyVisible = false;
        int i = 0;
        while (!anyVisible && i < labels.length) {
            anyVisible = labels[i++].isVisible();
        }
        return !anyVisible;
    }

    private boolean areAllNecessaryEntriesValid() {
        return isUserNameFreeToUse() && areEmailsTheSame() && isEmailFreeToUse();
    }

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
            userNameErrorLabel.setText("User Name already In Use");
            userNameErrorLabel.setVisible(true);
        } else {
            userNameErrorLabel.setText("");
            userNameErrorLabel.setVisible(false);
        }
        return isFreeToUse;
    }

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
            emailErrorLabel.setText("Email already In Use");
            emailErrorLabel.setVisible(true);
        } else {
            emailErrorLabel.setText("");
            emailErrorLabel.setVisible(false);
        }
        return isFreeToUse;
    }

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

    private void showErrorWindow() {
        JFrame frame = new JFrame();
        frame.add(new JPanel().add(new JLabel("Something went wrong. Try Again Later.")));
        frame.setSize(200, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }

}