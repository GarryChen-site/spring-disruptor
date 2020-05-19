package com.garry.springlifecycle.async.future;



import com.garry.springlifecycle.domain.message.DomainMessage;
import com.garry.springlifecycle.utils.Debug;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChannelExecutor {
	private final static String module = ChannelExecutor.class.getName();
	private ExecutorService executor;

	public ChannelExecutor(String maxconcurrentTaskCount) {
		executor = Executors.newCachedThreadPool();
	}

	public void actionListener(DomainMessage domainMessage) {
		EventResultFuture eventMessageFuture = (EventResultFuture) domainMessage.getEventResultHandler();
		try {
			if (eventMessageFuture.getMessageListener() == null) {
				return;
			}
			if (eventMessageFuture.isAsyn()) {
				executor.execute(eventMessageFuture.getFutureTask());
			} else {
				eventMessageFuture.getFutureTask().run();
			}
		} catch (Exception e) {
			Debug.logError("[JdonFramework]actionChannelListener() error" + e, module);
		}
	}

	public void stop() {
		while (!executor.isShutdown()) {
			try {
				executor.shutdownNow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		executor = null;
	}

}
