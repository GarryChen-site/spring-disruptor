package com.garry.springlifecycle.businessproxy.target;


import com.garry.springlifecycle.businessproxy.TargetMetaDef;
import com.garry.springlifecycle.container.access.TargetMetaRequestsHolder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Factory that create target service object
 * 
 * 
 */
@Component
public class DefaultTargetServiceFactory implements TargetServiceFactory {

	private final static String module = DefaultTargetServiceFactory.class.getName();

	private final TargetMetaRequestsHolder targetMetaRequestsHolder;

	private final ApplicationContext applicationContext;

	public DefaultTargetServiceFactory(ApplicationContext applicationContext, TargetMetaRequestsHolder targetMetaRequestsHolder) {
		this.targetMetaRequestsHolder = targetMetaRequestsHolder;
		this.applicationContext = applicationContext;
	}

	public Object create() {
		Object o = null;
		TargetMetaDef targetMetaDef = null;
		try {
			targetMetaDef = targetMetaRequestsHolder.getTargetMetaRequest().getTargetMetaDef();
			TargetObjectFactory targetObjectFactory = targetMetaDef.getTargetObjectFactory();
			o = targetObjectFactory.create(applicationContext);
		} catch (Exception ex) {
		} finally {
		}
		return o;
	}

	public Object destroy() {
		targetMetaRequestsHolder.setTargetMetaRequest(null);
		return null;
	}

}
