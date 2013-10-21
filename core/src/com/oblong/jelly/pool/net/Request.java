
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import java.util.HashMap;

import com.oblong.jelly.*;
import com.oblong.jelly.pool.ServerErrorCode;


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
            return retort(index(stamp(res, 2), 0), 1);
        }
    },
    NTH_PROTEIN(6, 1, 3) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(stamp(protein(res, 0, 2), 1), 2);
        }
    },
    NEXT(7, 1, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0, 3), 1), 2), 3);
        }
    },
    PROBE_FWD(8, 2, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0, 3), 1), 2), 3);
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

    /**
     * Corresponds to POOL_CMD_AWAIT_NEXT_SINGLE
     */
    AWAIT_NEXT(11, 1, 4, true) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 1, 3), 2), 3), 0);
        }
    },
    /**
     * Corresponds to POOL_CMD_MULTI_ADD_AWAITER
     */
    ADD_AWAITER(12, 0, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 1, 0), 2), 3), 0);
        }
    },
    FANCY_ADD_AWAITER(20, 2, 3) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(stamp(index(res, 2), 1), 0);
        }
    },
    INFO(15, 1, 2) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(protein(res, 1, 0), 0);
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
            return retort(index(stamp(protein(res, 0, 3), 1), 2), 3);
        }
    },
    PREV(19, 1, 4) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            return retort(index(stamp(protein(res, 0, 3), 1), 2), 3);
        }
    }
    ,
    SUB_FETCH(22, 1, 3) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            list(index(index(res, 2), 1), 0);
            return Slaw.int64(0);
        }
    },
    GREENHOUSE(31, 0, 0) {
        Slaw getRetort(Slaw res, int v) throws ProtocolException {
            throw new ProtocolException (res, "GREENHOUSE is a dummy command");
        }
    };

    public static Request fromCode(int code) {
        return i2r.get(code);
    }

    public int code() { return code; }
    public int arity() { return arity; }
    public int responseArity() { return responseArity; }
    public boolean timeouts() { return timeouts; }

    public Slaw send(NetConnection conn, Slaw... args)
                    throws PoolException {
        checkRequest(conn, args);
        return checkResponse(conn.send(this, args), conn.version());
    }

    public Slaw sendAndClose(NetConnection conn, Slaw... args)
                    throws PoolException {
        try {
            return send(conn, args);
        } finally {
            conn.close();
        }
    }

    abstract Slaw getRetort(Slaw res, int v) throws ProtocolException;

    private void checkRequest(NetConnection conn, Slaw... args)
        throws PoolException {
        if (conn == null || !conn.isOpen()) {
            throw new InOutException("Connection closed");
        }
        if (!conn.supportedRequests().contains(this))
            throw new InvalidOperationException("Unsupported op " + this);
        if (arity != args.length)
            throw new ProtocolException(this + " expects " + arity + " args"
                                        + ", but was invoked with arg list "
                                        + Slaw.list(args));
    }

    private Slaw checkResponse(Slaw res, int v) throws PoolException {
        if (timeouts) return checkTimeoutResponse(res, v);
        if (res == null) throw new ProtocolException("No response");
        final Slaw ret = checkRetort(res, v);
        final ServerError err = ServerError.getError(v, ret);
        if (err != ServerError.SPLEND)
            throw err.asException(res, ret.emitLong());
        return res;
    }

    private Slaw checkTimeoutResponse(Slaw res, int v) throws PoolException {
        if (res == null) return null;
        final Slaw ret = checkRetort(res, v);
        ServerError err = ServerError.getError(v, ret);
        return err == ServerError.TIMEOUT ? null : res;
    }

    private Slaw checkRetort(Slaw res, int v) throws PoolException {
        if (res.count() < responseArity)
            throw new ProtocolException(res, "Wrong response arity in "
                                        + this + "\n" + res + "\n("
                                        + responseArity + " expected)");
        return getRetort(res, v);
    }

    private static Slaw index(Slaw s, int p) throws ProtocolException {
        return check(s, p, "int64", s.nth(p).isNumber(NumericIlk.INT64));
    }

    private static Slaw protein(Slaw s, int p, int rp)
        throws ProtocolException {
        return check(s, p, "protein or nil with error code",
                     s.nth(p).isProtein()
                     || (s.nth(p).isNil() && retort(s, rp).emitLong() != 0));
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

    private static Slaw list(Slaw s, int p) throws ProtocolException {
        return check(s, p, "list", s.nth(p).isList());
    }

    private static Slaw check(Slaw s, int p, String kind, boolean b)
        throws ProtocolException {
        if (!b)
            throw new ProtocolException(s, kind + " expected at " + p
                                           + " (response was: " + s + ")");
        return s;
    }

    private Request(int c, int a, int ra) {
        this(c, a, ra, false);
    }

    /***
     *
     * @param c code
     * @param a arity
     * @param ra responseArity
     * @param tos timeouts
     */
    private Request(int c, int a, int ra, boolean tos) {
        code = c;
        arity = a;
        responseArity = ra;
        timeouts = tos;
    }

    private final int code;
    private final int arity;
    private final int responseArity;
    private final boolean timeouts;

    private static final HashMap<Integer, Request> i2r =
       new HashMap<Integer, Request>();

    static {
        for (Request r : values()) i2r.put(r.code(), r);
    }
}
