package streamingservice;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;
import streamingservice.UI.AccountCreator;

import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;

public class App {

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    private static final String MUSIC_FILE_PATH = "resources" + System.getProperty("file.separator") + "music.json";
    private static final String USER_FILE_PATH = "resources" + System.getProperty("file.separator") + "users.json";
   
      /**
     * Play a given audio file.
     * @param file Path of the audio file.
     */
    void mp3play(String file) {
        try {
            // It uses CECS327InputStream as InputStream to play the song 
             InputStream is = new CECS327InputStream(file);
             Player mp3player = new Player(is);
             mp3player.play();
	     }
	     catch (JavaLayerException exception) 
         {
	       exception.printStackTrace();
	     }
         catch (IOException exception)
         {
             exception.printStackTrace();
         }  
    }
     
     
    public static void main(String[] args) throws IOException {
        //App player = new App();
        //player.mp3play("resources/imperial.mp3");
        JFrame creator = new AccountCreator(FRAME_WIDTH, FRAME_HEIGHT, USER_FILE_PATH);
        creator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        creator.setVisible(true);
        creator.setResizable(false);
    }
 
}

