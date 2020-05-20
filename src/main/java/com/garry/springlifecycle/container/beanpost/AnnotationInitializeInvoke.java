package com.garry.springlifecycle.container.beanpost;


import com.garry.springlifecycle.annotation.*;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.annotation.model.OnEvent;
import com.garry.springlifecycle.aop.interceptor.InterceptorsChain;
import com.garry.springlifecycle.businessproxy.TargetMetaDef;
import com.garry.springlifecycle.businessproxy.dyncproxy.cglib.CGLIBDynamicProxyWeaving;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.container.access.TargetMetaRequest;
import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import com.garry.springlifecycle.container.beanpost.aop.interceptor.util.AbstractAdvisor;
import com.garry.springlifecycle.container.beanpost.aop.interceptor.util.AopClient;
import com.garry.springlifecycle.container.beanpost.aop.interceptor.util.MethodInterceptorImpl;
import com.garry.springlifecycle.container.beanpost.aop.interceptor.util.MethodInvocation;
import com.garry.springlifecycle.container.finder.ComponentKeys;
import com.garry.springlifecycle.container.interceptor.IntroduceInfoHolder;
import com.garry.springlifecycle.domain.message.consumer.ConsumerMethodHolder;
import com.garry.springlifecycle.domain.message.consumer.ModelConsumerMethodHolder;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.Debug;
import com.garry.springlifecycle.utils.UtilValidate;
import jdk.nashorn.internal.scripts.JD;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;

// CustomizeAspectProxy
// CGLIBProxyInstanceFactoryVisitable# accept
@Component
public class AnnotationInitializeInvoke implements BeanPostProcessor, ApplicationContextAware {

    private static final String MODEL = AnnotationInitializeInvoke.class.getName();

    private List<AbstractAdvisor> advisors;

    private ApplicationContext applicationContext;

    public static final String TOPICNAME2 = "MEHTOD_TOPIC";


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        applicationContext = (AnnotationConfigApplicationContext) applicationContext;

        if (bean.getClass().isAnnotationPresent(Interceptor.class)) {
            final InterceptorsChain existedInterceptorsChain = (InterceptorsChain) applicationContext.getBean(ComponentKeys.INTERCEPTOR_CHAIN);

            final Class<?> interceptorClass = bean.getClass();
            final Interceptor interceptor = interceptorClass.getAnnotation(Interceptor.class);

            String interceptorName = interceptorClass.getName();
            if (!UtilValidate.isEmpty(interceptor.value())) {
                interceptorName = interceptor.value();
            } else if (!UtilValidate.isEmpty(interceptor.name())) {
                interceptorName = interceptor.name();
            }

            final AnnotationHolder annotationHolder = applicationContext.getBean(AnnotationHolder.class);
            annotationHolder.addComponent(interceptorName, interceptorClass);
            final IntroduceInfoHolder introduceInfoHolder = applicationContext.getBean(IntroduceInfoHolder.class);
            // aop registerAspect
//            if (!(MethodInterceptor.class.isAssignableFrom(interceptorClass))) {
//                continue;
//            }
            if (!UtilValidate.isEmpty(interceptor.pointcut())) {
                final String[] targets = interceptor.pointcut().split(",");
                for (int i = 0; i < targets.length; i++) {
                    final Class targetClass = annotationHolder.getComponentClass(targets[i]);
                    if (targetClass != null) {
                        introduceInfoHolder.addTargetClassNames(targetClass, targets[i]);
                    }
                    // aop registerAspect
                    existedInterceptorsChain.addInterceptor(targets[i], interceptorName);
                }
            } else {
                final List<String> targetNames = introduceInfoHolder.getIntroducerNameByIntroducedName(interceptorName);
                for (String targetName : targetNames) {
                    existedInterceptorsChain.addInterceptor(targetName, interceptorName);
                }
            }
            POJOTargetMetaDef pojoTargetMetaDef = new POJOTargetMetaDef(interceptorName, interceptorClass.getName());
            annotationHolder.getTargetMetaDefHolder().add(interceptorName, pojoTargetMetaDef);
//            return bean;
        }

