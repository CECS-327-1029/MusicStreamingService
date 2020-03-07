package streamingservice.clientside.panels;

import com.google.gson.JsonObject;
import streamingservice.clientside.CommunicationModule;
import streamingservice.clientside.GUIManager;
import streamingservice.clientside.ProxyInterface;

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

    private ProxyInterface proxy;
    private CommunicationModule module;

    public LogIn(CardLayout screenTransitionCardLayout, JPanel rootPanel, UserProfile userProfile,
                 ProxyInterface proxy, CommunicationModule module) {
        this.proxy = proxy;
        this.module = module;

        usernameInput.addActionListener(e -> loginButton.doClick());

        // checks if the user is in the system when the log in button is clicked
        loginButton.addActionListener(e ->{
            // if the user is in the system show them their profile
            JsonObject object = proxy.syncExecution("getUserId", usernameInput.getText());
            String userId = (String) module.sendMessage(object, "UNKNOWN");
            if(!userId.equals("")) {
                usernameInput.setText("");
                usernameNotFoundLabel.setVisible(false);
                userProfile.setUser(userId);  // allows to get the user's information in the user profile screen
                screenTransitionCardLayout.show(rootPanel, GUIManager.USER_PROFILE);
            }else{
                // show the user that they weren't found
                usernameInput.setText("");
                usernameNotFoundLabel.setVisible(true);
            }
        });

        // moves the user to the screen where they will create an account
        createAccountBtn.addActionListener(e -> {
            usernameInput.setText("");
            usernameNotFoundLabel.setVisible(false);
            screenTransitionCardLayout.show(rootPanel, GUIManager.CREATE_ACCOUNT);
        });
    }

    public JPanel getLogInPanel() { return logInPanel; }

}