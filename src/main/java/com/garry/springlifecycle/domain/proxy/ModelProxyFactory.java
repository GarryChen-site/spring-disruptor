package com.garry.springlifecycle.domain.proxy;


import com.garry.springlifecycle.utils.Debug;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;

/**
 * This class is used to create the proxy for models,it uses cglib to create
 * model proxy.
 * 
 * for example :
 * 
 * @Introduce("message")
 * public class MyModelEvent{
 * 
 * @Send(value="MyModel.findName",asyn=true)
 * public DomainMessage
 *                                           asyncFindName(MyModel myModel) {
 *                                           return new DomainMessage(myModel);
 *                                           } }
 * 
 *                                           For the above class,the
 *                                           MessageInterceptor named "message"
 *                                           will be apply to MyModelEvent,when
 *                                           the asyncFindName method is
 *                                           invoked,MessageInterceptor will
 *                                           intercept this invocation,and send
 *                                           the DomainMessage to the Listener
 *                                           named "MyModel.findName".
 * 
 * @author xmuzyu banq
 * 
 */
@Component
public class ModelProxyFactory {
	private final static String module = ModelProxyFactory.class.getName();

	public ModelProxyFactory() {
		super();
	}

	public Object create(final Class modelClass, final MethodInterceptor methodInterceptor) {

		Object dynamicProxy = null;
		try {
			Enhancer enhancer = new Enhancer();
			enhancer.setCallback(methodInterceptor);
			enhancer.setSuperclass(modelClass);
			dynamicProxy = enhancer.create();
		} catch (Exception e) {
			Debug.logError("create error " + e, module);
		}
		return dynamicProxy;
	}

}
