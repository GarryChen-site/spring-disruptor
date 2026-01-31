package com.garry.springlifecycle.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking classes as singleton components in the container.
 * <p>
 * This annotation is equivalent to the XML configuration:
 * 
 * <pre>{@code
 * <component name="abc" class="com.sample.Abc" />
 * }</pre>
 * 
 * <h3>Component Naming</h3>
 * The component's name will default to the fully qualified class name (obtained
 * via {@code getClass().getName()}).
 * 
 * <h3>Usage Notes</h3>
 * <ul>
 * <li>Singleton components are not directly exposed to clients by default, so
 * no explicit name is required.</li>
 * <li>If you need to access the component from client code, use the fully
 * qualified class name.</li>
 * </ul>
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Singleton {
}
