module streamingservice {
    opens streamingservice.serverside;
    opens streamingservice.serverside.songinfo;
    opens streamingservice.DFS;
    requires javazoom;
    requires com.google.gson;
    requires java.desktop;
    requires java.rmi;
    exports streamingservice.clientside;
    exports streamingservice.DFS;
}