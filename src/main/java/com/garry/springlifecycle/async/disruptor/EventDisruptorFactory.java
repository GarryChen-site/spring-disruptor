package com.garry.springlifecycle.async.disruptor;

import com.lmax.disruptor.EventFactory;

public class EventDisruptorFactory implements EventFactory<EventDisruptor> {

	// create event Event;
	public EventDisruptor newInstance() {
		return new EventDisruptor();
	}
}
