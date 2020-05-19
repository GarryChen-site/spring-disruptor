package com.garry.springlifecycle.aop.interceptor;



import com.garry.springlifecycle.annotation.Interceptor;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Interceptors chain
 * 
 * all interceptors will add in this collection
 * 
 * @author <event href="mailto:banqiao@jdon.com">banq</event>
 * 
 */
@Interceptor
public class InterceptorsChain  {
	private final static String module = InterceptorsChain.class.getName();
	public final static String NAME = "InterceptorsChain";

	/**
	 * the key is target name
	 */
	private final Map<String, List<Advisor>> chain;

	public InterceptorsChain() {
		chain = new ConcurrentHashMap<String, List<Advisor>>();
	}

	public void start() {
		Debug.logVerbose("[JdonFramework]InterceptorsChain start..", module);
	}

	public void stop() {
		chain.clear();
	}

	public void addInterceptor(String pointcut, String InterceptorName) {
		if (pointcut == null) {
			System.err.print("pointcut is null in InterceptorsChain");
			return;
		}
		List<Advisor> interceptors = (List) chain.get(pointcut);
		if (interceptors == null) {
			interceptors = new ArrayList();
			chain.put(pointcut, interceptors);
		}
		Advisor advisor = new Advisor(InterceptorName, pointcut);
		interceptors.add(advisor);
	}

	public boolean findInterceptorFromChainByName(String pointcut, String InterceptorName) {
		boolean ok = false;
		List<Advisor> interceptors = (List) chain.get(pointcut);
		for (Advisor advisor : interceptors) {
			if (advisor.getAdviceName().equals(InterceptorName)) {
				ok = true;
				break;
			}
		}
		return ok;
	}

	/**
	 * @return Returns the interceptors.
	 */
	public List<Advisor> getAdvisors(String pointcut) {
		return chain.get(pointcut);
	}

	public int size() {
		return chain.size();
	}
}
