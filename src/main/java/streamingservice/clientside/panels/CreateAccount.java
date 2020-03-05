package streamingservice.clientside.panels;

import streamingservice.clientside.Client;
import streamingservice.clientside.CommunicationModule;
import streamingservice.clientside.GUIManager;
import streamingservice.clientside.ProxyInterface;
import streamingservice.serverside.FileHandler;

import javax.swing.*;
import java.awt.*;

public class CreateAccount {

    // the create account panel that holds widgets to simulate a create account screen
    private JPanel createAccountPanel;
    private JLabel createAccountTitleLabel; // label to display the title "Create Account"
    // following labels "instructs" the user to enter the appropriate information to their right
    private JLabel userNameLabel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel reEmailLabel;
    // following text fields accepts information from the new user
    private JTextField userNameTF;
    private JTextField firstNameTF;
    private JTextField lastNameTF;
    private JTextField emailTF;
    private JTextField reEmailTF;
    // following labels display error messages
    private JLabel userNameErrorLabel;
    private JLabel firstNameErrorLabel;
    private JLabel lastNameErrorLabel;
    private JLabel emailErrorLabel;
    private JLabel reEmailErrorLabel;
    private JButton submitButton;      // review the information given and create an account for the user
    private JButton logInButton;

    private ProxyInterface proxy;
    private CommunicationModule module;

    public CreateAccount(CardLayout screenTransitionCardLayout, JPanel rootPanel, UserProfile userProfile,
                         ProxyInterface proxy, CommunicationModule module) {
        this.proxy = proxy;
        this.module = module;

        // check if the user has entered the necessary and valid information
        submitButton.addActionListener(e -> {
            if (checkIfAllEntriesFilled() && areAllNecessaryEntriesValid()) {
                String id;
                if ((id = createAccount()) != null) {  // write their information to the users.json file
                    userProfile.setUser(id);   // allows to get the user's information in the user profile screen
                    clearAll();         // clear the fields before leaving screen
                    screenTransitionCardLayout.show(rootPanel, GUIManager.USER_PROFILE); // go to user's profile
                }
            }
        });

        logInButton.addActionListener(e -> {
            clearAll(); // clear the fields before leaving screen
            screenTransitionCardLayout.show(rootPanel, GUIManager.LOG_IN); // go back to the log in screen
        });
    }

    /**
     * Returns the panel that holds widgets to simulate a create account form
     * @return the create account Panel
     */
    public JPanel getCreateAccountPanel() { return createAccountPanel; }

    /**
     * Creates an account based on the information given in the form.
     */
    private String createAccount() {
        return FileHandler.addUserToSystem(firstNameTF.getText(), lastNameTF.getText(), emailTF.getText(), userNameTF.getText());
    }

    /**
     * Clears all text fields and error labels.
     */
    private void clearAll() {
        userNameTF.setText("");
        firstNameTF.setText("");
        lastNameTF.setText("");
        emailTF.setText("");
        reEmailTF.setText("");
        userNameErrorLabel.setVisible(false);
        firstNameErrorLabel.setVisible(false);
        lastNameErrorLabel.setVisible(false);
        emailErrorLabel.setVisible(false);
        reEmailErrorLabel.setVisible(false);
    }

    /**
     * Checks if all the entries in the create account are valid.
     * If the entry is missing, then alert the user that the field is
     * needed. If at least one error label error is visible to the user,
     * then return false to signify that an entry is not filled; otherwise,
     * return true to represent that all entries are filled.
     *
     * @return true if all entries are filled in or false if at least one is empty
     */
    private boolean checkIfAllEntriesFilled() {
        if (userNameTF.getText().trim().toLowerCase().equals("")) {
            userNameErrorLabel.setVisible(true);
            userNameErrorLabel.setText("User Name Needed");
        } else {
            userNameErrorLabel.setText("");
            userNameErrorLabel.setVisible(false);
        }

        if (firstNameTF.getText().trim().equals("")) {
            firstNameErrorLabel.setVisible(true);
            firstNameErrorLabel.setText("First Name Needed");
        } else {
            firstNameErrorLabel.setText("");
            firstNameErrorLabel.setVisible(false);
        }

        if (lastNameTF.getText().trim().equals("")) {
            lastNameErrorLabel.setVisible(true);
            lastNameErrorLabel.setText("Last Name Needed");
        } else {
            lastNameErrorLabel.setText("");
            lastNameErrorLabel.setVisible(false);
        }

        if (emailTF.getText().trim().toLowerCase().equals("")) {
            emailErrorLabel.setVisible(true);
            emailErrorLabel.setText("Email Needed");
        } else {
            emailErrorLabel.setText("");
            emailErrorLabel.setVisible(false);
        }

        if (reEmailTF.getText().trim().toLowerCase().equals("")) {
            reEmailErrorLabel.setVisible(true);
            reEmailErrorLabel.setText("Email Re-entry Needed");
        } else {
            reEmailErrorLabel.setText("");
            reEmailErrorLabel.setVisible(false);
        }

        JLabel[] errorLabels = {userNameErrorLabel, firstNameErrorLabel, lastNameErrorLabel,
                emailErrorLabel, reEmailErrorLabel};
        boolean anyVisible = false;
        int i = 0;
        while (!anyVisible && i < errorLabels.length) {
            anyVisible = errorLabels[i++].isVisible();
        }
        return !anyVisible;
    }

    /**
     * Checks to see if the user entered all necessary entries and that they are free
     * to use their given entry.
     * @return true if the user can use the fields chosen, false if they can't
     */
    private boolean areAllNecessaryEntriesValid() {
        boolean isUserNameFree = FileHandler.isUserNameFreeToUse(userNameTF.getText());
        boolean isEmailFree = FileHandler.isEmailFreeToUse(emailTF.getText());

        if (!isUserNameFree) {
            // tell the user name is in use
            userNameErrorLabel.setText("User Name already In Use");
            userNameErrorLabel.setVisible(true);
        } else {
            userNameErrorLabel.setText("");
            userNameErrorLabel.setVisible(false);
        }

        if (!isEmailFree) {
            // tell user the email is in use
            emailErrorLabel.setText("Email already In Use");
            emailErrorLabel.setVisible(true);
        } else {
            emailErrorLabel.setText("");
            emailErrorLabel.setVisible(false);
        }

        return isUserNameFree && areEmailsTheSame() && isEmailFree;
    }

    /**
     * Determines if the two emails required are the same one. If they are different,
     * then tell the user they don't match.
     * @return true if the given emails are the same, false otherwise
     */
    private boolean areEmailsTheSame() {
        boolean isSame = emailTF.getText().equals(reEmailTF.getText());
        if (!isSame) {
            reEmailErrorLabel.setText("Emails are different");
            reEmailErrorLabel.setVisible(true);
        } else {
            reEmailErrorLabel.setText("");
            reEmailErrorLabel.setVisible(false);
        }
        return isSame;
    }

}