        if (bean.getClass().isAnnotationPresent(JDComponent.class)) {
            final Class<?> componentClass = bean.getClass();
            final JDComponent component = componentClass.getAnnotation(JDComponent.class);
            //去 NullPointerException
//            if (component == null){
//
//            }
            final String componentName = UtilValidate.isEmpty(component.value()) ? componentClass.getName() : component.value();
            final AnnotationHolder annotationHolder = (AnnotationHolder) applicationContext.getBean("annotationHolder");
            annotationHolder.addComponent(componentName, componentClass);
            final POJOTargetMetaDef pojoTargetMetaDef = new POJOTargetMetaDef(componentName, componentClass.getName());
            annotationHolder.getTargetMetaDefHolder().add(componentName, pojoTargetMetaDef);

            final Method[] allDecaredMethods = ClassUtil.getAllDecaredMethods(componentClass);
            for (Method componentMethod : allDecaredMethods) {
                if (componentMethod.isAnnotationPresent(OnEvent.class)) {
                    addOnEventConsumerMethod(componentMethod, componentClass, applicationContext);
                }
            }
//            return bean;
        }

        if (bean.getClass().isAnnotationPresent(Introduce.class)) {
            final Class<?> introduceClass = bean.getClass();
            final Introduce introduce = introduceClass.getAnnotation(Introduce.class);
            // 去 NullPointerException
//            if (introduce == null) {
//                continue;
//            }

            final String[] adviceNames = introduce.values();
            final IntroduceInfoHolder introduceInfoHolder = (IntroduceInfoHolder) applicationContext.getBean("introduceInfoHolder");
            introduceInfoHolder.addIntroduceInfo(adviceNames, introduceClass);
            final AnnotationHolder annotationHolder = applicationContext.getBean(AnnotationHolder.class);
            final String targetName = annotationHolder.getComponentName(introduceClass);
            introduceInfoHolder.addTargetClassNames(introduceClass, targetName);


            final AopClient aopClient = (AopClient) applicationContext.getBean("aopClient");
            final POJOTargetMetaDef pojoTargetMetaDef = new POJOTargetMetaDef(beanName, bean.getClass().getName());
            final TargetMetaRequest targetMetaRequest = new TargetMetaRequest(pojoTargetMetaDef);

            Enhancer enhancer = new Enhancer();
            enhancer.setCallback(new CGLIBDynamicProxyWeaving(targetMetaRequest, aopClient));
            enhancer.setInterfaces(getInterfaces(targetMetaRequest.getTargetMetaDef()));
            return enhancer.create();
        }


//        final Map<String, Object> modelBeans = applicationContext.getBeansWithAnnotation(Model.class);
//        if (!modelBeans.isEmpty()) {
//            for (Object modelObject : modelBeans.values()) {
//                final Class<?> modelClass = modelObject.getClass();
//                final Method[] allDecaredMethods = ClassUtil.getAllDecaredMethods(modelClass);
//                for (Method modelMethod : allDecaredMethods) {
//                    if (modelMethod.isAnnotationPresent(OnCommand.class)) {
//                        addOnCommandConsumerMethod(modelMethod, modelClass, applicationContext);
//                    }
//                }
//            }
//
//        }



