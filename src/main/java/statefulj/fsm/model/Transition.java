package statefulj.fsm.model;

import statefulj.fsm.RetryException;

/**
 * A Transition is a "reaction" to an Event based off the State of
 * the Stateful Entity
 * It is comprised of an optional next State value and an optional Action
 * @param <T>
 */
public interface Transition<T> {

    StateActionPair<T> getStateActionPair(T stateful, String event, Object ... args) throws RetryException;
}
