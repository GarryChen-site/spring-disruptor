package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.async.disruptor.pool.DisruptorCommandPoolFactory;
import com.garry.springlifecycle.async.disruptor.pool.DomainCommandHandlerFirst;
import com.garry.springlifecycle.async.disruptor.pool.DomainEventHandlerDecorator;
import com.garry.springlifecycle.container.beanpost.AfterAllInitializing;
import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.consumer.DomainCommandDispatchHandler;
import com.garry.springlifecycle.domain.message.consumer.ModelConsumerMethodHolder;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DisruptorForCommandFactory implements ApplicationContextAware {
    public final static String module = DisruptorForCommandFactory.class.getName();
    protected final Map<String, TreeSet<DomainEventHandler<?>>> handlesMap;

    private ApplicationContext applicationContext;

    private DisruptorCommandPoolFactory disruptorCommandPoolFactory;

    private DisruptorFactory disruptorFactory;

    public DisruptorForCommandFactory(DisruptorParams disruptorParams,
            DisruptorCommandPoolFactory disruptorCommandPoolFactory,
            DisruptorFactory disruptorFactory) {
        this.handlesMap = new ConcurrentHashMap<String, TreeSet<DomainEventHandler<?>>>();
        this.disruptorCommandPoolFactory = disruptorCommandPoolFactory;
        this.disruptorCommandPoolFactory.setDisruptorForCommandFactory(this);
        this.disruptorFactory = disruptorFactory;
    }

    public Disruptor<EventDisruptor> getDisruptor(String topic, Object target) {
        return this.disruptorCommandPoolFactory.getDisruptor(topic, target);
    }

    public void releaseDisruptor(Object owner) {

    }

    private Disruptor<EventDisruptor> createDw(String topic) {
        return disruptorFactory.createDw(topic);
    }

    private Disruptor<EventDisruptor> createDisruptorWithEventHandler(String topic) {
        TreeSet<DomainEventHandler<?>> handlers = handlesMap.get(topic);
        if (handlers == null)// not inited
        {
            handlers = this.getTreeSet();
            handlers = loadOnCommandConsumers(topic, handlers);
            handlesMap.put(topic, handlers);
        }
        if (handlers.isEmpty())
            return null;

        Disruptor<EventDisruptor> dw = createDw(topic);
        EventHandlerGroup<EventDisruptor> eh = dw.handleEventsWith(new DomainCommandHandlerFirst(this));

        for (DomainEventHandler<?> handler : handlers) {
            DomainEventHandlerAdapter dea = new DomainEventHandlerDecorator(handler);
            eh = eh.handleEventsWith(dea);
        }
        return dw;
    }

    /**
     * one event one EventDisruptor
     *
     * @param topic
     * @return
     */
    public Disruptor<EventDisruptor> createDisruptor(String topic) {

        Disruptor<EventDisruptor> disruptor = createDisruptorWithEventHandler(topic);
        if (disruptor != null)
            disruptor.start();
        return disruptor;
    }

    public boolean isContain(String topic) {

        boolean isExist = applicationContext.containsBean(AfterAllInitializing.MODEL_TOPIC_NAME_METHOD + topic);

        return isExist;
        // if
        // (applicationContext.containsBean(AfterAllInitializing.MODEL_TOPIC_NAME_METHOD
        // + topic) == null) {
        // return false;
        // } else
        // return true;
    }

    public ModelConsumerMethodHolder getModelConsumerMethodHolder(String topic) {
        return (ModelConsumerMethodHolder) applicationContext
                .getBean(AfterAllInitializing.MODEL_TOPIC_NAME_METHOD + topic);
    }

    protected TreeSet<DomainEventHandler<?>> loadOnCommandConsumers(String topic, TreeSet<DomainEventHandler<?>> ehs) {
        ModelConsumerMethodHolder modelConsumerMethodHolder = getModelConsumerMethodHolder(topic);
        if (modelConsumerMethodHolder == null)
            return ehs;
        DomainCommandDispatchHandler domainCommandDispatchHandler = new DomainCommandDispatchHandler(
                modelConsumerMethodHolder);
        ehs.add(domainCommandDispatchHandler);
        return ehs;

    }

    // @Override
    // public void start() {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void stop() {
    // this.containerWrapper = null;
    // this.handlesMap.clear();
    //
    // }

    public TreeSet<DomainEventHandler<?>> getTreeSet() {
        return new TreeSet<>(new Comparator<DomainEventHandler<?>>() {
            public int compare(DomainEventHandler<?> num1, DomainEventHandler<?> num2) {
                String inum1, inum2;
                inum1 = num1.getClass().getName();
                inum2 = num2.getClass().getName();
                if (inum1.compareTo(inum2) < 1) {
                    return -1; // returning the first object
                } else {

                    return 1;
                }
            }

        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
