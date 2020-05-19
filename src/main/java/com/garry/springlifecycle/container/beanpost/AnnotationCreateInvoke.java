package com.garry.springlifecycle.container.beanpost;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AnnotationCreateInvoke implements BeanFactoryPostProcessor, ApplicationContextAware {

    private AnnotationConfigApplicationContext annotationConfigApplicationContext;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final Scanner scanner = new Scanner((BeanDefinitionRegistry) beanFactory);
        scanner.setResourceLoader(this.annotationConfigApplicationContext);
//        final String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
//        Arrays.stream(beanDefinitionNames)
//                .forEach(name -> System.out.println(name));
//        System.out.println("****************Create " + beanDefinitionNames[beanDefinitionNames.length - 1]);
        scanner.scan("com.garry.springlifecycle");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.annotationConfigApplicationContext = (AnnotationConfigApplicationContext) applicationContext;
    }
}
