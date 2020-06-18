package statefulj.fsm.model.impl;

import statefulj.fsm.RetryException;
import statefulj.fsm.model.Action;
import statefulj.fsm.model.State;
import statefulj.fsm.model.StateActionPair;
import statefulj.fsm.model.Transition;

public class DeterministicTransitionImpl<T> implements Transition<T> {

    private StateActionPair<T> stateActionPair;

    public DeterministicTransitionImpl(State<T> from, State<T> to, String event) {
        stateActionPair = new StateActionPairImpl<T>(to, null);
        from.addTransition(event, this);
    }

    public DeterministicTransitionImpl(State<T> from, State<T> to, String event, Action<T> action) {
        stateActionPair = new StateActionPairImpl<T>(to, action);
        from.addTransition(event, this);
    }

    public DeterministicTransitionImpl(State<T> to, Action<T> action) {
        stateActionPair = new StateActionPairImpl<T>(to, action);
    }

    public DeterministicTransitionImpl(State<T> to) {
        stateActionPair = new StateActionPairImpl<>(to, null);
    }

    @Override
    public StateActionPair<T> getStateActionPair(T stateful, String event, Object... args) throws RetryException {
        return stateActionPair;
    }

    public void setStateActionPair(StateActionPair<T> stateActionPair) {
        this.stateActionPair = stateActionPair;
    }

    @Override
    public String toString() {
        return "DeterministicTransition[state=" + this.stateActionPair.getState().getName() + ", action=" + this.stateActionPair.getAction() + "]";
    }
}
