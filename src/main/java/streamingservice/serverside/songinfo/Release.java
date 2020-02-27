package streamingservice.serverside.songinfo;

public class Release {

    private long id;
    private String name;

    public Release() {}

    public Release(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Release{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}