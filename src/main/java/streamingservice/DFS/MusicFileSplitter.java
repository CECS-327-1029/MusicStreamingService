package streamingservice.DFS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import streamingservice.serverside.Song;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MusicFileSplitter {
    private static final String FS = System.getProperty("file.separator");

    private static final String JSON_FILES_PATH = "src"+FS+"main"+FS+"java"+FS+"streamingservice"+FS+"serverside"+FS;
    private static final String MUSIC_FILE_PATH = JSON_FILES_PATH+"music.json";

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        splitMusicFile(5);
    }

    public static void splitMusicFile(int numberOfFiles) {
        if (numberOfFiles <= 0) return;

        try (Reader reader = Files.newBufferedReader(Paths.get(MUSIC_FILE_PATH))) {
            ArrayList<Song> allSongs = GSON.fromJson(reader, new TypeToken<ArrayList<Song>>() {}.getType());
            reader.close();

            int batchSize = allSongs.size() / numberOfFiles;
            for (int i = 0; i < numberOfFiles; i++) {
                List<Song> newFileContent = allSongs.subList(batchSize * i, batchSize * (i + 1));
                Writer writer = Files.newBufferedWriter(Paths.get("music-"+(i+1)+".json"));
                GSON.toJson(newFileContent, writer);
                writer.close();
            }

        } catch (IOException ignored) { }

    }


}