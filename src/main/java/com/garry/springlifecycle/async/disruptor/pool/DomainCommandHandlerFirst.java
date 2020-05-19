package com.garry.springlifecycle.async.disruptor.pool;


import com.garry.springlifecycle.async.disruptor.DisruptorForCommandFactory;
import com.garry.springlifecycle.async.disruptor.EventDisruptor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;

public class DomainCommandHandlerFirst implements EventHandler<EventDisruptor>, LifecycleAware {

	private DisruptorForCommandFactory disruptorForCommandFactory;
	private DisruptorSwitcher disruptorSwitcher;

	public DomainCommandHandlerFirst(DisruptorForCommandFactory disruptorForCommandFactory) {
		super();
		this.disruptorSwitcher = new DisruptorSwitcher();
		this.disruptorForCommandFactory = disruptorForCommandFactory;
	}

	@Override
	public void onShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEvent(EventDisruptor event, long arg1, boolean arg2) throws Exception {

	}
}
