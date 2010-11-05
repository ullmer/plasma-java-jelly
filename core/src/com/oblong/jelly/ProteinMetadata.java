// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 *
 * @author jao
 */
public interface ProteinMetadata {

    long index();
    double timestamp();

    long size();
    long ingestsSize();
    long descripsSize();
    long ingestsNumber();
    long descripsNumber();
    long dataSize();

}
