package com.garry.springlifecycle.businessproxy.target;


import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.context.ApplicationContext;

public class SingletonPOJOObjectFactory extends POJOObjectFactory {
	private final static String module = SingletonPOJOObjectFactory.class.getName();

	public SingletonPOJOObjectFactory(POJOTargetMetaDef pOJOTargetMetaDef) {
		super(pOJOTargetMetaDef);
	}

	public Object create(ApplicationContext applicationContext) throws Exception {
		Object o = null;
		try {
			Debug.logVerbose("[JdonFramework] create singleton pojo Object for " + pOJOTargetMetaDef.getName(), module);
			o = applicationContext.getBean(pOJOTargetMetaDef.getName());
			Debug.logVerbose("[JdonFramework] create singleton pojo Object id " + o.hashCode(), module);
		} catch (Exception ex) {
			Debug.logError("[JdonFramework]create Singleton error: " + ex + " for class=" + pOJOTargetMetaDef.getClassName(), module);
			throw new Exception(ex);
		} catch (Throwable tw) {
			Debug.logError("[JdonFramework]create Singleton error: " + tw + " for class=" + pOJOTargetMetaDef.getClassName(), module);
			throw new Exception(tw);
		}
		return o;
	}

}
