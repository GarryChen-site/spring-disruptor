package statefulj.fsm.model.impl;

import statefulj.fsm.model.Action;
import statefulj.fsm.model.State;
import statefulj.fsm.model.StateActionPair;

public class StateActionPairImpl<T> implements StateActionPair<T> {

    State<T> state;
    Action<T> action;

    public StateActionPairImpl(State<T> state, Action<T> action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public State<T> getState() {
        return state;
    }

    @Override
    public Action<T> getAction() {
        return action;
    }

    public void setState(State<T> state) {
        this.state = state;
    }

    public void setAction(Action<T> action) {
        this.action = action;
    }
}
