/*
 * 
 */
package com.garry.springlifecycle.test.cqrs;


import com.garry.springlifecycle.annotation.model.Owner;
import com.garry.springlifecycle.annotation.model.Receiver;
import com.garry.springlifecycle.annotation.model.Send;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.test.cqrs.a.AggregateRootA;
import com.garry.springlifecycle.test.cqrs.b.AggregateRootB;

public interface AService {

	@Send("CommandtoEventA")
	public DomainMessage commandAandB(@Owner String rootId, @Receiver AggregateRootA model, int state);

	AggregateRootA getAggregateRootA(String id);
	
	AggregateRootB getAggregateRootB(String id);
}
