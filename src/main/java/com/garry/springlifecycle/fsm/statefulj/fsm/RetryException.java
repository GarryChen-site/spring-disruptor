package com.garry.springlifecycle.fsm.statefulj.fsm;

/**
 * Exception indicates that the FSM needs to retry the event
 */
public class RetryException extends Exception {

    public RetryException() {
        super();
    }

    public RetryException(String message) {
        super(message);
    }
}
