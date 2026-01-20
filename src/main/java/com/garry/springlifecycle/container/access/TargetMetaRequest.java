/*
 * 
 */
package com.garry.springlifecycle.container.access;



import com.garry.springlifecycle.businessproxy.TargetMetaDef;
import com.garry.springlifecycle.businessproxy.meta.MethodMetaArgs;

import java.io.Serializable;

/**
 * Every container's user has one  UserTargetMetaDef object
 * this object is event DTO when this user enter businerss proxy.
 * it reduce the method's parameters amount;
 * it's scope is event instance for per request of one user ;
 * TargetMetaDef object's scope is for event service for all users;
 * 
 *
 */
public class TargetMetaRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9137088748339607292L;

	private final TargetMetaDef targetMetaDef;
    
//    private final ComponentVisitor componentVisitor;
    
    private volatile MethodMetaArgs methodMetaArgs;

    private volatile String visitableName;
    
//    private volatile SessionContext sessionContext;

    /**
     *  ComponentVisitor is HttpSessionComponentVisitor created in {@link UserTargetMetaDefFactory#createTargetMetaRequest(TargetMetaDef, com.jdon.controller.context.ContextHolder)}
     * 
     * @param targetMetaDef
     */
    public TargetMetaRequest(TargetMetaDef targetMetaDef) {
        super();
        this.targetMetaDef = targetMetaDef;
//        this.componentVisitor = componentVisitor;
    }

    /**
     * @return Returns the targetMetaDef.
     */
    public TargetMetaDef getTargetMetaDef() {
        return targetMetaDef;
    }  




    /*
     * 1.SessionContextInterceptor:targetMetaRequest.setVisitableName(ComponentKeys.SESSIONCONTEXT_FACTORY);
     * set vistable value be SessionContextFactoryVisitable
     * 
     * 2.StatefulInterceptor: setVisitableName(ComponentKeys.TARGETSERVICE_FACTORY)
     * set vistable value be com.jdon.bussinessproxy.target.TargetServiceFactoryVisitable
     * 
     * 3.MethodInvokerUtil:createTargetObject setVisitableName(ComponentKeys.TARGETSERVICE_FACTORY);
     * set vistable value be com.jdon.bussinessproxy.target.TargetServiceFactoryVisitable
     * 
     * 4. ServiceAccessorImp: getService setVisitableName(ComponentKeys.PROXYINSTANCE_FACTORY);
     * set vistable value be  com.jdon.bussinessproxy.dyncproxy.ProxyInstanceFactoryVisitable
     * 
     * 5. WebServiceDecorator:
     * set vistable value be SessionContextFactoryVisitable
     * 
     * used in ComponentOriginalVisitor#visit
     */


    public MethodMetaArgs getMethodMetaArgs() {
        return methodMetaArgs;
    }

    public void setMethodMetaArgs(MethodMetaArgs methodMetaArgs) {
        this.methodMetaArgs = methodMetaArgs;
    }
}
