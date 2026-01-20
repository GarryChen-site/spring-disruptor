package com.garry.springlifecycle.async.disruptor;

import org.springframework.stereotype.Component;

/**
 * Example Consumer using @OnEvent method annotation
 * 
 * This demonstrates the alternative @OnEvent pattern where a method
 * is annotated instead of implementing the DomainEventHandler interface.
 * 
 * The framework will automatically discover this method and create
 * a DomainEventDispatchHandler to invoke it.
 */
@Component
public class OrderNotificationService {

    private volatile int notificationsSent = 0;

    /**
     * This method will be called when a message is published to "orderCreated"
     * topic.
     * The framework automatically extracts the Order object from DomainMessage
     * and passes it as a parameter.
     */
    // Note: @OnEvent annotation would be used here, but it's commented out
    // because we need the annotation scanning to be properly configured
    // @OnEvent("orderCreated")
    public String sendNotification(Order order) {
        System.out.println("[OrderNotificationService] Sending notification for order: "
                + order.getOrderId() + " to customer: " + order.getCustomerName());

        notificationsSent++;

        // Return value is automatically set as the event result
        return "Notification sent to " + order.getCustomerName();
    }

    public int getNotificationsSent() {
        return notificationsSent;
    }

    public void reset() {
        notificationsSent = 0;
    }
}
