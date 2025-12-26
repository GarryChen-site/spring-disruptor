package com.garry.springlifecycle;

import com.garry.springlifecycle.async.disruptor.pool.Student;
import com.garry.springlifecycle.test.event.AI;
import com.garry.springlifecycle.test.event.B;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class SpringLifecycleApplicationTests {

	@Autowired
	Student student;

	@Autowired
	ApplicationContext applicationContext;

	@Test
	void contextLoads() {
	}

	@Test
	public void student_say_hello() {
		final String wow = student.sayHello("wow");
		assertEquals("wow", wow);
	}

	@Test
	public void test_applicationContext_say_hello() {
		final Student student = (Student) applicationContext.getBean("student");
		assertEquals(student.sayHello("qwe"), "qwe");
	}

	@Test
	public void test_applicationContext_Component_event() {
		System.out.println("currentThread:" + Thread.currentThread().getName());
		final AI a = (AI) applicationContext.getBean("producer");
		a.sayHello("123");
		a.ma();

		B bean = applicationContext.getBean(B.class);
		System.out.println(bean.getResult());
	}
}
