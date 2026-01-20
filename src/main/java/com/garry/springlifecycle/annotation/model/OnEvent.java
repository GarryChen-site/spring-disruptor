package com.garry.springlifecycle.annotation.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks event method as event handler for event domain event sent by @Send;
 * 
 * it can be used together with in those classes with @Component or @Service;
 * 
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface OnEvent {
	String value();
}
