/*
 * Copyright 2003-2006 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain event copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.garry.springlifecycle.aop.interceptor;


import com.garry.springlifecycle.annotation.Interceptor;
import com.garry.springlifecycle.annotation.intercept.Poolable;
import com.garry.springlifecycle.aop.reflection.ProxyMethodInvocation;
import com.garry.springlifecycle.businessproxy.TargetMetaDef;
import com.garry.springlifecycle.businessproxy.target.TargetServiceFactory;
import com.garry.springlifecycle.container.access.TargetMetaRequestsHolder;
import com.garry.springlifecycle.container.finder.ComponentKeys;
import com.garry.springlifecycle.controller.cache.InstanceCache;
import com.garry.springlifecycle.controller.pool.CommonsPoolAdapter;
import com.garry.springlifecycle.controller.pool.CommonsPoolFactory;
import com.garry.springlifecycle.controller.pool.Pool;
import com.garry.springlifecycle.controller.pool.PoolConfigure;
import com.garry.springlifecycle.utils.Debug;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.context.ApplicationContext;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * PoolInterceptor must be the last in Interceptors. this class is active for
 * the pojoServices that implements com.jdon.controller.pool.Poolable.
 * 
 * @author <event href="mailto:banqiao@jdon.com">banq </event>
 * 
 */
//@Interceptor(name = "poolInterceptor",pointcut = "pojoServices")
public class PoolInterceptor implements MethodInterceptor {
	private final static String module = PoolInterceptor.class.getName();

	/**
	 * one target object, one pool
	 */
	private ConcurrentMap poolFactorys;

	private TargetServiceFactory targetServiceFactory;

	private ApplicationContext applicationContext;

	private PoolConfigure poolConfigure;

	private List isPoolableCache = new CopyOnWriteArrayList();

	private Set unPoolableCache = new HashSet();

	private TargetMetaRequestsHolder targetMetaRequestsHolder;

	/**
	 * @param containerCallback
	 */
	public PoolInterceptor(TargetServiceFactory targetServiceFactory, TargetMetaRequestsHolder targetMetaRequestsHolder,
                           ApplicationContext applicationContext, PoolConfigure poolConfigure) {
		super();
		this.targetServiceFactory = targetServiceFactory;
		this.targetMetaRequestsHolder = targetMetaRequestsHolder;
		this.applicationContext = applicationContext;
		this.poolConfigure = poolConfigure;
		this.poolFactorys = new ConcurrentHashMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.intercept
	 * .MethodInvocation)
	 */
	public Object invoke(MethodInvocation invocation) throws Throwable {
		ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) invocation;
		TargetMetaDef targetMetaDef = targetMetaRequestsHolder.getTargetMetaRequest().getTargetMetaDef();
		if (targetMetaDef.isEJB())
			return invocation.proceed();

		if (!isPoolabe(targetMetaDef)) {
			 Debug.logVerbose("[JdonFramework] target service is not Poolable: " + targetMetaDef.getClassName() + " pool unactiive", module);
			return invocation.proceed(); // 下一个interceptor
		}
		Debug.logVerbose("[JdonFramework] enter PoolInterceptor", module);
		CommonsPoolFactory commonsPoolFactory = getCommonsPoolFactoryCache(targetMetaDef);

