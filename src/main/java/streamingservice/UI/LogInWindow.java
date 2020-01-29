package streamingservice.UI;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import streamingservice.music.User;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class LogInWindow {

    private static final String USER_FILE_PATH = "resources\\users.json";

    public LogInWindow() {

    }

    public boolean validateEntry(String userName) {
        boolean found = false;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
            List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());
            if (users == null) { return false; }
            int i = 0;
            while (!found) {
                if (users.get(i++).getUserName().equals(userName)) { found = true; }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: SHOW A MESSAGE THAT SAYS THERE WAS AN ERROR
        }
        return false;
    }


}