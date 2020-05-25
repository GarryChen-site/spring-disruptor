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
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
import java.util.*;

public class Scanner extends ClassPathBeanDefinitionScanner {

    public final static String TOPICNAME = "CONSUMER_TOPIC";
    public final static String TOPICNAME2 = "MEHTOD_TOPIC";

    private AnnotationHolder annotationHolder;

    private IntroduceInfoHolder introduceInfoHolder;

    public Scanner(BeanDefinitionRegistry registry) {
        super(registry);
    }


    @Override
    protected void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(Consumer.class));
        this.addIncludeFilter(new AnnotationTypeFilter(Model.class));
        this.addIncludeFilter(new AnnotationTypeFilter(JDService.class));
        this.addIncludeFilter(new AnnotationTypeFilter(JDComponent.class));
        this.addIncludeFilter(new AnnotationTypeFilter(Introduce.class));
        this.addIncludeFilter(new AnnotationTypeFilter(Interceptor.class));
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        final Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        final DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) this.getRegistry();
//        annotationHolder = new AnnotationHolder();
//        introduceInfoHolder = new IntroduceInfoHolder();

        // @Consumer load
//        loadAnnotationConsumers(defaultListableBeanFactory, Consumer.class, annotationHolder);

        // @Model load
//        loadAnnotationModelConsumers(defaultListableBeanFactory, Model.class, annotationHolder);

        // @Service load
//        loadAnnotationServices(defaultListableBeanFactory, JDService.class, annotationHolder);

        // @Component load
//        loadAnnotationComponents(defaultListableBeanFactory, JDComponent.class, annotationHolder);

        // @Introduce load
//        loadAnnotationIntroduces(defaultListableBeanFactory, Introduce.class, annotationHolder);

        // @Interceptor load
//        loadAnnotationInterceptors(defaultListableBeanFactory, Interceptor.class, annotationHolder);


//        defaultListableBeanFactory.registerSingleton("introduceInfoHolder", introduceInfoHolder);
//        defaultListableBeanFactory.registerSingleton("annotationHolder", annotationHolder);


//        //@Consumer load
//        final Map<String, Object> consumerAnnotations = defaultListableBeanFactory.getBeansWithAnnotation(Consumer.class);
//
//
//        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
//            final GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();
//
//            // @Consumer load
//            loadAnnotationConsumers(beanDefinition, defaultListableBeanFactory);
//            // @Model load
//            loadAnnotationModels(beanDefinition, defaultListableBeanFactory);
//            // skip @JDService
//
//            // @JDComponent load
//            loadAnnotationComponents(beanDefinition, defaultListableBeanFactory);
//
//            // @Introduce load
//            loadAnnotationIntroduceInfos(beanDefinition, defaultListableBeanFactory);
//
//            // @Interceptors
//            loadAnnotationInterceptors(beanDefinition, defaultListableBeanFactory);
//
//        }
        return beanDefinitionHolders;
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
        String topicKey = TOPICNAME + topicName;
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

    //    @Deprecated
//    private void loadAnnotationInterceptors(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        final boolean isInterceptor = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Interceptor");
//        if (!isInterceptor) {
//            return;
//        }
//        final String interceptorClassName = beanDefinition.getBeanClassName();
//        final int length = StringUtil.split(interceptorClassName, ".").length;
//        String removeBeanName = StringUtil.split(interceptorClassName, ".")[length - 1];
//        removeBeanName = (new StringBuilder()).append(Character.toLowerCase(removeBeanName.charAt(0))).
//                append(removeBeanName.substring(1)).toString();
//        if (registry.containsBeanDefinition(removeBeanName)) {
//            registry.removeBeanDefinition(removeBeanName);
////            final Interceptor in = ((ScannedGenericBeanDefinition) beanDefinition).getBeanClass().getAnnotation(Interceptor.class);
//            final Class aClass = Utils.createClass(interceptorClassName);
//            final Interceptor in = (Interceptor) aClass.getAnnotation(Interceptor.class);
//            registry.registerBeanDefinition(in.name(), beanDefinition);
//        }
//
////        createAnnotationInterceptor(interceptorClassName,beanDefinition,registry);
//    }

//    @Deprecated
//    private void createAnnotationInterceptor(String interceptorClassName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        final Class interceptorClass = Utils.createClass(interceptorClassName);
//        final Interceptor interceptor = (Interceptor) interceptorClass.getAnnotation(Interceptor.class);
//
//        if (!UtilValidate.isEmpty(interceptor.value())) {
//            interceptorClassName = interceptor.value();
//        } else if (!UtilValidate.isEmpty(interceptor.name())) {
//            interceptorClassName = interceptor.name();
//        }
//
//        // 都是初始化之后的动作
//        // annotationHolder.addComponent(name, cclass);
//    }

