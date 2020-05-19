package com.garry.springlifecycle.async.disruptor;


import org.springframework.stereotype.Component;

@Component
public class DisruptorParams {

    private final int ringBufferSize;

    public DisruptorParams() {
        ringBufferSize = 2048;
    }

    public int getRingBufferSize() {
        return ringBufferSize;
    }
}
