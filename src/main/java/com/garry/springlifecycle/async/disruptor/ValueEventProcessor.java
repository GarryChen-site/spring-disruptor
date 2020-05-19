package com.garry.springlifecycle.async.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;

public class ValueEventProcessor {

	protected final RingBuffer<EventResultDisruptor> ringBuffer;

	private long waitAtSequence = 0;

	public ValueEventProcessor(RingBuffer<EventResultDisruptor> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	public void send(Object result) {

		waitAtSequence = ringBuffer.next();
		EventResultDisruptor ve = ringBuffer.get(waitAtSequence);
		ve.setValue(result);
		ringBuffer.publish(waitAtSequence);
	}

	public EventResultDisruptor waitFor() {
		SequenceBarrier barrier = ringBuffer.newBarrier();
		try {
			long a = barrier.waitFor(waitAtSequence);
			if (ringBuffer != null)
				return ringBuffer.get(a);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			barrier.alert();
		}
		return null;
	}

	/**
	 * not really block, the waiting time is longer than not block.
	 */
	public EventResultDisruptor waitForBlocking() {
		SequenceBarrier barrier = ringBuffer.newBarrier();
		try {
			long a = barrier.waitFor(waitAtSequence);
			if (ringBuffer != null)
				return ringBuffer.get(a);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			barrier.alert();
		}
		return null;
	}

	public long getWaitAtSequence() {
		return waitAtSequence;
	}

	public void setWaitAtSequence(long waitAtSequence) {
		this.waitAtSequence = waitAtSequence;
	}

	public void clear() {

	}

	public RingBuffer<EventResultDisruptor> getRingBuffer() {
		return ringBuffer;
	}

}
