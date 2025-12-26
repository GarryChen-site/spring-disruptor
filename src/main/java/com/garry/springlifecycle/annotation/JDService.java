package com.garry.springlifecycle.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * the @Service(="abc")
 * is equals to:
 * {@code
 *   <Service name="abc" class="com.sample.Abc" />
 * }
 * Service'name is called by the client.
 * 
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component
public @interface JDService {
	String value();
}
