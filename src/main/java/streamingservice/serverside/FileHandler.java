package streamingservice.serverside;


import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import streamingservice.clientside.panels.SEARCH_FILTER;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FileHandler {

    // set the path to files containing all user and song information
    // set the path to files containing all user and song information
    private static final String FS = System.getProperty("file.separator");
    private static final String MP3_DIRECTORY_PATH = "resources" + FS + "mp3s" + FS;
    private static final String IMPERIAL_SONG_PATH = MP3_DIRECTORY_PATH + "490183.mp3";

    private static final String JSON_FILES_PATH = "src"+FS+"main"+FS+"java"+FS+"streamingservice"+FS+"serverside"+FS;
    private static final String MUSIC_FILE_PATH = JSON_FILES_PATH+"music.json";
    private static final String USER_FILE_PATH = JSON_FILES_PATH+"users.json";

    private static final String USER_NAME_FIELD = "userName";
    private static final String EMAIL_FIELD = "email";
    private static final String ID_FIELD = "id";
    private static final String EMPTY_FILE_INDICATOR = "[{}]";

    private static final Gson GSON = new Gson();

    private static int isNameInSystemIdx(String userName) {
        return isContainedInUsersFile(userName, USER_NAME_FIELD);
    }

    private static int isIdInSystemIdx(String id) {
        return isContainedInUsersFile(id, ID_FIELD);
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
     * @param first first name of the user to add
     * @param last last name of the user to add
     * @param email email of the user to add
     * @param userName userName of the user to add
     * @return a String specifying if the new user's id or null if the user wasn't created
     */
    public static String addUserToSystem(String first, String last, String email, String userName) {
        
        String returnId = null;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            if (allUsers == null) { allUsers = new ArrayList<>(); }
            allUsers.add(new User(first, last, email, userName));

            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            GSON.toJson(allUsers, writer);
            writer.close();
            returnId = allUsers.get(allUsers.size() - 1).getId().toString();
        } catch (IOException ignored){ }
        return returnId;
    }

    public static String getUserPlaylists(String id) {
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            //ArrayList<Tuple2<String, String>> playlists = new ArrayList<>();
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            int index = isIdInSystemIdx(id);
            JsonObject object = new JsonObject();
            if (allUsers.get(index).getNumberOfPlaylists() != 0) {
                for (Playlist playlist : allUsers.get(index).getPlaylists()) {
                    //playlists.add(new Tuple2<>(playlist.getId(), playlist.getPlaylistName()));
                    object.addProperty(playlist.getId(), playlist.getPlaylistName());
                }
                return object.toString();
            }
            return "null";
        } catch (IOException ignored) { }
        return "null";
    }

    public static String getPlaylistSongs(String userId, String playlistId) {
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            int index = isIdInSystemIdx(userId);
            JsonObject object = new JsonObject();
            List<Playlist> playlists = allUsers.get(index).getPlaylists();
            if (playlists.size() != 0) {
                for (int i = 0; i < playlists.size() && object.size() == 0; i++) {
                    if (playlists.get(i).getId().equals(playlistId)) {
                        playlists.get(i).getSongs().forEach(tuple -> object.addProperty(tuple.getValue0(), tuple.getValue1()));
                    }
                }
                return object.toString();
            }
            return "null";
        } catch (IOException ignored) { }
        return "null";
    }

    public static void updateUserPlaylists(String userId, String playlistId, String songId, String songName, boolean shouldAdd) {
        boolean wasUpdated = false;
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            List<Playlist> playlists = allUsers.get(isIdInSystemIdx(userId)).getPlaylists();
            for (int i = 0; i < playlists.size() && !wasUpdated; i++) {
                if (playlists.get(i).getId().equals(playlistId)) {
                    if (shouldAdd) {    // add the song to playlist
                        playlists.get(i).addSong(new Tuple2<>(songId, songName));
                        wasUpdated = true;
                    } else {    // delete the song from the playlist
                        ArrayList<Tuple2<String, String>> songs = playlists.get(i).getSongs();
                        for (int j = 0; j < songs.size() && !wasUpdated; j++) {
                            if (songs.get(j).getValue0().equals(songId)) {
                                playlists.get(i).removeSong(j);
                                wasUpdated = true;
                            }
                        }
                    }
                }
            }
            if (wasUpdated) {
                Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
                GSON.toJson(allUsers, writer);
                writer.close();
            }
        }catch(IOException ignored){ }
    }

    public static void createPlaylist(String userId, String newPlaylistName) {
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            if (!doesPlaylistExist(userId, newPlaylistName)) {
                Playlist newPlaylist = new Playlist(newPlaylistName);
                allUsers.get(isIdInSystemIdx(userId)).addPlaylist(newPlaylist);
            }
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            GSON.toJson(allUsers, writer);
            writer.close();
        }catch(IOException ignored){ }
    }

    public static void deletePlaylist(String userId, String playlistName) {
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();

            if (doesPlaylistExist(userId, playlistName)) {
                User user = allUsers.get(isIdInSystemIdx(userId));
                List<Playlist> playlists = user.getPlaylists();
                int j = -1;
                for (int i = 0; i < playlists.size() && j == -1; i++) {
                    if (playlists.get(i).getPlaylistName().equals(playlistName)) {
                        j = i;
                    }
                }
                user.removePlaylist(j);
            }

            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            GSON.toJson(allUsers, writer);
            writer.close();
        }catch(IOException ignored){ }
    }

    private static boolean doesPlaylistExist(String userId, String playlistName) {
        boolean exists = false;
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            List<Playlist> playlists = allUsers.get(isIdInSystemIdx(userId)).getPlaylists();
            for (int i = 0;  i < playlists.size() && !exists; i++) {
                exists = playlists.get(i).getPlaylistName().equals(playlistName);
            }
        }catch(IOException ignored){ }
        return exists;
    }

    public static boolean isSongInPlaylist(String userId, String playlistId, String songId) {
        boolean isContained = false;
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            List<Playlist> playlists = allUsers.get(isIdInSystemIdx(userId)).getPlaylists();
            for (int i = 0; i < playlists.size() && !isContained; i++) {
                if (playlists.get(i).getId().equals(playlistId)) {
                    ArrayList<Tuple2<String, String>> songs = playlists.get(i).getSongs();
                   for (int j = 0; j < songs.size() && !isContained; j++) {
                       isContained = songs.get(j).getValue0().equals(songId);
                   }
                }
            }
        }catch(IOException ignored){ }
        return isContained;
    }

    public static String getUserName(String id) {
        int index = isIdInSystemIdx(id);
        if (index == -1) return "";
        String value = "";
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            value = allUsers.get(index).getUserName();
        } catch (IOException ignored){ }

        return value;
    }

    public static String getUserId(String userName) {
        int index = isNameInSystemIdx(userName);
        if (index == -1) return "";
        String id = "";
        try(Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            id = allUsers.get(index).getId().toString();
        } catch (IOException ignored){ }
        return id;
    }

    public static boolean userHasPlaylists(String id) {
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            int index = isIdInSystemIdx(id);
            return allUsers.get(index).getNumberOfPlaylists() != 0;
        } catch (IOException ignored) { }
        return false;
    }

    public static String getListOf(String filter, String keyword, boolean searchByID, String idFilter) {
        try (Reader reader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH))) {
            List<Song> allSongs = GSON.fromJson(reader, new TypeToken<List<Song>>() {}.getType());
            reader.close();

            SEARCH_FILTER filter1 = SEARCH_FILTER.fromValue(filter);
            SEARCH_FILTER idFilter1 = null;
            if (idFilter != null) { idFilter1 = SEARCH_FILTER.fromValue(idFilter); }

            JsonObject object = new JsonObject();
            // goes through the list of all songs and gets those songs whose title contains the keyword given
            for (Song song : allSongs) {
                String id = song.getID(searchByID ? idFilter1 : filter1);
                String name = song.getName(searchByID ? idFilter1 : filter1);
                String value = searchByID ? id : (name != null ? name : id);
                if (value.toLowerCase().contains(keyword.toLowerCase().trim()) && isFreeToAdd(object, id)) {
                    if (searchByID) {
                        object.addProperty(song.getID(filter1), song.getName(filter1));
                    } else {
                        object.addProperty(id, name);
                    }
                }
            }
            return object.toString();
        } catch (IOException ignored) { }
        return "null";
    }

    private static boolean isFreeToAdd(JsonObject jsonObject, String song) {
        boolean isFreeToAdd = true;
        Iterator<Map.Entry<String, JsonElement>> it = jsonObject.entrySet().iterator();
        while (it.hasNext() && isFreeToAdd) {
            if (it.next().getKey().equals(song)) {
                isFreeToAdd = false;
            }
        }
        return isFreeToAdd;
    }

    private static boolean freeToAdd(List<Tuple2<String, String>> list, Tuple2<String, String> pair) {
        boolean isFreeToAdd = true;
        for (int i = 0; i < list.size() && isFreeToAdd; i++) {
            if (list.get(i).getValue0().equals(pair.getValue0())) {
                isFreeToAdd = false;
            }
        }
        return isFreeToAdd;
    }

    public static void addSongToQueue(String userId, String songId, String songName, boolean addLast) {
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            Tuple2<String, String> song = new Tuple2<>(songId, songName);
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            ArrayList<Tuple2<String, String>> queue = allUsers.get(isIdInSystemIdx(userId)).getQueuedSongs();
            if (freeToAdd(queue, song)) {
                if (addLast) { queue.add(song);
                } else { queue.add(0, song); }
            }
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            GSON.toJson(allUsers, writer);
            writer.close();
        } catch (IOException ignored) { }

    }

    public static String getQueuedSongs(String userId) {
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            Gson GSON = new Gson();
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            JsonObject jsonObject = new JsonObject();
            allUsers.get(isIdInSystemIdx(userId)).getQueuedSongs()
                    .forEach(song -> jsonObject.addProperty(song.getValue0(), song.getValue1()));
            return jsonObject.toString();
        } catch (IOException ignored) { }
        return null;
    }

    public static void clearQueue(String userId) {
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            allUsers.get(isIdInSystemIdx(userId)).getQueuedSongs().clear();
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            GSON.toJson(allUsers, writer);
            writer.close();
        } catch (IOException ignored) { }
    }

    public static void adjustQueue(String userId, int index, boolean removeOnlyIndex) {
        try (Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH))) {
            List<User> allUsers = GSON.fromJson(reader, new TypeToken<List<User>>() {}.getType());
            reader.close();
            ArrayList<Tuple2<String, String>> queuedSongs = allUsers.get(isIdInSystemIdx(userId)).getQueuedSongs();
            if (index != 0) {
                if (!removeOnlyIndex) {
                    queuedSongs.set(0, queuedSongs.get(index));
                }
                for (int i = index; i < queuedSongs.size() - 1; i++) {
                    queuedSongs.set(i, queuedSongs.get(i + 1));
                }
                queuedSongs.remove(queuedSongs.size() - 1);
                Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
                GSON.toJson(allUsers, writer);
                writer.close();
            }
        } catch (IOException ignored) { }
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
            if (userList != null) {
                /*checks to see if the json file is empty. If the json file is not empty then the method
                 * will iterate through all the json file to find the userName that the user input */
                if (!userList.toString().equals(EMPTY_FILE_INDICATOR)) {    // check only if there are people in the system
                    //iterate through the file
                    // !ifFound is used to exit the loop once a match is found
                    for (int i = 0; i < userList.size() && userIndex == -1; i++) {
                        //is saving the userName of userObject
                        String userInfo = ((JsonObject) userList.get(i)).get(searchBy).toString();
                        //removing the "" from the string
                        userInfo = userInfo.replace("\"", "");
                        //checks to see if the names are the same
                        userIndex = searchInput.equals(userInfo) ? i : -1;
                    }
                }
            }
        } catch(IOException | ClassCastException ignored){ }
        return userIndex;
    }

    public static String getSongInfo(String songId) {
        String info = "";
        try (Reader reader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH))) {
            List<Song> allSongs = GSON.fromJson(reader, new TypeToken<List<Song>>() {}.getType());
            reader.close();
            // goes through the list of all songs and gets those songs whose title contains the keyword given
            for (int i = 0; i < allSongs.size() && info.equals(""); i++) {
                if (allSongs.get(i).getSong().getId().equals(songId)) {
                    Song song = allSongs.get(i);
                    info = song.getSong().getTitle() + " by " + song.getArtist().getName() + " on " + song.getRelease().getName();
                }
            }
        } catch (IOException ignored) { }
        return info;
    }

    /**
     * This method will find the path of the song in the system.
     * But for testing purposes, only use the imperial sound
     * @param song a <Code>Song</Code> object
     * @return the relative path of the song
     */
    public static String getSongPath(Tuple2<String, String> song) {
        //TODO: Uncomment following line and remove return IMPERIAL_SONG_PATH when there is a mp3 associated with each song in music.json.
        // return MP3_DIRECTORY_PATH + song.getValue0() + ".mp3";
        return IMPERIAL_SONG_PATH;
    }
}