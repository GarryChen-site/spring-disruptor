package com.garry.springlifecycle.domain.proxy;


import com.garry.springlifecycle.utils.Debug;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

public class ModelCGLIBMethodInterceptorImp implements MethodInterceptor {
	private final static String module = ModelCGLIBMethodInterceptorImp.class.getName();
	private List<org.aopalliance.intercept.MethodInterceptor> methodInterceptors;

	public ModelCGLIBMethodInterceptorImp(List<org.aopalliance.intercept.MethodInterceptor> methodInterceptors) {
		super();
		this.methodInterceptors = methodInterceptors;
	}

	public Object intercept(Object object, Method invokedmethod, Object[] args, MethodProxy methodProxy) throws Throwable {
		if (invokedmethod.getName().equals("finalize"))
			return null;

		Object result = null;
		try {
			Debug.logVerbose("[JdonFramework]<----> executing MethodInterceptor for method=" + invokedmethod.getDeclaringClass().getName() + "."
					+ invokedmethod.getName() + " successfully!", module);

			MethodInvocation methodInvocation = new ModelMethodInvocation(object, methodInterceptors, invokedmethod, args, methodProxy);
			result = methodInvocation.proceed();

			Debug.logVerbose("<-----><end:", module);
		} catch (Exception ex) {
			Debug.logError(ex, module);
		} catch (Throwable ex) {
			throw new Throwable(ex);
		}

		return result;
	}

}