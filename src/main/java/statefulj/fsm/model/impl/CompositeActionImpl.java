package statefulj.fsm.model.impl;

import statefulj.fsm.RetryException;
import statefulj.fsm.model.Action;

import java.util.List;


/**
 * A "composite" Action which is composed of a set of Action. When invoked,
 * it will iterate and invoke all the composition Actions
 * @param <T>
 */
public class CompositeActionImpl<T> implements Action<T> {

    List<Action<T>> actions;

    public CompositeActionImpl(List<Action<T>> actions) {
        this.actions = actions;
    }

    @Override
    public void execute(T stateful, String event, Object... args) throws RetryException {
        for (Action<T> action : this.actions){
            action.execute(stateful, event, args);
        }
    }
}
