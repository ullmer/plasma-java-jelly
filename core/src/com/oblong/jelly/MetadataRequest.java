// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 * @author jao
 */
public final class MetadataRequest {

    public MetadataRequest(long index,
                        boolean descrips,
                        boolean ingests,
                        long dataStart,
                        long dataLength) {
        this.index = index;
        this.descrips = descrips;
        this.ingests = ingests;
        this.dataStart = dataStart;
        this.dataLength = dataLength;
    }

    public MetadataRequest(long index) { this(index, false, false, -1, -1); }

    public MetadataRequest(Slaw req) {
        this(readLong(req, IDX_K, -1),
             readBool(req, DES_K, false),
             readBool(req, ING_K, false),
             readLong(req, DOF_K, -1),
             readLong(req, DLN_K, 0));
    }

    public long index() { return index; }
    public MetadataRequest index(long index) {
        this.index = index;
        return this;
    }

    public boolean descrips() { return descrips; }
    public MetadataRequest descrips(boolean descrips) {
        this.descrips = descrips;
        return this;
    }

    public boolean ingests() { return ingests; }
    public MetadataRequest ingests(boolean ingests) {
        this.ingests = ingests;
        return this;
    }

    public long dataStart() { return dataStart; }
    public MetadataRequest dataStart(long dataStart) {
        this.dataStart = Math.max(-1, dataStart);
        return this;
    }

    public long dataLength() { return dataLength; }
    public MetadataRequest dataLength(long dataLength) {
        this.dataLength = Math.max(-1, dataLength);
        return this;
    }

    public Slaw toSlaw() {
        return Slaw.map(IDX_K, Slaw.int64(index),
                        DES_K, Slaw.bool(descrips),
                        ING_K, Slaw.bool(ingests),
                        DOF_K, Slaw.int64(dataStart),
                        DLN_K, Slaw.int64(dataLength));
    }

    private static final long readLong(Slaw map, Slaw key, long def) {
        final Slaw v = map.find(key);
        return v != null && v.isNumber(NumericIlk.INT64) ? v.emitLong() : def;
    }

    private static final boolean readBool(Slaw map, Slaw key, boolean def) {
        final Slaw v = map.find(key);
        return v != null && v.isBoolean() ? v.emitBoolean() : def;
    }

    private static final Slaw IDX_K = Slaw.string("idx");
    private static final Slaw DES_K = Slaw.string("des");
    private static final Slaw ING_K = Slaw.string("ing");
    private static final Slaw DOF_K = Slaw.string("roff");
    private static final Slaw DLN_K = Slaw.string("bytes");

    private long index;
    private boolean descrips;
    private boolean ingests;
    private long dataStart;
    private long dataLength;
}
