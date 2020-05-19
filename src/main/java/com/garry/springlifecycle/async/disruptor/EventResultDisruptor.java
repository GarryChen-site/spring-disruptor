package com.garry.springlifecycle.async.disruptor;

/**
 * 
 * A Consumer send response back to the Subscriber by this value object
 * 
 * @author banq
 * 
 */
public class EventResultDisruptor {

	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	public void clear() {
		value = null;
	}

}
