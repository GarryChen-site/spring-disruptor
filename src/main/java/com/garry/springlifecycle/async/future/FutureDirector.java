package com.garry.springlifecycle.async.future;


import com.garry.springlifecycle.domain.message.DomainMessage;
import org.springframework.stereotype.Component;

@Component
public class FutureDirector {

	private ChannelExecutor channelExecutor;

	public FutureDirector() {
		channelExecutor = new ChannelExecutor("50");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jdon.async.message.MessageMediator#sendMessage(com.jdon.async.message
	 * .EventMessage)
	 */
	public void fire(DomainMessage domainMessage) {
		channelExecutor.actionListener(domainMessage);
	}

	public void stop() {
		channelExecutor.stop();

	}

}
