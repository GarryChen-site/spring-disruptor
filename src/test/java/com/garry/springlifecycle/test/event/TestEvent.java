package com.garry.springlifecycle.test.event;

public class TestEvent {

	private final int s;

	private int result;

	public TestEvent(int s) {
		super();
		this.s = s;
	}

	public int getS() {
		return s;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

}
