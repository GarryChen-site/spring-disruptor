package com.garry.springlifecycle.container.beanpost.aop.interceptor.util;

import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class MethodInvocation implements Proceed {

    private List<AbstractAdvisor> advisors;

    private Object sourceObject;

    private Method sourceMethod;

    private Object[] sourceParameters;

    private MethodProxy sourceMethodProxy;

    private int advisorIndex = -1;

    public MethodInvocation(Object o, Method method, Object[] objects, MethodProxy methodProxy,
                            List<AbstractAdvisor> advisors){
        this.sourceObject = o;
        this.sourceMethod = method;
        this.sourceParameters = objects;
        this.sourceMethodProxy = methodProxy;
        this.advisors = advisors;
    }


    @Override
    public Object proceed(MethodInvocation methodInvocation) throws Throwable {
        if (advisorIndex == advisors.size() - 1){
            return sourceMethodProxy.invokeSuper(sourceObject, sourceParameters);
        }else {
            advisorIndex += 1;
            return advisors.get(advisorIndex).proceed(this);
        }
    }

    public static class AroundAdvisor extends AbstractAdvisor {

        public AroundAdvisor(Object aspectObject, Method aspectMethod) {
            super(aspectObject, aspectMethod);
            order = AbstractAdvisor.AROUND_ORDER;
        }

        public Object proceed(MethodInvocation methodInvocation) throws Throwable {
            Object[] param = { methodInvocation, methodInvocation.sourceParameters };
            return aspectMethod.invoke(aspectObject, param);
        }
    }

    public static class BeforeAdvisor extends AbstractAdvisor {

        public BeforeAdvisor(Object aspectObject, Method aspectMethod) {
            super(aspectObject, aspectMethod);
            order = AbstractAdvisor.BEFORE_ORDER;
        }

        public Object proceed(MethodInvocation methodInvocation) throws Throwable {
            Object[] param = { methodInvocation.sourceParameters };
            aspectMethod.invoke(aspectObject, param);
            return methodInvocation.proceed(methodInvocation);
        }
    }

    public static class AfterAdvisor extends AbstractAdvisor {

        public AfterAdvisor(Object aspectObject, Method aspectMethod) {
            super(aspectObject, aspectMethod);
            order = AbstractAdvisor.AFTER_ORDER;
        }

        public Object proceed(MethodInvocation methodInvocation) throws Throwable {
            methodInvocation.proceed(methodInvocation);
            Object[] param = { methodInvocation.sourceParameters };
            return aspectMethod.invoke(aspectObject, param);
        }
    }
}
