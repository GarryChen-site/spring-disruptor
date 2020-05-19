package com.garry.springlifecycle.test.event;


import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.model.OnEvent;

@JDComponent("consumer")
public class B {

	@OnEvent("maTest")
	public void mb(TestEvent testEvent) throws Exception {
		testEvent.setResult(testEvent.getS() + 1);
		System.out.println("consumer B event.@OnEvent.mb.." + testEvent.getResult() + "\n");
//		Assert.assertEquals(testEvent.getResult(), 100);
	}
}
