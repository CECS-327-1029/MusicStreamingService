package streamingservice.UI.panels;

import streamingservice.UI.GUIManager;
import streamingservice.music.FileHandler;
import streamingservice.music.User;

import javax.swing.*;
import java.awt.*;

public class LogIn {

    // the log-in panel that holds widgets to simulate a log in screen
    private JPanel logInPanel;
    private JButton createAccountBtn;       // user clicks this button to go to the create account window
    private JLabel newUserLabel;            // label above the create account button to ask if they're new
    private JLabel usernameLabel;           // label used to show that they need a username to log-in
    private JTextField usernameInput;       // text field where a returning user can enter their username
    private JButton loginButton;            // user clicks this button to be redirected to their profile
    private JLabel usernameNotFoundLabel;   // label that tells the user that their input/username was not found

    public LogIn(CardLayout screenTransitionCardLayout, JPanel rootPanel, UserProfile userProfile) {
        // checks if the user is in the system when the log in button is clicked
        loginButton.addActionListener(e ->{
            // if the user is in the system show them their profile
            User user = FileHandler.getUser(usernameInput.getText());
            if(user != null) {
                usernameInput.setText("");
                usernameNotFoundLabel.setVisible(false);
                userProfile.setUser(user);  // allows to get the user's information in the user profile screen
                screenTransitionCardLayout.show(rootPanel, GUIManager.USER_PROFILE);
            }else{
                // show the user that they weren't found
                usernameInput.setText("");
                usernameNotFoundLabel.setVisible(true);
            }
        });

        // moves the user to the screen where they will create an account
        createAccountBtn.addActionListener(e -> screenTransitionCardLayout.show(rootPanel, GUIManager.CREATE_ACCOUNT));
    }

    public JPanel getLogInPanel() { return logInPanel; }

}