package com.garry.springlifecycle.businessproxy.target;


import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import org.springframework.context.ApplicationContext;

/**
 * 
 */
public class POJOObjectFactory implements TargetObjectFactory {
	private final static String module = POJOObjectFactory.class.getName();

	protected POJOTargetMetaDef pOJOTargetMetaDef;

	/**
	 * @param containerCallback
	 */
	public POJOObjectFactory(POJOTargetMetaDef pOJOTargetMetaDef) {
		super();
		this.pOJOTargetMetaDef = pOJOTargetMetaDef;
	}

	public Object create(ApplicationContext applicationContext) throws Exception {
		Object o = null;
		try {
			o = applicationContext.getBean(pOJOTargetMetaDef.getName());
			// o.hashCode(), module);
		} catch (Exception ex) {
			throw new Exception(ex);
		} catch (Throwable tw) {
			throw new Exception(tw);
		}
		return o;
	}

}
