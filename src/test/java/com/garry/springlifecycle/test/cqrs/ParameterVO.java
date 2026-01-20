/*
 * 
 */
package com.garry.springlifecycle.test.cqrs;

public class ParameterVO {

	private final int id;
	private final int stateValue;
	private final String nextId;

	public ParameterVO(int id, int value, String nextId) {
		super();
		this.id = id;
		this.stateValue = value;
		this.nextId = nextId;
	}

	public int getValue() {
		return stateValue;
	}

	public String getNextId() {
		return nextId;
	}

	public int getId() {
		return id;
	}
	
	

}
