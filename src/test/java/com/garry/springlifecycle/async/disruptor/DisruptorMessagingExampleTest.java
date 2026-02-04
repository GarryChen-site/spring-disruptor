package com.garry.springlifecycle.async.disruptor;

import com.garry.springlifecycle.domain.message.DomainEventHandler;
import com.garry.springlifecycle.domain.message.DomainMessage;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive example demonstrating the core idea of the framework:
 * 
 * Using LMAX Disruptor for high-performance, asynchronous, topic-based
 * messaging between domain models with annotation-driven configuration.
 * 
 * This example shows:
 * 1. Creating a Disruptor for a specific topic ("orderCreated")
 * 2. Registering multiple consumers for the same topic
 * 3. Publishing messages through the Disruptor ring buffer
 * 4. Consumers processing events asynchronously
 * 5. Retrieving results from consumers (blocking and non-blocking)
 * 
 * Key Benefits:
 * - Ultra-low latency (microseconds vs milliseconds for traditional queues)
 * - Lock-free, high-throughput message passing
 * - Multiple consumers per topic (fan-out pattern)
 * - Deterministic ordering (consumers sorted by class name)
 * - Async result retrieval with timeout support
 */
public class DisruptorMessagingExampleTest {

    private DisruptorFactory disruptorFactory;
    private OrderEventConsumer orderConsumer;
    private Disruptor<EventDisruptor> disruptor;

    @BeforeEach
    public void setUp() {
        disruptorFactory = new DisruptorFactory();
        orderConsumer = new OrderEventConsumer();
    }

    @AfterEach
    public void tearDown() {
        if (disruptor != null) {
            disruptor.shutdown();
        }
    }

    /**
     * Example 1: Basic Producer-Consumer Pattern
     * 
     * Demonstrates:
     * - Creating a Disruptor for a topic
     * - Registering a consumer
     * - Publishing a message
     * - Consumer processing the event
     * - Retrieving the result
     */
    @Test
    public void testBasicProducerConsumer() throws Exception {
        System.out.println("\n=== Example 1: Basic Producer-Consumer ===\n");

        // 1. Create a TreeSet of handlers for the topic
        TreeSet<DomainEventHandler<EventDisruptor>> handlers = disruptorFactory.getTreeSet();
        handlers.add(orderConsumer);

        // 2. Create a Disruptor for the "orderCreated" topic
        disruptor = disruptorFactory.createSingleDw("orderCreated");
        disruptorFactory.addEventMessageHandler(disruptor, "orderCreated", handlers);
        disruptor.start();

        // 3. Create an order (this would normally come from @Send method)
        Order order = new Order("ORD-001", "John Doe", 299.99);
        DomainMessage domainMessage = new DomainMessage(order);

        // 4. Publish the message to the Disruptor
        RingBuffer<EventDisruptor> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();

        EventDisruptor eventDisruptor = ringBuffer.get(sequence);
        eventDisruptor.setTopic("orderCreated");
        eventDisruptor.setDomainMessage(domainMessage);

        ringBuffer.publish(sequence);

        // 5. Retrieve the result (blocking until consumer processes it)
        String result = (String) domainMessage.getBlockEventResult();

        System.out.println("\n[Test] Result received: " + result);

        // Assertions
        assertNotNull(result);
        assertTrue(result.contains("ORD-001"));
        assertTrue(result.contains("processed successfully"));
        assertEquals("ORD-001", orderConsumer.getLastProcessedOrder());
        assertEquals(1, orderConsumer.getProcessedCount());
    }

    /**
     * Example 2: Multiple Consumers (Fan-out Pattern)
     * 
     * Demonstrates:
     * - Multiple consumers listening to the same topic
     * - All consumers receive the same message
     * - Consumers execute in sequence (sorted by class name)
     */
    @Test
    public void testMultipleConsumers() throws Exception {
        System.out.println("\n=== Example 2: Multiple Consumers (Fan-out) ===\n");

        // Create multiple consumers
        OrderEventConsumer consumer1 = new OrderEventConsumer();

        // Second consumer that validates the order
        DomainEventHandler<EventDisruptor> validationConsumer = new DomainEventHandler<EventDisruptor>() {
            @Override
            public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
                Order order = (Order) event.getDomainMessage().getEventSource();
                System.out.println("[ValidationConsumer] Validating order: " + order.getOrderId());

                // Validate order amount
                if (order.getAmount() > 0) {
                    order.setStatus("VALIDATED");
                    System.out.println("[ValidationConsumer] Order validated successfully");
                }
            }
        };

