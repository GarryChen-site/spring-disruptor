package com.garry.springlifecycle.container.annotation.type;



import com.garry.springlifecycle.annotation.Model;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.controller.context.AppContextWrapper;
import com.garry.springlifecycle.domain.message.consumer.ConsumerMethodHolder;
import com.garry.springlifecycle.domain.message.consumer.ModelConsumerMethodHolder;
import com.garry.springlifecycle.utils.ClassUtil;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Set;

public class ModelConsumerLoader {

    public final static String TOPICNAME2 = "MEHTOD_TOPIC_COMMAND";

    AnnotationScaner annotationScaner;

    public ModelConsumerLoader(AnnotationScaner annotationScaner) {
        super();
        this.annotationScaner = annotationScaner;
    }

    public void loadAnnotationModels(AnnotationHolder annotationHolder, AppContextWrapper context, ApplicationContext applicationContext) {
        Set<String> classes = annotationScaner.getScannedAnnotations(context).get(Model.class.getName());
        if (classes == null)
            return;
        for (String className : classes) {
            Class cclass = Utils.createClass(className);
            loadMethodAnnotations(cclass, applicationContext);
        }
    }

    /**
     * add the class to consumers annotated with @OnCommand
     *
     * @param cclass
     * @param containerWrapper
     */

    public void loadMethodAnnotations(Class cclass, ApplicationContext applicationContext) {
        try {
            for (Method method : ClassUtil.getAllDecaredMethods(cclass)) {
                if (method.isAnnotationPresent(OnCommand.class)) {
                    addConsumerMethod(method, cclass, applicationContext);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addConsumerMethod(Method method, Class cclass, ApplicationContext applicationContext) {
        OnCommand onCommand = method.getAnnotation(OnCommand.class);
        String consumerKey = TOPICNAME2 + onCommand.value();
        ModelConsumerMethodHolder modelConsumerMethodHolder = getContainerConsumers(consumerKey, applicationContext);
        modelConsumerMethodHolder.setConsumerMethodHolder(new ConsumerMethodHolder(cclass.getName(), method));
    }

    public ModelConsumerMethodHolder getContainerConsumers(String topicKey, ApplicationContext applicationContext) {
        ModelConsumerMethodHolder modelConsumerMethodHolder = (ModelConsumerMethodHolder) applicationContext.getBean(topicKey);
        if (modelConsumerMethodHolder == null) {
            modelConsumerMethodHolder = new ModelConsumerMethodHolder();
//            containerWrapper.register(topicKey, modelConsumerMethodHolder);
        }
        return modelConsumerMethodHolder;
    }

}
