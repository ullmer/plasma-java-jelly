// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Parameters specifying a protein metadata request.
 *
 * This is a simple container class with a set of settable fields. All
 * setters return a reference to the called object, so that the can be
 * easily chained. For instance,
 * <pre>
 *   hose.metadata(new MetadataRequest(3).dataStart(1).dataLength(2));
 * </pre>
 * will request metadata for a protein with index 3, and ask for a
 * slice of its data of two bytes, starting at an offset of 1.
 *
 * @author jao
 */
public final class MetadataRequest {

    /**
     * Factory creating an array of requests for a consecutive range
     * of @c size indexes, starting at @c idx. Each request is created
     * using {@link #MetadataRequest(long)}. Handy for quickly creating
     * the argument to {@link Hose#metadata(MetadataRequest...)}.
     */
    public static final MetadataRequest[] range(long idx, int size) {
        final MetadataRequest[] reqs = new MetadataRequest[size];
        for (int i = 0; i < size; ++i) reqs[i] = new MetadataRequest(idx + i);
        return reqs;
    }

    /**
     * Full constructor specifying all fields. Negative values of
     * <code>dataStart</code> and <code>dataLength</code> are
     * normalized to -1.
     */
    public MetadataRequest(long index,
                           boolean descrips,
                           boolean ingests,
                           long dataStart,
                           long dataLength) {
        this.index = index;
        this.descrips = descrips;
        this.ingests = ingests;
        this.dataStart = Math.max(-1, dataStart);
        this.dataLength = Math.max(-1, dataLength);
    }

    /**
     * A request for the given index, without descrips, ingests or
     * data. Equivalent to
     * <code>MetadataRequest(index, false, false, -1, -1)</code>.
     */
    public MetadataRequest(long index) { this(index, false, false, -1, -1); }

    /**
     * Constructs a request from its Slaw representation.
     * This is an internal format, constructed by {@link #toSlaw}.
     */
    public MetadataRequest(Slaw req) {
        this(readLong(req, IDX_K, -1),
             readBool(req, DES_K, false),
             readBool(req, ING_K, false),
             readLong(req, DOF_K, -1),
             readLong(req, DLN_K, 0));
    }

    /**
     * The index of the protein whose metadata we're retrieving.
     * @see ProteinMetadata#index
     */
    public long index() { return index; }

    /** Setter for {@link #index()}. */
    public MetadataRequest index(long index) {
        this.index = index;
        return this;
    }

    /**
     * Whether we want to retrieve the protein's descrips.
     * @see ProteinMetadata#descrips
     */
    public boolean descrips() { return descrips; }

    /** Setter for {@link #descrips()}. */
    public MetadataRequest descrips(boolean descrips) {
        this.descrips = descrips;
        return this;
    }

    /**
     * Whether we want to retrieve the protein's ingests.
     * @see ProteinMetadata#ingests
     */
    public boolean ingests() { return ingests; }

    /** Setter for {@link #ingests()}. */
    public MetadataRequest ingests(boolean ingests) {
        this.ingests = ingests;
        return this;
    }

    /**
     * The start of the protein data slice we want to retrieve. Set it
     * to a negative value to specify an empty slice.
     */
    public long dataStart() { return dataStart; }

    /**
     * Setter for {@link #dataStart()}. Negative values are nomalized
     * to -1.
     */
    public MetadataRequest dataStart(long dataStart) {
        this.dataStart = Math.max(-1, dataStart);
        return this;
    }

    /**
     * The length of the protein data slice we want to retrieve. Set
     * it to a negative value to specify "until the end". If you set
     * this value to 0, no data will be retrieved. If {@link
     * #dataStart()} is set to a negative value, this field has no effect.
     */
    public long dataLength() { return dataLength; }

    /**
     * Setter for {@link #dataLength()}. Negative values are nomalized
     * to -1.
     */
    public MetadataRequest dataLength(long dataLength) {
        this.dataLength = Math.max(-1, dataLength);
        return this;
    }

    /**
     * Internal representation of this request as a Slaw. Its
     * structure is not guaranteed to remain stable, so don't rely on
     * it in your code.
     */
    public Slaw toSlaw() {
        return Slaw.map(IDX_K, Slaw.int64(index),
                        DES_K, Slaw.bool(descrips),
                        ING_K, Slaw.bool(ingests),
                        DOF_K, Slaw.int64(dataStart),
                        DLN_K, Slaw.int64(dataLength));
    }

    /**
     * String representation of this request. This representation is a
     * rendering of the internal Slaw representation returned by
     * {@link #toSlaw}. Stress on <i>internal</i>: you shouldn't rely
     * on it being stable or, worse, parsable. Just for debugging
     * purposes.
     */
    @Override public String toString() { return toSlaw().toString(); }

    /**
     * Unsurprising equality method. Two requests are equal when all
     * their fields are equal.
     */
    @Override public boolean equals(Object o) {
        if (!(o instanceof MetadataRequest)) return false;
        final MetadataRequest om = (MetadataRequest)o;
        return om.index == index
            && om.descrips == descrips
            && om.ingests == ingests
            && om.dataStart == dataStart
            && om.dataLength == dataLength;
    }

    @Override public int hashCode() {
        return (int)(1 + index + dataStart + dataLength
                     + (descrips ? 13 : 17) + (ingests ? 23 : 43));
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
    private static final Slaw DLN_K = Slaw.string("rbytes");

    private long index;
    private boolean descrips;
    private boolean ingests;
    private long dataStart;
    private long dataLength;
}
