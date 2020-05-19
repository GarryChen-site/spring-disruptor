package com.garry.springlifecycle.container.beanpost;

import com.garry.springlifecycle.annotation.Consumer;
import com.garry.springlifecycle.annotation.Interceptor;
import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.annotation.model.OnEvent;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.container.annotation.type.Utils;
import com.garry.springlifecycle.container.interceptor.IntroduceInfoHolder;
import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.consumer.ModelConsumerMethodHolder;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.UtilValidate;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class Scanner extends ClassPathBeanDefinitionScanner {

    public final static String TOPICNAME = "CONSUMER_TOPIC";
    public final static String TOPICNAME2 = "MEHTOD_TOPIC";

    public Scanner(BeanDefinitionRegistry registry) {
        super(registry);
    }


    @Override
    protected void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(Consumer.class));
        this.addIncludeFilter(new AnnotationTypeFilter(JDComponent.class));
        this.addIncludeFilter(new AnnotationTypeFilter(Introduce.class));
        this.addIncludeFilter(new AnnotationTypeFilter(Interceptor.class));
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        final Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        final BeanDefinitionRegistry registry = this.getRegistry();
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            final GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionHolder.getBeanDefinition();

            // @Consumer load
            loadAnnotationConsumers(beanDefinition, registry);
            // @Model load
            loadAnnotationModels(beanDefinition, registry);
            // skip @JDService

            // @JDComponent load
            loadAnnotationComponents(beanDefinition, registry);

            // @Introduce load
            loadAnnotationIntroduceInfos(beanDefinition, registry);

            // @Interceptors
