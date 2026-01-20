/***
 * 
 *
 * 
 */
package com.garry.springlifecycle.fsm.tools;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ReflectionUtilsTest {
	
	@Target({ElementType.FIELD, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@interface Bar {
		
		public String value() default "";
		
	}	
	
	@Test
	public void testFieldFromGetter() {
		
		class Foo {

			String foo = "boo";

			@Bar
			public String getFoo() {
				return foo;
			}

			@SuppressWarnings("unused")
			public void setFoo(String foo) {
				this.foo = foo;
			}
		}
		
		Field foo = ReflectionUtils.getReferencedField(Foo.class, Bar.class);
		assertNotNull(foo);
		assertEquals("foo", foo.getName());
	}
}
