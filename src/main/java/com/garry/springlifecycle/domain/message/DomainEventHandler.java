
package com.garry.springlifecycle.domain.message;

import com.garry.springlifecycle.async.disruptor.EventDisruptor;

/**
 * This is event Disruptor EvenHandler.
 * 
 * if event class annotated with @Consumer(XX); it must implements
 * com.jdon.domain.message.DomainEventHandler
 * 
 * 
 * * Domain Model producer /Consumer:
 * 
 * 1. annotate the producer class with @Model and @Introduce("message")
 * 
 * 
 * 2. annotate the method with @Send("mytopic") of the producer class;
 * 
 * 3. the "mytopic" value in @Send("mytopic") is equals to the "mytopic" value
 * in @Consumer("mytopic");
 * 
 * 4. annotate the consumer class with @Consumer("mytopic");
 * 
 * 5. the consumer class must implements
 * com.jdon.domain.message.DomainEventHandler
 * 
 * 
 * @param <EventDisruptor>
 */
public interface DomainEventHandler<T> {

	void onEvent(EventDisruptor event, final boolean endOfBatch) throws Exception;
}
