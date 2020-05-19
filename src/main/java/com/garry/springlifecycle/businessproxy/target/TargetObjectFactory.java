package com.garry.springlifecycle.businessproxy.target;


import org.springframework.context.ApplicationContext;

public interface TargetObjectFactory {

	Object create(ApplicationContext applicationContext) throws Exception;

}