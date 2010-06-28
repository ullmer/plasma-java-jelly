// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.impl;

/**
 *
 * Created: Fri Jun 25 23:35:35 2010
 *
 * @author jao
 */
public class ServerErrorCode {

    // Some file-related op failed
    public static final long POOL_FILE_BADTH = pe(500);
    public static final long POOL_NULL_HOSE = pe(505);
    // Problem with semaphores
    public static final long POOL_SEMAPHORES_BADTH = pe(510);
    // mmap didn't work
    public static final long POOL_MMAP_BADTH = pe(520);
    // User tried to create an mmap pool on NFS
    public static final long POOL_INAPPROPRIATE_FILESYSTEM = pe(525);
    // Unknown pool type
    public static final long POOL_TYPE_BADTH = pe(540);
    // Pool config file problem
    public static final long POOL_CONFIG_BADTH = pe(545);
    // Unexpected pool-version in config file
    public static final long POOL_WRONG_VERSION = pe(547);
    // Something about the pool itself is bad/invalid
    public static final long POOL_CORRUPT = pe(548);
    // Invalid pool name
    public static final long POOL_POOLNAME_BADTH = pe(550);
    // Problem with fifos
    public static final long POOL_FIFO_BADTH = pe(555);
    // The size specified for a pool was not a number or beyond bouds
    public static final long POOL_INVALID_SIZE = pe(560);
    // No pool with this name
    public static final long POOL_NO_SUCH_POOL = pe(570);
    // Attempted to create existing pool.
    public static final long POOL_EXISTS_V3 = pe(575);
    // Invalid network op number. This error usually indicates a mismatch
    // between the protocols used by a client and a remote pool server.
    public static final long POOL_NAUGHTY_OP = pe(580);
    // The requested protein was not available
    public static final long POOL_NO_SUCH_PROTEIN = pe(635);
    // Await period expired
    public static final long POOL_AWAIT_TIMEDOUT = pe(640);
    // Await canceled by wake(),
    public static final long POOL_AWAIT_WOKEN = pe(650);
    // Protein bigger than pool
    public static final long POOL_PROTEIN_BIGGER_THAN_POOL = pe(700);
    // Tried to deposit a non-protein slaw
    public static final long POOL_NOT_A_PROTEIN = pe(800);
    // Writing config file failed
    public static final long POOL_CONF_WRITE_BADTH = pe(900);
    // Reading config file failed
    public static final long POOL_CONF_READ_BADTH = pe(910);
    // No duffel with this name
    public static final long POOL_DUFFEL_NOT_FOUND = pe(920);
    // Problem sending over network
    public static final long POOL_SEND_BADTH = pe(1000);
    // Problem reading over network
    public static final long POOL_RECV_BADTH = pe(1010);
    // Problem making network socket
    public static final long POOL_SOCK_BADTH = pe(1020);
    // Network pool server busy
    public static final long POOL_SERVER_BUSY = pe(1030);
    // Network pool server unreachable
    public static final long POOL_SERVER_UNREACH = pe(1040);
    // Pool hose already part of a gang
    public static final long POOL_ALREADY_GANG_MEMBER = pe(1050);
    // Pool hose is not a member of a given gang
    public static final long POOL_NOT_A_GANG_MEMBER = pe(1055);
    // pool_next_multi(), called on an empty gang
    public static final long POOL_EMPTY_GANG = pe(1060);
    // A NULL gang was passed to any of the gang functions
    public static final long POOL_NULL_GANG = pe(1070);
    // The pool type does not support what you want to do to it.
    public static final long POOL_UNSUPPORTED_OPERATION = pe(1100);

    public static final long POOL_EMPTY_V2 = pe(10);
    public static final long POOL_MALLOC_BADTH_V2 = pe(530);
    public static final long POOL_ARG_BADTH_V2 = pe(590);
    public static final long POOL_DISCARDED_PROTEIN_V2 = pe(610);
    public static final long POOL_FUTURE_PROTEIN_V2 = pe(620);

    public static final long SLAW_CORRUPT_PROTEIN = se(0);
    public static final long SLAW_CORRUPT_SLAW = se(1);
    public static final long SLAW_FABRICATOR_BADNESS = se(2);
    public static final long SLAW_NOT_NUMERIC = se(3);
    public static final long SLAW_RANGE_ERR = se(4);
    public static final long SLAW_UNIDENTIFIED_SLAW = se(5);
    public static final long SLAW_WRONG_LENGTH = se(6);
    public static final long SLAW_NOT_FOUND = se(7);

    public static final long SLAW_ALIAS_NOT_SUPPORTED = ioe(0);
    public static final long SLAW_BAD_TAG = ioe(1);
    public static final long SLAW_END_OF_FILE = ioe(2);
    public static final long SLAW_PARSING_BADNESS = ioe(3);
    public static final long SLAW_WRONG_FORMAT = ioe(4);
    public static final long SLAW_WRONG_VERSION = ioe(5);
    public static final long SLAW_YAML_ERR = ioe(6);
    public static final long SLAW_NO_YAML = ioe(7);

    private static final long FIRST_POOL = 200000;
    private static final long FIRST_SLAW = 210000;
    private static final long FIRST_IO = 220000;

    private static long pe(long off) { return -(FIRST_POOL + off); }
    private static long se(long off) { return -(FIRST_SLAW + off); }
    private static long ioe(long off) { return -(FIRST_IO + off); }

    private ServerErrorCode() {}
}
