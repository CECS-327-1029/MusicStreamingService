package streamingservice.UI;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import streamingservice.music.User;



import javax.swing.*;
import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AccountCreator extends JFrame {

    private static final int FRAME_WIDTH = 600;
    private static final int FRAME_HEIGHT = 500;
    private static final String USER_FILE_PATH = "resources" + System.getProperty("file.separator") + "users.json";

    private Gson gson;
    private List<User> users;

    private JPanel createAccountPanel;
    private JLabel titleLabel;
    private JLabel userNameLabel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel reEnterEmailLabel;
    private JTextField userNameTF;
    private JTextField firstNameTF;
    private JTextField lastNameTF;
    private JTextField emailTF;
    private JTextField reEnterEmailTF;
    private JButton submitButton;
    private JLabel errorUserNameLabel;
    private JLabel errorFirstNameLabel;
    private JLabel errorLastNameLabel;
    private JLabel errorEmailLabel;
    private JLabel errorEmailReEntryLabel;

    public AccountCreator() throws IOException {
        setTitle("Create Account");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        createAccountPanel.setBackground(Color.yellow);
        submitButton.setBackground(Color.MAGENTA);
        add(createAccountPanel);

        gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(USER_FILE_PATH));
        users = gson.fromJson(reader, new TypeToken<List<User>>() {}.getType());

        submitButton.addActionListener(e -> {
                if (checkIfAllEntriesFilled() && areAllNecessaryEntriesValid()) {
                    createAccount();
                    //TODO: Go to the user's profile
                    System.exit(0);
                }
            }
        );
    }

    private void createAccount() {
        User newUser = new User(firstNameTF.getText(), lastNameTF.getText(), emailTF.getText(), userNameTF.getText());
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
        if (userNameTF.getText().trim().equals("")) {
            errorUserNameLabel.setVisible(true);
            errorUserNameLabel.setText("User Name Needed");
        } else {
            errorUserNameLabel.setText("");
            errorUserNameLabel.setVisible(false);
        }

        if (firstNameTF.getText().trim().equals("")) {
            errorFirstNameLabel.setVisible(true);
            errorFirstNameLabel.setText("First Name Needed");
        } else {
            errorFirstNameLabel.setText("");
            errorFirstNameLabel.setVisible(false);
        }

        if (lastNameTF.getText().trim().equals("")) {
            errorLastNameLabel.setVisible(true);
            errorLastNameLabel.setText("Last Name Needed");
        } else {
            errorLastNameLabel.setText("");
            errorLastNameLabel.setVisible(false);
        }

        if (emailTF.getText().trim().equals("")) {
            errorEmailLabel.setVisible(true);
            errorEmailLabel.setText("Email Needed");
        } else {
            errorEmailLabel.setText("");
            errorEmailLabel.setVisible(false);
        }

        if (reEnterEmailTF.getText().trim().equals("")) {
            errorEmailReEntryLabel.setVisible(true);
            errorEmailReEntryLabel.setText("Email Re-entry Needed");
        } else {
            errorEmailReEntryLabel.setText("");
            errorEmailReEntryLabel.setVisible(false);
        }

        JLabel[] labels = {errorUserNameLabel, errorFirstNameLabel, errorLastNameLabel, errorEmailLabel, errorEmailReEntryLabel};
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
            errorUserNameLabel.setText("User Name already In Use");
            errorUserNameLabel.setVisible(true);
        } else {
            errorUserNameLabel.setText("");
            errorUserNameLabel.setVisible(false);
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
            errorEmailLabel.setText("Email already In Use");
            errorEmailLabel.setVisible(true);
        } else {
            errorEmailLabel.setText("");
            errorEmailLabel.setVisible(false);
        }
        return isFreeToUse;
    }

    private boolean areEmailsTheSame() {
        boolean isSame = emailTF.getText().equals(reEnterEmailTF.getText());
        if (!isSame) {
            errorEmailReEntryLabel.setText("Emails are different");
            errorEmailReEntryLabel.setVisible(true);
        } else {
            errorEmailReEntryLabel.setText("");
            errorEmailReEntryLabel.setVisible(false);
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