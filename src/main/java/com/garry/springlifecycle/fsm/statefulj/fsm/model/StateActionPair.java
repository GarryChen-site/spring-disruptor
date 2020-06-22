package com.garry.springlifecycle.fsm.statefulj.fsm.model;


/**
 * a State/action pair
 * @param <T>
 */
public interface StateActionPair<T> {

    State<T> getState();

    Action<T> getAction();
}
