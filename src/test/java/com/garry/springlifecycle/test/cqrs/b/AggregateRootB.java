/*
 * 
 */
package com.garry.springlifecycle.test.cqrs.b;



import com.garry.springlifecycle.annotation.Model;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.test.cqrs.ParameterVO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

//@Model
public class AggregateRootB {
	private String id;
	
	private final AtomicInteger state = new AtomicInteger(200);
	private final Map<Integer, Integer> states = new ConcurrentHashMap(); 

	public AggregateRootB(String id) {
		super();
		this.id = id;
	}

	@OnCommand("CommandToB")
	public Object save(ParameterVO parameterVO) {
		int newstate = state.addAndGet(parameterVO.getValue()); 
		System.out.print("\n AggregateRootB Action " + newstate);
		states.put(parameterVO.getId(), newstate);
		ParameterVO parameterVOnew = new ParameterVO(parameterVO.getId(),newstate,parameterVO.getNextId());
		return parameterVOnew;

	}
	
	public int getState(int id){
		return states.get(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getState() {
		return state.get();
	}
	
	
}
