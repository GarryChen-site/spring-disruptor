package com.garry.springlifecycle.businessproxy.meta;


import com.garry.springlifecycle.businessproxy.TargetMetaDef;

/**
 * TargetMetaDef abstract concrete class
 * implements some common functions for pojo or ejb
 * 
 *
 */
public abstract class AbstractTargetMetaDef implements TargetMetaDef, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4114446785637199234L;
	/**
	 * 
	 */
	private Class[] interfaces;
	

	public Class[] getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(Class[] interfaces) {
		this.interfaces = interfaces;
	}
	

    
    
}
