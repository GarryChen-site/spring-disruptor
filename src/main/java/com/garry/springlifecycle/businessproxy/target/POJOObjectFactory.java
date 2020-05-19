package com.garry.springlifecycle.businessproxy.target;


import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.context.ApplicationContext;

/**
 * @author <event href="mailto:banqiao@jdon.com">banq</event>
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
			Debug.logVerbose("[JdonFramework] create new pojo Object for " + pOJOTargetMetaDef.getName(), module);
			o = applicationContext.getBean(pOJOTargetMetaDef.getName());
			// Debug.logVerbose("[JdonFramework] create new pojo Object id " +
			// o.hashCode(), module);
		} catch (Exception ex) {
			Debug.logError("[JdonFramework]create error: " + ex + " name=" + pOJOTargetMetaDef.getName(), module);
			throw new Exception(ex);
		} catch (Throwable tw) {
			Debug.logError("[JdonFramework]create Throwable error: " + tw + " name=" + pOJOTargetMetaDef.getName(), module);
			throw new Exception(tw);
		}
		return o;
	}

}
