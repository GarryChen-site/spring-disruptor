package com.garry.springlifecycle.container.beanpost;

import com.garry.springlifecycle.annotation.*;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.annotation.model.OnEvent;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.container.access.TargetMetaDefHolder;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.container.interceptor.IntroduceInfoHolder;
import com.garry.springlifecycle.domain.message.consumer.ConsumerMethodHolder;
import com.garry.springlifecycle.domain.message.consumer.ModelConsumerMethodHolder;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.UtilValidate;
import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 当所有的singleton的bean都初始化完了之后才会回调这个接口
 * <p>
 * 解析注解的操作应该在这里
 */
@Component
public class AfterAllInitializing implements SmartInitializingSingleton {

    public final static String CONSUMER_TOPIC_NAME = "CONSUMER_TOPIC";
    public final static String CONSUMER_TOPIC_NAME_METHOD = "METHOD_TOPIC";
    public final static String MODEL_TOPIC_NAME_METHOD = "MEHTOD_TOPIC_COMMAND";


    private DefaultListableBeanFactory defaultListableBeanFactory;

    private AnnotationHolder annotationHolder;

    private IntroduceInfoHolder introduceInfoHolder;

    public AfterAllInitializing(DefaultListableBeanFactory defaultListableBeanFactory) {
        this.defaultListableBeanFactory = defaultListableBeanFactory;
    }

    @Override
    public void afterSingletonsInstantiated() {

        annotationHolder = new AnnotationHolder();
        introduceInfoHolder = new IntroduceInfoHolder();

        // @Consumer load
        loadAnnotationConsumers(defaultListableBeanFactory, Consumer.class, annotationHolder);

        // @Model load
        loadAnnotationModelConsumers(defaultListableBeanFactory, Model.class, annotationHolder);

        // @Service load
        loadAnnotationServices(defaultListableBeanFactory, JDService.class, annotationHolder);

        // @Component load
        loadAnnotationComponents(defaultListableBeanFactory, JDComponent.class, annotationHolder);

        // @Introduce load
        loadAnnotationIntroduces(defaultListableBeanFactory, Introduce.class, annotationHolder);

        // @Interceptor load
        loadAnnotationInterceptors(defaultListableBeanFactory, Interceptor.class, annotationHolder);

        defaultListableBeanFactory.registerSingleton("introduceInfoHolder", introduceInfoHolder);
        defaultListableBeanFactory.registerSingleton("annotationHolder", annotationHolder);

        final Map<String, Object> introduces = defaultListableBeanFactory.getBeansWithAnnotation(Introduce.class);

        for (String beanName : introduces.keySet()) {
            defaultListableBeanFactory.removeBeanDefinition(beanName);
            final Object object = introduces.get(beanName);
            final Introduce introduce = object.getClass().getAnnotation(Introduce.class);
            final String[] values = introduce.values();
            ProxyFactory proxyFactory = new ProxyFactory();
            for (String value : values) {
                final Advice advice = (Advice) defaultListableBeanFactory.getBean(value);
                proxyFactory.addAdvice(advice);
            }
            proxyFactory.setTarget(object);
            final Object proxy = proxyFactory.getProxy();
            defaultListableBeanFactory.registerSingleton(beanName, proxy);
        }
    }


    private void loadAnnotationInterceptors(DefaultListableBeanFactory defaultListableBeanFactory, Class<Interceptor> interceptorClass, AnnotationHolder annotationHolder) {
        final Map<String, Object> interceptors = defaultListableBeanFactory.getBeansWithAnnotation(interceptorClass);
        if (interceptors.isEmpty()) {
            return;
        }
        for (Object object : interceptors.values()) {
            createAnnotationInterceptorClass(object, annotationHolder, defaultListableBeanFactory);
        }
    }

    private void createAnnotationInterceptorClass(Object object, AnnotationHolder annotationHolder, DefaultListableBeanFactory defaultListableBeanFactory) {
        final Class<?> cClass = object.getClass();
        final Interceptor interceptor = cClass.getAnnotation(Interceptor.class);

        String name = cClass.getName();
        if (!UtilValidate.isEmpty(interceptor.value())) {
            name = interceptor.value();
        } else if (!UtilValidate.isEmpty(interceptor.name())) {
            name = interceptor.name();
        }

        annotationHolder.addComponent(name, cClass);
        annotationHolder.getInterceptors().put(name, cClass);
        if (!UtilValidate.isEmpty(interceptor.pointcut())) {
            final String[] targets = interceptor.pointcut().split(",");
            for (int i = 0; i < targets.length; i++) {
                final Class targetClass = annotationHolder.getComponentClass(targets[i]);
                if (targetClass != null) {
                    introduceInfoHolder.addTargetClassNames(targetClass, targets[i]);
                }
            }
        }
        final POJOTargetMetaDef pojoTargetMetaDef = new POJOTargetMetaDef(name, cClass.getName());
        annotationHolder.getTargetMetaDefHolder().add(name, pojoTargetMetaDef);
    }

