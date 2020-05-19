package com.garry.springlifecycle.aop.interceptor;

public class Advisor {

	private String adviceName;
	private String pointcutName;

	public Advisor(String adviceName, String pointcutName) {
		super();
		this.adviceName = adviceName;
		this.pointcutName = pointcutName;
	}

	public String getAdviceName() {
		return adviceName;
	}

	public void setAdviceName(String adviceName) {
		this.adviceName = adviceName;
	}

	public String getPointcutName() {
		return pointcutName;
	}

	public void setPointcutName(String pointcutName) {
		this.pointcutName = pointcutName;
	}

}
