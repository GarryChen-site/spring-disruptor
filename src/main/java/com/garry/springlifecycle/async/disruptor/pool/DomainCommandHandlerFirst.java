package com.garry.springlifecycle.async.disruptor.pool;

import com.garry.springlifecycle.async.disruptor.DisruptorForCommandFactory;
import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.lmax.disruptor.EventHandler;

public class DomainCommandHandlerFirst implements EventHandler<EventDisruptor> {

	private DisruptorForCommandFactory disruptorForCommandFactory;
	private DisruptorSwitcher disruptorSwitcher;

	public DomainCommandHandlerFirst(DisruptorForCommandFactory disruptorForCommandFactory) {
		super();
		this.disruptorSwitcher = new DisruptorSwitcher();
		this.disruptorForCommandFactory = disruptorForCommandFactory;
	}

	@Override
	public void onEvent(EventDisruptor event, long arg1, boolean arg2) throws Exception {

	}
}