    private void loadAnnotationIntroduces(DefaultListableBeanFactory defaultListableBeanFactory, Class<Introduce> introduceClass, AnnotationHolder annotationHolder) {
        final Map<String, Object> introduces = defaultListableBeanFactory.getBeansWithAnnotation(introduceClass);
        if (introduces.isEmpty()) {
            return;
        }
        for (Object object : introduces.values()) {
            createAnnotationIntroduceClass(object, annotationHolder, defaultListableBeanFactory);
        }
    }

    private void createAnnotationIntroduceClass(Object object, AnnotationHolder annotationHolder, DefaultListableBeanFactory defaultListableBeanFactory) {
        final Class<?> targetClass = object.getClass();
        final Introduce introduce = targetClass.getAnnotation(Introduce.class);


        final String[] adviceName = introduce.values();
        introduceInfoHolder.addIntroduceInfo(adviceName, targetClass);
        String targetName = annotationHolder.getComponentName(targetClass);
        if (targetName == null) {
            final TargetMetaDefHolder targetMetaDefHolder = (TargetMetaDefHolder) defaultListableBeanFactory.getBean("targetMetaDefHolder");
            targetName = targetMetaDefHolder.lookupForName(targetClass.getName());
        }
        introduceInfoHolder.addTargetClassNames(targetClass, targetName);
    }

    private void loadAnnotationComponents(DefaultListableBeanFactory defaultListableBeanFactory, Class<JDComponent> jdComponentClass, AnnotationHolder annotationHolder) {
        final Map<String, Object> components = defaultListableBeanFactory.getBeansWithAnnotation(jdComponentClass);
        if (components.isEmpty()) {
            return;
        }
        for (Object object : components.values()) {
            createAnnotationComponentClass(object, annotationHolder, defaultListableBeanFactory);
        }
    }

    private void loadAnnotationServices(DefaultListableBeanFactory defaultListableBeanFactory, Class<JDService> jdServiceClass, AnnotationHolder annotationHolder) {
        final Map<String, Object> services = defaultListableBeanFactory.getBeansWithAnnotation(jdServiceClass);
        if (services.isEmpty()) {
            return;
        }
        for (Object object : services.values()) {
            createAnnotationServiceClass(object, annotationHolder, defaultListableBeanFactory);
        }
    }

    private void createAnnotationServiceClass(Object object, AnnotationHolder annotationHolder, DefaultListableBeanFactory defaultListableBeanFactory) {
        final Class<?> cClass = object.getClass();
        final JDService service = cClass.getAnnotation(JDService.class);

        final String name = UtilValidate.isEmpty(service.value()) ? cClass.getName() : service.value();
        annotationHolder.addComponent(name, cClass);
        createPOJOTargetMetaDef(name, cClass.getName(), annotationHolder.getTargetMetaDefHolder(), cClass);
        loadOnEventMethodAnnotations(cClass, defaultListableBeanFactory);

    }

