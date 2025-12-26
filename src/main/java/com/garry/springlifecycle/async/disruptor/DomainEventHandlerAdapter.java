package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.lmax.disruptor.EventHandler;

public class DomainEventHandlerAdapter implements EventHandler<EventDisruptor> {
	private DomainEventHandler handler;

	public DomainEventHandlerAdapter(DomainEventHandler handler) {
		super();
		this.handler = handler;
	}

	public void onEvent(EventDisruptor event, long sequence, boolean endOfBatch) throws Exception {
		try {
			handler.onEvent(event, endOfBatch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
