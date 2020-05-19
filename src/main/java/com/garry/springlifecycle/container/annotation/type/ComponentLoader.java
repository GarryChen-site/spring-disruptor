package com.garry.springlifecycle.container.annotation.type;



import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.utils.Debug;
import com.garry.springlifecycle.utils.UtilValidate;
import org.springframework.context.ApplicationContext;

import java.util.Set;

public class ComponentLoader {
	public final static String module = ComponentLoader.class.getName();

	AnnotationScaner annotationScaner;
	ConsumerLoader consumerLoader;

	public ComponentLoader(AnnotationScaner annotationScaner, ConsumerLoader consumerLoader) {
		super();
		this.annotationScaner = annotationScaner;
		this.consumerLoader = consumerLoader;
	}

	public void loadAnnotationComponents(AnnotationHolder annotationHolder, AppContextWrapper context,
										 ApplicationContext applicationContext) {
		Set<String> classes = annotationScaner.getScannedAnnotations(context).get(JDComponent.class.getName());
		if (classes == null)
			return;
		Debug.logVerbose("[JdonFramework] found Annotation components size:" + classes.size(), module);
		for (Object className : classes) {
			createAnnotationComponentClass((String) className, annotationHolder, applicationContext);
		}
	}

	public void createAnnotationComponentClass(String className, AnnotationHolder annotationHolder,
											   ApplicationContext applicationContext) {
		try {
			Class cclass = Utils.createClass(className);
			JDComponent cp = (JDComponent) cclass.getAnnotation(JDComponent.class);
			Debug.logVerbose("[JdonFramework] load Annotation component name:" + cclass.getName() + " class:" + className, module);

			String name = UtilValidate.isEmpty(cp.value()) ? cclass.getName() : cp.value();
			annotationHolder.addComponent(name, cclass);
			POJOTargetMetaDef pojoMetaDef = new POJOTargetMetaDef(name, className);
			annotationHolder.getTargetMetaDefHolder().add(name, pojoMetaDef);

			consumerLoader.loadMethodAnnotations(cclass, applicationContext);
		} catch (Exception e) {
			Debug.logError("[JdonFramework] createAnnotationComponentClass error:" + e + className, module);

		}
	}

}
