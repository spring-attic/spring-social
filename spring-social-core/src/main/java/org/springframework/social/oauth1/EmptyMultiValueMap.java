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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.MultiValueMap;

class EmptyMultiValueMap<K, V> implements MultiValueMap<K, V> {

	private static final MultiValueMap<Object, Object> INSTANCE = new EmptyMultiValueMap<Object, Object>();
	
	@SuppressWarnings("unchecked")
	public static <K, V> MultiValueMap<K, V> instance() {
		return (MultiValueMap<K, V>) INSTANCE;
	}
	
	private EmptyMultiValueMap() {		
	}
	
	private final Map<K, List<V>> targetMap = Collections.emptyMap();
	
	public void add(K key, V value) {
		throw new UnsupportedOperationException("This empty MultiValueMap is not modifiable");
	}

	public V getFirst(K key) {
		return null;
	}

	public void set(K key, V value) {
		throw new UnsupportedOperationException("This empty MultiValueMap is not modifiable");
	}

	public void setAll(Map<K, V> values) {
		throw new UnsupportedOperationException("This empty MultiValueMap is not modifiable");
	}

	public Map<K, V> toSingleValueMap() {
		return Collections.emptyMap();
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
