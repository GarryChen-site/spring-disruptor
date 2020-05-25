package com.garry.springlifecycle.container.beanpost;

import com.garry.springlifecycle.businessproxy.TargetMetaDef;
import com.garry.springlifecycle.businessproxy.dyncproxy.cglib.CGLIBDynamicProxyWeaving;
import com.garry.springlifecycle.businessproxy.meta.POJOTargetMetaDef;
import com.garry.springlifecycle.container.access.TargetMetaDefHolder;
import com.garry.springlifecycle.container.access.TargetMetaRequest;
import com.garry.springlifecycle.container.beanpost.aop.interceptor.util.AopClient;
import com.garry.springlifecycle.utils.ClassUtil;
import com.garry.springlifecycle.utils.Debug;
import net.sf.cglib.proxy.Enhancer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//@Component
public class IntroduceFactoryBean<T> implements InitializingBean,FactoryBean<T>, ApplicationContextAware {

    private static final String MODEL = IntroduceFactoryBean.class.getName();

    private String innerClassName;

    private ApplicationContext applicationContext;

    public void setInnerClassName(String innerClassName) {
        this.innerClassName = innerClassName;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public T getObject() throws Exception {
//        final TargetMetaDefHolder targetMetaDefHolder = (TargetMetaDefHolder)applicationContext.getBean("targetMetaDefHolder");

//        final TargetMetaDef targetMetaDef = targetMetaDefHolder.getTargetMetaDef("producer");
        final POJOTargetMetaDef targetMetaDef = new POJOTargetMetaDef("producer",innerClassName);
        final Class<?> aClass = Class.forName(innerClassName);
        final Class[] allInterfaces = ClassUtil.getAllInterfaces(aClass);
        targetMetaDef.setInterfaces(allInterfaces);

        TargetMetaRequest targetMetaRequest = new TargetMetaRequest(targetMetaDef);

        final AopClient aopClient = (AopClient) applicationContext.getBean("aopClient");
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new CGLIBDynamicProxyWeaving(targetMetaRequest, aopClient));
//        enhancer.setInterfaces(getInterfaces(targetMetaRequest.getTargetMetaDef()));
        final Class[] interfaces = getInterfaces(targetMetaDef);
        enhancer.setInterfaces(interfaces);
        return (T)enhancer.create();
    }

    @Override
    public Class<?> getObjectType() {
//        try {
//            return Class.forName(innerClassName);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    protected Class[] getInterfaces(TargetMetaDef targetMetaDef) {
        Class[] interfaces = targetMetaDef.getInterfaces();
        if (interfaces != null)
            return interfaces;
        try {
            interfaces = getPOJOInterfaces(targetMetaDef);
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
}
