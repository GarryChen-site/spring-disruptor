package com.garry.springlifecycle.container.finder;

/**
 * the important keys in container.xml
 * 
 * @author <event href="mailto:banqiao@jdon.com">banq</event>
 * 
 */
public interface ComponentKeys {

	String WEBSERVICE = "webService";

	String WEBSERVICE_FACTORY = "webServiceFactory";

	String VISITOR_FACTORY = "visitorFactory";

	String SESSIONCONTEXT_SETUP = "sessionContextSetup";

	String INTERCEPTOR_CHAIN = "interceptorsChain";

	String INSTANCE_CACHE = "instanceCache";

	String INTERCEPTOR_CHAIN_FACTORY = "advisorChainFactory";

	String DOMAIN_PROXY_FACTORY = "domainProxyFactory";

	/**
	 * the SERVICE_METALOADER_NAME value must be the value of container.xml
	 * 
	 */
	String SERVICE_METALOADER_NAME = "targetMetaDefLoader";

	/**
	 * the SERVICE_METAHOADER_NAME value must be the value of container.xml
	 * 
	 */
	String SERVICE_METAHOLDER_NAME = "targetMetaDefHolder";

	/**
	 * the AOP_CLIENT value must be the value of container.xml
	 * 
	 */
	String AOP_CLIENT = "aopClient";

	String MODEL_MANAGER = "modelManager";

	String PROXYINSTANCE_FACTORY = "proxyInstanceFactoryVisitable";

	String TARGETSERVICE_FACTORY = "targetServiceFactoryVisitable";

	String SESSIONCONTEXT_FACTORY = "sessionContextFactoryVisitable";

}
