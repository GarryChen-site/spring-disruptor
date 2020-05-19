package com.garry.springlifecycle.container.annotation.type;



import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.container.access.TargetMetaDefHolder;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.container.finder.ComponentKeys;
import com.garry.springlifecycle.container.interceptor.IntroduceInfoHolder;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.utils.Debug;
import org.springframework.context.ApplicationContext;

import java.util.Set;

public class InroduceLoader {

	public final static String module = InroduceLoader.class.getName();

	private AnnotationScaner annotationScaner;

	private IntroduceInfoHolder introduceInfoHolder;

	public InroduceLoader(AnnotationScaner annotationScaner, IntroduceInfoHolder introduceInfoHolder) {
		super();
		this.annotationScaner = annotationScaner;
		this.introduceInfoHolder = introduceInfoHolder;
	}

	public void loadAnnotationIntroduceInfos(AnnotationHolder annotationHolder, AppContextWrapper context, ApplicationContext applicationContext) {
		Set<String> classes = annotationScaner.getScannedAnnotations(context).get(Introduce.class.getName());
		if (classes == null)
			return;
		Debug.logVerbose("[JdonFramework] found Annotation IntroduceInfo size:" + classes.size(), module);
		for (Object className : classes) {
			createAnnotationIntroduceInfoClass((String) className, annotationHolder, applicationContext);
		}
	}

	public void createAnnotationIntroduceInfoClass(String className, AnnotationHolder annotationHolder, ApplicationContext applicationContext) {
		try {
			Class targetclass = Utils.createClass(className);
			Introduce cp = (Introduce) targetclass.getAnnotation(Introduce.class);

			String[] adviceName = cp.values();
			introduceInfoHolder.addIntroduceInfo(adviceName, targetclass);
			String targetName = annotationHolder.getComponentName(targetclass);
			if (targetName == null) {// iterate xml component
				TargetMetaDefHolder targetMetaDefHolder = (TargetMetaDefHolder) applicationContext.getBean(ComponentKeys.SERVICE_METAHOLDER_NAME);
				targetName = targetMetaDefHolder.lookupForName(targetclass.getName());
			}
			introduceInfoHolder.addTargetClassNames(targetclass, targetName);
			Debug.logVerbose("[JdonFramework] load Annotation IntroduceInfo name:" + adviceName + " target class:" + className, module);
		} catch (Exception e) {
			Debug.logError("[JdonFramework] createAnnotationIntroduceInfoClass error:" + e + className, module);
		}
	}
}
