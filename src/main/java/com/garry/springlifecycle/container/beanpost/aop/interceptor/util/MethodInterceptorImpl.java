package com.garry.springlifecycle.container.beanpost.aop.interceptor.util;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class MethodInterceptorImpl implements MethodInterceptor {

    private Map<Method, List<AbstractAdvisor>> advisorMap;

    public MethodInterceptorImpl(Map<Method, List<AbstractAdvisor>> advisorMap) {
        this.advisorMap = advisorMap;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        List<AbstractAdvisor> advisorList = advisorMap.get(method);
        if (advisorList == null) {
            return methodProxy.invokeSuper(o, objects);
        } else {
            MethodInvocation methodInvocation = new MethodInvocation(o, method, objects, methodProxy, advisorList);
            return methodInvocation.proceed(methodInvocation);
        }
    }
}
