package com.garry.springlifecycle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ComponentScan("com.garry.springlifecycle")
public class AnnotationApplicationContext {

    public static void main(String[] args) {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AnnotationApplicationContext.class);
        applicationContext.refresh();
        final String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        System.out.println("***************Finish " + beanDefinitionNames[beanDefinitionNames.length - 1]);
        Arrays.stream(beanDefinitionNames)
                .forEach(name -> System.out.println(name));
    }
}
