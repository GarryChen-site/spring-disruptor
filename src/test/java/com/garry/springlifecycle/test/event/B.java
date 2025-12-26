package com.garry.springlifecycle.test.event;


import com.garry.springlifecycle.annotation.JDComponent;
import com.garry.springlifecycle.annotation.model.OnEvent;

@JDComponent("consumer")
public class B {

	private int result;

	@OnEvent("maTest")
	public void mb(TestEvent testEvent) throws Exception {
		setResult(testEvent.getS() + 1);
		testEvent.setResult(testEvent.getS() + 1);
//		while (testEvent.getS() == 99){
//
//		}
		System.out.println("consumer B event.@OnEvent.mb.." + testEvent.getResult() + "### " + "currentThread:" + Thread.currentThread().getName());

//		Assert.assertEquals(testEvent.getResult(), 100);
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
