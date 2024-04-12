package ch.epfl.chacun;

/**
 * A simple generic Pair class that can hold two objects of any types.
 *
 * @author Bjork Pedersen (376143)
 * @param <A> the type of the first object in the Pair
 * @param <B> the type of the second object in the Pair
 */
public class Pair<A, B> {
    private A first;
    private B second;

    /**
     * Constructs a new Pair with the given objects.
     *
     * @param first the first object in the Pair
     * @param second the second object in the Pair
     */
    protected Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first object in the Pair.
     *
     * @return the first object in the Pair
     */
    public A getFirst() {
        return first;
    }

    /**
     * Returns the second object in the Pair.
     *
     * @return the second object in the Pair
     */
    public B getSecond() {
        return second;
    }
}