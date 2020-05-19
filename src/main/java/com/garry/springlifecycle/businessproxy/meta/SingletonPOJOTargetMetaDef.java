package com.garry.springlifecycle.businessproxy.meta;


import com.garry.springlifecycle.businessproxy.target.SingletonPOJOObjectFactory;
import com.garry.springlifecycle.businessproxy.target.TargetObjectFactory;

public class SingletonPOJOTargetMetaDef extends POJOTargetMetaDef {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5892020922243280686L;

	public SingletonPOJOTargetMetaDef(String name, String className) {
		super(name, className);
	}

	public SingletonPOJOTargetMetaDef(String name, String className, String[] constructors) {
		super(name, className, constructors);
	}

	protected void init() {
		pOJOObjectFactory = new SingletonPOJOObjectFactory(this);
	}

	public TargetObjectFactory getTargetObjectFactory() {
		if (pOJOObjectFactory == null)
			this.init();
		return pOJOObjectFactory;
	}
}
