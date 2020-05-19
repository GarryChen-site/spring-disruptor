package com.garry.springlifecycle.aop.reflection;



import com.garry.springlifecycle.businessproxy.TargetMetaDef;
import com.garry.springlifecycle.businessproxy.meta.MethodMetaArgs;
import com.garry.springlifecycle.businessproxy.target.TargetServiceFactory;
import com.garry.springlifecycle.container.access.TargetMetaRequest;
import com.garry.springlifecycle.container.access.TargetMetaRequestsHolder;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class MethodConstructor {

    private final static String module = MethodConstructor.class.getName();

    private final MethodInvokerUtil methodInvokerUtil ;
    
    private final TargetMetaRequestsHolder targetMetaRequestsHolder;

    private final ApplicationContext applicationContext;

    public MethodConstructor(ApplicationContext applicationContext,
                             TargetMetaRequestsHolder targetMetaRequestsHolder) {
    	this.applicationContext = applicationContext;
		this.targetMetaRequestsHolder = targetMetaRequestsHolder;
		this.methodInvokerUtil = new MethodInvokerUtil(targetMetaRequestsHolder);
	}

	/**
     * @return Returns the methodInvokerUtil.
     */
    public MethodInvokerUtil getMethodInvokerUtil() {
        return methodInvokerUtil;
    }
    
    /**
     * ejb's method creating must at first get service's EJB Object;
     * pojo's method creating can only need service's class. 
     *  
     * @param targetServiceFactory
     * @param targetMetaRequest
     * @param methodMetaArgs
     * @return
     */
    public Method createMethod(TargetServiceFactory targetServiceFactory) {
        Method method = null;
        Debug.logVerbose("[JdonFramework] enter create the Method " , module);
        try {
        	TargetMetaRequest targetMetaRequest = targetMetaRequestsHolder.getTargetMetaRequest();
            if (targetMetaRequest.getTargetMetaDef().isEJB()) { 
                Object obj= methodInvokerUtil.createTargetObject(targetServiceFactory);
                method = createObjectMethod(obj, targetMetaRequest.getMethodMetaArgs());
            }else{
                method = createPojoMethod();
            }
        } catch (Exception ex) {
            Debug.logError("[JdonFramework] createMethod error: " + ex, module);
        }
        
        return method;

    }
    
    
    /**
     * create event method object by its meta definition
     * @param targetMetaDef
     * @param cw
     * @param methodMetaArgs
     */
    public Method createPojoMethod() {
        Method method = null;
        TargetMetaRequest targetMetaRequest = targetMetaRequestsHolder.getTargetMetaRequest();
        TargetMetaDef targetMetaDef = targetMetaRequest.getTargetMetaDef();
        MethodMetaArgs methodMetaArgs = targetMetaRequest.getMethodMetaArgs();        
        Debug.logVerbose("[JdonFramework] createPOJO Method :" + methodMetaArgs.getMethodName() + " for target service: " + targetMetaDef.getName(), module);        
        try {       
            Class thisCLass = applicationContext.getType(targetMetaDef.getName());
            if (thisCLass == null) return null;
            method = thisCLass.getMethod(methodMetaArgs.getMethodName(),
                    methodMetaArgs.getParamTypes());
        } catch (NoSuchMethodException ne) {
            Debug.logError("[JdonFramework] method name:"
                    + methodMetaArgs.getMethodName() + " or method parameters type don't match with your service's method", module);
            Object types[] = methodMetaArgs.getParamTypes();
            for(int i = 0; i<types.length; i ++){
                Debug.logError("[JdonFramework]service's method parameter type must be:" + types[i] + "; ", module);                
            }
        } catch (Exception ex) {
            Debug.logError("[JdonFramework] createPojoMethod error: " + ex, module);
        }
        
        return method;

    }
    
    /**
     * create event method object by target Object
     * @param ownerClass
     * @param methodMetaArgs
     * @return
     */
    public Method createObjectMethod(Object ownerClass, MethodMetaArgs methodMetaArgs) {
        Method m = null;        
        try {
            m = ownerClass.getClass().getMethod(methodMetaArgs.getMethodName(), 
                                                methodMetaArgs.getParamTypes());
        } catch (NoSuchMethodException nsme) {
            String errS = " NoSuchMethod:" + methodMetaArgs.getMethodName() + " in MethodMetaArgs of className:"
                    + ownerClass.getClass().getName();
            Debug.logError(errS, module);
        } catch (Exception ex) {
            Debug.logError("[JdonFramework] createMethod error:" + ex, module);
        }
        return m;
    }
    
    /**
     * create event method object
     * @param ownerClass
     * @param methodName
     * @param paramTypes
     * @return
     */
    public Method createObjectMethod(Object ownerClass, String methodName,
            Class[] paramTypes) {
        Method m = null;
        try {
            m = ownerClass.getClass().getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException nsme) {
            String errS = " NoSuchMethod:" + methodName + " in className:"
                    + ownerClass.getClass().getName() + " or method's args type error";
            Debug.logError(errS, module);
        } catch (Exception ex) {
            Debug.logError("[JdonFramework] createMethod error:" + ex, module);
        }
        return m;
    }

}
