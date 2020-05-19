package com.garry.springlifecycle.aop.joinpoint;

/**
 * Static Pointcut
 * 
 * @author <event href="mailto:banqiao@jdon.com">banq</event>
 * 
 */
public interface Pointcut {

	String TARGET_PROPS_SERVICES = "services";

	String EJB_TARGET_PROPS_SERVICES = "ejbServices";

	String POJO_TARGET_PROPS_SERVICES = "pojoServices";

	String DOMAIN = "domain"; // for component

}
