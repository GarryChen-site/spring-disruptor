package com.garry.springlifecycle.test.consumer;


import com.garry.springlifecycle.annotation.Consumer;
import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.garry.springlifecycle.domain.message.DomainEventHandler;

@Consumer("mychannel")
public class CDomainEventHandler implements DomainEventHandler {
	public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
		System.out.println("CDomainEventHandler Action" + event.getDomainMessage().getEventSource());
		event.getDomainMessage().setEventResult("hello-1");
	}
}
