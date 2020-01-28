package streamingservice.UI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import streamingservice.music.User;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateAccount {

    private static final String USER_FILE_PATH = "resources\\users.json";

    private User newUser;

    public CreateAccount(User newUser) {
        this.newUser = newUser;
        writeNewUserToJson();
    }

    public void writeNewUserToJson() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            List<User> users = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(newUser);
            gson.toJson(users, writer);
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: SHOW A WINDOW THAT SAYS THERE WAS AN ERROR
        }

    }

}