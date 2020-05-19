package com.garry.springlifecycle.businessproxy.meta;

public class MethodMetaArgs implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1085038049278832959L;
	private final String methodName;
	private final Class[] paramTypes;
	private final Object[] args;

	public MethodMetaArgs(String methodName, Class[] paramTypes, Object[] args) {
		this.methodName = methodName;
		this.paramTypes = paramTypes;
		this.args = args;
	}

	public String getMethodName() {

		return methodName;
	}

	public Class[] getParamTypes() {

		return paramTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(methodName);
		int length = paramTypes.length;
		for (int i = 0; i < length; i++) {
			sb.append(paramTypes[i]).append(args[i]);

		}
		return sb.toString();
	}

}
