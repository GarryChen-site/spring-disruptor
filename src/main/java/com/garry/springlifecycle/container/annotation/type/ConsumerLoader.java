package com.garry.springlifecycle.container.annotation.type;



import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.Consumer;
import com.garry.springlifecycle.annotation.JDService;
import com.garry.springlifecycle.annotation.model.OnEvent;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.consumer.ConsumerMethodHolder;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.Debug;
import com.garry.springlifecycle.utils.UtilValidate;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.*;

// todo 整合并加入Spring
public class ConsumerLoader {
	public final static String module = ConsumerLoader.class.getName();
	public final static String TOPICNAME = "CONSUMER_TOPIC";
	public final static String TOPICNAME2 = "MEHTOD_TOPIC";

	AnnotationScaner annotationScaner;

	public ConsumerLoader(AnnotationScaner annotationScaner) {
		super();
		this.annotationScaner = annotationScaner;
	}

	public void loadAnnotationConsumers(AnnotationHolder annotationHolder, AppContextWrapper context, ApplicationContext applicationContext) {
		Set<String> classes = annotationScaner.getScannedAnnotations(context).get(Consumer.class.getName());
		if (classes == null)
			return;
		Debug.logVerbose("[JdonFramework] found Annotation components size:" + classes.size(), module);
		for (String className : classes) {
			createAnnotationConsumerClass(className, annotationHolder, applicationContext);
		}
	}

	public void createAnnotationConsumerClass(String className, AnnotationHolder annotationHolder, ApplicationContext applicationContext) {
		try {
			Class cclass = Utils.createClass(className);
			if (!DomainEventHandler.class.isAssignableFrom(cclass)) {
				Debug.logError("[JdonFramework] " + cclass.getName()
						+ " that with @Consumer annotataion must also implements  com.jdon.domain.message.DomainEventHandler ", module);
				return;
			}
			Consumer consumer = (Consumer) cclass.getAnnotation(Consumer.class);
			Debug.logVerbose("[JdonFramework] load Annotation Consumer name:" + cclass.getName() + " class:" + className, module);

			String topicname = UtilValidate.isEmpty(consumer.value()) ? cclass.getName() : consumer.value();
			String topicKey = ConsumerLoader.TOPICNAME + topicname;
			Collection<String> consumers = getContainerConsumers(topicKey,applicationContext );
			String name = getConsumerName(cclass);
			consumers.add(name);
//			containerWrapper.register(name, cclass);

		} catch (Exception e) {
			Debug.logError("[JdonFramework] createAnnotationComponentClass error:" + e + className, module);

		}
	}

	/**
	 * add the class to consumers annotated with @OnEvent
	 * 
	 * @param cclass
	 * @param containerWrapper
	 */

	public void loadMethodAnnotations(Class cclass, ApplicationContext applicationContext) {
		try {
			for (Method method : ClassUtil.getAllDecaredMethods(cclass)) {
				if (method.isAnnotationPresent(OnEvent.class)) {
					addConsumerMethod(method, cclass, applicationContext);
					// } else {
					// Method mm = ClassUtil.finddAnnotationForMethod(method,
					// OnEvent.class);
					// if (mm != null) {
					// addConsumerMethod(mm, cclass, containerWrapper);
					// }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addConsumerMethod(Method method, Class cclass, ApplicationContext applicationContext) {
		OnEvent onEvent = method.getAnnotation(OnEvent.class);
		String consumerKey = ConsumerLoader.TOPICNAME2 + onEvent.value();
		Collection consumerMethods = getContainerConsumers(consumerKey, applicationContext);
		String componentname = getConsumerName(cclass);
		consumerMethods.add(new ConsumerMethodHolder(componentname, method));
	}

	public Collection<String> getContainerConsumers(String topicKey, ApplicationContext applicationContext) {

		Collection consumers = (Collection) applicationContext.getBean(topicKey);
		if (consumers == null) {
			consumers = new ArrayList();
//			containerWrapper.register(topicKey, consumers);
		}
		return consumers;
	}

	protected TreeSet createNewSet() {
		return new TreeSet(new Comparator() {
			public int compare(Object num1, Object num2) {
				String inum1, inum2;
				inum1 = num1.getClass().getName();
				inum2 = num2.getClass().getName();
				if (inum1.compareTo(inum2) < 1) {
					return -1; // returning the first object
				} else {

					return 1;
				}
			}

		});
	}

	public Boolean implementsInterface(Class cclass, Class interf) {
		for (Class c : cclass.getInterfaces()) {
			if (c.equals(interf)) {
				return true;
			}
		}
		return false;
	}

	protected String getConsumerName(Class cclass) {
		String name = "";
		// ComponentLoader will do it with @Component;
		if (cclass.isAnnotationPresent(JDComponent.class)) {
			JDComponent cp = (JDComponent) cclass.getAnnotation(JDComponent.class);
			name = UtilValidate.isEmpty(cp.value()) ? cclass.getName() : cp.value();
		} else if (cclass.isAnnotationPresent(JDService.class)) {
			JDService cp = (JDService) cclass.getAnnotation(JDService.class);
			name = UtilValidate.isEmpty(cp.value()) ? cclass.getName() : cp.value();
		} else {
			// directly @Consumer with no @Component or @Service
			name = cclass.getName();
		}
		return name;
	}

}
