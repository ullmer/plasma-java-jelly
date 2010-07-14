package com.oblong.jelly.pool.impl;

import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress.BadAddress;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.CorruptPoolException;
import com.oblong.jelly.pool.InvalidOperationException;
import com.oblong.jelly.pool.NoSuchPoolException;
import com.oblong.jelly.pool.NoSuchProteinException;
import com.oblong.jelly.pool.PoolExistsException;
import com.oblong.jelly.pool.ProtocolException;
import com.oblong.jelly.pool.TimeoutException;

import static com.oblong.jelly.pool.impl.ServerErrorCode.*;
import com.oblong.util.Pair;

public enum ServerError {
    SPLEND {
        public PoolException asException(Slaw res, long code) {
            return null;
        }
    },
    NO_SUCH_PROTEIN {
        public PoolException asException(Slaw res, long code) {
            return new NoSuchProteinException(code);
        }
    },
    NO_SUCH_POOL {
        public PoolException asException(Slaw res, long code) {
            return new NoSuchPoolException(code);
        }
    },
    POOL_EXISTS {
        public PoolException asException(Slaw res, long code) {
            return new PoolExistsException(code);
        }
    },
    TIMEOUT {
        public PoolException asException(Slaw res, long code) {
            return new TimeoutException(code);
        }
    },
    CORRUPT_POOL {
        public PoolException asException(Slaw res, long code) {
            return new CorruptPoolException(code);
        }
    },
    BAD_NAME {
        public PoolException asException(Slaw res, long code) {
            return new BadAddress(code);
        }
    },
    NO_OP {
        public PoolException asException(Slaw res, long code) {
            return new InvalidOperationException(code);
        }
    },
    PROTOCOL_ERROR {
        public PoolException asException(Slaw res, long code) {
            return new ProtocolException(res, code);
        }
    },
    UNREGISTERED {
        public PoolException asException(Slaw res, long code) {
            return new ProtocolException(res, code);
        }
    };

    public abstract PoolException asException(Slaw res, long code);

    public static ServerError getError(int v, long sc) {
        final ServerError e = C_TO_ERROR.get(Pair.create(v, sc));
        return e == null ? (sc >= 0 ? SPLEND : UNREGISTERED) : e;
    }

    public static ServerError getError(int v, Slaw retort) {
        if (!retort.isNumber()) return PROTOCOL_ERROR;
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

    private static void reg(int v, ServerError err, ServerErrorCode... cs) {
        for (ServerErrorCode c : cs) {
            for (int i = 0; i <= v; ++i) {
                registerError(v, c.code(), err);
            }
        }
    }

    private static final
    ConcurrentHashMap<Pair<Integer,Long>, ServerError> C_TO_ERROR;

    private static final
    ConcurrentHashMap<Pair<Integer, ServerError>, Long> ERROR_TO_C;

    static {
        C_TO_ERROR = new ConcurrentHashMap<Pair<Integer,Long>, ServerError>();
        ERROR_TO_C = new ConcurrentHashMap<Pair<Integer,ServerError>, Long>();

        reg(3, CORRUPT_POOL,
            POOL_FILE_BADTH,
            POOL_NULL_HOSE,
            POOL_SEMAPHORES_BADTH,
            POOL_MMAP_BADTH,
            POOL_INAPPROPRIATE_FILESYSTEM,
            POOL_TYPE_BADTH,
            POOL_CONFIG_BADTH,
            POOL_WRONG_VERSION,
            POOL_CORRUPT,
            POOL_FIFO_BADTH,
            POOL_CONF_WRITE_BADTH,
            POOL_CONF_READ_BADTH,
            POOL_DUFFEL_NOT_FOUND);
        reg(3, PROTOCOL_ERROR,
            POOL_NAUGHTY_OP,
            POOL_NOT_A_PROTEIN,
            POOL_INVALID_SIZE,
            POOL_PROTEIN_BIGGER_THAN_POOL,
            POOL_SEND_BADTH,
            POOL_RECV_BADTH,
            POOL_SOCK_BADTH,
            POOL_SERVER_BUSY,
            POOL_SERVER_UNREACH);
        reg(3, BAD_NAME, POOL_POOLNAME_BADTH);
        reg(3, TIMEOUT, POOL_AWAIT_TIMEDOUT, POOL_AWAIT_WOKEN);
        reg(3, NO_SUCH_PROTEIN, POOL_NO_SUCH_PROTEIN);
        reg(3, NO_SUCH_POOL, POOL_NO_SUCH_POOL);
        reg(3, POOL_EXISTS, POOL_EXISTS_V3);
        reg(3, NO_OP, POOL_UNSUPPORTED_OPERATION);

        reg(2, NO_SUCH_PROTEIN,
            POOL_EMPTY_V2,
            POOL_DISCARDED_PROTEIN_V2,
            POOL_FUTURE_PROTEIN_V2);
        reg(2, CORRUPT_POOL, POOL_MALLOC_BADTH_V2);
        reg(2, PROTOCOL_ERROR, POOL_ARG_BADTH_V2);
    }
}
