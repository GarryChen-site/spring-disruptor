package statefulj.fsm.model;

/**
 * Represents a State with the FSM. Holds a map of events and Transitions
 * @param <T>
 */
public interface State<T> {

    /**
     * Name of the State. This value is persistent as the State value in the Stateful Entity
     * @return
     */
    String getName();

    /**
     * Returns the Transition for an event
     * @param event
     * @return
     */
    Transition<T> getTransition(String event);

    /**
     * Whether this State is an End State
     * @return
     */
    boolean isEndState();

    /**
     * Whether this is a Blocking State. If Blocking, event will not process unless there is
     * an explicit Transition for the event.
     * If blocked, the FSM will retry the event until the FSM transitions out of the blocked State
     *
     * @return
     */
    boolean isBlocking();

    /**
     * Set whether or not this is a Blocking State
     * @param isBlocking
     */
    void setBlocking(boolean isBlocking);

    /**
     * Remove a Transition from the State
     * @param event
     */
    void removeTransition(String event);

    /**
     * Add a transition
     * @param event
     * @param transition
     */
    void addTransition(String event, Transition<T> transition);

    /**
     * Add a deterministic Transition with an Action
     * @param event
     * @param next
     * @param action
     */
    void addTransition(String event, State<T> next, Action<T> action);

    /**
     * Add a deterministic Transition with no Action
     * @param event
     * @param next
     */
    void addTransition(String event, State<T> next);
}
