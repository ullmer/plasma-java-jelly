package com.oblong.jelly.schema.fields;

/**
 * Not having generic param T for this class, because it would cause problem with multi-level inheritance
 * of model objects.
 *
 * User: karol
 * Date: 11/18/13
 * Time: 8:23 PM
 */
public interface HasUid {

	public Uid<?> getUid();

}
