package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.annotation.Consumer;
import com.garry.springlifecycle.domain.message.DomainEventHandler;
import org.springframework.stereotype.Component;

/**
 * Example Consumer: Handles order creation events
 * 
 * This demonstrates the @Consumer annotation pattern where a class
 * implements DomainEventHandler to receive messages from a topic.
 * 
 * Pattern: @Send("orderCreated") -> @Consumer("orderCreated")
 */
@Consumer("orderCreated")
@Component
public class OrderEventConsumer implements DomainEventHandler<EventDisruptor> {

    private volatile String lastProcessedOrder;
    private volatile int processedCount = 0;

    @Override
    public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
        // Extract the order from the domain message
        Order order = (Order) event.getDomainMessage().getEventSource();

        System.out.println("[OrderEventConsumer] Processing order: " + order.getOrderId()
                + ", Customer: " + order.getCustomerName()
                + ", Amount: $" + order.getAmount());

        // Simulate order processing
        Thread.sleep(10);

        lastProcessedOrder = order.getOrderId();
        processedCount++;

        // Set result back to the message (for synchronous result retrieval)
        String result = "Order " + order.getOrderId() + " processed successfully";
        event.getDomainMessage().setEventResult(result);

        System.out.println("[OrderEventConsumer] Completed: " + result);
    }

    public String getLastProcessedOrder() {
        return lastProcessedOrder;
    }

    public int getProcessedCount() {
        return processedCount;
    }

    public void reset() {
        lastProcessedOrder = null;
        processedCount = 0;
    }
}
