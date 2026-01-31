package com.garry.springlifecycle.annotation.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Domain Model should normal live in memory not in database. so cache in memory
 * is very important for domain model life cycle.
 * 
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface Inject {
	String value() default "";
}
