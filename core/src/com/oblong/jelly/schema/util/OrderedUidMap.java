package com.oblong.jelly.schema.util;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.fields.HasUid;
import com.oblong.jelly.schema.fields.Uid;
import com.oblong.util.OrderedMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author Karol, 2014-04-10
 */
public class OrderedUidMap <TUid extends HasUid, TItem extends HasUid> {

	protected OrderedMap<Uid<TUid>, TItem> internalMap = new OrderedMap<Uid<TUid>, TItem>();

//	public void clear() {
//		internalMap.clear();
//	}

	public void put(TItem item) {
		if ( contains(item) ){
			throw new IllegalArgumentException("Item is already present. Cannot put it second time: " + item);
		}
		internalMap.put(getUidCast(item), item);
	}

	public boolean contains(TItem item) {
		return internalMap.containsKey(getUidCast(item));
	}

	public int size() {
		return internalMap.size();
	}

	public TItem getByUid(Uid<TUid> uid) {
		return internalMap.get(uid);
	}

	public void remove(TItem item) {
		internalMap.remove(getUidCast(item));
	}

	public TItem remove(Uid<TUid> uid) {
		return internalMap.remove(uid);
	}

	private Uid<TUid> getUidCast(TItem item) {
            /*
		//return (Uid<TUid>) item.getUid();
		Uid<?> uid = item.getUid(); // BAU: next four lines suggested by copilot in response to java21 error
		if (uid instanceof Uid<?>) { return (Uid<TUid>) uid; 
		} else { // Handle the case where the cast is not safe
  		  throw new ClassCastException("Cannot cast to Uid<TUid>");
		}
            */
            //neither original nor copilot suggestion compiling.  Punting for the moment		
	    throw new ClassCastException("Cannot cast to Uid<TUid>");
	}

	public Collection<TItem> values() {
		return internalMap.values();
	}

	public ArrayList<TItem> toList() {
		ArrayList<TItem> list = new ArrayList<TItem>(internalMap.values());
		return list;
	}

	public Slaw getUidsSlawList() {
		return Slaw.list(internalMap.keySet());
	}

	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	protected Set<TItem> createAllItemsSet() {
		return internalMap.createSet();
	}

	@Override public String toString() {
		return "OrderedUidMap{" + internalMap +
				'}';
	}

}
