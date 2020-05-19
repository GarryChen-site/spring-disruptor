
package com.garry.springlifecycle.test.consumer;


import com.garry.springlifecycle.annotation.Consumer;
import com.garry.springlifecycle.annotation.model.OnCommand;
import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.garry.springlifecycle.domain.message.DomainEventHandler;

@Consumer("saveMyModel")
public class RepositoryListener implements DomainEventHandler {


	public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
		System.out.println(" No.1 @OnEvent:" + this.getClass().getName());
	}

	@OnCommand("transfer")
	public String sayHello(){
		System.out.println("******************** wow");
		return "yeah!";
	}

}
