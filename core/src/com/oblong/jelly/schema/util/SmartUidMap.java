package com.oblong.jelly.schema.util;

import com.oblong.jelly.schema.fields.HasUid;
import com.oblong.jelly.schema.fields.Uid;

import java.util.Collection;
import java.util.Set;

/**
 * @author Karol, 2014-07-31
 */
public abstract class SmartUidMap <TUid extends HasUid, TExistingItem extends Updatable<TIncoming>, TIncoming>
		extends OrderedUidMap <TUid, TExistingItem> {


	public void setInitialItems(Collection<? extends TIncoming> incomingItems) {
		if ( ! isEmpty() ) {
			throw new IllegalStateException("Map no longer empty. Unable to set INITIAL items");
		}
		updateFromIncomingItems(incomingItems); // for now, just redirecting, but I want to make the distinction between initial and update
	}

	public void updateFromIncomingItems(Collection<? extends TIncoming> incomingItems) {
		Set<TExistingItem> notYetProvenToStillExist = createAllItemsSet(); // removal candidates

		for (TIncoming incomingItem : incomingItems) {
			Uid<TUid> incomingItemUid = getIncomingItemUid(incomingItem);
			TExistingItem existingItem = getByUid(incomingItemUid);
			if (existingItem == null) {
				TExistingItem item = createItemFromIncoming(incomingItem);
				put(item);
			} else {
				updateExistingItemFromIncoming(existingItem, incomingItem);
				notYetProvenToStillExist.remove(existingItem); // no longer a removal candidate
			}
		}

		// Remove items, which are no longer existing:
		for (TExistingItem itemToRemove : notYetProvenToStillExist) {
			remove(itemToRemove);
		}
	}

	public TExistingItem acquireByUidEvenIfUnknown(Uid<TUid> uid) {
		TExistingItem existingItem = getByUid(uid);
		if ( existingItem == null ) {
			existingItem = createItemYetUnknown(uid);
			put(existingItem);
		}
		return existingItem;
	}

	protected void updateExistingItemFromIncoming(TExistingItem existingItem, TIncoming incomingItem) {
		existingItem.updateFrom(incomingItem);
	}

	protected abstract TExistingItem createItemFromIncoming(TIncoming incomingItem);

	protected abstract Uid<TUid> getIncomingItemUid(TIncoming incomingItem);

	protected TExistingItem createItemYetUnknown(Uid<TUid> uid) {
		throw new UnsupportedOperationException("Unsupported createItemYetUnknown, for uid: " + uid);
	}

//	protected void onPostDeleteExistingItem(TItem existingItemToBeDeleted) {
//		// default: do nothing
//	}
//
//	protected void onPostAddExistingItem(TItem existingItemToBeDeleted) {
//		// default: do nothing
//	}

}
