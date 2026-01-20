/*
 * 
 */
package com.garry.springlifecycle.test.cqrs.a;



import com.garry.springlifecycle.annotation.Model;
import com.garry.springlifecycle.annotation.model.Inject;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.test.cqrs.ParameterVO;

import java.util.concurrent.atomic.AtomicInteger;

//@Model
public class AggregateRootA {
	private String id;

	private String aggregateRootBId;

	private final AtomicInteger state = new AtomicInteger(100);

	@Inject
	private DomainEventProduceIF domainEventProducer;

	public AggregateRootA(String id) {
		super();
		this.id = id;
	}

	@OnCommand("CommandtoEventA")
	public Object save(ParameterVO parameterVO) {
	    //以单线程方式更新状态
		int newstate = state.addAndGet(parameterVO.getValue());		
		System.out.print("\n AggregateRootA Action " + newstate);
		ParameterVO parameterVONew = new ParameterVO(parameterVO.getId(), newstate, parameterVO.getNextId());
		//一个reactive事件产生
		return domainEventProducer.sendtoAnotherAggragate(parameterVONew);

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAggregateRootBId() {
		return aggregateRootBId;
	}

	public void setAggregateRootBId(String aggregateRootBId) {
		this.aggregateRootBId = aggregateRootBId;
	}
	
	public int getState() {
		return state.get();
	}


}
