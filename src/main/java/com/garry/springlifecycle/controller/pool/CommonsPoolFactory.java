/**
 */

package com.garry.springlifecycle.controller.pool;

import com.garry.springlifecycle.businessproxy.target.TargetServiceFactory;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class CommonsPoolFactory implements PooledObjectFactory<Object> {
	private final static String module = CommonsPoolFactory.class.getName();

	private final TargetServiceFactory targetServiceFactory;
	private CommonsPoolAdapter pool;

	/**
	 * @param targetServiceFactory
	 */
	public CommonsPoolFactory(TargetServiceFactory targetServiceFactory, String maxSize) {
		super();
		this.targetServiceFactory = targetServiceFactory;
	}

	public void setPool(CommonsPoolAdapter pool) {
		this.pool = pool;
	}

	/**
	 * @return Returns the pool.
	 */
	public CommonsPoolAdapter getPool() {
		return pool;
	}

	public PooledObject<Object> makeObject() {
		Object o = null;
		try {
			o = targetServiceFactory.create();
		} catch (Exception ex) {
		}
		return new DefaultPooledObject<>(o);
	}

	public void destroyObject(PooledObject<Object> p) throws Exception {
		targetServiceFactory.destroy();
	}

	public void activateObject(PooledObject<Object> p) throws Exception {
	}

	public void passivateObject(PooledObject<Object> p) throws Exception {
	}

	public boolean validateObject(PooledObject<Object> p) {
		return true;
	}

}
