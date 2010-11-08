// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oblong.jelly.BadAddressException;
import com.oblong.jelly.MetadataRequest;
import com.oblong.jelly.Hose;
import com.oblong.jelly.NoSuchPoolException;
import com.oblong.jelly.NoSuchProteinException;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;

final class MemHose implements Hose {

    @Override public int version() { return 3; }

    @Override public Slaw info() { return Slaw.map(); }

    @Override public String name() { return name; }

    @Override public void setName(String n) { name = n; }

    @Override public PoolAddress poolAddress() { return address; }

    @Override public boolean isConnected() { return connected; }

    @Override public void withdraw() { connected = false; }

    @Override public long index() { return index; }

    @Override public long newestIndex() throws PoolException {
        checkConnected();
        polled = null;
        return pool.newestIndex();
    }

    @Override public long oldestIndex() throws PoolException {
        checkConnected();
        polled = null;
        return pool.oldestIndex();
    }

    @Override public void seekTo(long index) {
        polled = null;
        this.index = index;
    }

    @Override public void seekBy(long offset) {
        seekTo(index + offset);
    }

    @Override public void toLast() throws PoolException {
        seekTo(newestIndex());
    }

    @Override public void runOut() throws PoolException {
        seekTo(1 + newestIndex());
    }

    @Override public void rewind() throws PoolException {
        seekTo(oldestIndex());
    }

    @Override public Protein deposit(Protein p) throws PoolException {
        final PoolProtein dp = new PoolProtein(pool.deposit(p), this);
        if (polledIndex == dp.index()) polled = dp;
        return dp;
    }

    @Override public Protein nth(long index) throws PoolException {
        return checkProtein(pool.nth(index));
    }

    @Override public Protein nth(long index,
                                 boolean descrips,
                                 boolean ingests,
                                 boolean data) throws PoolException {
        return checkProtein(partialProtein(index, descrips, ingests, data));
    }

    @Override public List<Protein> range(long from, long to)
        throws PoolException {
        final List<Protein> result = new ArrayList<Protein>();
        for (long k = from; k < to; ++k) {
            final Protein p = partialProtein(k, true, true, true);
            if (p != null) result.add(p);
        }
        return result;
    }

    @Override public ProteinMetadata metadata(MetadataRequest req)
        throws PoolException {
        final ProteinMetadata md = makeMeta(req);
        if (md == null) throw new NoSuchProteinException(0);
        return md;
    }

    @Override public List<ProteinMetadata> metadata(MetadataRequest... rs)
        throws PoolException {
        final List<ProteinMetadata> result = new ArrayList<ProteinMetadata>();
        for (MetadataRequest r : rs) {
            final ProteinMetadata md = makeMeta(r);
            if (md != null) result.add(md);
        }
        return result;
    }

    @Override public Protein current() throws PoolException {
        return nth(index);
    }

    @Override public Protein next(Slaw... descrips) throws PoolException {
        return checkProtein(getNext(descrips));
    }

    @Override public Protein awaitNext(long period, TimeUnit unit)
        throws PoolException, TimeoutException {
    	final PoolProtein p = await(unit.toMillis(period)/1000.00);
    	if (p == null) throw new TimeoutException();
        return checkProtein(p);
    }

    @Override public Protein awaitNext() throws PoolException {
        return checkProtein(await(-1));
    }

    @Override public Protein previous(Slaw... descrips) throws PoolException {
        return checkProtein(getPrev(descrips));
    }

    @Override public boolean poll(Slaw... descrips) {
        if (polled == null || !polled.matches(descrips))
            polled = pool.find(index, descrips, true);
        return polled != null;
    }

    @Override public Protein peek() {
        return polled;
    }

    @Override public Hose dup() throws PoolException {
        final Hose result = Pool.participate(address);
        result.setName(name);
        result.seekTo(index);
        return result;
    }

    @Override public Hose dupAndClose() throws PoolException {
        withdraw();
        return dup();
    }

    MemHose(MemPool pool, PoolServerAddress addr) {
        this.pool = pool;
        index = 0;
        connected = true;
        polled = null;
        polledIndex = -1;
        try {
            address = new PoolAddress(addr, pool.name());
            name = address.toString();
        } catch (BadAddressException e) {
            assert false;
            address = null;
            name = null;
        }
    }

    private ProteinMetadata makeMeta(MetadataRequest r) {
        final long i = r.index();
        final Protein p = pool.nth(i,
                                   r.descrips(),
                                   r.ingests(),
                                   r.dataStart(),
                                   r.dataLength());
        return p != null ? new MemProteinMetadata(pool.nth(i), p) : null;
    }

    private PoolProtein getNext(Slaw... desc) {
        PoolProtein p = maybePolled(desc);
        if (p == null) {
            final long idx = Math.max(pool.oldestIndex(), index);
            p = desc.length == 0 ?
                pool.next(idx, 0) : pool.find(idx, desc, true);
        }
        if (p != null) index = p.index() + 1;
        return p;
    }

    private PoolProtein getPrev(Slaw... desc) {
        polled = null;
        final long idx = Math.min(pool.newestIndex(), index - 1);
        final PoolProtein p = desc.length == 0 ?
            pool.nth(idx) : pool.find(idx, desc, false);
        if (p != null) index = p.index();
        return p;
    }

    private PoolProtein await(double timeout) throws PoolException {
        if (timeout == 0) return getNext();
        PoolProtein p = maybePolled();
        if (p == null) p = pool.next(index, timeout);
        if (p != null) ++index;
        return p;
    }

    private PoolProtein maybePolled(Slaw... descrips) {
        PoolProtein p = null;
        if (polled != null && polled.matches(descrips)) p = polled;
        polled = null;
        return p;
    }

    private void checkConnected() throws PoolException {
    	if (!connected) throw new NoSuchPoolException(0);
    }

    private PoolProtein partialProtein(long idx, boolean d,
                                       boolean i, boolean r) {
        return new PoolProtein(pool.nth(idx, d, i, r ? 0 : -1, -1), this);
    }

    private Protein checkProtein(PoolProtein p)
        throws NoSuchProteinException {
        if (p == null) throw new NoSuchProteinException(0);
        return p.source() == null? new PoolProtein(p, this) : p;
    }

    private String name;
    private long index;
    private boolean connected;
    private PoolAddress address;
    private MemPool pool;
    private PoolProtein polled;
    private long polledIndex;
}
