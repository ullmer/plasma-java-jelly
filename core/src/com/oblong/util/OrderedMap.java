package com.oblong.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Ordered map, with type safety increased over Java's library Map (Object params changed to either K or V).
 *
 * @author Karol, 2014-04-10
 */
public class OrderedMap <K,V> {

	protected LinkedHashMap<K, V> internalMap = new LinkedHashMap<K, V>();

	public boolean containsValue(V value) {
		return internalMap.containsValue(value);
	}

	public Set<K> keySet() {
		return internalMap.keySet();
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		internalMap.putAll(m);
	}

	public boolean containsKey(K key) {
		return internalMap.containsKey(key);
	}

	public void clear() {
		internalMap.clear();
	}

	public Collection<V> values() {
		return internalMap.values();
	}

	public V remove(K key) {
		return internalMap.remove(key);
	}

	public V put(K key, V value) {
		return internalMap.put(key, value);
	}

	public int size() {
		return internalMap.size();
	}

	public Set<Map.Entry<K,V>> entrySet() {
		return internalMap.entrySet();
	}

	public V get(K key) {
		return internalMap.get(key);
	}

	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

}
