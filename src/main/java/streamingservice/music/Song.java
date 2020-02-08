package streamingservice.music;

import streamingservice.music.songinfo.Artist;
import streamingservice.music.songinfo.Release;

public class Song implements Comparable<Song> {

    private Release release;
    private Artist artist;
    private streamingservice.music.songinfo.Song song;

    public Song() {}

    public Song(Release release, Artist artist, streamingservice.music.songinfo.Song song) {
        this.release = release;
        this.artist = artist;
        this.song = song;
    }

    public Release getRelease() { return release; }

    public Artist getArtist() { return artist; }

    public streamingservice.music.songinfo.Song getSong() { return song; }

    @Override
    public int compareTo(Song o) {
        return this.getSong().getTitle().compareTo(o.getSong().getTitle());
    }

    @Override
    public String toString() {
        return "SongUnit{" +
                "\nrelease=" + release +
                ",\nartist=" + artist +
                ",\nsong=" + song +
                "\n}";
    }

}