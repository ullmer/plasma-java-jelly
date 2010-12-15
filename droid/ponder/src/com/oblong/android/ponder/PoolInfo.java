// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.HashMap;

import com.oblong.jelly.Hose;
import com.oblong.jelly.MetadataRequest;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolMetadata;
import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.Slaw;

/**
 *
 * Created: Mon Dec 13 15:14:50 2010
 *
 * @author jao
 */
final class PoolInfo {

    static PoolInfo tryGet(PoolAddress address) {
        return infos.get(address);
    }

    static PoolInfo get(PoolAddress address) throws PoolException {
        PoolInfo info = infos.get(address);
        if (info == null) {
            info = new PoolInfo(address);
            infos.put(address, info);
        }
        return info;
    }

    PoolAddress address() { return address; }
    PoolCursor cursor() { return cursor; }
    PoolMetadata metadata() { return metadata; }

    ProteinMetadata metadata(long idx) throws PoolException {
        return hose.metadata(new MetadataRequest(idx, true, true, 0, -1));
    }

    private PoolInfo(PoolAddress addr) throws PoolException {
        hose = Pool.participate(addr);
        metadata = hose.metadata();
        cursor = new PoolCursor(hose);
        address = addr;
    }

    private final PoolAddress address;
    private final PoolMetadata metadata;
    private final PoolCursor cursor;
    private final Hose hose;

    private static final HashMap<PoolAddress, PoolInfo> infos =
        new HashMap<PoolAddress, PoolInfo>();

}
