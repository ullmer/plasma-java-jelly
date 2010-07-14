// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;

/**
 *
 *
 * Created: Thu Jun 10 22:55:16 2010
 *
 * @author jao
 */
public interface Hose {

    int version();
    Slaw info();

    String name();
    void setName(String n);
    PoolAddress poolAddress();
    boolean isConnected();

    void withdraw() throws PoolException;

    long index();
    long newestIndex() throws PoolException;
    long oldestIndex() throws PoolException;

    void seekTo(long index);
    void seekBy(long offset);
    void toLast() throws PoolException;
    void runOut() throws PoolException;
    void rewind() throws PoolException;

    Protein deposit(Protein p) throws PoolException;

    Protein current() throws PoolException;
    Protein next() throws PoolException;
    Protein next(Slaw descrip) throws PoolException;
    Protein awaitNext(long period, TimeUnit unit) throws PoolException;
    Protein awaitNext() throws PoolException;
    Protein previous() throws PoolException;
    Protein previous(Slaw descrip) throws PoolException;
    Protein nth(long index) throws PoolException;
}
