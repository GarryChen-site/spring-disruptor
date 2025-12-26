/**
 * Copyright 2003-2006 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain event copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.garry.springlifecycle.controller.pool;

import com.garry.springlifecycle.businessproxy.target.TargetServiceFactory;
import com.garry.springlifecycle.utils.Debug;
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
			Debug.logError("[JdonFramework] Pool can not make object, error: " + ex, module);
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
