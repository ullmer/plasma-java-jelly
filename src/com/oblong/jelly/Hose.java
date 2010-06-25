// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 *
 * Created: Thu Jun 10 22:55:16 2010
 *
 * @author jao
 */
public interface Hose {

    double WAIT = -1;
    double NO_WAIT = 0;
    
    int version();

    String name();
    void setName(String n);
    String poolName();
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
    
    Protein next() throws PoolException;
    Protein next(double timeout) throws PoolException;
    Protein previous() throws PoolException;
    Protein nth(long index) throws PoolException;
}
