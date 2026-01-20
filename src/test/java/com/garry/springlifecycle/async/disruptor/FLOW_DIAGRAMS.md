# Example Flow Visualization

## Basic Producer-Consumer Flow (Example 1)

```
┌─────────────────────────────────────────────────────────────────┐
│                         Test Code                               │
│  1. Create Order("ORD-001", "John Doe", 299.99)                │
│  2. Wrap in DomainMessage                                       │
│  3. Get next sequence from RingBuffer                           │
│  4. Set EventDisruptor data                                     │
│  5. Publish to RingBuffer                                       │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │   Disruptor RingBuffer │
        │   Topic: "orderCreated"│
        │   Size: 8 slots        │
        └────────┬───────────────┘
                 │
                 │ (async, microsecond latency)
                 ▼
    ┌────────────────────────────────┐
    │  DomainEventHandlerAdapter     │
    │  (bridges to Disruptor API)    │
    └────────┬───────────────────────┘
             │
             ▼
┌────────────────────────────────────────┐
│     OrderEventConsumer                 │
│  @Consumer("orderCreated")             │
│                                        │
│  onEvent(EventDisruptor event) {       │
│    Order order = extract from event   │
│    Process order...                    │
│    setEventResult("processed")         │
│  }                                     │
└────────┬───────────────────────────────┘
         │
         │ (result set)
         ▼
┌────────────────────────────────────────┐
│         DomainMessage                  │
│  eventResultHandler.send(result)       │
└────────┬───────────────────────────────┘
         │
         │ (blocking/non-blocking retrieval)
         ▼
┌────────────────────────────────────────┐
│           Test Code                    │
│  result = domainMessage.getResult()    │
│  Assert result contains "ORD-001"      │
└────────────────────────────────────────┘
```

## Multiple Consumers Flow (Example 2)

```
                    RingBuffer
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌─────────────┐ ┌──────────────┐
│   Consumer1  │ │ Validation  │ │   Logging    │
│ (Processing) │ │  Consumer   │ │   Consumer   │
│              │ │ (Validates) │ │   (Logs)     │
└──────────────┘ └─────────────┘ └──────────────┘
     (sequential execution, sorted by class name)
```

## High-Throughput Flow (Example 3)

```
Test publishes 100 messages in ~10-50ms
        │
        ▼
┌─────────────────────────────────────┐
│      RingBuffer (Size: 8)           │
│  [0][1][2][3][4][5][6][7]          │
│   ↻  ↻  ↻  ↻  ↻  ↻  ↻  ↻           │
│  (circular, pre-allocated)          │
└─────────────┬───────────────────────┘
              │
              ▼
      BatchConsumer processes
      with endOfBatch optimization
              │
              ▼
    Throughput: ~2000-10000 msg/sec
    Latency: ~0.1-0.5 ms/message
```

## Performance Characteristics

### Why Disruptor is Fast

1. **Lock-Free**: Uses CAS (Compare-And-Swap) operations
   ```
   Traditional Queue:        Disruptor:
   ┌─────────┐              ┌─────────┐
   │  Lock   │              │   CAS   │
   │ Acquire │              │ (atomic)│
   │  Write  │              │  Write  │
   │ Release │              │         │
   └─────────┘              └─────────┘
   ~1000ns                  ~10ns
   ```

2. **Pre-allocated Array**: No GC pressure
   ```
   Traditional Queue:        Disruptor:
   new Object() for each    Pre-allocated array
   message → GC pressure    → Zero GC
   ```

3. **Cache-Friendly**: Sequential memory access
   ```
   Array: [0][1][2][3][4]...
          ↑  CPU cache line
   Better CPU cache utilization
   ```

## Real-World Scenario: Order Processing

```
OrderService.createOrder()
  @Send("orderCreated")
         │
         ▼
    Disruptor publishes to "orderCreated" topic
         │
    ┌────┴────┬──────────┬──────────┐
    ▼         ▼          ▼          ▼
Validate   Notify   Inventory   Logging
Consumer   Consumer  Consumer   Consumer
    │         │          │          │
    └─────────┴──────────┴──────────┘
              │
              ▼
    All processed in <1ms
    (vs 10-100ms with traditional queue)
```

## Key Metrics from Examples

| Example | Messages | Time | Throughput | Latency |
|---------|----------|------|------------|---------|
| Example 1 | 1 | ~1ms | N/A | ~1ms |
| Example 2 | 1 (3 consumers) | ~1ms | N/A | ~1ms |
| Example 3 | 100 | ~10-50ms | 2K-10K/sec | 0.1-0.5ms |
| Example 4 | 1 (delayed) | 200ms | N/A | 200ms |
| Example 5 | 1 (with error) | ~1ms | N/A | ~1ms |

**Note**: Actual performance depends on hardware, but Disruptor consistently
outperforms traditional queues by 10-100x in throughput and latency.
