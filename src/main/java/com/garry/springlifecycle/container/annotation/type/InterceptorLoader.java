package com.garry.springlifecycle.container.annotation.type;



import com.garry.springlifecycle.annotation.Interceptor;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.container.interceptor.IntroduceInfoHolder;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.utils.Debug;
import com.garry.springlifecycle.utils.UtilValidate;

import java.util.Set;

public class InterceptorLoader {

	public final static String module = InterceptorLoader.class.getName();

	private AnnotationScaner annotationScaner;

	private IntroduceInfoHolder introduceInfoHolder;

	public InterceptorLoader(AnnotationScaner annotationScaner, IntroduceInfoHolder introduceInfoHolder) {
		super();
		this.annotationScaner = annotationScaner;
		this.introduceInfoHolder = introduceInfoHolder;
	}

	public void loadAnnotationInterceptors(AnnotationHolder annotationHolder, AppContextWrapper context) {
		Set<String> classes = annotationScaner.getScannedAnnotations(context).get(Interceptor.class.getName());
		if (classes == null)
			return;
		Debug.logVerbose("[JdonFramework] found Annotation Interceptor size:" + classes.size(), module);
		for (Object className : classes) {
			createAnnotationInterceptor((String) className, annotationHolder);
		}
	}

	public void createAnnotationInterceptor(String className, AnnotationHolder annotationHolder) {
		try {
			Class cclass = Utils.createClass(className);
			Interceptor inter = (Interceptor) cclass.getAnnotation(Interceptor.class);

			String name = cclass.getName();
			if (!UtilValidate.isEmpty(inter.value())) {
				name = inter.value();
			} else if (!UtilValidate.isEmpty(inter.name())) {
				name = inter.name();
			}

			annotationHolder.addComponent(name, cclass);
			annotationHolder.getInterceptors().put(name, cclass);
			if (!UtilValidate.isEmpty(inter.pointcut())) {
				String[] targets = inter.pointcut().split(",");
				for (int i = 0; i < targets.length; i++) {
					Class targetClass = annotationHolder.getComponentClass(targets[i]);
					if (targetClass != null)
						introduceInfoHolder.addTargetClassNames(targetClass, targets[i]);
				}
			}
			POJOTargetMetaDef pojoMetaDef = new POJOTargetMetaDef(name, className);
			annotationHolder.getTargetMetaDefHolder().add(name, pojoMetaDef);
			Debug.logVerbose("[JdonFramework] load Annotation Interceptor name:" + name + " target class:" + className, module);
		} catch (Exception e) {
			Debug.logError("[JdonFramework] createAnnotationInterceptorClass error:" + e + className, module);
		}
	}

}
