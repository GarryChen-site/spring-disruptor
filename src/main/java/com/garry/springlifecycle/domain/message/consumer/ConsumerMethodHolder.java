package com.garry.springlifecycle.domain.message.consumer;

import java.lang.reflect.Method;

public class ConsumerMethodHolder {

	private String className;
	private Method method;

	public ConsumerMethodHolder(String className, Method method) {
		super();
		this.className = className;
		this.method = method;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
