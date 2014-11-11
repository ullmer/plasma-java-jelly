package com.oblong.jelly.schema.util;

/**
 * Created by KD on 2014-06-06.
 */
public class DeletableMixin implements Deletable {

	/* Used to distinguish which items are on a list only due to their deletion being animated */
	private boolean deleted;

	@Override public boolean isDeleted() {
		return deleted;
	}

	@Override public void setDeleted() {
		this.deleted = true;
	}
}
