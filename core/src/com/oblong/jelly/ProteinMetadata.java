// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Interface describing protein metadata fields.
 *
 * Metadata about proteins in a pool can be obtained using {@link
 * Hose#metadata(MetadataRequest)} and {@link
 * Hose#metadata(MetadataRequest...)}. This interface lists the pieces
 * of information composing metadata.
 *
 * @author jao
 */
public interface ProteinMetadata {

    /**
     * The protein's index. This is the value that would be returned
     * by {@link Protein#index}, had you retrieved the protein. This
     * field is always available.
     */
    long index();

    /**
     * The protein's timestamp. This is the value that would be
     * returned by {@link Protein#timestamp()}, had you retrieved the
     * protein. This field is always available.
     */
    double timestamp();

    /**
     * The total size, in bytes, of the protein. This is the network
     * payload of the serialized protein. Always available.
     */
    long size();

    /**
     * The total size, in bytes, of the serialized descrips (i.e.,
     * their network payload). Note that this is the size correspoding
     * to the descrips of the protein in the pool, not necessarily to
     * the descrips returned by {@link #descrips()} below. Always
     * available.
     */
    long descripsSize();

    /**
     * The total size, in bytes, of the serialized ingests (i.e.,
     * their network payload). Note that this is the size associated
     * to the ingests of the protein in the pool, not necessarily to
     * the descrips returned by {@link #descrips()} below. Always
     * available.
     */
    long ingestsSize();

    /**
     * The total size, in bytes, of the serialized protein data (i.e.,
     * their network payload). Note that this is the size of the data
     * of the protein in the pool, not necessarily that of the
     * descrips returned by {@link #descrips()} below. Always
     * available.
     */
    long dataSize();

    /**
     * Number of descrips of the protein in the pool. For lists or
     * maps, this is the value returned by {@link Slaw#count()}. If
     * the protein's descrips are not a list or a map, this method
     * returns 0. This is the value corresponding to the protein in
     * the pool, not necessarily to the descrips returned by {@link
     * #ingests()} below.
     */
    long descripsNumber();

    /**
     * Number of ingests of the protein in the pool. For lists or
     * maps, this is the value returned by {@link Slaw#count()}. If
     * the protein's ingests are not a list or a map, this method
     * returns 0. This is the value corresponding to the protein in
     * the pool, not necessarily to the ingests returned by {@link
     * #ingests()} below.
     */
    long ingestsNumber();

    /**
     * If requested in the corresponding MetadataRequest, the
     * protein's descrips; null otherwise.
     */
    Slaw descrips();

    /**
     * If requested in the corresponding MetadataRequest, the
     * protein's ingests; null otherwise.
     */
    Slaw ingests();

    /**
     * The protein data requested in the corresponding
     * MetadataRequest. In general, this is a slice of the protein's
     * data, as specified by {@link MetadataRequest#dataStart} and
     * {@link MetadataRequest#dataLength}. So, in general,
     * <code>data().size() != dataSize()</code>.
     */
    byte[] data();
}
