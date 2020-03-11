package streamingservice.serverside;

import java.util.Objects;

public class Tuple2<X, Y> {

    private X value0;
    private Y value1;

    public Tuple2() {}

    public Tuple2(X value0, Y value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public X getValue0() {
        return value0;
    }

    public Y getValue1() { return value1; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
        boolean eq = value0.equals(tuple2.value0);
        if (value1 != null) { eq = eq && value1.equals(tuple2.value1); }
        return eq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value0, value1);
    }

    @Override
    public String toString() {
        return "("+value0+", "+value1+")";
    }
}