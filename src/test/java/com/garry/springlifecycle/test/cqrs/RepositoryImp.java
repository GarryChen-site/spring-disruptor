/*
 * 
 */
package com.garry.springlifecycle.test.cqrs;



import com.garry.springlifecycle.annotation.Introduce;
import com.garry.springlifecycle.annotation.pointcut.Around;
import com.garry.springlifecycle.test.cqrs.a.AggregateRootA;
import com.garry.springlifecycle.test.cqrs.b.AggregateRootB;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component()
//@Introduce("modelCache")
public class RepositoryImp implements RepositoryIF {
	private final AtomicInteger sequenceId = new AtomicInteger(0); 

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.sample.test.cqrs.ABRepositoryIF#loadA(java.lang.String)
	 */
	@Around
	public AggregateRootA getA(String id) {
		AggregateRootA model = new AggregateRootA(id);		
		return model;

	}
	
	public int loadSequencId(){
		return sequenceId.incrementAndGet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jdon.sample.test.cqrs.ABRepositoryIF#loadB(java.lang.String)
	 */
	@Around
	public AggregateRootB getB(String id) {
		return new AggregateRootB(id);

	}

}
