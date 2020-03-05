package streamingservice.clientside;


import streamingservice.clientside.GUIManager;
import streamingservice.serverside.Server;

import java.net.SocketException;
import java.net.UnknownHostException;

public class App {

    public static void main(String[] args) throws SocketException, UnknownHostException {
        new Server().start();
        new GUIManager();

    }

}

