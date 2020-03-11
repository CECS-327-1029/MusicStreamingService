package streamingservice.clientside;


import streamingservice.clientside.panels.CreateAccount;
import streamingservice.clientside.panels.LogIn;
import streamingservice.clientside.panels.UserProfile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketException;
import java.net.UnknownHostException;

public class GUIManager {

    // set the width of the Jframe window
    private static final int FRAME_WIDTH = 1215;
    private static final int FRAME_HEIGHT = 650;

    // names of the cards
    public static final String LOG_IN = "Log In Card";
    public static final String CREATE_ACCOUNT = "create Account Card";
    public static final String USER_PROFILE = "User Profile Card";

    private Client client = new Client();
    private ProxyInterface proxy = new ProxyInterface(client);

    public GUIManager() throws SocketException, UnknownHostException {
        JPanel rootPanel = new JPanel();
        // frame to display everything
        JFrame mainFrame = new JFrame();
        // layout that will be used to switch between the log-in, create account, and user profile screens
        CardLayout screenTransitionCardLayout = new CardLayout();

        UserProfile userProfile = new UserProfile(mainFrame, proxy);
        JPanel logInPanel = new LogIn(screenTransitionCardLayout, rootPanel, userProfile, proxy).getLogInPanel();
        JPanel createAccountPanel = new CreateAccount(screenTransitionCardLayout, rootPanel, userProfile, proxy).getCreateAccountPanel();
        JPanel userProfilePanel = userProfile.getUserProfilePanel();

        rootPanel.setLayout(screenTransitionCardLayout);
        rootPanel.add(logInPanel, LOG_IN);
        rootPanel.add(createAccountPanel, CREATE_ACCOUNT);
        rootPanel.add(userProfilePanel, USER_PROFILE);
        // the first screen that will be displayed is the log in form
        screenTransitionCardLayout.show(rootPanel, LOG_IN);

        // sets up the frame that will display our screens
        mainFrame.add(rootPanel);
        mainFrame.pack();
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);


    }



}