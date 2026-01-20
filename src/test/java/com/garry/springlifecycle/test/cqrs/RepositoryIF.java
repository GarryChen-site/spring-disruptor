/*
 * 
 */
package com.garry.springlifecycle.test.cqrs;


import com.garry.springlifecycle.annotation.pointcut.Around;
import com.garry.springlifecycle.test.cqrs.a.AggregateRootA;
import com.garry.springlifecycle.test.cqrs.b.AggregateRootB;

public interface RepositoryIF {

	@Around
	public abstract AggregateRootA getA(String id);
	
	public int loadSequencId();

	@Around
	public abstract AggregateRootB getB(String id);

}