// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.impl;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;

import com.oblong.jelly.pool.ProtocolException;

/**
 *
 * Created: Sat Jun 19 03:56:52 2010
 *
 * @author jao
 */
public enum Request {
    CREATE(0, 3, 1) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(res, 0);
        }
    },
    DISPOSE(1, 1, 1) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(res, 0);
        }
    },
    PARTICIPATE(2, 2, 1) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(res, 0);
        }
    },
    PARTICIPATE_C(3, 4, 1){
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(res, 0);
        }
    },
    WITHDRAW(4, 0, 1) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(res, 0);
        }
    },
    DEPOSIT(5, 1, 3) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return index(retort(index(res, 0), 1), 2);
        }
    },
    NTH_PROTEIN(6, 1, 3){
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(stamp(protein(res, 0), 1), 2);
        }
    },
    NEXT(7, 1, 4) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    },
    PROBE_FWD(8, 2, 4) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    },
    NEWEST_INDEX(9, 0, 2) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(res, 0), 1);
        }
    },
    OLDEST_INDEX(10, 0, 2) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(res, 0), 1);
        }
    },
    AWAIT_NEXT(11, 1, 4) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(stamp(protein(res, 1), 2), 3), 0);
        }
    },
    ADD_AWAITER(12, 0, 4) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(stamp(protein(res, 1), 2), 3), 0);
        }
    },
    INFO(15, 1, 2) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return protein(retort(res, 0), 1);
        }
    },
    LIST(16, 0, 2) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(res, 0);
        }
    },
    INDEX_LOOKUP(17, 3, 2) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(res, 0), 1);
        }
    },
    PROBE_BACK(18, 2, 4) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    },
    PREV(19, 1, 4) {
        Slaw getRetort(Slaw res) throws ProtocolException {
            return retort(index(stamp(protein(res, 0), 1), 2), 3);
        }
    };

    public int code() { return code; }
    public int arity() { return arity; }
    public int responseArity() { return responseArity; }

    public Slaw send(ServerConnection conn, Slaw... args)
        throws PoolException {
        if (conn == null || !conn.isOpen()) {
            throw new ProtocolException("Connection closed");
        }
        assert arity == args.length;
        return checkResponse(conn.send(this, args), conn.version());
    }

    public Slaw sendAndClose(ServerConnection conn, Slaw... args)
        throws PoolException {
        try {
            return send(conn, args);
        } finally {
            conn.close();
        }
    }

    abstract Slaw getRetort(Slaw lst) throws ProtocolException;

    private Slaw checkResponse(Slaw res, int v) throws PoolException {
        if (res.count() < responseArity)
            throw new ProtocolException(res, "Wrong response arity "
                                            + res.count() + " ("
                                            + responseArity + " expected)");
        final Slaw ret = getRetort(res);
        final ServerError err = ServerError.getError(v, ret);
        if (err != ServerError.SPLEND) 
            throw err.asException(res, ret.emitLong());
        return res;
    }

    private static Slaw index(Slaw s, int p) throws ProtocolException {
        return check(s, p, "int32", s.nth(p).isNumber(NumericIlk.INT32));
    }

    private static Slaw protein(Slaw s, int p) throws ProtocolException {
        return check(s, p, "protein", s.nth(p).isProtein());
    }

    private static Slaw retort(Slaw s, int p) throws ProtocolException {
        final Slaw ret = s.nth(p);
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
            throw new ProtocolException(s, kind + "expected at " + p);
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
}
