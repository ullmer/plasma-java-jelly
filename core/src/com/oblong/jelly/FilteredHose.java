
/* (c)  oblong industries */

package com.oblong.jelly;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Decorated Hose that reads only those proteins matching a list of
 * descrips.
 *
 * <p>This hose forwards all operations to a second wrapped hose, but
 * filters all incoming proteins so that they match a list of descrips
 * provided at construction time.
 *
 * @author jao
 */
public final class FilteredHose implements Hose {

    /**
     * Creates a new filtered hose wrapping the given one and
     * reading only those proteins matching the list of descrips.
     *
     * <p>The connection embodied by <code>hose</code> is transferred
     * to the new FilteredHose instance.
     */
    public FilteredHose(Hose hose, Slaw... descrips) throws PoolException {
        this.hose = hose.dupAndClose();
        this.descrips = descrips;
    }

    @Override public int version() { return hose.version(); }

    @Override public PoolMetadata metadata() throws PoolException {
        return hose.metadata();
    }

    @Override public PoolAddress poolAddress() { return hose.poolAddress(); }

    @Override public boolean isConnected() { return hose.isConnected(); }

    @Override public String name() { return hose.name(); }

    @Override public void setName(String name) { hose.setName(name); }

    @Override public long index() { return hose.index(); }

    @Override public void rewind() throws PoolException { hose.rewind(); }

    @Override public long newestIndex() throws PoolException {
        return hose.newestIndex();
    }

    @Override public long oldestIndex() throws PoolException {
        return hose.oldestIndex();
    }

    @Override public void seekTo(long l) { hose.seekTo(l); }

    @Override public void seekBy(long l) { hose.seekBy(l); }

    @Override public void toLast() throws PoolException { hose.toLast(); }

    @Override public void runOut() throws PoolException { hose.runOut(); }

    /**
     * Retrieves the protein corresponding to the current local index,
     * provided that it matches this hose's filter. Otherwise, looks
     * for the next protein in the pool satisfying that criterium, and
     * updates the local index accordingly.
     *
     * @see Hose#current
     */
    @Override public Protein current() throws PoolException {
        final Protein p = hose.current();
        return p.matches(descrips) ? p : next();
    }

    /**
     * Retrieves the nth protein in the pool, provided that it matches
     * this hose's filter. Otherwise, a {@link NoSuchProteinException}
     * is thrown.
     *
     * @see Hose#nth
     */
    @Override public Protein nth(long l) throws PoolException {
        final Protein p = hose.nth(l);
        if (!p.matches(descrips)) throw new NoSuchProteinException(0);
        return p;
    }

    /**
     * Like {@link Hose#nth(long, boolean, boolean, boolean)},
     * provided the requested (partial) protein matches this hose
     * filter. Otherwise, a {@link NoSuchProteinException} is throw.
     */
    @Override public Protein nth(long index,
                                 boolean descrips,
                                 boolean ingests,
                                 boolean data) throws PoolException {
        final Protein p = hose.nth(index, descrips, ingests, data);
        if (!p.matches(this.descrips)) throw new NoSuchProteinException(0);
        return p;
    }

    /**
     * Retrieves those proteins in the range [from, to) that match
     * this hose's filter.
     *
     * @inheritDoc
     */
    @Override public List<Protein> range(long from, long to)
        throws PoolException {
        final List<Protein> result = hose.range(from, to);
        final Iterator<Protein> it = result.iterator();
        while (it.hasNext())
            if (!it.next().matches(this.descrips)) it.remove();
        return result;
    }

    /**
     * Retrieves metadata for a protein according to the request. No
     * filtering is performed, since the metadata does not necessarily
     * include descrips.
     */
    @Override public ProteinMetadata metadata(MetadataRequest req)
        throws PoolException {
        return hose.metadata(req);
    }

    /**
     * Retrieves metadata for proteins according to the request list.
     * No filtering is performed, since the metadata does not
     * necessarily include descrips.
     */
    @Override public List<ProteinMetadata> metadata(MetadataRequest... reqs)
        throws PoolException {
        return hose.metadata(reqs);
    }

    /**
     * Returns next protein matching this hose's filter <i>and</i> the
     * given descrips list. I.e., this method implements the following
     * algorithm:
     * <pre>
     *   while (true) {
     *     Protein p = wrappedHose.next(descrips);
     *     if (p.matches(descs)) return p;
     *   }
     * </pre>
     *
     * @see Hose#next
     */
    @Override public Protein next(Slaw... descs) throws PoolException {
        while (true) {
            final Protein p = hose.next(descrips);
            if (p.matches(descs)) return p;
        }
    }

    /**
     * Works like {@link Hose#awaitNext(long, TimeUnit)} (q.v.), but
     * awaiting for proteins matching this hose's filter.
     */
    @Override public Protein awaitNext(long l, TimeUnit timeUnit)
        throws PoolException, TimeoutException {
        if (l == 0) return next();
        if (l < 0) return awaitNext();
        long remaining = timeUnit.toMillis(l);
        long start = System.currentTimeMillis();
        while (remaining > 0) {
            try {
                final Protein p = hose.awaitNext(remaining,
                                                 TimeUnit.MILLISECONDS);
                if (p.matches(descrips)) return p;
            } catch (TimeoutException e) {
                // keep on trying
            } finally {
                remaining -= System.currentTimeMillis() - start;
                start = System.currentTimeMillis();
            }
        }
        throw new TimeoutException();
    }

    /**
     * Works like {@link Hose#awaitNext()} (q.v.), but awaiting for
     * proteins matching this hose's filter.
     */
    @Override public Protein awaitNext() throws PoolException {
        while (true) {
            final Protein p = hose.awaitNext();
            if (p.matches(descrips)) return p;
        }
    }

    /**
     * Returns previous protein matching this hose's filter <i>and</i> the
     * given descrips list. I.e., this method implements the following
     * algorithm:
     * <pre>
     *   while (true) {
     *     Protein p = wrappedHose.previous(descrips);
     *     if (p.matches(descs)) return p;
     *   }
     * </pre>
     *
     * @see Hose#previous
     */
    @Override public Protein previous(Slaw... descs) throws PoolException {
        while (true) {
            final Protein p = hose.previous(descrips);
            if (p.matches(descs)) return p;
        }
    }

    @Override public Protein deposit(Protein protein) throws PoolException {
        return hose.deposit(protein);
    }

    @Override public void withdraw() { hose.withdraw(); }

    @Override public Hose dup() throws PoolException {
        return new FilteredHose(hose.dup(), descrips);
    }

    @Override public Hose dupAndClose() throws PoolException {
        return new FilteredHose(hose, descrips);
    }

    @Override public void closeConnectionAbruptly() {
        hose.closeConnectionAbruptly();
    }

    private final Hose hose;
    private final Slaw[] descrips;
}
