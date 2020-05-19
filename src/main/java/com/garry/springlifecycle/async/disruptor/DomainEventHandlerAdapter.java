package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.LifecycleAware;

public class DomainEventHandlerAdapter implements EventHandler<EventDisruptor>, LifecycleAware {
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

	@Override
	public void onStart() {
//		if (handler instanceof Startable) {
//			Startable st = (Startable) handler;
//			st.start();
//		}

	}

	@Override
	public void onShutdown() {
//		if (handler instanceof Startable) {
//			Startable st = (Startable) handler;
//			st.stop();
//		}

	}

}
