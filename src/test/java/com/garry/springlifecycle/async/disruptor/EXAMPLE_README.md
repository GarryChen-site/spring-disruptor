# Disruptor Messaging Framework - Example

This example demonstrates the **core idea** of the Spring-Disruptor integration framework: using LMAX Disruptor for high-performance, asynchronous, topic-based messaging between domain models.

## Files Created

1. **`Order.java`** - Simple domain object representing an order
2. **`OrderEventConsumer.java`** - Consumer using `@Consumer` annotation + `DomainEventHandler` interface
3. **`OrderNotificationService.java`** - Consumer using `@OnEvent` method annotation (alternative pattern)
4. **`DisruptorMessagingExampleTest.java`** - Comprehensive test suite with 5 examples

## Core Concepts Demonstrated

### 1. **Basic Producer-Consumer Pattern**
```java
@Test
public void testBasicProducerConsumer()
```
- Creates a Disruptor for a topic ("orderCreated")
- Registers a consumer
- Publishes a message through the ring buffer
- Consumer processes the event asynchronously
- Retrieves the result (blocking)

**Key Learning**: This is the fundamental pattern - publish to a topic, consumers receive and process asynchronously.

---

### 2. **Multiple Consumers (Fan-out Pattern)**
```java
@Test
public void testMultipleConsumers()
```
- Multiple consumers listen to the same topic
- All consumers receive the same message
- Consumers execute in sequence (sorted by class name for deterministic ordering)

**Key Learning**: One message → N consumers. Perfect for scenarios like:
- Order created → Send notification + Update inventory + Log event

---

### 3. **High-Throughput Batch Processing**
```java
@Test
public void testHighThroughputBatchProcessing()
```
- Publishes 100 messages rapidly
- Demonstrates Disruptor's ultra-low latency
- Uses `endOfBatch` flag for batch optimization
- Measures throughput and latency

**Key Learning**: Disruptor can handle thousands of messages per second with microsecond latency.

**Expected Output**:
```
Published 100 messages in ~10-50 ms
Throughput: ~2000-10000 messages/sec
Average latency: ~0.1-0.5 ms/message
```

---

### 4. **Async Result Retrieval with Timeout**
```java
@Test
public void testAsyncResultRetrieval()
```
- Publishes a message
- Continues doing other work (non-blocking)
- Retrieves result later with timeout support

**Key Learning**: Fire-and-forget OR request-response patterns are both supported.

---

### 5. **Error Handling and Resilience**
```java
@Test
public void testErrorHandling()
```
- One consumer throws an exception
- Other consumers continue processing
- Demonstrates isolation between consumers

**Key Learning**: Errors in one consumer don't affect others (thanks to `DomainEventHandlerAdapter`).

---

## How to Run

### Run All Examples
```bash
./gradlew test --tests DisruptorMessagingExampleTest
```

### Run a Specific Example
```bash
# Example 1: Basic pattern
./gradlew test --tests DisruptorMessagingExampleTest.testBasicProducerConsumer

# Example 3: High throughput
./gradlew test --tests DisruptorMessagingExampleTest.testHighThroughputBatchProcessing
```

---

## Architecture Flow

```
1. Create Disruptor for topic
   ↓
2. Register consumers (sorted by class name)
   ↓
3. Start Disruptor
   ↓
4. Publish message to RingBuffer
   ↓
5. Consumers process event (in sequence)
   ↓
6. Set result back to DomainMessage
   ↓
7. Producer retrieves result (optional)
```

---

## Key Benefits Demonstrated

### 🚀 **Performance**
- **Microsecond latency** (vs milliseconds for traditional queues)
- **Lock-free** concurrency (CAS operations)
- **Cache-friendly** (pre-allocated ring buffer)

### 🎯 **Patterns**
- **Topic-based pub/sub** (1:N fan-out)
- **Async messaging** with optional result retrieval
- **Batch processing** optimization

### 🛡️ **Resilience**
- **Error isolation** between consumers
- **Deterministic ordering** (sorted consumers)
- **Timeout support** for result retrieval

---

## Comparison: Traditional Queue vs Disruptor

| Feature | Traditional Queue | Disruptor |
|---------|------------------|-----------|
| Latency | Milliseconds | Microseconds |
| Locking | Yes (contention) | No (lock-free) |
| Throughput | ~10K msg/sec | ~100K+ msg/sec |
| Memory | Heap allocation | Pre-allocated array |
| Batching | Limited | Native support |

---

## Real-World Use Cases

1. **Order Processing System**
   - Order created → Validate + Send notification + Update inventory + Log

2. **Event Sourcing**
   - Domain event → Multiple projections + Audit log + Analytics

3. **Real-time Analytics**
   - User action → Update metrics + Send to data warehouse + Trigger alerts

4. **Microservices Communication**
   - Service A → Service B + Service C (async, low latency)

---

## Framework Integration (Production Usage)

In production, you would use annotations instead of manual Disruptor creation:

### Producer (Domain Model)
```java
@Model
@Introduce("message")
public class OrderService {
    
    @Send("orderCreated")
    public DomainMessage createOrder(Order order) {
        // Business logic
        return new DomainMessage(order);
    }
}
```

### Consumer (Event Handler)
```java
@Consumer("orderCreated")
public class OrderEventConsumer implements DomainEventHandler<EventDisruptor> {
    
    @Override
    public void onEvent(EventDisruptor event, boolean endOfBatch) {
        Order order = (Order) event.getDomainMessage().getEventSource();
        // Process order
    }
}
```

The framework automatically:
- Scans for `@Consumer` and `@Send` annotations
- Creates Disruptors for each topic
- Wires consumers to Disruptors
- Intercepts `@Send` methods and publishes to Disruptor

---

## Summary

This example demonstrates the **core innovation** of the framework:

> **Combining Spring's annotation-driven programming model with Disruptor's ultra-low latency messaging to create a high-performance, asynchronous event-driven architecture.**

The framework makes it trivial to build reactive, event-driven systems with microsecond latency while maintaining clean, declarative code.
