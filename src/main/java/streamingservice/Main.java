package streamingservice;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.*;

import java.io.IOException;
import java.io.InputStream;

public class Main {
   
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
     
     
    public static void main(String[] args) {
        Main player = new Main();
        player.mp3play("resources/imperial.mp3");
    }
 
}

