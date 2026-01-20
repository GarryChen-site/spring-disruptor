package com.garry.springlifecycle.businessproxy.target;


import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import org.springframework.context.ApplicationContext;

public class SingletonPOJOObjectFactory extends POJOObjectFactory {
	private final static String module = SingletonPOJOObjectFactory.class.getName();

	public SingletonPOJOObjectFactory(POJOTargetMetaDef pOJOTargetMetaDef) {
		super(pOJOTargetMetaDef);
	}

	public Object create(ApplicationContext applicationContext) throws Exception {
		Object o = null;
		try {
			o = applicationContext.getBean(pOJOTargetMetaDef.getName());
		} catch (Exception ex) {
			throw new Exception(ex);
		} catch (Throwable tw) {
			throw new Exception(tw);
		}
		return o;
	}

}
