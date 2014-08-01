package com.oblong.jelly.schema.util;

import com.oblong.jelly.schema.fields.HasUid;

/**
 * @author Karol, 2014-07-31
 */
public interface Updatable<TFrom> extends HasUid {

	public void updateFrom(TFrom from);
}
