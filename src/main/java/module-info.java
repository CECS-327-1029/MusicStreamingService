module streamingservice {
    opens streamingservice.serverside;
    opens streamingservice.serverside.songinfo;
    requires javazoom;
    requires com.google.gson;
    requires java.desktop;
    exports streamingservice.clientside;
}