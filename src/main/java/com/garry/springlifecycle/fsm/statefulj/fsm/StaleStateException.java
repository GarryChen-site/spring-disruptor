package com.garry.springlifecycle.fsm.statefulj.fsm;

/**
 * Indicates that the evaluated State was inconsistent with the Persistent State
 */
public class StaleStateException extends RetryException {

    public StaleStateException() {
        super();
    }

    public StaleStateException(String err) {
        super(err);
    }
}
