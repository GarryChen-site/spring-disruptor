package com.garry.springlifecycle.container.annotation.type;



import com.garry.springlifecycle.annotation.JDService;
import com.garry.springlifecycle.annotation.Singleton;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.businessproxy.meta.SingletonPOJOTargetMetaDef;
import com.garry.springlifecycle.container.access.TargetMetaDefHolder;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.utils.Debug;
import com.garry.springlifecycle.utils.UtilValidate;
import org.springframework.context.ApplicationContext;

import java.util.Set;

public class ServiceLoader {
	public final static String module = ServiceLoader.class.getName();

	AnnotationScaner annotationScaner;
	ConsumerLoader consumerLoader;

	public ServiceLoader(AnnotationScaner annotationScaner, ConsumerLoader consumerLoader) {
		super();
		this.annotationScaner = annotationScaner;
		this.consumerLoader = consumerLoader;
	}

	public void loadAnnotationServices(AnnotationHolder annotationHolder, AppContextWrapper context, ApplicationContext applicationContext) {
		Set<String> classes = annotationScaner.getScannedAnnotations(context).get(JDService.class.getName());
		if (classes == null)
			return;
		Debug.logVerbose("[JdonFramework] found Annotation components size:" + classes.size(), module);
		for (Object className : classes) {
			createAnnotationServiceClass((String) className, annotationHolder,applicationContext);
		}
	}

	public void createAnnotationServiceClass(String className, AnnotationHolder annotationHolder, ApplicationContext applicationContext) {
		try {
			Class cclass = Utils.createClass(className);
			JDService serv = (JDService) cclass.getAnnotation(JDService.class);
			Debug.logVerbose("[JdonFramework] load Annotation service name:" + serv.value() + " class:" + className, module);

			String name = UtilValidate.isEmpty(serv.value()) ? cclass.getName() : serv.value();
			annotationHolder.addComponent(name, cclass);
			createPOJOTargetMetaDef(name, className, annotationHolder.getTargetMetaDefHolder(), cclass);

			consumerLoader.loadMethodAnnotations(cclass, applicationContext);
		} catch (Exception e) {
			Debug.logError("[JdonFramework] createAnnotationserviceClass error:" + e, module);
		}
	}

	public void createPOJOTargetMetaDef(String name, String className, TargetMetaDefHolder targetMetaDefHolder, Class cclass) {
		POJOTargetMetaDef pojoMetaDef = null;
		if (cclass.isAnnotationPresent(Singleton.class)) {
			pojoMetaDef = new SingletonPOJOTargetMetaDef(name, className);
		} else {
			pojoMetaDef = new POJOTargetMetaDef(name, className);
		}
		targetMetaDefHolder.add(name, pojoMetaDef);
	}
}
