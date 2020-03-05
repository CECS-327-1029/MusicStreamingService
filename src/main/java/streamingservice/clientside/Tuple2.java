package streamingservice.clientside;

public class Tuple2<X, Y> {

    private static final int SIZE = 1;
    private X value0;
    private Y value1;

    public Tuple2() {}

    public Tuple2(X value0, Y value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public static int getSIZE() {
        return SIZE;
    }

    public X getValue0() {
        return value0;
    }

    public Y getValue1() {
        return value1;
    }

    @Override
    public String toString() {
        return "("+value0+", "+value1+")";
    }
}