		Pool pool = commonsPoolFactory.getPool();
		Object poa = null;
		Object result = null;
		try {
			poa = pool.acquirePoolable();
			Debug.logVerbose("[JdonFramework] borrow event object:" + targetMetaDef.getClassName() + " id:" + poa.hashCode() + " from pool", module);
			Debug.logVerbose("[JdonFramework]pool state: active=" + pool.getNumActive() + " free=" + pool.getNumIdle(), module);

			// set the object that borrowed from pool to MethodInvocation
			// so later other Interceptors or MethodInvocation can use it!
			proxyMethodInvocation.setThis(poa);
			result = invocation.proceed();
		} catch (Exception ex) {
			Debug.logError(ex, module);
		} finally {
			if (poa != null) {
				pool.releasePoolable(poa);
				Debug.logVerbose("[JdonFramework] realease event object:" + targetMetaDef.getClassName() + " to pool", module);
			}
		}
		return result;
	}
	
	private CommonsPoolFactory getCommonsPoolFactoryCache(TargetMetaDef targetMetaDef){
		CommonsPoolFactory commonsPoolFactoryExist = (CommonsPoolFactory) poolFactorys.get(targetMetaDef.getCacheKey());
		CommonsPoolFactory commonsPoolFactoryNew = null;
		if (commonsPoolFactoryExist == null) {
			commonsPoolFactoryNew = getCommonsPoolFactory(targetMetaDef);
			commonsPoolFactoryExist= (CommonsPoolFactory)poolFactorys.putIfAbsent(targetMetaDef.getCacheKey(), commonsPoolFactoryNew);
		}
		return commonsPoolFactoryExist != null?commonsPoolFactoryExist:commonsPoolFactoryNew;

	}

	/**
	 * every target service has its CommonsPoolFactory, we cache it in
	 * container, next time, we can get the cached Object Pool for the target
	 * service;
	 * 
	 * @param targetMetaDef
	 * @return
	 */
	private CommonsPoolFactory getCommonsPoolFactory(TargetMetaDef targetMetaDef) {
		try {
			InstanceCache instanceCache = (InstanceCache) applicationContext.getBean(ComponentKeys.INSTANCE_CACHE);

			String key = targetMetaDef.getCacheKey() + " CommonsPoolFactory";

			CommonsPoolFactory commonsPoolFactoryExist = (CommonsPoolFactory) instanceCache.get(key);
			CommonsPoolFactory commonsPoolFactoryNew = null;
			if (commonsPoolFactoryExist == null) {
				Debug.logVerbose("[JdonFramework] first time call commonsPoolFactory， create it:" + key, module);
				commonsPoolFactoryNew = create(targetServiceFactory, poolConfigure.getMaxPoolSize());
				commonsPoolFactoryExist = (CommonsPoolFactory)instanceCache.putIfAbsent(key, commonsPoolFactoryNew);
			}
			return commonsPoolFactoryExist != null?commonsPoolFactoryExist:commonsPoolFactoryNew;

		} catch (Exception ex) {
			Debug.logError(ex, module);
			return null;
		}
		
	}

	public CommonsPoolFactory create(TargetServiceFactory targetServiceFactory, String maxSize) {
		CommonsPoolFactory commonsPoolFactory = new CommonsPoolFactory(targetServiceFactory, maxSize);

		GenericObjectPool apachePool = new GenericObjectPool(commonsPoolFactory);
		CommonsPoolAdapter pool = new CommonsPoolAdapter(apachePool);
		if (maxSize == null) {
			Debug.logError("[JdonFramework] not set pool's max size", module);
		} else {
			int maxInt = Integer.parseInt(maxSize);
			pool.setMaxPoolSize(maxInt);
		}

		commonsPoolFactory.setPool(pool);
		return commonsPoolFactory;

	}

	public boolean isPoolabe(TargetMetaDef targetMetaDef) {
		boolean found = false;
		if (isPoolableCache.contains(targetMetaDef.getName())) {
			found = true;
		} else if (!unPoolableCache.contains(targetMetaDef.getName())) {
			Debug.logVerbose("[JdonFramework] check if it is event Poolable", module);
			Class thisCLass = applicationContext.getType(targetMetaDef.getName());
			if (Poolable.class.isAssignableFrom(thisCLass) || thisCLass.isAnnotationPresent(Poolable.class)) {
				found = true;
				isPoolableCache.add(targetMetaDef.getName());
				Debug.logVerbose("可以被pool " + targetMetaDef.getName(), module);
			} else {
				unPoolableCache.add(targetMetaDef.getName());
				Debug.logVerbose("不可以被pool " + targetMetaDef.getName(), module);
			}
		}
		return found;
	}


}
