package com.garry.springlifecycle.container.beanpost.aop.interceptor.util;

import com.garry.springlifecycle.annotation.Interceptor;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public abstract class AbstractAdvisor implements Proceed {

    public static final int AFTER_ORDER = 0;

    public static final int AROUND_ORDER = 1;

    public static final int BEFORE_ORDER = 2;

    private Pattern pointCutPattern;

    protected String pointCut;

    protected Object aspectObject;

    protected Method aspectMethod;

    protected int order;

    public AbstractAdvisor(Object aspectObject, Method aspectMethod) {
        pointCut = aspectObject.getClass().getAnnotation(Interceptor.class).pointcut();
        pointCutPattern = Pattern.compile(pointCut);
        this.aspectObject = aspectObject;
        this.aspectMethod = aspectMethod;
    }

    public boolean isMatch(Class<?> matchClass, Method method){
        return pointCutPattern.matcher(matchClass.getName() + "." + method.getName()).find();
    }

    public int getOrder() {
        return order;
    }
}
