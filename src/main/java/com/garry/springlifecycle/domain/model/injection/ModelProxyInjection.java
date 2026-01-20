package com.garry.springlifecycle.domain.model.injection;



import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.JDService;
import com.garry.springlifecycle.annotation.model.Inject;
import com.garry.springlifecycle.domain.advsior.ModelAdvisor;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.ObjectCreator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * This class is responsible for inject the fileds marked with @Inject into the
 * models,if the injected fields are also marked with @Introduce,the interceptor
 * will be involved again.
 * 
 * for example:
 * 
 * 
 * public class MyModle{
 * 
 * @Inject MyDomainEvent myDomainEvent;
 * 
 * @Inject MyDomainService MyDomainService; }
 * 
 * @Introduce("message") class MyDomainEvent{
 * 
 *                       }
 * 
 * 
 */
@Component
public class ModelProxyInjection implements ApplicationContextAware {
	private final static String module = ModelProxyInjection.class.getName();
	private ModelAdvisor modelAdvisor;
//	private ContainerCallback containerCallback;

	private ApplicationContext applicationContext;

	public ModelProxyInjection(ModelAdvisor modelAdvisor) {
		super();
		this.modelAdvisor = modelAdvisor;
	}

	public void injectProperties(Object targetModel) {
		Class fClass = null;
		try {
			Field[] fields = ClassUtil.getAllDecaredFields(targetModel.getClass());
			if (fields == null)
				return;
			for (Field field : fields) {
				if (field.isAnnotationPresent(Inject.class)) {
					fClass = field.getType();
					Object fieldObject = getInjectObject(targetModel, fClass);
					if (field.getType().isAssignableFrom(fieldObject.getClass())) {
						try {
							field.setAccessible(true);
							field.set(targetModel, fieldObject);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		} catch (Exception e) {
		}
	}

	protected Object getInjectObject(Object targetModel, Class fClass) {
		Object o = createTargetComponent(targetModel, fClass);
		if (o == null)
			o = createTargetObject(targetModel, fClass);
		return o;

	}

	protected Object createTargetObject(Object targetModel, Class fClass) {
		Object o = null;
		try {
			o = ObjectCreator.createObject(fClass);
			o = modelAdvisor.createProxy(o);
		} catch (Exception e) {
		}
		return o;

	}

	protected Object createTargetComponent(Object targetModel, Class fClass) {
		Object o = null;
		try {
			List<Object> objects = (List<Object>) applicationContext.getBean(fClass);
			// List should be have only one.
			for (Object instance : objects) {
				o = instance;
				break;
			}
			if (o != null)
				o = modelAdvisor.createProxy(o);
		} catch (Exception e) {
		}
		return o;

	}

	protected boolean isComponent(Object instance) {
		if (instance.getClass().isAnnotationPresent(JDComponent.class))
			return true;
		if (instance.getClass().isAnnotationPresent(JDService.class))
			return true;
		return false;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
