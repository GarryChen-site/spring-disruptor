/*
 * 
 */
package com.garry.springlifecycle.controller.cache;


import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * simple instance cache this class can be used cache some components.
 * 
 * 
 */
@Component
public class InstanceCache  {
	public final static String NAME = "InstanceCache";

	private ConcurrentMap pool = new ConcurrentHashMap();

	public void start() {

	}

	public void stop() {
		pool.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.controller.cache.Cache#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return pool.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.controller.cache.Cache#put(java.lang.Object,
	 * java.lang.Object)
	 */
	public void put(Object key, Object value) {
		pool.put(key, value);
	}
	
	public Object putIfAbsent(Object key, Object value) {
		return pool.putIfAbsent(key, value);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.controller.cache.Cache#remove(java.lang.Object)
	 */
	public void remove(Object key) {
		pool.remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.controller.cache.Cache#size()
	 */
	public long size() {
		return pool.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.controller.cache.Cache#clear()
	 */
	public void clear() {
		pool.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.controller.cache.Cache#contain(java.lang.Object)
	 */
	public boolean contain(Object key) {
		return pool.containsKey(key);
	}

	public Set keys() {
		return pool.keySet();
	}

}
