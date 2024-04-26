package ch.epfl.chacun.utils;

/**
 * Provide tuples in java (represented by subclasses), as their not implemented.
 * <p>
 * All the available tuples :
 * {@link Pair},
 * {@link Triple},
 * {@link Quad},
 * {@link Quint},
 * {@link Six},
 *
 * @author Eliott Tornassat (381356)
 */
public class Tuples {
    // prevents default constructor
    private Tuples() {
    }

    /**
     * Represent a group of two variables, a and b.
     */
    public record Pair<A, B>(A a, B b) {
    }

    /**
     * Represent a group of three variables, a, b, and c.
     */
    public record Triple<A, B, C>(A a, B b, C c) {
        /**
         * Construct a triple tuple from a {@link Pair} pair and a third element c.
         *
         * @param pair the tuple
         * @param c    the fourth element
         */
        public Triple(Pair<A, B> pair, C c) {
            this(pair.a, pair.b, c);
        }
    }

    /**
     * Represent a group of four variables, a, b, c and d.
     */
    public record Quad<A, B, C, D>(A a, B b, C c, D d) {
        /**
         * Construct a quad tuple from a {@link Triple} triple and a fourth element d.
         *
         * @param triple the tuple
         * @param d      the fourth element
         */
        public Quad(Triple<A, B, C> triple, D d) {
            this(triple.a, triple.b, triple.c, d);
        }
    }

    /**
     * Represent a group of five variables, a, b, c, d and e.
     */
    public record Quint<A, B, C, D, E>(A a, B b, C c, D d, E e) {
        /**
         * Construct a quint tuple from a {@link Quad} quad tuple and a fifth element e.
         *
         * @param quad the tuple
         * @param e    the fourth element
         */
        public Quint(Quad<A, B, C, D> quad, E e) {
            this(quad.a, quad.b, quad.c, quad.d, e);
        }
    }

    /**
     * Represent a group of six variables, a, b, c, d and e.
     */
    public record Six<A, B, C, D, E, F>(A a, B b, C c, D d, E e, F f) {
        /**
         * Construct a quint tuple from a {@link Quint} quint tuple and a fifth element e.
         *
         * @param quint the tuple
         * @param f     the fifth element
         */
        public Six(Quint<A, B, C, D, E> quint, F f) {
            this(quint.a, quint.b, quint.c, quint.d, quint.e, f);
        }
    }

}
