package streamingservice.music;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import streamingservice.music.songinfo.Artist;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileHandler {

    // set the path to files containing all user and song information
    private static final String MUSIC_FILE_PATH = "resources" + System.getProperty("file.separator") + "music.json";
    private static final String USER_FILE_PATH = "resources" + System.getProperty("file.separator") + "users.json";

    private static final String USER_NAME_FIELD = "userName";
    private static final String EMAIL_FIELD = "email";
    private static final String EMPTY_FILE_INDICATOR = "[{}]";

    /**
     * Looks through the users.json file to check if the given user name is in the system.
     * @param userName the name given by the user
     * @return true if the user name was found, false if it wasn't found
     */
    public static boolean isNameInSystem(String userName) {
        return isContainedInUsersFile(userName, USER_NAME_FIELD) != -1;
    }

    public static int isNameInSystemIdx(String userName) {
        return isContainedInUsersFile(userName, USER_NAME_FIELD);
    }

    /**
     * Looks through the users.json file to determine if the user name given by the user is free to use.
     * @param userName the name given by the user
     * @return true if user can use this user name, false if the user name is in use
     */
    public static boolean isUserNameFreeToUse(String userName) {
        return isContainedInUsersFile(userName, USER_NAME_FIELD) == -1;
    }

    /**
     * Looks through the users.json file to determines if the email given by the user is free to use.
     * @param email the email given by the user
     * @return true if user can use this email, false if the email is in use
     */
    public static boolean isEmailFreeToUse(String email) {
        return isContainedInUsersFile(email, EMAIL_FIELD) == -1;
    }

    /**
     * Adds the user passed into the user.json file.
     * @param user the new user to be added
     * @return a boolean specifying if the user was or wasn't added
     */
    public static boolean addUserToSystem(User user) {
        Gson gson = new Gson();
        boolean wasUserAdded = false;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
            List<User> allUsers = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();

            if (allUsers == null) { allUsers = new ArrayList<>(); }
            allUsers.add(user);

            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            gson.toJson(allUsers, writer);
            writer.close();
            wasUserAdded = true;
        } catch (IOException e) { e.printStackTrace(); }
        return wasUserAdded;
    }

    /**
     * First, the function determines if the user is in the user.json file. If so, then retrieve that user
     * from the file based on its index given from the function <code>isNameInSystemIdx</code>.
     * If the user is not found or there was an error opening the file, then return null.
     * @param userName the user name of the user to retrieve
     * @return a <code>User</code> object or null
     */
    public static User getUser(String userName) {
        int index = isNameInSystemIdx(userName);
        if (index == -1) return null;
        User user = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
            List<User> allUsers = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();

            user = allUsers.get(index);
        } catch (IOException e) { e.getMessage(); }

        return user;
    }

    /**
     * Searches the file and gets all the songs whose title contains the keyword being searched for.
     * @param keyword the name of a song or a substring of it
     * @return an <code>ArrayList</code> of the type <code>Song</code> or null if an error occurred.
     */
    public static ArrayList<Song> searchForSongs(String keyword) {
        ArrayList<Song> songsFound = new ArrayList<>();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH));
            List<Song> allSongs = new Gson().fromJson(reader, new TypeToken<List<Song>>() {}.getType());
            reader.close();

            // goes through the list of all songs and adds those songs whose title contains the keyword given
            allSongs.stream()
                    .filter(song -> song.getSong().getTitle().toLowerCase().contains(keyword.toLowerCase().trim()))
                    .forEachOrdered(songsFound::add);

            Collections.sort(songsFound);
        } catch (IOException e) { songsFound = null; }
        return songsFound;
    }

    /**
     * Searches the file and gets all the artists whose name contains the keyword begin searched for.
     * The artist is stored in a map along side a list of all their songs.
     * @param keyword the name of an artist or a substring of it
     * @return a <Code>TreeMap</Code> containing all artists along with a list of their songs
     */
    public static TreeMap<Artist, ArrayList<Song>> searchForArtists(String keyword) {
        TreeMap<Artist, ArrayList<Song>> artistSongMap = new TreeMap<>();
        try {
            // get all songs from the music.json file
            Reader reader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH));
            List<Song> allSongs = new Gson().fromJson(reader, new TypeToken<List<Song>>() {}.getType());
            reader.close();

            for (Song song : allSongs) {
                Artist artist = song.getArtist();

                if (artist.getName().toLowerCase().contains(keyword.toLowerCase().trim())) { // keyword is in the artist's name
                    if (!artistSongMap.containsKey(artist)) {   // initialize an artist's list if it isn't
                        artistSongMap.put(artist, new ArrayList<>());
                    }
                    artistSongMap.get(artist).add(song);    // add the song to the list
                }
            }

            // sort the list of songs of each user
            artistSongMap.forEach((artist, songs) -> Collections.sort(songs));
        } catch (IOException e) { artistSongMap = null; }

        return artistSongMap;
    }

    /**
     * Searches through the users.json file to determine if the given search input
     * already belongs to another account. If the search input exists in the system, then
     * return -1; otherwise, return its index position to signify that the name is open for use.
     * The input can be searched by a parameter in the user.json file, i.e. the user name or email.
     * @param searchInput the user name wished to use by the new user
     * @param  searchBy the method by which we want to compare the search input
     * @return -1 if the search input was not found, else it's index position in the file
     */
    private static int isContainedInUsersFile(String searchInput, String searchBy){
        int userIndex = -1;

        try(FileReader reader = new FileReader(USER_FILE_PATH)){
            //Read Json file
            JsonArray userList = (JsonArray) new JsonParser().parse(reader);
            /*checks to see if the json file is empty. If the json file is not empty then the method
             * will iterate through all the json file to find the userName that the user input */
            if(!userList.toString().equals(EMPTY_FILE_INDICATOR)){    // check only if there are people in the system
                //iterate through the file
                // !ifFound is used to exit the loop once a match is found
                for(int i = 0; i < userList.size() && userIndex == -1; i++){
                    //is saving the userName of userObject
                    String userInfo = ((JsonObject) userList.get(i)).get(searchBy).toString();
                    //removing the "" from the string
                    userInfo = userInfo.replace("\"", "");
                    //checks to see if the names are the same
                    userIndex = searchInput.equals(userInfo) ? i : -1;
                }
            }
        } catch(IOException | ClassCastException e){
            System.out.println(e.getMessage());
        }
        return userIndex;
    }


}