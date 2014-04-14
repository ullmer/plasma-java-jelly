
/* (c)  oblong industries */

package com.oblong.jelly.pool.mem;

import com.oblong.jelly.*;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.util.ThreadChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MemHose implements Hose {

    protected final ThreadChecker threadChecker = new ThreadChecker();

    /* Ummm... version is an attribute of the TCP pool protocol,
     * so I'm unclear why we have one here in MemHose. */
    @Override public int version() {
        threadChecker.check();
        return 3;
    }

    @Override public PoolMetadata metadata() {
        threadChecker.check();
        return nullMetadata;
    }

    @Override public String name() {
        threadChecker.check();
        return name;
    }

    @Override public void setName(String n) {
        threadChecker.check();
        name = n;
    }

    @Override public PoolAddress poolAddress() {
        threadChecker.check();
        return address;
    }

    @Override public boolean isConnected() {
        threadChecker.check();
        return connected;
    }

    @Override public void withdraw() {
        threadChecker.check();
        setNotConnected();
    }

    @Override public void closeConnectionAbruptly() {
        // exempt from threadChecker - see super method javadoc
        setNotConnected();
    }

    private void setNotConnected() {
        connected = false;
    }

    @Override public long index() {
        threadChecker.check();
        return index;
    }

    @Override public long newestIndex() throws PoolException {
        threadChecker.check();
        checkConnected();
        return pool.newestIndex();
    }

    @Override public long oldestIndex() throws PoolException {
        threadChecker.check();
        checkConnected();
        return pool.oldestIndex();
    }

    @Override public void seekTo(long index) {
        threadChecker.check();
        this.index = index;
    }

    @Override public void seekBy(long offset) {
        threadChecker.check();
        seekTo(index + offset);
    }

    @Override public void toLast() throws PoolException {
        threadChecker.check();
        seekTo(newestIndex());
    }

    @Override public void runOut() throws PoolException {
        threadChecker.check();
        seekTo(1 + newestIndex());
    }

    @Override public void rewind() throws PoolException {
        threadChecker.check();
        seekTo(oldestIndex());
    }

    @Override public Protein deposit(Protein p) throws PoolException {
        threadChecker.check();
        final PoolProtein dp = new PoolProtein(pool.deposit(p), this);
        return dp;
    }

    @Override public Protein nth(long index) throws PoolException {
        threadChecker.check();
        return checkProtein(pool.nth(index));
    }

    @Override public Protein nth(long index,
                                 boolean descrips,
                                 boolean ingests,
                                 boolean data) throws PoolException {
        threadChecker.check();
        return checkProtein(partialProtein(index, descrips, ingests, data));
    }

    @Override public List<Protein> range(long from, long to)
            throws PoolException {
        threadChecker.check();
        final List<Protein> result = new ArrayList<Protein>();
        for (long k = from; k < to; ++k) {
            final Protein p = partialProtein(k, true, true, true);
            if (p != null) result.add(p);
        }
        return result;
    }

    @Override public ProteinMetadata metadata(MetadataRequest req)
            throws PoolException {
        threadChecker.check();
        final ProteinMetadata md = makeMeta(req);
        if (md == null) throw new NoSuchProteinException(0);
        return md;
    }

    @Override public List<ProteinMetadata> metadata(MetadataRequest... rs)
            throws PoolException {
        threadChecker.check();
        final List<ProteinMetadata> result = new ArrayList<ProteinMetadata>();
        for (MetadataRequest r : rs) {
            final ProteinMetadata md = makeMeta(r);
            if (md != null) result.add(md);
        }
        return result;
    }

    @Override public Protein current() throws PoolException {
        threadChecker.check();
        return nth(index);
    }

    @Override public Protein next(Slaw... descrips) throws PoolException {
        threadChecker.check();
        return checkProtein(getNext(descrips));
    }

    @Override public Protein awaitNext(long period, TimeUnit unit)
            throws PoolException, TimeoutException {
        threadChecker.check();
        final PoolProtein p = await (unit . toMillis (period) / 1000.00);
        if (p == null)
            throw new TimeoutException();
        return checkProtein(p);
    }

    @Override public Protein awaitNext() throws PoolException {
        threadChecker.check();
        /* -1 means "wait forever", although it might be nice to have
         * a defined constant for it, like POOL_WAIT_FOREVER which
         * is #defined in libPlasma/c/pool.h.  And actually, Jelly
         * does define constants for WAIT_FOREVER and NO_WAIT, but
         * they are in NetHose, and are private. */
        return checkProtein (await (-1));
    }

    @Override public Protein previous(Slaw... descrips) throws PoolException {
        threadChecker.check();
        return checkProtein(getPrev(descrips));
    }

    @Override public Hose dup() throws PoolException {
        threadChecker.check();
        final Hose result = Pool.participate(address);
        result.setName(name);
        result.seekTo(index);
        return result;
    }

    @Override public Hose dupAndClose() throws PoolException {
        threadChecker.check();
        withdraw();
        return dup();
    }

    public MemHose(MemPool pool, PoolServerAddress addr) {
        threadChecker.check();
        this.pool = pool;
        index = pool.newestIndex() + 1; // initializing to this value fixes duplicate protein bug
//        index = 0;
        connected = true;
        try {
            address = new PoolAddress(addr, pool.name());
            name = address.toString();
        } catch (BadAddressException e) {
            /* The PoolAddress constructor throws BadAddressException
             * if the pool name is empty.  I guess what Jao is asserting here
             * is that pool.name() can't be the empty string.  This might
             * be a good place to call your new exception handler thingy.
             * Or my inclination would be to throw a RuntimeException that
             * wraps the BadAddressException. */
            assert false; // wtf?
            address = null;
            name = null;
        }
    }

    private ProteinMetadata makeMeta(MetadataRequest r) {
        threadChecker.check();
        final long i = r.index();
        final Protein p = pool.nth(i,
                                   r.descrips(),
                                   r.ingests(),
                                   r.dataStart(),
                                   r.dataLength());
        return p != null ? new MemProteinMetadata(pool.nth(i), p) : null;
    }

    private PoolProtein getNext(Slaw... desc) {
        threadChecker.check();
        final long idx = Math.max(pool.oldestIndex(), index);
        final PoolProtein p = desc.length == 0 ?
                pool.next(idx, 0) : pool.find(idx, desc, true);
        if (p != null) index = p.index() + 1;
        return p;
    }

    private PoolProtein getPrev(Slaw... desc) {
        threadChecker.check();
        final long idx = Math.min(pool.newestIndex(), index - 1);
        final PoolProtein p = desc.length == 0 ?
            pool.nth(idx) : pool.find(idx, desc, false);
        if (p != null) index = p.index();
        return p;
    }

    protected PoolProtein await(double timeout) throws PoolException {
        threadChecker.check();
        if (timeout == 0) return getNext();
        PoolProtein p = pool.next(index, timeout);
        if (p != null) ++index;
        return p;
    }

    private void checkConnected() throws PoolException {
        threadChecker.check();
        if (! connected)
            throw new NoSuchPoolException (0);
    }

    private PoolProtein partialProtein(long idx, boolean d,
                                       boolean i, boolean r) {
        threadChecker.check();
        PoolProtein p = pool.nth (idx, d, i, r  ?  0  :  -1, -1);
        if (p == null)
            return null;
        else
            return new PoolProtein (p, this);
    }

    private Protein checkProtein(PoolProtein p)
            throws NoSuchProteinException {
        threadChecker.check();
        if (p == null) throw new NoSuchProteinException(0);
        return p.source() == null? new PoolProtein(p, this) : p;
    }

    private String name;
    private long index;
    private volatile boolean connected;
    private PoolAddress address;

    public MemPool getPool () {
        threadChecker.check();
        return pool;
    }

    private final MemPool pool;

    private static final PoolMetadata nullMetadata =
        new PoolMetadata() {
            public long size() { return 0; }
            public long usedSize() { return 0; }
            public long indexCapacity() { return -1; }
            public long usedIndexCapacity() { return 0; }
        };
}
