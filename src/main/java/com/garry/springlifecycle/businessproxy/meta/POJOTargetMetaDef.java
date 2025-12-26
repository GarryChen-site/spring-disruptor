package com.garry.springlifecycle.businessproxy.meta;


import com.garry.springlifecycle.businessproxy.target.POJOObjectFactory;
import com.garry.springlifecycle.businessproxy.target.TargetObjectFactory;
import org.springframework.stereotype.Component;

/**
 * POJO Service Meta Definition
 * 
 * @author <event href="mailto:banqiao@jdon.com">banq</event>
 * 
 */
public class POJOTargetMetaDef extends AbstractTargetMetaDef {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5220467463142903890L;
	private String className;
	private String name;
	private String[] constructors;
	protected POJOObjectFactory pOJOObjectFactory;

	public POJOTargetMetaDef() {
	}

	public POJOTargetMetaDef(String name, String className) {
		this.name = name;
		this.className = className;
	}

	public POJOTargetMetaDef(String name, String className, String[] constructors) {
		this.name = name;
		this.className = className;
		this.constructors = constructors;
	}

	public boolean isEJB() {
		return false;
	}

	public String getClassName() {
		return this.className;
	}

	public String getCacheKey() {
		return this.className;
	}

	public String[] getConstructors() {
		return constructors;
	}

	public void setConstructors(String[] constructors) {
		this.constructors = constructors;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public TargetObjectFactory getTargetObjectFactory() {
		if (pOJOObjectFactory == null)
			this.init();
		return pOJOObjectFactory;
	}

	protected void init() {
		pOJOObjectFactory = new POJOObjectFactory(this);
	}

}
