package com.garry.springlifecycle.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking classes that intercept method calls using AOP
 * (Aspect-Oriented Programming).
 * <p>
 * There are two modes of operation depending on whether a target is specified:
 * 
 * <h3>Global Interceptor (No Target)</h3>
 * When used without a target value, the interceptor will apply to all
 * components
 * called by the client (e.g., via {@code AppUtil.getService} or
 * {@code WebApp.getService}).
 * 
 * <p>
 * <b>Example:</b>
 * </p>
 * 
 * <pre>
 * &#64;Interceptor("aroundAdvice")
 * public class GlobalMethodInterceptor implements MethodInterceptor {
 * 	// Intercepts all component method calls
 * }
 * </pre>
 * 
 * <h3>Targeted Interceptor</h3>
 * When used with a target value, the interceptor will only apply to the
 * specified
 * component names, not to all components.
 * 
 * <p>
 * <b>Example:</b>
 * </p>
 * 
 * <pre>
 * &#64;Interceptor(name = "myInterceptor", pointcut = "event,c")
 * public class TargetedMethodInterceptor implements MethodInterceptor {
 * 	// Only intercepts method calls for components named "event" and "c"
 * }
 * </pre>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Component
public @interface Interceptor {
	String value() default "";

	String name() default "";

	String pointcut() default "";
}
