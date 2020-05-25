package com.garry.springlifecycle.container.interceptor;


import com.garry.springlifecycle.utils.Debug;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class MethodInvocationImp implements MethodInvocation {
	private final static String module = MethodInvocationImp.class.getName();

	private final Method method;

	private final Object[] args;

	protected final List interceptors;

	protected Object target;

	protected int currentInterceptorInt = -1;

	protected BeforeAfterMethodTarget beforeAfterMethodTarget;

	protected MethodProxy methodProxy;

	public MethodInvocationImp(Object target, BeforeAfterMethodTarget beforeAfterMethodTarget, List interceptors, Method method, Object[] args,
                               MethodProxy methodProxy) {
		this.interceptors = interceptors;
		this.method = method;
		this.args = args;
		this.beforeAfterMethodTarget = beforeAfterMethodTarget;
		this.methodProxy = methodProxy;
		this.target = target;
	}

	/**
	 * Invokes next interceptor/proxy target. now there is no mixin
	 */
	public Object proceed() throws Throwable, Exception {
		if (currentInterceptorInt == interceptors.size() - 1) {
			Debug.logVerbose("[JdonFramework] finish call all inteceptors", module);
			return beforeAfterMethodTarget.invoke(method, args, methodProxy);
		}

		Object interceptor = interceptors.get(++currentInterceptorInt);
		if (interceptor != null) {
			MethodInterceptor methodInterceptor = (MethodInterceptor) interceptor;
			return methodInterceptor.invoke(this);
		} else {
			Debug.logVerbose("[JdonFramework] null finish call all inteceptors", module);
			return beforeAfterMethodTarget.invoke(method, args, methodProxy);
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
