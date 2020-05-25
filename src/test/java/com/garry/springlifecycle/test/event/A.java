package com.garry.springlifecycle.test.event;


import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.model.Send;

@JDComponent("producer")
@Introduce(values = "componentmessage")
public class A implements AI {


	// @Send("maTest")
	// see AI.ma()
	@Send("maTest")
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
