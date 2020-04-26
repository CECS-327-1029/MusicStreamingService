package streamingservice.serverside;

import java.util.Arrays;

public enum SEARCH_FILTER {
    SONGS("Songs"),
    ARTIST("Artists"),
    ALBUM("Albums"),
    GENRE("Genres");

    private String name;

    private SEARCH_FILTER(String name) {
        this.name=name;
    }

    public boolean equalsName(String o) {
        return name.equals(o);
    }

    public static SEARCH_FILTER fromValue(String keyword) {
        for(SEARCH_FILTER filter : SEARCH_FILTER.values()){
            if(filter.name.equals(keyword)) return filter;
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public static String[] toArray() {
        return Arrays.stream(SEARCH_FILTER.class.getEnumConstants()).map(Enum::toString).toArray(String[]::new);
    }

}
