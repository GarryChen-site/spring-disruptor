package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.async.EventResultHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;

import java.util.concurrent.TimeUnit;

public class EventResultHandlerImp implements EventResultHandler {

	// MILLISECONDS default is one seconds
	protected ValueEventProcessor valueEventProcessor;

	public EventResultHandlerImp(int timeoutforeturnResult) {
		super();
		RingBuffer ringBuffer = RingBuffer.createSingleProducer(new EventResultFactory(), 1,
				new TimeoutBlockingWaitStrategy(timeoutforeturnResult,
				TimeUnit.MILLISECONDS));
		this.valueEventProcessor = new ValueEventProcessor(ringBuffer);

	}

	/**
	 * send event result
	 * 
	 */
	public void send(Object result) {
		valueEventProcessor.send(result);
	}

	public Object get() {
		Object result = null;
		EventResultDisruptor ve = valueEventProcessor.waitFor();
		if (ve != null) {
			result = ve.getValue();
			ve.clear();
			// clear();
		}
		return result;

	}

	public Object getBlockedValue() {
		Object result = null;
		EventResultDisruptor ve = valueEventProcessor.waitForBlocking();
		if (ve != null) {
			result = ve.getValue();
			ve.clear();
			// clear();
		}
		return result;
	}

	public void clear() {
		valueEventProcessor.clear();
	}

	/**
	 * deprecated since 6.6.8
	 */
	public void setWaitforTimeout(int timeoutforeturnResult) {
		System.err.print("deprecated since 6.6.8");
	}

}
