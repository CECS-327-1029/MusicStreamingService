package streamingservice.UI;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MusicPlayer {

    FileInputStream FIS;
    BufferedInputStream BIS;

    public Player player;
    public long pauseLocation; //This will keep track of how much of the song is left
    public long songTotalLength; //This will keep track of the whole length of the song
    public String songLocation; //stores the song path

    public void stop()
    {
        if(player != null)
        {
            player.close();
            pauseLocation = 0; //Stopping the song will mean that the song will start over from the beginning if we press play again.
        }
    }

    public void pause() throws IOException {
        if(player != null)
        {
            pauseLocation = FIS.available(); //Checks how much of the song is available left. Returns a Long type value.
            player.close();
        }
    }

    public void play(String path)
    {
        try
        {
            FIS = new FileInputStream(path);
            BIS = new BufferedInputStream(FIS);

            player = new Player(BIS);

            songTotalLength = FIS.available(); //gets the full length of the song & getting what's left
            songLocation = path + "" ; //Keeps track of the song path
        }

        catch(FileNotFoundException except)
        {
            except.printStackTrace();
        }

        catch (JavaLayerException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;

        new Thread()
        {
            public void run()
            {
                try {
                    player.play();
                } catch (JavaLayerException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void resume()
    {
        try
        {
            FIS = new FileInputStream(songLocation);
            BIS = new BufferedInputStream(FIS);
            player = new Player(BIS);

            FIS.skip(songTotalLength - pauseLocation); //Play the song again but from the time frame of the total song length minus the location it was paused.
        }

        catch(FileNotFoundException except)
        {
            except.printStackTrace();
        }

        catch (JavaLayerException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)

        {
            e.printStackTrace();
        }
        ;

        new Thread()
        {
            public void run()
            {
                try {
                    player.play();
                } catch (JavaLayerException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

    }


}