    private void loadOnEventMethodAnnotations(Class<?> cClass, DefaultListableBeanFactory defaultListableBeanFactory) {
        for (Method method : ClassUtil.getAllDecaredMethods(cClass)) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                addOnEventConsumerMethod(method, cClass, defaultListableBeanFactory);
            }
        }
    }

    private void createPOJOTargetMetaDef(String name, String className, TargetMetaDefHolder targetMetaDefHolder, Class<?> cClass) {
        POJOTargetMetaDef pojoTargetMetaDef = new POJOTargetMetaDef(name, className);
        targetMetaDefHolder.add(name, pojoTargetMetaDef);
    }

    private void loadAnnotationModelConsumers(DefaultListableBeanFactory defaultListableBeanFactory, Class<Model> modelClass, AnnotationHolder annotationHolder) {
        final Map<String, Object> models = defaultListableBeanFactory.getBeansWithAnnotation(modelClass);
        if (models.isEmpty()) {
            return;
        }
        for (Object object : models.values()) {
            loadOnCommandMethodAnnotations(object, defaultListableBeanFactory);
        }
    }

    private void loadOnCommandMethodAnnotations(Object object, DefaultListableBeanFactory defaultListableBeanFactory) {
        final Class<?> cClass = object.getClass();
        for (Method method : ClassUtil.getAllDecaredMethods(cClass)) {
            if (method.isAnnotationPresent(OnCommand.class)) {
                addOnCommandConsumerMethod(method, cClass, defaultListableBeanFactory);
            }
        }
    }

    private void loadAnnotationConsumers(DefaultListableBeanFactory defaultListableBeanFactory, Class<Consumer> consumerClass, AnnotationHolder annotationHolder) {
        final Map<String, Object> consumers = defaultListableBeanFactory.getBeansWithAnnotation(consumerClass);
        if (consumers.isEmpty()) {
            return;
        }
        for (Object object : consumers.values()) {
            createAnnotationConsumerClass(object, annotationHolder, defaultListableBeanFactory);
        }

    }

    private void createAnnotationConsumerClass(Object object, AnnotationHolder annotationHolder, DefaultListableBeanFactory defaultListableBeanFactory) {
        final Class<?> consumerClass = object.getClass();
        final Consumer consumer = consumerClass.getAnnotation(Consumer.class);
        String topicName = UtilValidate.isEmpty(consumer.value()) ? consumerClass.getName() : consumer.value();
        String topicKey = CONSUMER_TOPIC_NAME + topicName;
        final Collection<String> consumers = getContainerConsumers(topicKey, defaultListableBeanFactory);
        final String name = getConsumerName(consumerClass);
        consumers.add(name);
        final String registerName = consumerClass.getName();
        if (defaultListableBeanFactory.containsBean(registerName)) {
            defaultListableBeanFactory.removeBeanDefinition(registerName);
        }
        defaultListableBeanFactory.registerSingleton(name, object);
    }

    private String getConsumerName(Class<?> cclass) {
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

    private Collection<String> getContainerConsumers(String topicKey, DefaultListableBeanFactory defaultListableBeanFactory) {
        final boolean isExit = defaultListableBeanFactory.containsBean(topicKey);
        Collection<String> consumers = null;
        if (!isExit) {
            consumers = new ArrayList();
            defaultListableBeanFactory.registerSingleton(topicKey, consumers);
            return (Collection<String>) consumers;
        }
        consumers = (Collection) defaultListableBeanFactory.getBean(topicKey);
        return consumers;
    }


    private void createAnnotationComponentClass(Object object, AnnotationHolder annotationHolder, DefaultListableBeanFactory defaultListableBeanFactory) {
        final Class<?> componentClass = object.getClass();
        final JDComponent jdComponent = (JDComponent) componentClass.getAnnotation(JDComponent.class);
        String componentName = UtilValidate.isEmpty(jdComponent.value()) ? componentClass.getName() : jdComponent.value();
        annotationHolder.addComponent(componentName, componentClass);
        POJOTargetMetaDef pojoTargetMetaDef = new POJOTargetMetaDef(componentName, componentClass.getName());
        annotationHolder.getTargetMetaDefHolder().add(componentName, pojoTargetMetaDef);
        loadOnEventAnnotations(componentClass, defaultListableBeanFactory);
    }

    private void loadOnEventAnnotations(Class componentClass, DefaultListableBeanFactory defaultListableBeanFactory) {
        for (Method method : ClassUtil.getAllDecaredMethods(componentClass)) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                addOnEventConsumerMethod(method, componentClass, defaultListableBeanFactory);
            }
        }
    }

    private void addOnEventConsumerMethod(Method method, Class cclass, DefaultListableBeanFactory defaultListableBeanFactory) {
        final OnEvent onEvent = method.getAnnotation(OnEvent.class);
        String onEventKey = CONSUMER_TOPIC_NAME_METHOD + onEvent.value();
        final Collection<ConsumerMethodHolder> consumerMethodHolders = getContainerConsumerMethodHolders(onEventKey, defaultListableBeanFactory);
        final String componentName = getConsumerName(cclass);
        consumerMethodHolders.add(new ConsumerMethodHolder(componentName, method));
    }

    private Collection<ConsumerMethodHolder> getContainerConsumerMethodHolders(String onEventKey, DefaultListableBeanFactory defaultListableBeanFactory) {
        final boolean isExist = defaultListableBeanFactory.containsBean(onEventKey);
        Collection<ConsumerMethodHolder> consumers = null;
        if (!isExist) {
            consumers = new ArrayList<>();
            defaultListableBeanFactory.registerSingleton(onEventKey, consumers);
            return consumers;
        }
        consumers = (Collection) defaultListableBeanFactory.getBean(onEventKey);
        return consumers;
    }


    private void addOnCommandConsumerMethod(Method method, Class cclass, DefaultListableBeanFactory defaultListableBeanFactory) {
        final OnCommand onCommand = method.getAnnotation(OnCommand.class);
        String consumerKey = MODEL_TOPIC_NAME_METHOD + onCommand.value();
        ModelConsumerMethodHolder modelConsumerMethodHolder = getModelHolderContainer(consumerKey, defaultListableBeanFactory);
        modelConsumerMethodHolder.setConsumerMethodHolder(new ConsumerMethodHolder(cclass.getName(), method));
    }

    private ModelConsumerMethodHolder getModelHolderContainer(String consumerKey, DefaultListableBeanFactory defaultListableBeanFactory) {
        final boolean isExist = defaultListableBeanFactory.containsBean(consumerKey);
        ModelConsumerMethodHolder modelConsumerMethodHolder = null;
        if (!isExist) {
            modelConsumerMethodHolder = new ModelConsumerMethodHolder();
            defaultListableBeanFactory.registerSingleton(consumerKey, modelConsumerMethodHolder);
            return modelConsumerMethodHolder;
        }
        modelConsumerMethodHolder = (ModelConsumerMethodHolder) defaultListableBeanFactory.getBean(consumerKey);
        return modelConsumerMethodHolder;
    }


}
