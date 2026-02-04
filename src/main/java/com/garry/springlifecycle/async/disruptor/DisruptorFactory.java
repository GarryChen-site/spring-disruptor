package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.async.disruptor.pool.DisruptorPoolFactory;
import com.garry.springlifecycle.container.beanpost.AfterAllInitializing;
import com.garry.springlifecycle.domain.message.DomainEventDispatchHandler;
import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.consumer.ConsumerMethodHolder;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * SLEEPING is event better option when you have event large number of event
 * processors and you need throughput when you don't mind event 1ms latency hit
 * in the worse case.
 * 
 * BLOCKING has the lowest throughput of all the strategies but it does not have
 * the 1ms latency spikes of SLEEPING. It uses no CPU when idle but it does not
 * scale up so well with increasing numbers of event processors because of the
 * contention on the lock. YIELDING and BUSY_SPIN have the best performance for
 * both throughput and latency but eat up event CPU.
 * 
 * YIELDING is more friendly in allowing other threads to run when cores are
 * limited.
 * It would be nice if Java had access to the x86 PAUSE instruction to save
 * power and further reduce latency that gets lost due to the wrong choices the
 * CPU can make with speculative execution of busy spin loops.
 * 
 * In all cases where you have sufficient cores then all the wait strategies
 * will beat pretty much any other alternative such as queues.
 * 
 * 
 */
@Component
public class DisruptorFactory implements ApplicationContextAware {
	public final static String module = DisruptorFactory.class.getName();
	protected final ConcurrentHashMap<String, TreeSet<DomainEventHandler<EventDisruptor>>> handlesMap;

	private int ringBufferSize;

	private ApplicationContext applicationContext;

	private DisruptorPoolFactory disruptorPoolFactory;

	public DisruptorFactory(DisruptorParams disruptorParams,
			DisruptorPoolFactory disruptorPoolFactory) {
		this.ringBufferSize = disruptorParams.getRingBufferSize();
		this.handlesMap = new ConcurrentHashMap<String, TreeSet<DomainEventHandler<EventDisruptor>>>();
		this.disruptorPoolFactory = disruptorPoolFactory;
		this.disruptorPoolFactory.setDisruptorFactory(this);

	}

	public DisruptorFactory() {
		this.ringBufferSize = 8;
		this.handlesMap = new ConcurrentHashMap<String, TreeSet<DomainEventHandler<EventDisruptor>>>();
		this.disruptorPoolFactory = new DisruptorPoolFactory();
		this.disruptorPoolFactory.setDisruptorFactory(this);
	}

	public Disruptor<EventDisruptor> createDw(String topic) {
		int size = ringBufferSize;
		return new Disruptor<>(new EventDisruptorFactory(), size, Executors.defaultThreadFactory());
	}

	public Disruptor<EventDisruptor> createSingleDw(String topic) {
		int size = ringBufferSize;
		WaitStrategy waitStrategy = new BlockingWaitStrategy();
		return new Disruptor<>(new EventDisruptorFactory(), size, Executors.defaultThreadFactory(), ProducerType.SINGLE,
				waitStrategy);
	}

	public Disruptor<EventDisruptor> addEventMessageHandler(Disruptor<EventDisruptor> dw, String topic,
			TreeSet<DomainEventHandler<EventDisruptor>> handlers) {
		if (handlers.size() == 0)
			return null;
		EventHandlerGroup<EventDisruptor> eh = null;
		for (DomainEventHandler<EventDisruptor> handler : handlers) {
			DomainEventHandlerAdapter dea = new DomainEventHandlerAdapter(handler);
			if (eh == null) {
				eh = dw.handleEventsWith(dea);
			} else {
				eh = eh.handleEventsWith(dea);
			}
		}
		return dw;
	}

	public Disruptor<EventDisruptor> getDisruptor(String topic) {
		return this.disruptorPoolFactory.getDisruptor(topic);
	}

	public void releaseDisruptor(Object owner) {

	}

	/**
	 * one topic one EventDisruptor
	 * 
	 * @param topic
	 * @return Disruptor
	 */
	public Disruptor<EventDisruptor> createDisruptor(String topic) {
		TreeSet<DomainEventHandler<EventDisruptor>> handlers = getHandles(topic);
		if (handlers == null)
			return null;

		Disruptor<EventDisruptor> dw = createDw(topic);
		Disruptor<EventDisruptor> disruptor = addEventMessageHandler(dw, topic, handlers);
		if (disruptor == null)
			return null;
		disruptor.start();
		return disruptor;
	}

