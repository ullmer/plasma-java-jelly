// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

/**
 *
 * Created: Sat Jun 19 03:56:52 2010
 *
 * @author jao
 */
enum Request {
    CREATE(0), DISPOSE(1), PARTICIPATE(2), PARTICIPATE_C(3), WITHDRAW(4),
    DEPOSIT(5), NTH_PROTEIN(6), NEXT(7), PROBE_FWD(8), NEWEST_INDEX(9),
    OLDEST_INDEX(10), AWAIT_NEXT(11), ADD_AWAITER(12),
    INFO(15), LIST(16), INDEX_LOOKUP(17), PROBE_BACK(18), PREV(19);

    public int code() { return code; }

    private Request(int c) { code = c; }
    private final int code;
}
