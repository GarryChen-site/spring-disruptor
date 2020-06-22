package com.garry.springlifecycle.fsm.statefulj.fsm.model.impl;

import com.garry.springlifecycle.fsm.statefulj.fsm.RetryException;
import com.garry.springlifecycle.fsm.statefulj.fsm.WaitAndRetryException;
import com.garry.springlifecycle.fsm.statefulj.fsm.model.Action;

public class WaitAndRetryActionImpl<T> implements Action<T> {

    private int wait = 0;

    /**
     * Constructor with a wait time expressed in milliseconds
     * @param wait
     */
    public WaitAndRetryActionImpl(int wait) {
        this.wait = wait;
    }

    @Override
    public void execute(T stateful, String event, Object... args) throws RetryException {
        throw new WaitAndRetryException(this.wait);
    }
}
