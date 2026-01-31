package com.garry.springlifecycle.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking service classes that are accessible by client code.
 * <p>
 * This annotation is equivalent to the XML configuration:
 * 
 * <pre>{@code
 * <service name="abc" class="com.sample.Abc" />
 * }</pre>
 * 
 * <h3>Service Naming</h3>
 * The service name (specified via {@code value()}) is used by clients to look
 * up and invoke the service.
 * Unlike {@code @Singleton}, services are explicitly exposed to client code.
 * 
 * <h3>Example</h3>
 * 
 * <pre>
 * &#64;JDService("myService")
 * public class MyServiceImpl {
 * 	// Service implementation
 * }
 * </pre>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component
public @interface JDService {
	String value();
}