//    private void loadAnnotationIntroduceInfos(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        final boolean isIntroduce = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Introduce");
//        if (!isIntroduce) {
//            return;
//        }
//        createIntroduceInfoHolder("introduceInfoHolder", registry);
////        beanDefinition.getPropertyValues().add("innerClassName",beanDefinition.getBeanClassName());
////        ((ScannedGenericBeanDefinition) beanDefinition).setBeanClass(IntroduceFactoryBean.class);
//
//
//
////        final String introduceClassName = beanDefinition.getBeanClassName();
////        createAnnotationIntroduceInfoClass(introduceClassName, beanDefinition, registry);
//    }

//    @Deprecated
//    private void createAnnotationIntroduceInfoClass(String introduceClassName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        final Class introduceClass = Utils.createClass(introduceClassName);
//        final Introduce introduce = (Introduce) introduceClass.getAnnotation(Introduce.class);
//
//        String[] adviceNames = introduce.values();
//
//        createIntroduceInfoHolder("introduceInfoHolder", registry);
//        // 初始化时再进行
//        // introduceInfoHolder.addIntroduceInfo(adviceNames, introduceClass);
//        // String targetName = annotationHolder.getComponentName(introduceClass);
//        // TargetMetaDefHolder 这是个bean
//        // 			if (targetName == null) {// iterate xml component
//        //				TargetMetaDefHolder targetMetaDefHolder = (TargetMetaDefHolder) containerWrapper.lookup(ComponentKeys.SERVICE_METAHOLDER_NAME);
//        //				targetName = targetMetaDefHolder.lookupForName(targetclass.getName());
//        //			}
//        // 完后再执行
//        // introduceInfoHolder.addTargetClassNames(introduceClass, targetName);
//
//    }
//
//    private void createIntroduceInfoHolder(String introduceInfoHolder, BeanDefinitionRegistry registry) {
//        // 是否可以直接创建 IntroduceInfoHolder 下面的也类似 除了 list类的
//        BeanDefinition introduceInfoBeanDefinition = null;
//        final boolean isExist = registry.containsBeanDefinition(introduceInfoHolder);
//        if (!isExist) {
//            introduceInfoBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(IntroduceInfoHolder.class).getBeanDefinition();
//            registry.registerBeanDefinition(introduceInfoHolder, introduceInfoBeanDefinition);
//        }
//
//    }

//    private void loadAnnotationComponents(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        final boolean isJDComponent = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.JDComponent");
//        if (!isJDComponent) {
//            return;
//        }
//
////        createAnnotationHolder("annotationHolder", registry);
////        createPOJOTargetMetaDef("pojoMetaDef", registry);
//
//        final String beanClassName = beanDefinition.getBeanClassName();
//        final Class componentClass = Utils.createClass(beanClassName);
//        final JDComponent jdComponent = (JDComponent) componentClass.getAnnotation(JDComponent.class);
//        final String componentInAnnotation = jdComponent.value();
//        final int length = StringUtil.split(beanClassName, ".").length;
//        String removeBeanName = StringUtil.split(beanClassName, ".")[length - 1];
//        // student
//        removeBeanName = (new StringBuilder()).append(Character.toLowerCase(removeBeanName.charAt(0))).
//                append(removeBeanName.substring(1)).toString();
//        if (registry.containsBeanDefinition(removeBeanName)) {
//            registry.removeBeanDefinition(removeBeanName);
//        }
//        registry.registerBeanDefinition(componentInAnnotation, beanDefinition);
//        createAnnotationComponentClass(componentClass, beanDefinition, registry);
//    }


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
        String onEventKey = TOPICNAME2 + onEvent.value();
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

//    private void createOnEventKey(String onEventKey, BeanDefinitionRegistry registry) {
//        BeanDefinition onEventBeanDefinition = null;
//        final boolean isExist = registry.containsBeanDefinition(onEventKey);
//        if (!isExist) {
//            onEventBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ArrayList.class).getBeanDefinition();
//            registry.registerBeanDefinition(onEventKey, onEventBeanDefinition);
//        }
//    }

//    private void createPOJOTargetMetaDef(String pojoMetaDef, BeanDefinitionRegistry registry) {
//        BeanDefinition pojoTargetMetaBeanDefinition = null;
//        final boolean isExist = registry.containsBeanDefinition(pojoMetaDef);
//        if (!isExist) {
//            pojoTargetMetaBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(POJOTargetMetaDef.class).getBeanDefinition();
//            registry.registerBeanDefinition(pojoMetaDef, pojoTargetMetaBeanDefinition);
//        }
//    }

