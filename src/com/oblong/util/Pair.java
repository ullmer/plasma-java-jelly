package com.oblong.util;

/**
 * A generic, immutable, object pair.
 *
 * Created: Thu Apr 15 12:20:14 2010
 *
 * @author <a href="mailto:jao@oblong.com">jao </a>
 */
public final class Pair<F, S> {

    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public static <T, U> Pair<T, U> create(T first, U second) {
        return new Pair<T, U>(first, second);
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Pair<?,?>)) return false;
        final Pair<?, ?> other = (Pair<?, ?>) o;
        return eq(this.first, other.first) && eq(this.second, other.second);
    }

    @Override public int hashCode() {
        int hFirst = this.first == null ? 0 : this.first.hashCode();
        int hSecond = this.second == null ? 0 : this.second.hashCode();

        return hFirst + (17 * hSecond);
    }

    @Override public String toString() {
        return "<" + first + "," + second + ">";
    }

    private static boolean eq(Object o1, Object o2) {
        return (o1 == null) ? o2 == null : o1.equals(o2);
    }

} // end class
