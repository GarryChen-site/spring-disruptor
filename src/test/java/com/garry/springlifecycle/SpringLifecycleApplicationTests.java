package com.garry.springlifecycle;

import com.garry.springlifecycle.async.disruptor.pool.Student;
import com.garry.springlifecycle.test.event.AI;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
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
	public void student_say_hello(){
		final String wow = student.sayHello("wow");
		Assert.assertEquals("wow",wow);
	}

	@Test
	public void test_applicationContext_say_hello(){
		final Student student = (Student) applicationContext.getBean("student");
		Assert.assertEquals(student.sayHello("qwe"),"qwe");
	}

	@Test
	public void test_applicationContext_Component_event(){
		final AI a = (AI) applicationContext.getBean("producer");
//		a.sayHello("123");
		a.ma();
	}
}