	/**
	 * single producer :single consumer
	 * 
	 * no lock
	 * 
	 * @param topic 主题
	 * @return Disruptor
	 */
	public Disruptor<EventDisruptor> createSingleDisruptor(String topic) {
		TreeSet<DomainEventHandler<EventDisruptor>> handlers = getHandles(topic);
		if (handlers == null)
			return null;
		Disruptor<EventDisruptor> dw = createSingleDw(topic);
		Disruptor<EventDisruptor> disruptor = addEventMessageHandler(dw, topic, handlers);
		if (disruptor == null)
			return null;
		disruptor.start();
		return disruptor;
	}

	private TreeSet<DomainEventHandler<EventDisruptor>> getHandles(String topic) {
		TreeSet<DomainEventHandler<EventDisruptor>> handlersExist = handlesMap.get(topic);
		TreeSet<DomainEventHandler<EventDisruptor>> handlersNew = null;
		if (handlersExist == null)// not inited
		{
			handlersNew = getTreeSet();
			handlersNew.addAll(loadEvenHandler(topic));
			handlersNew.addAll(loadOnEventConsumers(topic));
			if (handlersNew.size() == 0) {
				// maybe by mistake in @Component(topicName)
				return null;
			}
			handlersExist = handlesMap.putIfAbsent(topic, handlersNew);
		}
		return handlersExist != null ? handlersExist : handlersNew;
	}

	public boolean isContain(String topic) {
		boolean isExist = applicationContext.containsBean(AfterAllInitializing.CONSUMER_TOPIC_NAME + topic);
		boolean isMethodExist = applicationContext
				.containsBean(AfterAllInitializing.CONSUMER_TOPIC_NAME_METHOD + topic);
		if ((!isExist) && (!isMethodExist)) {
			return false;
		} else
			return true;

	}

	/**
	 * if there are many consumers, execution order will be alphabetical list by
	 * Name of @Consumer class.
	 * 
	 * @param topic
	 * @return Collection
	 */
	protected Collection<DomainEventHandler<EventDisruptor>> loadEvenHandler(String topic) {
		Collection<DomainEventHandler<EventDisruptor>> ehs = new ArrayList<>();
		boolean isExist = applicationContext.containsBean(AfterAllInitializing.CONSUMER_TOPIC_NAME + topic);
		if (!isExist) {
			return ehs;
		}
		@SuppressWarnings("unchecked")
		Collection<String> consumers = (Collection<String>) applicationContext
				.getBean(AfterAllInitializing.CONSUMER_TOPIC_NAME + topic);
		if (consumers.size() == 0) {
			return ehs;
		}
		for (String consumerName : consumers) {
			DomainEventHandler<EventDisruptor> eh = applicationContext.getBean(consumerName, DomainEventHandler.class);
			ehs.add(eh);
		}

		return ehs;

	}

	protected Collection<DomainEventHandler<EventDisruptor>> loadOnEventConsumers(String topic) {
		Collection<DomainEventHandler<EventDisruptor>> ehs = new ArrayList<>();
		final boolean isExist = applicationContext
				.containsBean(AfterAllInitializing.CONSUMER_TOPIC_NAME_METHOD + topic);
		if (!isExist) {
			return ehs;
		}
		Collection<?> consumerMethods = (Collection<?>) applicationContext
				.getBean(AfterAllInitializing.CONSUMER_TOPIC_NAME_METHOD + topic);
		for (Object o : consumerMethods) {
			ConsumerMethodHolder consumerMethodHolder = (ConsumerMethodHolder) o;
			DomainEventDispatchHandler domainEventDispatchHandler = new DomainEventDispatchHandler(consumerMethodHolder,
					applicationContext);
			ehs.add(domainEventDispatchHandler);
		}
		return ehs;

	}

	public TreeSet<DomainEventHandler<EventDisruptor>> getTreeSet() {
		return new TreeSet<>(new Comparator<DomainEventHandler<EventDisruptor>>() {
			public int compare(DomainEventHandler<EventDisruptor> num1, DomainEventHandler<EventDisruptor> num2) {
				String inum1, inum2;
				if (num1 instanceof DomainEventDispatchHandler) {
					inum1 = ((DomainEventDispatchHandler) num1).getSortName();
				} else {
					inum1 = num1.getClass().getName();
				}
				if (num2 instanceof DomainEventDispatchHandler) {
					inum2 = ((DomainEventDispatchHandler) num2).getSortName();
				} else {
					inum2 = num2.getClass().getName();
				}
				if (inum1.compareTo(inum2) < 1) {
					return -1; // returning the first object
				} else {

					return 1;
				}
			}

		});
	}

	public void start() {
		// TODO Auto-generated method stub

	}

	public void stop() {
		// this.containerWrapper = null;
		this.handlesMap.clear();
		this.ringBufferSize = 0;

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
