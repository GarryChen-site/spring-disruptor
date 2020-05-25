package com.garry.springlifecycle.businessproxy.dyncproxy.cglib;



import com.garry.springlifecycle.container.access.TargetMetaRequest;
import com.garry.springlifecycle.container.beanpost.aop.interceptor.util.AopClient;
import com.garry.springlifecycle.utils.Debug;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


import java.lang.reflect.Method;

/**
 * CGLIB Dynamic Proxy Weaving mode Weaving implemention is dynamic proxy Every
 * target service object has its DynamicProxyWeaving object
 * 
 * problem:
 * 
 * memory leak is more entwined in cglib. The leak can be removed by having
 * cglib loaded by event different classloader than the one for the web app. The
 * solution is to move the cglib jar and the asm jar to the shared lib directory
 * of Tomcat. http://users.cis.fiu.edu/~downeyt/webdev/memory_leaks.shtml
 * 
 * @author banq
 */

public class CGLIBDynamicProxyWeaving implements MethodInterceptor, java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4915858712286729975L;

	private final static String module = CGLIBDynamicProxyWeaving.class.getName();

	private AopClient aopClient;

	private TargetMetaRequest targetMetaRequest;

	public CGLIBDynamicProxyWeaving(TargetMetaRequest targetMetaRequest, AopClient aopClient) {
		this.aopClient = aopClient;
		this.targetMetaRequest = targetMetaRequest;
	}

	public Object intercept(Object object, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		Debug.logVerbose("<################################>Action: JdonFramework core entrance intercept", module);
		Debug.logVerbose("[JdonFramework]<################>execute method=" + method.getDeclaringClass().getName() + "." + method.getName(), module);
		if (method.getName().equals("finalize")) {
			return null;
		}
		Object result = null;
		try {
			result = aopClient.invoke(targetMetaRequest, method, objects);
			Debug.logVerbose(
					"[JdonFramework]<################>finish executing method=" + method.getDeclaringClass().getName() + "." + method.getName()
							+ " successfully!", module);
			Debug.logVerbose("<################################><end:", module);
		} catch (Exception ex) {
			Debug.logError(ex, module);
		} catch (Throwable ex) {
			throw new Throwable(ex);
		}

		return result;
	}

	/**
	 * 方法调用 需要拦截方法在这里实现。目前实现arround intercept
	 * 
	 * @param p_proxy
	 *            Object
	 * @param m
	 *            Method
	 * @param args
	 *            Object[]
	 * @throws Throwable
	 * @return Object
	 */
	public Object invoke(Object p_proxy, Method m, Object[] args) throws Throwable {
		Debug.logVerbose("<################################>Action: JdonFramework core entrance invoke", module);
		Debug.logVerbose("[JdonFramework]<################>execute method=" + m.getDeclaringClass().getName() + "." + m.getName(), module);
		Object result = null;
		try {
			result = aopClient.invoke(targetMetaRequest, m, args);
			Debug.logVerbose("[JdonFramework]<################>finish executing method=" + m.getDeclaringClass().getName() + "." + m.getName()
					+ " successfully!", module);
			Debug.logVerbose("<################################><end:", module);
		} catch (Exception ex) {
			Debug.logError(ex, module);
		} catch (Throwable ex) {
			throw new Throwable(ex);
		}

		return result;

	}

}
