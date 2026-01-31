package com.garry.springlifecycle.annotation.model;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for marking methods that send domain messages/events.
 * <p>
 * Domain Models should normally live in memory, not in a database, so caching
 * in memory
 * is very important for the domain model life cycle.
 * 
 * <h3>Usage Example</h3>
 * 
 * <h4>Step 1: Annotate the Producer Class</h4>
 * 
 * <pre>
 * &#64;Model
 * &#64;Introduce("message")
 * public class DomainEvent {
 * 	// Domain event implementation
 * }
 * </pre>
 * <p>
 * The value "message" in {@code &#64;Introduce("message")} refers to the
 * {@code MessageInterceptor} configured in aspect.xml
 * 
 * <h4>Step 2: Annotate the Producer Method</h4>
 * 
 * <pre>
 * &#64;Send("mytopic")
 * public DomainMessage myMethod() {
 * 	DomainMessage em = new DomainMessage(this.name);
 * 	return em;
 * }
 * </pre>
 * 
 * <h4>Step 3: Topic Matching</h4>
 * The "mytopic" value in {@code &#64;Send("mytopic")} must match the value in
 * {@code &#64;Consumer("mytopic")}
 * 
 * <h4>Step 4 &amp; 5: Configure the Consumer</h4>
 * There are two ways to create a consumer:
 * 
 * <p>
 * <b>Option 1: Implement DomainEventHandler</b>
 * </p>
 * 
 * <pre>
 * &#64;Consumer("mytopic")
 * public class MyDomainEventHandler implements DomainEventHandler {
 * 	public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
 * 		// Handle event
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * <b>Option 2: Use &#64;OnEvent Annotation</b>
 * </p>
 * 
 * <pre>
 * &#64;OnEvent("mytopic")
 * public void handleEvent(EventDisruptor event) {
 * 	// Handle event
 * }
 * </pre>
 * 
 * <h3>Message Patterns</h3>
 * 
 * <p>
 * <b>Topic/Queue (1:N or 1:1):</b>
 * </p>
 * {@code &#64;Send(topicName)} =&gt; {@code &#64;Consumer(topicName)}
 * 
 * <p>
 * <b>Legacy Queue (1:1) - version 6.3 and below:</b>
 * </p>
 * {@code &#64;Send(topicName)} =&gt; {@code &#64;Component(topicName)}
 * <p>
 * The message accepter class annotated with {@code &#64;Component(topicName)}
 * must implement
 * {@code com.garry.domain.message.MessageListener}
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Send {
	/**
	 * topic/queue name
	 *
	 * <br>
	 * @Send(topicName) ==> @Consumer(topicName);
	 * 
	 * @return topic/queue name
	 */
	String value();

	boolean asyn() default true;
}
