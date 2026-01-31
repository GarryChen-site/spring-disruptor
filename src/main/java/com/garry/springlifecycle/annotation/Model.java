package com.garry.springlifecycle.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Domain Model should normal live in memory not in database.
 * So cache in memory is very important for domain model life cycle.
 * 
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component
public @interface Model {

	/**
	 * disable from version 6.5
	 * 
	 * @return
	 */
	boolean isCacheable() default true;

	/**
	 * disable from version 6.5
	 * 
	 * @return
	 */
	boolean isModified() default false;
}
