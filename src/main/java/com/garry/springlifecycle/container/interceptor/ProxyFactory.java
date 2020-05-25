package com.garry.springlifecycle.container.interceptor;


import com.garry.springlifecycle.utils.Debug;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyFactory {
	private final static String module = ProxyFactory.class.getName();

	public Object createProxy(MethodInterceptor methodInterceptor, Object target, Class[] interfaces) {
		Debug.logVerbose("[JdonFramework]enter Proxy.newProxyInstance ", module);
		Object dynamicProxy = null;
		try {
			Enhancer enhancer = new Enhancer();
			enhancer.setCallback(methodInterceptor);
			enhancer.setInterfaces(interfaces);
			dynamicProxy = enhancer.create();
		} catch (Exception ex) {
			Debug.logError("[JdonFramework] Proxy.newProxyInstance error:" + ex, module);
		} catch (Throwable ex) {
			Debug.logError("[JdonFramework] Proxy.newProxyInstance error:" + ex, module);
		}
		return dynamicProxy;
	}

}
