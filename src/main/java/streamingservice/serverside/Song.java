package streamingservice.serverside;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import streamingservice.clientside.panels.SEARCH_FILTER;
import streamingservice.serverside.songinfo.Artist;
import streamingservice.serverside.songinfo.Release;

public class Song implements Comparable<Song> {

    private Release release;
    private Artist artist;
    private streamingservice.serverside.songinfo.Song song;

    public Song() {}

    public Song(Release release, Artist artist, streamingservice.serverside.songinfo.Song song) {
        this.release = release;
        this.artist = artist;
        this.song = song;
    }

    public Release getRelease() { return release; }

    public Artist getArtist() { return artist; }

    public streamingservice.serverside.songinfo.Song getSong() { return song; }

    public String getID(SEARCH_FILTER filter) {
        if (filter == SEARCH_FILTER.SONGS) { return song.getId(); }
        if (filter == SEARCH_FILTER.ARTIST) { return artist.getId(); }
        if (filter == SEARCH_FILTER.ALBUM) { return Long.toString(release.getId()); }
        if (filter == SEARCH_FILTER.GENRE) { return artist.getTerms(); }
        return null;
    }

    public String getName(SEARCH_FILTER filter) {
        if (filter == SEARCH_FILTER.SONGS) { return song.getTitle(); }
        if (filter == SEARCH_FILTER.ARTIST) { return artist.getName(); }
        if (filter == SEARCH_FILTER.ALBUM) { return release.getName(); }
        return null;
    }

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