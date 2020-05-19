package com.garry.springlifecycle.container.beanpost.aop.interceptor.util;

import com.garry.springlifecycle.aop.joinpoint.AdvisorChainFactory;
import com.garry.springlifecycle.aop.reflection.MethodConstructor;
import com.garry.springlifecycle.aop.reflection.ProxyMethodInvocation;
import com.garry.springlifecycle.businessproxy.target.TargetServiceFactory;
import com.garry.springlifecycle.container.access.TargetMetaRequest;
import com.garry.springlifecycle.container.access.TargetMetaRequestsHolder;
import com.garry.springlifecycle.utils.Debug;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

@Component
public class AopClient {


    private final static String module = AopClient.class.getName();

    private final AdvisorChainFactory advisorChainFactory;

    private final TargetServiceFactory targetServiceFactory;

    private final MethodConstructor methodConstructor;

    private final TargetMetaRequestsHolder targetMetaRequestsHolder;

    public AopClient(ApplicationContext applicationContext, AdvisorChainFactory advisorChainFactory, TargetServiceFactory targetServiceFactory,
                     TargetMetaRequestsHolder targetMetaRequestsHolder) {
        this.advisorChainFactory = advisorChainFactory;
        this.targetServiceFactory = targetServiceFactory;
        this.methodConstructor = new MethodConstructor(applicationContext, targetMetaRequestsHolder);
        this.targetMetaRequestsHolder = targetMetaRequestsHolder;
    }

    /**
     *
     * directly called by client with TargetMetaDef such as InvokerServlet:
     * Object object = (Service)service.execute(targetMetaDef, methodMetaArgs,
     * requestW); different target service has its Interceptor instance and
     * MethodInvocation instance
     *
     */
    public Object invoke() throws Throwable {
        TargetMetaRequest targetMetaRequest = targetMetaRequestsHolder.getTargetMetaRequest();
        Debug.logVerbose("[JdonFramework] enter AOP invoker for:" + targetMetaRequest.getTargetMetaDef().getClassName() + " method:"
                + targetMetaRequest.getMethodMetaArgs().getMethodName(), module);

        Object result = null;
        org.aopalliance.intercept.MethodInvocation methodInvocation = null;
        try {
            List<MethodInterceptor> chain = advisorChainFactory.create(targetMetaRequest.getTargetMetaDef());
            Object[] args = targetMetaRequest.getMethodMetaArgs().getArgs();
            Method method = methodConstructor.createMethod(targetServiceFactory);
            methodInvocation = new ProxyMethodInvocation(chain, targetMetaRequestsHolder, targetServiceFactory, method, args);
            Debug.logVerbose("[JdonFramework] MethodInvocation will proceed ... ", module);
            result = methodInvocation.proceed();
        } catch (Exception ex) {
            Debug.logError(ex, module);
            throw new Exception(ex);
        } catch (Throwable ex) {
            throw new Throwable(ex);
        } finally {
            targetMetaRequestsHolder.clear();
        }
        return result;
    }

    /**
     * dynamic proxy active this method when client call userService.xxxmethod
     *
     * @param targetMetaRequest
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    public Object invoke(TargetMetaRequest targetMetaRequest, Method method, Object[] args) throws Throwable {
        targetMetaRequestsHolder.setTargetMetaRequest(targetMetaRequest);
        Debug.logVerbose(
                "[JdonFramework] enter AOP invoker2 for:" + targetMetaRequest.getTargetMetaDef().getClassName() + " method:" + method.getName(),
                module);

        Object result = null;
        MethodInvocation methodInvocation = null;
        try {
            List<MethodInterceptor> chain = advisorChainFactory.create(targetMetaRequest.getTargetMetaDef());
            methodInvocation = new ProxyMethodInvocation(chain, targetMetaRequestsHolder, targetServiceFactory, method, args);
            Debug.logVerbose("[JdonFramework] MethodInvocation will proceed ... ", module);
            result = methodInvocation.proceed();
        } catch (Exception ex) {
            Debug.logError(ex, module);
            throw new Exception(ex);
        } catch (Throwable ex) {
            throw new Throwable(ex);
        } finally {
            targetMetaRequestsHolder.clear();
        }
        return result;

    }

}
