/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.oauth1;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.util.MultiValueMap;

class TreeMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable {

	private static final long serialVersionUID = 3801124242820219131L;

	private final Map<K, List<V>> targetMap;

	public TreeMultiValueMap() {
		this.targetMap = new TreeMap<K, List<V>>();
	}

	public TreeMultiValueMap(Map<K, List<V>> otherMap) {
		this.targetMap = new TreeMap<K, List<V>>(otherMap);
	}

	// MultiValueMap implementation

	public void add(K key, V value) {
		List<V> values = this.targetMap.get(key);
		if (values == null) {
			values = new LinkedList<V>();
			this.targetMap.put(key, values);
		}
		values.add(value);
	}

	public V getFirst(K key) {
		List<V> values = this.targetMap.get(key);
		return (values != null ? values.get(0) : null);
	}

	public void set(K key, V value) {
		List<V> values = new LinkedList<V>();
		values.add(value);
		this.targetMap.put(key, values);
	}

	public void setAll(Map<K, V> values) {
		for (Entry<K, V> entry : values.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	public Map<K, V> toSingleValueMap() {
		LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K,V>(this.targetMap.size());
		for (Entry<K, List<V>> entry : targetMap.entrySet()) {
			singleValueMap.put(entry.getKey(), entry.getValue().get(0));
		}
		return singleValueMap;
	}


	// Map implementation

	public int size() {
		return this.targetMap.size();
	}

	public boolean isEmpty() {
		return this.targetMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return this.targetMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this.targetMap.containsValue(value);
	}

	public List<V> get(Object key) {
		return this.targetMap.get(key);
	}

	public List<V> put(K key, List<V> value) {
		return this.targetMap.put(key, value);
	}

	public List<V> remove(Object key) {
		return this.targetMap.remove(key);
	}

	public void putAll(Map<? extends K, ? extends List<V>> m) {
		this.targetMap.putAll(m);
	}

	public void clear() {
		this.targetMap.clear();
	}

	public Set<K> keySet() {
		return this.targetMap.keySet();
	}

	public Collection<List<V>> values() {
		return this.targetMap.values();
	}

	public Set<Entry<K, List<V>>> entrySet() {
		return this.targetMap.entrySet();
	}


	@Override
	public boolean equals(Object obj) {
		return this.targetMap.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.targetMap.hashCode();
	}

	@Override
	public String toString() {
		return this.targetMap.toString();
	}

}
