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
import java.util.Arrays;
import java.util.List;

public class CreateAccount {

    private static final String USER_FILE_PATH = "resources\\users.json";

    private User[] users;

    public CreateAccount(User... users) {
        this.users = users;
        if (users != null) {
            writeNewUserToJson();
        }
    }

    private void writeNewUserToJson() {
        try {
            Gson gson = new Gson();
            Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
            List<User> currentUsers = new Gson().fromJson(reader, new TypeToken<List<User>>() {}.getType());
            Writer writer = Files.newBufferedWriter(Paths.get(USER_FILE_PATH));
            if (currentUsers == null) {
                currentUsers = new ArrayList<>();
            }
            assert users != null;
            currentUsers.addAll(Arrays.asList(users));
            gson.toJson(users, writer);
            writer.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: SHOW A MESSAGE THAT SAYS THERE WAS AN ERROR
        }

    }

}