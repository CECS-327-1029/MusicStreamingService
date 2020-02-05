package streamingservice.UI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import streamingservice.music.Song;
import streamingservice.music.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainUI {

    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final String MUSIC_FILE_PATH = "resources" + System.getProperty("file.separator") + "music.json";
    private static final String USER_FILE_PATH = "resources" + System.getProperty("file.separator") + "users.json";
    private static final String[] SEARCH_FILTERS = {"--- Search by ---", "Songs", "Artists"};

    private Gson gson;
    private List<User> users;
    private List<Song> songs;

    private CardLayout cardLayout;

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
    private JComboBox searchFilter;
    private JLabel songListLabel;
    private JButton addPlaylistButton;
    private JList playlistList;
    private JList listOfSongs;
    private JLabel currentSong;
    private JLabel currentArtist;
    private JLabel currentAlbum;
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
    //private JScrollPane listOfSongsScrollBar;


    public MainUI() throws IOException {

        cardLayout = new CardLayout();
        root.setLayout(cardLayout);
        root.add(LogIn, "Log In");
        root.add(AccountCreator, "Create Account");
        root.add(UserView, "User View");

        listOfSongs.setBorder(BorderFactory.createLineBorder(Color.black));
        playlistList.setBorder(BorderFactory.createLineBorder(Color.black));
        searchFilter.setModel(new DefaultComboBoxModel(SEARCH_FILTERS));

        //listOfSongsScrollBar = new JScrollPane(listOfSongs);

        cardLayout.show(root, "User View");

        createAccountBtn.addActionListener(e -> {
            cardLayout.show(root, "Create Account");
        });


        submitButton.addActionListener(e -> {
            if (checkIfAllEntriesFilled() && areAllNecessaryEntriesValid()) {
                createAccount();
                userNameDisplayLabel.setText(userNameTF.getText());
                cardLayout.show(root, "User View");
            }
        });

        searchButton.addActionListener(e -> {
            if (!searchTF.getText().trim().toLowerCase().equals("")) {
                String selectedItem = searchFilter.getSelectedItem().toString();
                if (selectedItem.equals("Songs")) {
                    // search by songs
                    searchBySongs();
                }
                else if (selectedItem.equals("Artists")) {
                    // search for songs

                }
            }
        });

        /*
        listOfSongs.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList theList = (JList) e.getSource();
                if (e.getClickCount() == 2) {
                    int index = theList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        Object o = theList.getModel().getElementAt(index);
                        System.out.println("Double-clicked on: " + o.toString());
                    }
                }
            }
        });
        */


        JFrame mainFrame = new JFrame("Music Streaming Service");
        mainFrame.add(root);
        mainFrame.pack();
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        gson = new Gson();
        Reader userReader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
        Reader musicReader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH));
        users = gson.fromJson(userReader, new TypeToken<List<User>>() {}.getType());
        songs = gson.fromJson(musicReader, new TypeToken<List<Song>>() {}.getType());
    }

    private void searchBySongs() {
        ArrayList<Song> songsFound = new ArrayList<>();
        for (Song song : songs) {
            if (song.getSong().getTitle().toLowerCase().contains(searchTF.getText().trim().toLowerCase())) {
                songsFound.add(song);
                System.out.println(song.getSong().getTitle());
            }
        }

    }

    private void createAccount() {
        User newUser = new User(firstNameTF.getText(), lastNameTF.getText(), emailTF.getText(), userNameTF.getText());
        //userNameDisplayLabel.setText(userNameTF.getText());
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(newUser);
        try {
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            gson.toJson(users, writer);
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
        if (users != null) {
            for (int i = 0; i < users.size() && isFreeToUse; i++) {
                if (users.get(i).getUserName().equals(userNameTF.getText())) {
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
        if (users != null) {
            for (int i = 0; i < users.size() && isFreeToUse; i++) {
                if (users.get(i).getEmail().equals(userNameTF.getText())) {
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
