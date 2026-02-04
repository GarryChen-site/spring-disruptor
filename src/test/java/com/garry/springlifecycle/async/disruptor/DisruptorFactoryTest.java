/*
 * 
 */
package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DisruptorFactoryTest {
	DisruptorFactory disruptorFactory;

	@BeforeEach
	protected void setUp() throws Exception {
		disruptorFactory = new DisruptorFactory();
	}

	@AfterEach
	protected void tearDown() throws Exception {
	}

	@Test
	public void testGetDisruptor() {
		TreeSet<DomainEventHandler<EventDisruptor>> handlers = disruptorFactory.getTreeSet();
		final DomainEventHandler<EventDisruptor> handler = new DomainEventHandler<EventDisruptor>() {
			@Override
			public void onEvent(EventDisruptor event, final boolean endOfBatch) throws Exception {
				System.out.println("MyEventA=" + event.getDomainMessage().getEventSource());
				event.getDomainMessage().setEventResult("not null");

			}
		};

		final DomainEventHandler<EventDisruptor> handler2 = new DomainEventHandler<EventDisruptor>() {
			@Override
			public void onEvent(EventDisruptor event, final boolean endOfBatch) throws Exception {
				System.out.println("MyEventA2=" + event.getDomainMessage().getEventSource());
				event.getDomainMessage().setEventResult(null);

			}
		};
		handlers.add(handler2);
		handlers.add(handler);

		Disruptor disruptor = disruptorFactory.createSingleDw("test");
		disruptorFactory.addEventMessageHandler(disruptor, "test", handlers);
		disruptor.start();

		int i = 0;

		// while (i < 10) {
		RingBuffer ringBuffer = disruptor.getRingBuffer();
		long sequence = ringBuffer.next();

		DomainMessage domainMessage = new DomainMessage(sequence);

		EventDisruptor eventDisruptor = (EventDisruptor) ringBuffer.get(sequence);
		eventDisruptor.setTopic("test");
		eventDisruptor.setDomainMessage(domainMessage);

		ringBuffer.publish(sequence);
		System.out.print("\n RESULT=" + domainMessage.getBlockEventResult());

		System.out.print("\n RESULT=" + domainMessage.getBlockEventResult());

		System.out.print("\n RESULT=" + domainMessage.getBlockEventResult());

		i++;
		System.out.print(i);

		// }

		System.out.print("ok");
	}

	@Test
	public void testValueEventProcessor() throws AlertException, InterruptedException, TimeoutException {
		RingBuffer ringBuffer = RingBuffer.createSingleProducer(new EventResultFactory(), 4,
				new TimeoutBlockingWaitStrategy(10000,
						TimeUnit.MILLISECONDS));
		ValueEventProcessor valueEventProcessor = new ValueEventProcessor(ringBuffer);

		int numMessages = ringBuffer.getBufferSize();
		int offset = 0;
		for (int i = 0; i < numMessages + offset; i++) {
			valueEventProcessor.send(i);
			System.out.print("\n push=" + i);
		}

		long expectedSequence = numMessages + offset - 1;
		SequenceBarrier barrier = ringBuffer.newBarrier();
		long available = barrier.waitFor(expectedSequence);
		assertEquals(expectedSequence, available);
		System.out.print("\n expectedSequence=" + expectedSequence);

		for (int i = 0; i < numMessages + offset; i++) {
			System.out.print("\n i=" + ((EventResultDisruptor) ringBuffer.get(i)).getValue() + " " + i);
		}
	}

}
