package com.garry.springlifecycle.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component
//@Component  不要是因为需要拦截注解获取annotation信息
public @interface Consumer {
    /**
     * topic name
     * @return
     */
    String value();
}
