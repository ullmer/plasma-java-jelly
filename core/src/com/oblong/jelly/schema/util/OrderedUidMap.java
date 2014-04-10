package com.oblong.jelly.schema.util;

import com.oblong.jelly.schema.fields.HasUid;
import com.oblong.jelly.schema.fields.Uid;
import com.oblong.util.OrderedMap;

/**
 * @author Karol, 2014-04-10
 */
public class OrderedUidMap <T extends HasUid> {

	protected OrderedMap<Uid<T>, T> internalMap = new OrderedMap<Uid<T>, T>();

	public void clear() {
		internalMap.clear();
	}

	public void put(T item) {
		if ( contains(item) ){
			throw new IllegalArgumentException("Item is already present. Cannot put it second time: " + item);
		}
		internalMap.put(getUidCast(item), item);
	}

	private boolean contains(T item) {
		return internalMap.containsKey(getUidCast(item));
	}

	public int size() {
		return internalMap.size();
	}

	public T get(Uid<T> uid) {
		return internalMap.get(uid);
	}

	public void remove(T item) {
		internalMap.remove(getUidCast(item));
	}

	public void remove(Uid<T> uid) {
		internalMap.remove(uid);
	}

	private Uid<T> getUidCast(T item) {
		return (Uid<T>) item.getUid();
	}

}
