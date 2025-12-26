package com.garry.springlifecycle.domain.advsior;



import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.domain.proxy.ModelCGLIBMethodInterceptorImp;
import com.garry.springlifecycle.domain.proxy.ModelProxyFactory;
import com.garry.springlifecycle.utils.Debug;
import net.sf.cglib.proxy.MethodInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * when event model with event @Introduce annotation is injected to another model, the
 * injected model will be enhanced using cglib.
 *
 * for example:
 *
 * @Introduce("message") public class DomainEvent{
 *
 *
 *                       }
 *
 *                       as this,the DomainEvent will be enhanced with
 *                       MessageInterceptor
 *
 * @author xmuzyu banq
 *
 */
@Component
public class ModelAdvisor implements ApplicationContextAware {
	private final static String module = ModelAdvisor.class.getName();

	private ApplicationContext applicationContext;
	private final ModelProxyFactory modelProxyFactory;

	private Map<Class, MethodInterceptor> modeInterceptors;

	public ModelAdvisor(ApplicationContext applicationContext, ModelProxyFactory modelProxyFactory) {
		super();
		this.modelProxyFactory = modelProxyFactory;
		this.modeInterceptors = new ConcurrentHashMap();
	}

	public Object createProxy(Object model) {
		if (!isAcceptable(model.getClass()))
			return model;
		MethodInterceptor methodInterceptor = modeInterceptors.get(model.getClass());
		if (methodInterceptor == null) {
			List methodInterceptors = getAdviceName(model);
			if (methodInterceptors == null || methodInterceptors.size() == 0)
				return model;
			methodInterceptor = new ModelCGLIBMethodInterceptorImp(methodInterceptors);
			modeInterceptors.put(model.getClass(), methodInterceptor);
		}
		return modelProxyFactory.create(model.getClass(), methodInterceptor);
	}

	public List getAdviceName(Object model) {
		List methodInterceptors = new ArrayList();
		try {
			Introduce introduce = model.getClass().getAnnotation(Introduce.class);
			if (introduce == null)
				return methodInterceptors;
			String[] adviceNames = introduce.values();
			if (adviceNames != null) {
				for (int i = 0; i < adviceNames.length; i++) {
					Object interceptorCustomzied = applicationContext.getBean(adviceNames[i]);
					methodInterceptors.add(interceptorCustomzied);
				}
			}
		} catch (Exception e) {
			Debug.logError(" getAdviceNameAnnotation:" + e, module);
		}
		return methodInterceptors;
	}

	public boolean isAcceptable(Class classz) {
		if (classz.isAnnotationPresent(Introduce.class)) {
			return true;
		} else
			return false;

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	//	@Override
//	public void start() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void stop() {
//		this.modeInterceptors.clear();
//
//	}

}
