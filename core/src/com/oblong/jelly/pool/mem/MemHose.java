// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oblong.jelly.BadAddressException;
import com.oblong.jelly.Hose;
import com.oblong.jelly.NoSuchPoolException;
import com.oblong.jelly.NoSuchProteinException;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
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
        return pool.newestIndex();
    }

    @Override public long oldestIndex() throws PoolException {
        checkConnected();
        return pool.oldestIndex();
    }

    @Override public void seekTo(long index) {
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
        return pool.deposit(p);
    }

    @Override public Protein nth(long index) throws PoolException {
        return checkProtein(pool.nth(index));
    }

    @Override public Protein current() throws PoolException {
        return nth(index);
    }

    @Override public Protein next(Slaw... descrips) throws PoolException {
        return checkProtein(getNext(descrips));
    }

    @Override public Protein awaitNext(long period, TimeUnit unit)
        throws PoolException, TimeoutException {
    	final Protein p = await(unit.toMillis(period)/1000.00);
    	if (p == null) throw new TimeoutException();
        return checkProtein(p);
    }

    @Override public Protein awaitNext() throws PoolException {
        return checkProtein(await(0));
    }

    @Override public Protein previous(Slaw... descrips) throws PoolException {
        return checkProtein(getPrev(descrips));
    }

    @Override public Hose dup() throws PoolException {
        final Hose result = Pool.participate(address);
        result.setName(name);
        result.seekTo(index);
        return result;
    }

    MemHose(MemPool pool, PoolServerAddress addr) {
        this.pool = pool;
        index = 0;
        connected = true;
        try {
	        address = new PoolAddress(addr, pool.name());
	        name = address.toString();
        } catch (BadAddressException e) {
        	assert false;
        	address = null;
        	name = null;
        }
    }

    private Protein getNext(Slaw... desc) {
        final long idx = Math.max(pool.oldestIndex(), index);
        final PoolProtein p = desc.length == 0 ?
            pool.next(idx, 0) : pool.find(idx, desc, false);
        if (p != null) index = p.index() + 1;
        return p;
    }

    private Protein getPrev(Slaw... desc) {
        final long idx = Math.min(pool.newestIndex(), index - 1);
        final PoolProtein p = desc.length == 0 ?
            pool.nth(idx) : pool.find(idx, desc, true);
        if (p != null) index = p.index();
        return p;
    }

    private Protein await(double timeout) throws PoolException {
        final PoolProtein p = pool.next(index, timeout);
        if (p != null) ++index;
        return p;
    }

    private void checkConnected() throws PoolException {
    	if (!connected) throw new NoSuchPoolException(0);
    }

    private Protein checkProtein(Protein p) throws NoSuchProteinException {
        if (p == null) throw new NoSuchProteinException(0);
        return p;
    }

    private String name;
    private long index;
    private boolean connected;
    private PoolAddress address;
    private MemPool pool;
}