//            interceptorLoad(beanDefinition, registry);

        }
        return beanDefinitionHolders;
    }

    @Deprecated
    private void interceptorLoad(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final boolean isInterceptor = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Interceptor");
        if (!isInterceptor){
            return;
        }
        final String interceptorClassName = beanDefinition.getBeanClassName();
        createAnnotationInterceptor(interceptorClassName,beanDefinition,registry);
    }

    @Deprecated
    private void createAnnotationInterceptor(String interceptorClassName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final Class interceptorClass = Utils.createClass(interceptorClassName);
        final Interceptor interceptor = (Interceptor) interceptorClass.getAnnotation(Interceptor.class);

        if (!UtilValidate.isEmpty(interceptor.value())){
            interceptorClassName = interceptor.value();
        }else if (!UtilValidate.isEmpty(interceptor.name())){
            interceptorClassName = interceptor.name();
        }

        // 都是初始化之后的动作
        // annotationHolder.addComponent(name, cclass);
    }

    private void loadAnnotationIntroduceInfos(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final boolean isIntroduce = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Introduce");
        if (!isIntroduce) {
            return;
        }
        createIntroduceInfoHolder("introduceInfoHolder", registry);

//        final String introduceClassName = beanDefinition.getBeanClassName();
//        createAnnotationIntroduceInfoClass(introduceClassName, beanDefinition, registry);
    }

    @Deprecated
    private void createAnnotationIntroduceInfoClass(String introduceClassName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final Class introduceClass = Utils.createClass(introduceClassName);
        final Introduce introduce = (Introduce) introduceClass.getAnnotation(Introduce.class);

        String[] adviceNames = introduce.values();

        createIntroduceInfoHolder("introduceInfoHolder", registry);
        // 初始化时再进行
        // introduceInfoHolder.addIntroduceInfo(adviceNames, introduceClass);
        // String targetName = annotationHolder.getComponentName(introduceClass);
        // TargetMetaDefHolder 这是个bean
        // 			if (targetName == null) {// iterate xml component
        //				TargetMetaDefHolder targetMetaDefHolder = (TargetMetaDefHolder) containerWrapper.lookup(ComponentKeys.SERVICE_METAHOLDER_NAME);
        //				targetName = targetMetaDefHolder.lookupForName(targetclass.getName());
        //			}
        // 完后再执行
        // introduceInfoHolder.addTargetClassNames(introduceClass, targetName);

    }

    private void createIntroduceInfoHolder(String introduceInfoHolder, BeanDefinitionRegistry registry) {
        // 是否可以直接创建 IntroduceInfoHolder 下面的也类似 除了 list类的
        BeanDefinition introduceInfoBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(introduceInfoHolder);
        if (!isExist) {
            introduceInfoBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(IntroduceInfoHolder.class).getBeanDefinition();
            registry.registerBeanDefinition(introduceInfoHolder, introduceInfoBeanDefinition);
        }

    }

    private void loadAnnotationComponents(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final boolean isJDComponent = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.JDComponent");
        if (!isJDComponent) {
            return;
        }

//        createAnnotationHolder("annotationHolder", registry);
//        createPOJOTargetMetaDef("pojoMetaDef", registry);

        final String beanClassName = beanDefinition.getBeanClassName();
        final Class componentClass = Utils.createClass(beanClassName);
        final JDComponent jdComponent = (JDComponent) componentClass.getAnnotation(JDComponent.class);
        final String componentInAnnotation = jdComponent.value();
        if (registry.containsBeanDefinition(beanClassName)){
            registry.removeBeanDefinition(beanClassName);
        }
        registry.registerBeanDefinition(componentInAnnotation,beanDefinition);
        createAnnotationComponentClass(componentClass, beanDefinition, registry);
    }

    private void createAnnotationComponentClass(Class componentClass, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final JDComponent jdComponent = (JDComponent) componentClass.getAnnotation(JDComponent.class);
        String componentName = UtilValidate.isEmpty(jdComponent.value()) ? componentClass.getName() : jdComponent.value();
//        createAnnotationHolder("annotationHolder", registry);
        //初始化时取出 并 annotationHolder.addComponent(componentName, componentClass);并执行下面两步
        createPOJOTargetMetaDef("pojoMetaDef", registry);
        // POJOTargetMetaDef pojoMetaDef = new POJOTargetMetaDef(name, className);
        // annotationHolder.getTargetMetaDefHolder().add(name, pojoMetaDef);
        loadOnEventAnnotations(componentClass, beanDefinition, registry);
    }

    private void loadOnEventAnnotations(Class componentClass, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        for (Method method : ClassUtil.getAllDecaredMethods(componentClass)) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                addOnEventConsumerMethod(method, beanDefinition, registry);
            }
        }
    }

    private void addOnEventConsumerMethod(Method method, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final OnEvent onEvent = method.getAnnotation(OnEvent.class);
        String onEventKey = TOPICNAME2 + onEvent.value();
        createOnEventKey(onEventKey, registry);
        // 初始化后再
        // String componentname = getConsumerName(cclass);
        // consumerMethods.add(new ConsumerMethodHolder(componentname, method));
    }

    private void createOnEventKey(String onEventKey, BeanDefinitionRegistry registry) {
        BeanDefinition onEventBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(onEventKey);
        if (!isExist) {
            onEventBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ArrayList.class).getBeanDefinition();
            registry.registerBeanDefinition(onEventKey, onEventBeanDefinition);
        }
    }

    private void createPOJOTargetMetaDef(String pojoMetaDef, BeanDefinitionRegistry registry) {
        BeanDefinition pojoTargetMetaBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(pojoMetaDef);
        if (!isExist) {
            pojoTargetMetaBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(POJOTargetMetaDef.class).getBeanDefinition();
            registry.registerBeanDefinition(pojoMetaDef, pojoTargetMetaBeanDefinition);
        }
    }

    private void createAnnotationHolder(String annotationHolder, BeanDefinitionRegistry registry) {
        BeanDefinition annotationHolderBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(annotationHolder);
        if (!isExist) {
            annotationHolderBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(AnnotationHolder.class).getBeanDefinition();
            registry.registerBeanDefinition(annotationHolder, annotationHolderBeanDefinition);
        }
    }


    private void loadAnnotationModels(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final boolean isModel = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Model");
        if (!isModel) {
            return;
        }

        registerContainerConsumersForModel("modelConsumerMethodHolder",registry);

        final String beanClassName = beanDefinition.getBeanClassName();
        final Class modelClass = Utils.createClass(beanClassName);
        loadOnCommandAnnotations(modelClass, beanDefinition, registry);

    }

    @Deprecated
    private void loadOnCommandAnnotations(Class modelClass, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        for (Method method : ClassUtil.getAllDecaredMethods(modelClass)) {
            if (method.isAnnotationPresent(OnCommand.class)) {
                addOnCommandConsumerMethod(method, beanDefinition, registry);
            }
        }
    }

    private void addOnCommandConsumerMethod(Method method, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final OnCommand onCommand = method.getAnnotation(OnCommand.class);
        String consumerKey = TOPICNAME2 + onCommand.value();
        registerContainerConsumersForModel(consumerKey, registry);//getContainerConsumers(String topicKey, ContainerWrapper containerWrapper)
        // 初始化后再取出modelConsumerMethodHolder 再setConsumerMethodHolder
        // consumerMethods.add(new ConsumerMethodHolder(componentname, method));
    }

    private void registerContainerConsumersForModel(String consumerKey, BeanDefinitionRegistry registry) {
        BeanDefinition modelBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(consumerKey);
        if (!isExist) {
            modelBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ModelConsumerMethodHolder.class).getBeanDefinition();
            registry.registerBeanDefinition(consumerKey, modelBeanDefinition);
        }
    }

    private void loadAnnotationConsumers(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        final boolean isConsumer = ((ScannedGenericBeanDefinition) beanDefinition).getMetadata().hasAnnotation("com.garry.springlifecycle.annotation.Consumer");
        if (!isConsumer) {
            return;
        }
        final String beanClassName = beanDefinition.getBeanClassName();
        createAnnotationConsumerClass(beanClassName, registry);
    }

    private void createAnnotationConsumerClass(String beanClassName, BeanDefinitionRegistry registry) {
        final Class consumerClass = Utils.createClass(beanClassName);
        if (!DomainEventHandler.class.isAssignableFrom(consumerClass)) {
            //that with @Consumer annotataion must also implements  com.jdon.domain.message.DomainEventHandler
            return;
        }
        final Consumer consumer = (Consumer) consumerClass.getAnnotation(Consumer.class);
        String consumerName = UtilValidate.isEmpty(consumer.value()) ? consumerClass.getName() : consumer.value();
        String consumerKey = TOPICNAME + consumerName;
        registerContainerConsumers(consumerKey, registry);//即 getContainerConsumers(topicKey, containerWrapper);
        // 初始化的时候,取出consumers实例，再add aClass.getName()
        // containerWrapper.register(name, cclass); 不需要了，Spring已经执行

    }

    /**
     * 应该返回consumers，但取不出
     *
     * @param topicKey
     * @param registry
     */
    private void registerContainerConsumers(String topicKey, BeanDefinitionRegistry registry) {
        BeanDefinition consumersBeanDefinition = null;
        final boolean isExist = registry.containsBeanDefinition(topicKey);
        if (!isExist) {
            consumersBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(ArrayList.class).getBeanDefinition();
            registry.registerBeanDefinition(topicKey, consumersBeanDefinition);
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition) && (
                beanDefinition.getMetadata().hasAnnotation(Consumer.class.getName())||
                beanDefinition.getMetadata().hasAnnotation(JDComponent.class.getName())||
                beanDefinition.getMetadata().hasAnnotation(Introduce.class.getName())||
                beanDefinition.getMetadata().hasAnnotation(Interceptor.class.getName()));
    }
}
