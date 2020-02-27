package streamingservice.serverside;

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

    public Tuple2<String, String> getValueOf(SEARCH_FILTER filter) {
        if (filter == SEARCH_FILTER.SONGS) { return new Tuple2<>(song.getId(), song.getTitle()); }
        if (filter == SEARCH_FILTER.ARTIST) { return new Tuple2<>(artist.getId(), artist.getName()); }
        if (filter == SEARCH_FILTER.ALBUM) { return new Tuple2<>(Long.toString(release.getId()), release.getName()); }
        if (filter == SEARCH_FILTER.GENRE) { return new Tuple2<>(artist.getTerms(), null); }
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