/**
 */

package com.garry.springlifecycle.controller.pool;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * ObjectPool pool = new StackObjectPool(new MyPoolableObjectFactory());
 * CommonsPoolAdapter cp = new CommonsPoolAdapter(pool); MyObject mo =
 * (MyObject)cp.borrowObject(); .... cp.returnObject(mo);
 * 
 * <p>
 * 
 *         </p>
 */
public class CommonsPoolAdapter implements Pool {
	private final GenericObjectPool<Object> pool;

	public CommonsPoolAdapter(GenericObjectPool<Object> pool) {

		this.pool = pool;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		pool.setMaxTotal(maxPoolSize);
	}

	public int getMaxPoolSize() {
		return pool.getMaxTotal();
	}

	public Object acquirePoolable() throws Exception {
		return this.pool.borrowObject();
	}

	public void releasePoolable(Object object) throws Exception {
		this.pool.returnObject(object);
	}

	public int getNumActive() {
		return this.pool.getNumActive();
	}

	public int getNumIdle() {
		return this.pool.getNumIdle();
	}

	public void close() {
		pool.clear();
		try {
			pool.close();
		} catch (Exception e) {
		}
	}

}
