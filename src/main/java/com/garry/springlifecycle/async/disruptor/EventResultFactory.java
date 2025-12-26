package com.garry.springlifecycle.async.disruptor;

import com.lmax.disruptor.EventFactory;

public class EventResultFactory implements EventFactory {

	public EventResultDisruptor newInstance() {
		return new EventResultDisruptor();
	}
}
