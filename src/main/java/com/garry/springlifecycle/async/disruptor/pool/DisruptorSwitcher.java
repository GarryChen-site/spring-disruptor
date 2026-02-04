package com.garry.springlifecycle.async.disruptor.pool;

public class DisruptorSwitcher {
	private static ThreadLocal<String> threadCache = new ThreadLocal<>();

	public String getCommandTopic() {
		return threadCache.get();
	}

	public void setCommandTopic(String topic) {
		threadCache.set(topic);
	}

	public void clear() {
		threadCache.remove();
	}

}
