package com.garry.springlifecycle.container.beanpost.aop.interceptor.util;


public interface Proceed {

    Object proceed(MethodInvocation methodInvocation) throws Throwable;
}
