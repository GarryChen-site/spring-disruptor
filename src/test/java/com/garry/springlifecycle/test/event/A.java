package com.garry.springlifecycle.test.event;


import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.JDComponent;

@JDComponent("producer")
@Introduce(values = "componentmessage")
public class A implements AI {

	public A() {
		System.out.println("A 启动咯");
	}

	// @Send("maTest")
	// see AI.ma()
	public TestEvent ma() {
		System.out.println("producer A event.send.ma..");
		return new TestEvent(99);
	}

	@Override
	public String sayHello(String name) {
		System.out.println(name);
		return name;
	}
}
