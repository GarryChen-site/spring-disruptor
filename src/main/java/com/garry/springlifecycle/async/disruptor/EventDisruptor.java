package com.garry.springlifecycle.async.disruptor;


import com.garry.springlifecycle.domain.message.DomainMessage;

/**
 * A subscriber send event EventDisruptor to RingBuffer.
 * 
 * Consumers will get the EventDisruptor with its OnEvent method from RingBuffer
 * 
 * 
 */
public class EventDisruptor {

	protected String topic;

	protected DomainMessage domainMessage;

	public EventDisruptor() {

	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public DomainMessage getDomainMessage() {
		return domainMessage;
	}

	public void setDomainMessage(DomainMessage domainMessage) {
		this.domainMessage = domainMessage;
	}

	public String getTopic() {
		return topic;
	}

}
