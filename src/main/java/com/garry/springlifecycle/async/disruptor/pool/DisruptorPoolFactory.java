package com.garry.springlifecycle.async.disruptor.pool;


import com.garry.springlifecycle.async.disruptor.DisruptorFactory;
import com.garry.springlifecycle.utils.Debug;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DisruptorPoolFactory  {
	public final static String module = DisruptorPoolFactory.class.getName();

	private DisruptorSwitcher disruptorSwitcher;
	private DisruptorFactory disruptorFactory;
	private ConcurrentHashMap<String, Disruptor> topicDisruptors;
	private ScheduledExecutorService scheduExecStatic = Executors.newScheduledThreadPool(1);

	public DisruptorPoolFactory() {
		super();
		this.disruptorSwitcher = new DisruptorSwitcher();
		this.topicDisruptors = new ConcurrentHashMap();
	}

	public void start() {
		Runnable task = new Runnable() {
			public void run() {
				stopDisruptor();
			}
		};
		scheduExecStatic.scheduleAtFixedRate(task, 60 * 60, 60 * 60, TimeUnit.SECONDS);
	}

	public void stop() {
		if (topicDisruptors != null) {
			stopDisruptor();
			topicDisruptors.clear();
			topicDisruptors = null;
		}

		disruptorFactory = null;
		scheduExecStatic.shutdownNow();
	}

	private void stopDisruptor() {
		Map<String, Disruptor> mydisruptors = new HashMap(topicDisruptors);
		topicDisruptors.clear();
		try {
			Thread.sleep(10000);// wait event while until all disruptor is done;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		for (String topic : mydisruptors.keySet()) {
			Disruptor disruptor = (Disruptor) mydisruptors.get(topic);
			try {
				disruptor.halt();
			} catch (Exception e) {
			}
		}
		mydisruptors.clear();

	}

	public Disruptor createAutoDisruptor(String topic) {
		if (disruptorSwitcher.getCommandTopic() != null) {
			return disruptorFactory.createSingleDisruptor(topic);
		} else
			return disruptorFactory.createDisruptor(topic);

	}

	public Disruptor getDisruptor(String topic) {
		Disruptor disruptor = (Disruptor) topicDisruptors.get(topic);
		if (disruptor == null) {
			disruptor = createAutoDisruptor(topic);
			if (disruptor == null) {
				Debug.logWarning("not create disruptor for " + topic, module);
				return null;
			}
			Disruptor disruptorOLd = topicDisruptors.putIfAbsent(topic, disruptor);
			if (disruptorOLd != null)
				disruptor = disruptorOLd;
		}
		return disruptor;
	}

	public DisruptorFactory getDisruptorFactory() {
		return disruptorFactory;
	}

	public void setDisruptorFactory(DisruptorFactory disruptorFactory) {
		this.disruptorFactory = disruptorFactory;
	}

}
