package com.garry.springlifecycle.container.beanpost;

import com.garry.springlifecycle.container.annotation.AnnotationHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class AnnotationCreateInvoke implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;



    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final Scanner scanner = new Scanner((DefaultListableBeanFactory) beanFactory);
        scanner.setResourceLoader(this.applicationContext);
        scanner.scan("com.garry.springlifecycle");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
