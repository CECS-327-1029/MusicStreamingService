module streamingservice {
    opens streamingservice.music;
    opens streamingservice.music.songinfo;
    requires javazoom;
    requires com.google.gson;
    requires java.desktop;
    exports streamingservice;
}