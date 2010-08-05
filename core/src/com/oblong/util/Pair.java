// Copyright (c) 2010 Oblong Industries

package com.oblong.util;

/**
 * A generic, immutable object pair, without encapsulation or further
 * pretensions: just a convenient placeholder for two objects.
 *
 * Created: Thu Apr 15 12:20:14 2010
 *
 * @author jao
 */
public final class Pair<F, S> {

    /**
     *  Standard constructor doing exactly what you would expect.
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Factory method, to ease construction syntactic overhead (unlike
     * the constructor, this method's generic types are not explicity
     * needed).
     */
    public static <T, U> Pair<T, U> create(T first, U second) {
        return new Pair<T, U>(first, second);
    }

    /**
     * First element in the pair. Although a Pair is immutable in the
     * sense that you cannot reasign its two components, this accessor
     * returns a direct reference to the first one, without defensive
     * copying.
     */
    public F first() { return first; }

    /**
     * Raw reference to second element in the pair.
     */
    public S second() { return second; }

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

    /**
     * More or less free-form string representation, based upon those
     * of @c F and @c S. Don't rely on this method for serialization
     * purposes.
     */
    @Override public String toString() {
        return "<" + first + "," + second + ">";
    }

    private static boolean eq(Object o1, Object o2) {
        return (o1 == null) ? o2 == null : o1.equals(o2);
    }

    private final S second;
    private final F first;

}
