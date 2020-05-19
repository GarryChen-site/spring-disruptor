package com.garry.springlifecycle.domain.proxy;


import com.garry.springlifecycle.utils.Debug;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ModelMethodInvocation implements MethodInvocation {
	private final static String module = ModelMethodInvocation.class.getName();

	protected final List interceptors;

	protected Object target;

	protected int currentInterceptorInt = -1;

	protected MethodProxy methodProxy;

	private final Object[] args;

	private final Method method;

	public ModelMethodInvocation(Object target, List interceptors, Method method, Object[] args, MethodProxy methodProxy) {
		this.interceptors = interceptors;
		this.method = method;
		this.args = args;
		this.methodProxy = methodProxy;
		this.target = target;
	}

	public Object proceed() throws Throwable {
		if (currentInterceptorInt == interceptors.size() - 1) {
			Debug.logVerbose("[JdonFramework] finish call all inteceptors", module);
			return methodProxy.invokeSuper(target, args);
		}

		Object interceptor = interceptors.get(++currentInterceptorInt);
		if (interceptor != null) {
			MethodInterceptor methodInterceptor = (MethodInterceptor) interceptor;
			return methodInterceptor.invoke(this);
		} else {
			Debug.logVerbose("[JdonFramework] null finish call all inteceptors", module);
			return methodProxy.invokeSuper(target, args);
		}

	}

	public Object[] getArguments() {
		return this.args;
	}

	public Object getThis() {
		return target;
	}

	public AccessibleObject getStaticPart() {
		return null;
	}

	public Method getMethod() {
		return method;
	}

}
