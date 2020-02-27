package streamingservice.serverside.songinfo;

import java.util.Objects;

public class Artist implements Comparable<Artist> {

    private double terms_freq;
    private String terms;
    private String name;
    private double familiarity;
    private double longitude;
    private String id;
    private String location;
    private double latitude;
    private String similar;
    private double hotttnesss;

    public Artist() {}

    public Artist(double terms_freq, String terms, String name, double familiarity, double longitude,
                  String id, String location, double latitude, String similar, double hotttnesss) {
        this.terms_freq = terms_freq;
        this.terms = terms;
        this.name = name;
        this.familiarity = familiarity;
        this.longitude = longitude;
        this.id = id;
        this.location = location;
        this.latitude = latitude;
        this.similar = similar;
        this.hotttnesss = hotttnesss;
    }

    public double getTerms_freq() {
        return terms_freq;
    }

    public String getTerms() {
        return terms;
    }

    public String getName() {
        return name;
    }

    public double getFamiliarity() {
        return familiarity;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getSimilar() {
        return similar;
    }

    public double getHotttnesss() {
        return hotttnesss;
    }

    @Override
    public int compareTo(Artist o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return id.equals(artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terms_freq, terms, name, familiarity, longitude, id, location, latitude, similar, hotttnesss);
    }

    @Override
    public String toString() {
        return "Artist{" +
                "terms_freq=" + terms_freq +
                ", terms='" + terms + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

}