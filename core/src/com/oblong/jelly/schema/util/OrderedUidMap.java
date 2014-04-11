package com.oblong.jelly.schema.util;

import com.oblong.jelly.schema.fields.HasUid;
import com.oblong.jelly.schema.fields.Uid;
import com.oblong.util.OrderedMap;

import java.util.Collection;

/**
 * @author Karol, 2014-04-10
 */
public class OrderedUidMap <TUid extends HasUid, TItem extends HasUid> {

	protected OrderedMap<Uid<TUid>, TItem> internalMap = new OrderedMap<Uid<TUid>, TItem>();

	public void clear() {
		internalMap.clear();
	}

	public void put(TItem item) {
		if ( contains(item) ){
			throw new IllegalArgumentException("Item is already present. Cannot put it second time: " + item);
		}
		internalMap.put(getUidCast(item), item);
	}

	private boolean contains(TItem item) {
		return internalMap.containsKey(getUidCast(item));
	}

	public int size() {
		return internalMap.size();
	}

	public TItem get(Uid<TUid> uid) {
		return internalMap.get(uid);
	}

	public void remove(TItem item) {
		internalMap.remove(getUidCast(item));
	}

	public void remove(Uid<TUid> uid) {
		internalMap.remove(uid);
	}

	private Uid<TUid> getUidCast(TItem item) {
		return (Uid<TUid>) item.getUid();
	}

	public Collection<TItem> values() {
		return internalMap.values();
	}

}
