package com.oblong.jelly.pool.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.Slaw;
import com.oblong.util.Pair;

public enum ServerError {
    NO_SUCH_PROTEIN,
    TIMEOUT,
    UNREGISTERED;

    public static ServerError getError(int v, long sc) {
        final ServerError e = C_TO_ERROR.get(Pair.create(v, sc));
        return e == null ? UNREGISTERED : e;
    }
    
    public static ServerError getError(int v, Slaw retort) {
        return getError(v, retort.emitLong());
    }
    
    public static long getServerCode(int v, ServerError e) {
        final Long c = ERROR_TO_C.get(Pair.create(v, e)); 
        return c == null ? 0 : c;
    }
    
    static void registerError(int v, long sc, ServerError err) {
        C_TO_ERROR.put(Pair.create(v, sc), err);
        ERROR_TO_C.put(Pair.create(v, err), sc);
    }

    private static final 
    ConcurrentHashMap<Pair<Integer,Long>, ServerError> C_TO_ERROR;
    
    private static final 
    ConcurrentHashMap<Pair<Integer, ServerError>, Long> ERROR_TO_C;
    
    static {
        C_TO_ERROR = new ConcurrentHashMap<Pair<Integer,Long>, ServerError>();
        ERROR_TO_C = new ConcurrentHashMap<Pair<Integer,ServerError>, Long>();
    }
}