        return bean;
    }

    /**
     * get the interface of target class if it is EJB, it is ejb local/remote
     * interface if it is pojo, it is event class .
     *
     * @param targetMetaDef
     * @return
     */
    protected Class[] getInterfaces(TargetMetaDef targetMetaDef) {
        Class[] interfaces = targetMetaDef.getInterfaces();
        if (interfaces != null)
            return interfaces;
        try {
            interfaces = getPOJOInterfaces(targetMetaDef);
//			Debug.logVerbose("[JdonFramework] getPOJOInterfaces size " + interfaces.length, module);
        } catch (Exception ex) {
            Debug.logError("[JdonFramework] getInterfaces error:" + ex);
        } catch (Throwable ex) {
            Debug.logError("[JdonFramework] getInterfaces error:" + ex);
        }
        if ((interfaces == null) || (interfaces.length == 0)) {
            Debug.logError("[JdonFramework] no find any interface for the service:" + targetMetaDef.getClassName(), MODEL);
        } else {
            targetMetaDef.setInterfaces(interfaces); // cache the result
            Debug.logVerbose("[JdonFramework]found the the below interfaces for the service:" + targetMetaDef.getClassName());
            for (int i = 0; i < interfaces.length; i++) {
                Debug.logVerbose(interfaces[i].getName() + ";", MODEL);
            }
        }
        return interfaces;
    }

    public Class[] getPOJOInterfaces(TargetMetaDef targetMetaDef) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class pojoClass = null;
        try {
            pojoClass = classLoader.loadClass(targetMetaDef.getClassName());
        } catch (ClassNotFoundException e) {
            Debug.logError("[JdonFramework] getPOJOInterfaces error:" + e);
        }
        return ClassUtil.getParentAllInterfaces(pojoClass);
    }

    private void addOnEventConsumerMethod(Method componentMethod, Class<?> componentClass, ApplicationContext
            applicationContext) {
        final OnEvent onEvent = componentMethod.getAnnotation(OnEvent.class);
        String consumerKey = TOPICNAME2 + onEvent.value();

        final ArrayList consumerMethods = (ArrayList) applicationContext.getBean(consumerKey);
        String componentName = getConsumerName(componentClass);
        consumerMethods.add(new ConsumerMethodHolder(componentName, componentMethod));
    }

    private String getConsumerName(Class<?> componentClass) {
        String name = "";
        if (componentClass.isAnnotationPresent(JDComponent.class)) {
            final JDComponent jdComponent = componentClass.getAnnotation(JDComponent.class);
            name = UtilValidate.isEmpty(jdComponent.value()) ? componentClass.getName() : jdComponent.value();
        } else {
            name = componentClass.getName();
        }
        return null;
    }

    private void addOnCommandConsumerMethod(Method modelMethod, Class<?> modelClass, ApplicationContext
            applicationContext) {
        final OnCommand onCommand = modelMethod.getAnnotation(OnCommand.class);
        String consumerKey = TOPICNAME2 + onCommand.value();

        final ModelConsumerMethodHolder modelConsumerMethodHolder = (ModelConsumerMethodHolder) applicationContext.getBean("modelConsumerMethodHolder");
        modelConsumerMethodHolder.setConsumerMethodHolder(new ConsumerMethodHolder(modelClass.getName(), modelMethod));
    }

    private Map<Method, List<AbstractAdvisor>> matchAdvisor(Object bean) {
        final Class<?> beanClass = bean.getClass();
        final Method[] methods = beanClass.getMethods();
        if (methods == null) {
            return Collections.emptyMap();
        }
        Map<Method, List<AbstractAdvisor>> methodListMap = new HashMap<>();
        for (Method method : methods) {
            for (AbstractAdvisor abstractAdvisor : advisors) {
                if (!abstractAdvisor.isMatch(bean.getClass(), method)) {
                    continue;
                }
                List<AbstractAdvisor> advisorList = methodListMap.get(method);
                if (advisorList == null) {
                    advisorList = new LinkedList<>();
                    methodListMap.put(method, advisorList);
                }
                advisorList.add(abstractAdvisor);
            }
        }
        return methodListMap;
    }

    private void buildAdvisor() {
        if (advisors != null) {
            return;
        }
        synchronized (this) {
            if (advisors != null) {
                return;
            }
            final String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
            advisors = new ArrayList<>();
            for (String beanName : beanDefinitionNames) {
                final Class<?> beanClass = applicationContext.getType(beanName);
                Interceptor interceptor = beanClass.getAnnotation(Interceptor.class);
                if (interceptor == null) {
                    continue;
                }
                Method[] methods = beanClass.getDeclaredMethods();
                if (methods == null) {
                    continue;
                }
                Object bean = applicationContext.getBean(beanName);
                List<AbstractAdvisor> beanAdvisorList = new ArrayList<AbstractAdvisor>(methods.length);
                for (Method method : methods) {
                    if (method.getName().equals("before")) {
                        beanAdvisorList.add(new MethodInvocation.BeforeAdvisor(bean, method));
                    } else if (method.getName().equals("around")) {
                        beanAdvisorList.add(new MethodInvocation.AroundAdvisor(bean, method));
                    } else if (method.getName().equals("after")) {
                        beanAdvisorList.add(new MethodInvocation.AfterAdvisor(bean, method));
                    }
                }
                advisors.addAll(beanAdvisorList);
            }
            Collections.sort(advisors, new Comparator<AbstractAdvisor>() {
                public int compare(AbstractAdvisor o1, AbstractAdvisor o2) {
                    return o1.getOrder() - o2.getOrder();
                }
            });
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