        // Third consumer that logs the order
        DomainEventHandler<EventDisruptor> loggingConsumer = new DomainEventHandler<EventDisruptor>() {
            @Override
            public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
                Order order = (Order) event.getDomainMessage().getEventSource();
                System.out.println("[LoggingConsumer] Logging order: " + order);
            }
        };

        // Register all consumers
        TreeSet<DomainEventHandler<EventDisruptor>> handlers = disruptorFactory.getTreeSet();
        handlers.add(consumer1);
        handlers.add(validationConsumer);
        handlers.add(loggingConsumer);

        // Create and start Disruptor
        disruptor = disruptorFactory.createSingleDw("orderCreated");
        disruptorFactory.addEventMessageHandler(disruptor, "orderCreated", handlers);
        disruptor.start();

        // Publish message
        Order order = new Order("ORD-002", "Jane Smith", 599.99);
        DomainMessage domainMessage = new DomainMessage(order);

        RingBuffer<EventDisruptor> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();

        EventDisruptor eventDisruptor = ringBuffer.get(sequence);
        eventDisruptor.setTopic("orderCreated");
        eventDisruptor.setDomainMessage(domainMessage);

        ringBuffer.publish(sequence);

        // Wait for processing
        Thread.sleep(100);

        System.out.println("\n[Test] All consumers processed the message");

        // Verify all consumers processed the message
        assertEquals("ORD-002", consumer1.getLastProcessedOrder());
        assertEquals("VALIDATED", order.getStatus());
    }

    /**
     * Example 3: High-Throughput Batch Processing
     * 
     * Demonstrates:
     * - Publishing multiple messages rapidly
     * - Disruptor's high-throughput capabilities
     * - Batch processing with endOfBatch flag
     */
    @Test
    public void testHighThroughputBatchProcessing() throws Exception {
        System.out.println("\n=== Example 3: High-Throughput Batch Processing ===\n");

        // Consumer that tracks batch processing
        final int[] batchCount = { 0 };
        final int[] eventCount = { 0 };

        DomainEventHandler<EventDisruptor> batchConsumer = new DomainEventHandler<EventDisruptor>() {
            @Override
            public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
                Order order = (Order) event.getDomainMessage().getEventSource();
                eventCount[0]++;

                if (endOfBatch) {
                    batchCount[0]++;
                    System.out.println("[BatchConsumer] End of batch #" + batchCount[0]
                            + ", processed " + eventCount[0] + " events so far");
                }
            }
        };

        TreeSet<DomainEventHandler<EventDisruptor>> handlers = disruptorFactory.getTreeSet();
        handlers.add(batchConsumer);

        disruptor = disruptorFactory.createSingleDw("orderCreated");
        disruptorFactory.addEventMessageHandler(disruptor, "orderCreated", handlers);
        disruptor.start();

        RingBuffer<EventDisruptor> ringBuffer = disruptor.getRingBuffer();

        // Publish 100 messages rapidly
        long startTime = System.nanoTime();
        int messageCount = 100;

        for (int i = 0; i < messageCount; i++) {
            Order order = new Order("ORD-" + String.format("%03d", i),
                    "Customer-" + i, 100.0 + i);
            DomainMessage domainMessage = new DomainMessage(order);

            long sequence = ringBuffer.next();
            EventDisruptor eventDisruptor = ringBuffer.get(sequence);
            eventDisruptor.setTopic("orderCreated");
            eventDisruptor.setDomainMessage(domainMessage);
            ringBuffer.publish(sequence);
        }

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;

        // Wait for all messages to be processed
        Thread.sleep(500);

        System.out.println("\n[Test] Published " + messageCount + " messages in "
                + String.format("%.2f", durationMs) + " ms");
        System.out.println("[Test] Throughput: "
                + String.format("%.0f", messageCount / (durationMs / 1000.0)) + " messages/sec");
        System.out.println("[Test] Average latency: "
                + String.format("%.3f", durationMs / messageCount) + " ms/message");

        assertEquals(messageCount, eventCount[0]);
        assertTrue(batchCount[0] > 0, "Should have processed at least one batch");
    }

    /**
     * Example 4: Async Result Retrieval with Timeout
     * 
     * Demonstrates:
     * - Non-blocking result retrieval
     * - Timeout handling
     * - Async processing patterns
     */
    @Test
    public void testAsyncResultRetrieval() throws Exception {
        System.out.println("\n=== Example 4: Async Result Retrieval ===\n");

        // Consumer with simulated delay
        DomainEventHandler<EventDisruptor> slowConsumer = new DomainEventHandler<EventDisruptor>() {
            @Override
            public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
                Order order = (Order) event.getDomainMessage().getEventSource();
                System.out.println("[SlowConsumer] Processing order: " + order.getOrderId());

                // Simulate slow processing
                Thread.sleep(200);

                event.getDomainMessage().setEventResult("Processed after delay: " + order.getOrderId());
            }
        };

        TreeSet<DomainEventHandler<EventDisruptor>> handlers = disruptorFactory.getTreeSet();
        handlers.add(slowConsumer);

        disruptor = disruptorFactory.createSingleDw("orderCreated");
        disruptorFactory.addEventMessageHandler(disruptor, "orderCreated", handlers);
        disruptor.start();

        // Publish message
        Order order = new Order("ORD-ASYNC", "Async Customer", 999.99);
        DomainMessage domainMessage = new DomainMessage(order, 5000); // 5 second timeout

        RingBuffer<EventDisruptor> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();

        EventDisruptor eventDisruptor = ringBuffer.get(sequence);
        eventDisruptor.setTopic("orderCreated");
        eventDisruptor.setDomainMessage(domainMessage);

        ringBuffer.publish(sequence);

        System.out.println("[Test] Message published, doing other work...");

        // Do other work while message is being processed
        Thread.sleep(50);
        System.out.println("[Test] Still doing work...");

        // Now retrieve the result (will block until available or timeout)
        String result = (String) domainMessage.getEventResult();

        System.out.println("[Test] Result received: " + result);

        assertNotNull(result);
        assertTrue(result.contains("ORD-ASYNC"));
    }

    /**
     * Example 5: Error Handling and Resilience
     * 
     * Demonstrates:
     * - Exception handling in consumers
     * - Continued processing after errors
     * - Isolation between consumers
     */
    @Test
    public void testErrorHandling() throws Exception {
        System.out.println("\n=== Example 5: Error Handling ===\n");

        // Consumer that throws an exception
        DomainEventHandler<EventDisruptor> faultyConsumer = new DomainEventHandler<EventDisruptor>() {
            @Override
            public void onEvent(EventDisruptor event, boolean endOfBatch) throws Exception {
                System.out.println("[FaultyConsumer] Throwing exception!");
                throw new RuntimeException("Simulated error");
            }
        };

        // Consumer that should still work
        OrderEventConsumer reliableConsumer = new OrderEventConsumer();

        TreeSet<DomainEventHandler<EventDisruptor>> handlers = disruptorFactory.getTreeSet();
        handlers.add(faultyConsumer);
        handlers.add(reliableConsumer);

        disruptor = disruptorFactory.createSingleDw("orderCreated");
        disruptorFactory.addEventMessageHandler(disruptor, "orderCreated", handlers);
        disruptor.start();

        // Publish message
        Order order = new Order("ORD-ERROR", "Error Test", 123.45);
        DomainMessage domainMessage = new DomainMessage(order);

        RingBuffer<EventDisruptor> ringBuffer = disruptor.getRingBuffer();
        long sequence = ringBuffer.next();

        EventDisruptor eventDisruptor = ringBuffer.get(sequence);
        eventDisruptor.setTopic("orderCreated");
        eventDisruptor.setDomainMessage(domainMessage);

        ringBuffer.publish(sequence);

        // Wait for processing
        Thread.sleep(100);

        System.out.println("\n[Test] Despite error in first consumer, second consumer still processed");

        // Verify the reliable consumer still processed the message
        assertEquals("ORD-ERROR", reliableConsumer.getLastProcessedOrder());
    }
}
