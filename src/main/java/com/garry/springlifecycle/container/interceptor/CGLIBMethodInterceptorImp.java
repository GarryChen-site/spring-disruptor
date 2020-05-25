package com.garry.springlifecycle.container.interceptor;


import com.garry.springlifecycle.utils.Debug;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.List;

public class CGLIBMethodInterceptorImp implements MethodInterceptor {
	private final static String module = CGLIBMethodInterceptorImp.class.getName();
	private List<org.aopalliance.intercept.MethodInterceptor> methodInterceptors;
	private BeforeAfterMethodTarget beforeAfterMethodTarget;
	private Object target;

	public CGLIBMethodInterceptorImp(Object target, Object interceptor, IntroduceInfo iinfo,
                                     List<org.aopalliance.intercept.MethodInterceptor> methodInterceptors) {
		super();
		this.methodInterceptors = methodInterceptors;
		this.beforeAfterMethodTarget = new BeforeAfterMethodTarget(target, interceptor, iinfo);
		this.target = target;
	}

	public Object intercept(Object object, Method invokedmethod, Object[] objects, MethodProxy methodProxy) throws Throwable {
		if (invokedmethod.getName().equals("finalize")) {
			return null;
		}

		Object result = null;
		try {
			Debug.logVerbose("<-----><begin:", module);
			Debug.logVerbose("[JdonFramework]<----> executing MethodInterceptor for method=" + invokedmethod.getDeclaringClass().getName() + "."
					+ invokedmethod.getName() + " successfully!", module);

			MethodInvocation methodInvocation = new MethodInvocationImp(target, beforeAfterMethodTarget, methodInterceptors, invokedmethod, objects,
					methodProxy);
			result = methodInvocation.proceed();

			Debug.logVerbose("<-----><end:", module);
		} catch (Exception ex) {
			Debug.logError(ex, module);
			throw new Exception(ex);
		} catch (Throwable ex) {
			throw new Throwable(ex);
		}

		return result;
	}

	public void clear() {
		if (this.methodInterceptors != null) {
			this.methodInterceptors.clear();
			this.methodInterceptors = null;
		}
		if (beforeAfterMethodTarget != null) {
			this.beforeAfterMethodTarget.clear();
			this.beforeAfterMethodTarget = null;
		}
		if (target != null) {
//			if (target instanceof Startable) {
//				Startable st = (Startable) target;
//				try {
//					st.stop();
//				} catch (Exception e) {
//				}
//			}
			this.target = null;
		}
	}

//	@Override
//	public void start() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void stop() {
//		clear();
//
//	}

}
