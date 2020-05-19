package com.garry.springlifecycle.domain.advsior;



import com.garry.springlifecycle.aop.joinpoint.AdvisorChainFactory;
import com.garry.springlifecycle.container.finder.ComponentKeys;
import com.garry.springlifecycle.container.interceptor.CGLIBMethodInterceptorImp;
import com.garry.springlifecycle.container.interceptor.IntroduceInfo;
import com.garry.springlifecycle.container.interceptor.IntroduceInfoHolder;
import com.garry.springlifecycle.container.interceptor.ProxyFactory;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo event model 是个web 可以不管
 * in container, when event component inject into another component or event model, the
 * advisor will create event proxy for injected class with its interfaces.
 * 
 * the condition for creating proxy:
 * 
 * 1. the inject component class has @Introduce
 * 
 * 2. the inject component class has its interfaces
 * 
 * 3. the inject component class's componet name is in the another class's
 * target parameter with @Interceptor(name="", target="xx,xx")
 * 
 * @author banq
 * 
 */
public class ComponentAdvsior {
	private final static String module = ComponentAdvsior.class.getName();
	public final static String NAME = "componentAdvsior";
	private ApplicationContext applicationContext;
	private final Map<String, Class[]> interfaceMaps = new HashMap();

	public ComponentAdvsior(ApplicationContext applicationContext) {
		this.applicationContext  = applicationContext;
	}

	public Object createProxy(Object o) {
		if (o == null)
			return o;
		IntroduceInfoHolder introduceInfoHolder = (IntroduceInfoHolder) applicationContext.getBean(IntroduceInfoHolder.NAME);
		if (introduceInfoHolder == null)
			return o;
		if (!introduceInfoHolder.containsThisClass(o.getClass()))
			return o;

		try {
			Class[] interfaces = getInterfaces(o.getClass());
			if (interfaces == null) {
				Debug.logError(" Your class:" + o.getClass()
						+ " has event annotation @Introduce, the class need implement event interface when it be register in container", module);
				return o;
			}

			IntroduceInfo iinfo = introduceInfoHolder.getIntroduceInfoByIntroducer(o.getClass());
			Object interceptor = null;
			if (iinfo != null)
				interceptor = applicationContext.getBean(iinfo.getAdviceName());

			AdvisorChainFactory acf = (AdvisorChainFactory) applicationContext.getBean(ComponentKeys.INTERCEPTOR_CHAIN_FACTORY);
			String targetName = introduceInfoHolder.getTargetName(o.getClass());
			if (targetName == null)
				return o;
			List<org.aopalliance.intercept.MethodInterceptor> methodInterceptors = acf.createTargetAdvice(targetName);

			MethodInterceptor mi = new CGLIBMethodInterceptorImp(o, interceptor, iinfo, methodInterceptors);
			ProxyFactory proxyFactory = (ProxyFactory) applicationContext.getBean(ComponentKeys.DOMAIN_PROXY_FACTORY);
			o = proxyFactory.createProxy(mi, o, interfaces);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return o;
	}

	public Class[] getInterfaces(Class pojoClass) {
		Class[] interfaces = interfaceMaps.get(pojoClass.getName());
		if (interfaces == null) {
			interfaces = ClassUtil.getParentAllInterfaces(pojoClass);
			interfaceMaps.put(pojoClass.getName(), interfaces);
		}

		return interfaces;

	}

}
