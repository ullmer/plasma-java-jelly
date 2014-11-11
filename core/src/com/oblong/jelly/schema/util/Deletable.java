package com.oblong.jelly.schema.util;

/**
 * Useful for marking items as deleted, while they are still animating.
 *
 * Created by KD on 2014-06-06.
 */
public interface Deletable {

	boolean isDeleted();

	void setDeleted();

	// TODO: addOnDeletedListener   ?
	// TODO: addOnDeleteListener   ?
	// TODO: addDeletedListener   ?
	// TODO: addDeleteListener   ?

}