//    private void createAnnotationHolder(String annotationHolder, BeanDefinitionRegistry registry) {
//        BeanDefinition annotationHolderBeanDefinition = null;
//        final boolean isExist = registry.containsBeanDefinition(annotationHolder);
//        if (!isExist) {
//            annotationHolderBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(AnnotationHolder.class).getBeanDefinition();
//            registry.registerBeanDefinition(annotationHolder, annotationHolderBeanDefinition);
//        }
//    }


//    private void loadAnnotationModelConsumers(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        final boolean isModel = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Model");
//        if (!isModel) {
//            return;
//        }
//
//        registerContainerConsumersForModel("modelConsumerMethodHolder", registry);
//
//        final String beanClassName = beanDefinition.getBeanClassName();
//        final Class modelClass = Utils.createClass(beanClassName);
////        loadOnCommandAnnotations(modelClass, beanDefinition, registry);
//
//    }

//    @Deprecated
//    private void loadOnCommandAnnotations(Class modelClass, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
//        for (Method method : ClassUtil.getAllDecaredMethods(modelClass)) {
//            if (method.isAnnotationPresent(OnCommand.class)) {
//                addOnCommandConsumerMethod(method, beanDefinition, registry);
//            }
//        }
//    }

    private void addOnCommandConsumerMethod(Method method, Class cclass, DefaultListableBeanFactory defaultListableBeanFactory) {
        final OnCommand onCommand = method.getAnnotation(OnCommand.class);
        String consumerKey = TOPICNAME2 + onCommand.value();
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

    private void registerContainerConsumersForModel(String consumerKey, BeanDefinitionRegistry registry) {
        BeanDefinition modelBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(consumerKey);
        if (!isExist) {
            modelBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ModelConsumerMethodHolder.class).getBeanDefinition();
            registry.registerBeanDefinition(consumerKey, modelBeanDefinition);
        }
    }

//    private void loadAnnotationConsumers(BeanDefinition beanDefinition, DefaultListableBeanFactory defaultListableBeanFactory) {
//        final Map<String, Object> beansWithAnnotation = defaultListableBeanFactory.getBeansWithAnnotation(Consumer.class);
//        final boolean isConsumer = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Consumer");
//        if (!isConsumer) {
//            return;
//        }
//        final String beanClassName = beanDefinition.getBeanClassName();
//        createAnnotationConsumerClass(beanClassName, registry);
//    }

//    private void createAnnotationConsumerClass(String beanClassName, BeanDefinitionRegistry registry) {
//        final Class consumerClass = Utils.createClass(beanClassName);
//        if (!DomainEventHandler.class.isAssignableFrom(consumerClass)) {
//            //that with @Consumer annotataion must also implements  com.jdon.domain.message.DomainEventHandler
//            return;
//        }
//        final Consumer consumer = (Consumer) consumerClass.getAnnotation(Consumer.class);
//        String consumerName = UtilValidate.isEmpty(consumer.value()) ? consumerClass.getName() : consumer.value();
//        String consumerKey = TOPICNAME + consumerName;
//        registerContainerConsumers(consumerKey, registry);//即 getContainerConsumers(topicKey, containerWrapper);
//        // 初始化的时候,取出consumers实例，再add aClass.getName()
//        // containerWrapper.register(name, cclass); 不需要了，Spring已经执行
//
//    }

    /**
     *
     *
     * @param topicKey
     * @param registry
     */
//    private void registerContainerConsumers(String topicKey, BeanDefinitionRegistry registry) {
//        BeanDefinition consumersBeanDefinition = null;
//        final boolean isExist = registry.containsBeanDefinition(topicKey);
//        if (!isExist) {
//            consumersBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ArrayList.class).getBeanDefinition();
//            registry.registerBeanDefinition(topicKey, consumersBeanDefinition);
//        }
//    }
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition) && (
                beanDefinition.getMetadata().hasAnnotation(Consumer.class.getName()) ||
                        beanDefinition.getMetadata().hasAnnotation(Model.class.getName()) ||
                        beanDefinition.getMetadata().hasAnnotation(JDService.class.getName()) ||
                        beanDefinition.getMetadata().hasAnnotation(JDComponent.class.getName()) ||
                        beanDefinition.getMetadata().hasAnnotation(Introduce.class.getName()) ||
                        beanDefinition.getMetadata().hasAnnotation(Interceptor.class.getName()));
    }
}
