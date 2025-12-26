package com.garry.springlifecycle.async.disruptor.pool;


import com.garry.springlifecycle.async.disruptor.DomainEventHandlerAdapter;
import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.garry.springlifecycle.domain.message.DomainEventHandler;

public class DomainEventHandlerDecorator extends DomainEventHandlerAdapter {
	private DisruptorSwitcher disruptorSwitcher;

	public DomainEventHandlerDecorator(DomainEventHandler handler) {
		super(handler);
		this.disruptorSwitcher = new DisruptorSwitcher();
	}

	public void onEvent(EventDisruptor event, long sequence, boolean endOfBatch) throws Exception {
		try {
			disruptorSwitcher.setCommandTopic(event.getTopic());
			super.onEvent(event, sequence, endOfBatch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
