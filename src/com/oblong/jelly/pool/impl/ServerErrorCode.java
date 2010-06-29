// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.impl;

/**
 *
 * Created: Fri Jun 25 23:35:35 2010
 *
 * @author jao
 */
public enum ServerErrorCode {
    POOL_SPLEND(0),
    // Some file-related op failed
    POOL_FILE_BADTH(pe(500)),
    POOL_NULL_HOSE(pe(505)),
    // Problem with semaphores
    POOL_SEMAPHORES_BADTH(pe(510)),
    // mmap didn't work
    POOL_MMAP_BADTH(pe(520)),
    // User tried to create an mmap pool on NFS
    POOL_INAPPROPRIATE_FILESYSTEM(pe(525)),
    // Unknown pool type
    POOL_TYPE_BADTH(pe(540)),
    // Pool config file problem
    POOL_CONFIG_BADTH(pe(545)),
    // Unexpected pool-version in config file
    POOL_WRONG_VERSION(pe(547)),
    // Something about the pool itself is bad/invalid
    POOL_CORRUPT(pe(548)),
    // Invalid pool name
    POOL_POOLNAME_BADTH(pe(550)),
    // Problem with fifos
    POOL_FIFO_BADTH(pe(555)),
    // The size specified for a pool was not a number or beyond bouds
    POOL_INVALID_SIZE(pe(560)),
    // No pool with this name
    POOL_NO_SUCH_POOL(pe(570)),
    // Attempted to create existing pool.
    POOL_EXISTS_V3(pe(575)),
    // Invalid network op number. This error usually indicates a mismatch
    // between the protocols used by a client and a remote pool server.
    POOL_NAUGHTY_OP(pe(580)),
    // The requested protein was not available
    POOL_NO_SUCH_PROTEIN(pe(635)),
    // Await period expired
    POOL_AWAIT_TIMEDOUT(pe(640)),
    // Await canceled by wake(),
    POOL_AWAIT_WOKEN(pe(650)),
    // Protein bigger than pool
    POOL_PROTEIN_BIGGER_THAN_POOL(pe(700)),
    // Tried to deposit a non-protein slaw
    POOL_NOT_A_PROTEIN(pe(800)),
    // Writing config file failed
    POOL_CONF_WRITE_BADTH(pe(900)),
    // Reading config file failed
    POOL_CONF_READ_BADTH(pe(910)),
    // No duffel with this name
    POOL_DUFFEL_NOT_FOUND(pe(920)),
    // Problem sending over network
    POOL_SEND_BADTH(pe(1000)),
    // Problem reading over network
    POOL_RECV_BADTH(pe(1010)),
    // Problem making network socket
    POOL_SOCK_BADTH(pe(1020)),
    // Network pool server busy
    POOL_SERVER_BUSY(pe(1030)),
    // Network pool server unreachable
    POOL_SERVER_UNREACH(pe(1040)),
    // Pool hose already part of a gang
    POOL_ALREADY_GANG_MEMBER(pe(1050)),
    // Pool hose is not a member of a given gang
    POOL_NOT_A_GANG_MEMBER(pe(1055)),
    // pool_next_multi(), called on an empty gang
    POOL_EMPTY_GANG(pe(1060)),
    // A NULL gang was passed to any of the gang functions
    POOL_NULL_GANG(pe(1070)),
    // The pool type does not support what you want to do to it.
    POOL_UNSUPPORTED_OPERATION(pe(1100)),

    POOL_EMPTY_V2(pe(10)),
    POOL_MALLOC_BADTH_V2(pe(530)),
    POOL_ARG_BADTH_V2(pe(590)),
    POOL_DISCARDED_PROTEIN_V2(pe(610)),
    POOL_FUTURE_PROTEIN_V2(pe(620)),

    SLAW_CORRUPT_PROTEIN(se(0)),
    SLAW_CORRUPT_SLAW(se(1)),
    SLAW_FABRICATOR_BADNESS(se(2)),
    SLAW_NOT_NUMERIC(se(3)),
    SLAW_RANGE_ERR(se(4)),
    SLAW_UNIDENTIFIED_SLAW(se(5)),
    SLAW_WRONG_LENGTH(se(6)),
    SLAW_NOT_FOUND(se(7)),

    SLAW_ALIAS_NOT_SUPPORTED(ioe(0)),
    SLAW_BAD_TAG(ioe(1)),
    SLAW_END_OF_FILE(ioe(2)),
    SLAW_PARSING_BADNESS(ioe(3)),
    SLAW_WRONG_FORMAT(ioe(4)),
    SLAW_WRONG_VERSION(ioe(5)),
    SLAW_YAML_ERR(ioe(6)),
    SLAW_NO_YAML(ioe(7));

    private static final long FIRST_POOL = 200000;
    private static final long FIRST_SLAW = 210000;
    private static final long FIRST_IO = 220000;

    public long code() { return code; }

    private static long pe(long off) { return -(FIRST_POOL + off); }
    private static long se(long off) { return -(FIRST_SLAW + off); }
    private static long ioe(long off) { return -(FIRST_IO + off); }

    private ServerErrorCode(long c) { code = c; }

    private final long code;
}
