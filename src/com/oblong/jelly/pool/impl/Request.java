// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.impl;

import java.util.HashMap;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;

import com.oblong.jelly.pool.InvalidOperationException;
import com.oblong.jelly.pool.ProtocolException;

/**
 *
 * Created: Sat Jun 19 03:56:52 2010
 *
 * @author jao
 */
public enum Request {
    CREATE(0, 3, 1) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(res, 0);
        }
    },
    DISPOSE(1, 1, 1) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(res, 0);
        }
    },
    PARTICIPATE(2, 2, 1) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(res, 0);
        }
    },
    PARTICIPATE_C(3, 4, 1){
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            final Slaw ret = retort(res, 0);
            if (v < 3) {
                final long c = ret.emitLong();
                if (c == ServerErrorCode.POOL_EXISTS_V3.code())
                    return Slaw.int64(0);
            }
            return ret;
        }
    },
    WITHDRAW(4, 0, 1) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(res, 0);
        }
    },
    DEPOSIT(5, 1, 3) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return index(retort(index(res, 0), 1), 2);
        }
    },
    NTH_PROTEIN(6, 1, 3){
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(stamp(protein(res, 0), 1), 2);
        }
    },
    NEXT(7, 1, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    },
    PROBE_FWD(8, 2, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    },
    NEWEST_INDEX(9, 0, 2) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(res, 0), 1);
        }
    },
    OLDEST_INDEX(10, 0, 2) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(res, 0), 1);
        }
    },
    AWAIT_NEXT(11, 1, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 1), 2), 3), 0);
        }
    },
    ADD_AWAITER(12, 0, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 1), 2), 3), 0);
        }
    },
    INFO(15, 1, 2) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return protein(retort(res, 0), 1);
        }
    },
    LIST(16, 0, 2) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(res, 0);
        }
    },
    INDEX_LOOKUP(17, 3, 2) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(res, 0), 1);
        }
    },
    PROBE_BACK(18, 2, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    },
    PREV(19, 1, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    };

    public static Request fromCode(int code) {
        return i2r.get(code);
    }

    public int code() { return code; }
    public int arity() { return arity; }
    public int responseArity() { return responseArity; }

    public Slaw send(PoolConnection conn, Slaw... args)
        throws PoolException {
        if (conn == null || !conn.isOpen()) {
            throw new ProtocolException("Connection closed");
        }
        if (!conn.supportedRequests().contains(this))
            throw new InvalidOperationException("Unsupported op " + this);
        assert arity == args.length
            : this + ": expected arity: " + arity
              + " but got " + args.length + " args";
        return checkResponse(conn.send(this, args), conn.version());
    }

    public Slaw sendAndClose(PoolConnection conn, Slaw... args)
        throws PoolException {
        try {
            return send(conn, args);
        } finally {
            conn.close();
        }
    }

    abstract Slaw getRetort(Slaw res, int v) throws ProtocolException;

    private Slaw checkResponse(Slaw res, int v) throws PoolException {
        if (res.count() < responseArity)
            throw new ProtocolException(res, "Wrong response arity "
                                            + res.count() + " ("
                                            + responseArity + " expected)");
        final Slaw ret = getRetort(res, v);
        final ServerError err = ServerError.getError(v, ret);
        if (err != ServerError.SPLEND)
            throw err.asException(res, ret.emitLong());
        return res;
    }

    private static Slaw index(Slaw s, int p) throws ProtocolException {
        return check(s, p, "int64", s.nth(p).isNumber(NumericIlk.INT64));
    }

    private static Slaw protein(Slaw s, int p) throws ProtocolException {
        return check(s, p, "protein", s.nth(p).isProtein());
    }

    private static Slaw retort(Slaw s, int p) throws ProtocolException {
        Slaw ret = s.nth(p);
        // For now, we discard additional information associated
        // with the error code, if any.
        if (ret.isList() && ret.count() > 0) ret = ret.nth(0);
        check(s, p, "number", ret.isNumber(NumericIlk.INT64));
        return ret;
    }

    private static Slaw stamp(Slaw s, int p) throws ProtocolException {
        return check(s, p, "float64",
                     s.nth(p).isNumber(NumericIlk.FLOAT64));
    }

    private static Slaw check(Slaw s, int p, String kind, boolean b)
        throws ProtocolException {
        if (!b)
            throw new ProtocolException(s, kind + " expected at " + p
                                           + " (was: " + s + ")");
        return s;
    }

    private Request(int c, int a, int ra) {
        code = c;
        arity = a;
        responseArity = ra;
    }

    private final int code;
    private final int arity;
    private final int responseArity;

    private static final HashMap<Integer, Request> i2r =
       new HashMap<Integer, Request>();

    static {
        for (Request r : values()) i2r.put(r.code(), r);
    }
}
