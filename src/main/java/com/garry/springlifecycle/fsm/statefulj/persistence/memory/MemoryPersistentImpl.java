package com.garry.springlifecycle.fsm.statefulj.persistence.memory;

import com.garry.springlifecycle.fsm.statefulj.fsm.Persistent;
import com.garry.springlifecycle.fsm.statefulj.fsm.StaleStateException;
import com.garry.springlifecycle.fsm.statefulj.fsm.model.State;
import com.garry.springlifecycle.fsm.statefulj.persistence.StateFieldAccessor;
import com.garry.springlifecycle.fsm.tools.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Thread safe,in memory Persistent
 * @param <T>
 */
public class MemoryPersistentImpl<T> implements Persistent<T> {

    private final Map<String, State<T>> states = new HashMap<>();
    private State<T> startState;
    private String stateFieldName;
    private StateFieldAccessor<T> stateFieldAccessor;

    public MemoryPersistentImpl() {
    }

    public MemoryPersistentImpl(final Collection<State<T>> states, final State<T> startState) {
        setStartState(startState);
        setStates(states);
    }

    public MemoryPersistentImpl(List<State<T>> states, State<T> startState, String stateFieldName) {
        this(states, startState);
        this.stateFieldName = stateFieldName;
    }

    public MemoryPersistentImpl(T stateful, List<State<T>> states, State<T> startState) {
        this(states, startState);
        this.setCurrent(stateful, startState);
    }

    public MemoryPersistentImpl(T stateful, List<State<T>> states, State<T> startState, String stateFieldName) {
        this(states, startState, stateFieldName);
        this.setCurrent(stateful, startState);
    }

    public synchronized Collection<State<T>> getStates() {
        return states.values();
    }

    public synchronized State<T> addState(final State<T> state) {
        return states.put(state.getName(), state);
    }

    public synchronized State<T> removeState(final State<T> state) {
        return removeState(state.getName());
    }

    public synchronized State<T> removeState(final String name) {
        return states.remove(name);
    }

    @Override
    public State<T> getCurrent(T stateful) {
        try {
            String key = (String)this.getStateFieldAccessor(stateful).getValue(stateful);
            State<T> state = (key != null) ? states.get(key) : null;
            return (state != null) ? state : this.startState;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrent(T stateful, State<T> current) {
        synchronized(stateful) {
            try {
                this.getStateFieldAccessor(stateful).setValue(stateful, current.getName());
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * Serialize all update of state.  Ensure that the current state is the same State that
     * was evaluated. If not, throw an exception
     *
     * (non-Javadoc)
     * @see org.com.garry.springlifecycle.fsm.Persister#setCurrent(org.com.garry.springlifecycle.fsm.model.State, org.com.garry.springlifecycle.fsm.model.State)
     */
    @Override
    public void setCurrent(T stateful, State<T> current, State<T> next) throws StaleStateException {
        synchronized(stateful) {
            if (this.getCurrent(stateful).equals(current)) {
                this.setCurrent(stateful, next);
            } else {
                throw new StaleStateException();
            }
        }
    }

    @Override
    public synchronized void setStates(final Collection<State<T>> states) {
        //Clear the map
        //
        this.states.clear();

        //Add new states
        //
        for(State state : states) {
            this.states.put(state.getName(), state);
        }
    }

    @Override
    public void setStartState(State<T> startState) {
        this.startState = startState;
    }

    public State<T> getStartState() {
        return startState;
    }

    public String getStateFieldName() {
        return stateFieldName;
    }

    private StateFieldAccessor<T> getStateFieldAccessor(final T stateful) {
        if (this.stateFieldAccessor == null)
            initStateFieldAccessor(stateful);
        return this.stateFieldAccessor;
    }

    private synchronized void initStateFieldAccessor(T stateful) {
        if (this.stateFieldAccessor == null) {
            Field stateField = locateStateField(stateful);
            this.stateFieldAccessor = new StateFieldAccessor(stateful.getClass(), stateField);
        }
    }

    private synchronized Field locateStateField(final T stateful) {
        Field field;

        // If a state field name was provided, retrieve by name
        //
        if (this.stateFieldName != null && !this.stateFieldName.equals("")) {
            field =
                    ReflectionUtils.getField(stateful.getClass(), this.stateFieldName);
        }


        // Else, fetch the field by Annotation
        //
        else {
            field = ReflectionUtils.getFirstAnnotatedField(stateful.getClass(), com.garry.springlifecycle.fsm.statefulj.persistence.annotations.State.class);
            if (field != null) {
                this.stateFieldName = field.getName();
            }
        }

        if (field == null) {
            throw new RuntimeException("Unable to locate a State field for stateful: " + stateful);
        }

        return field;
    }
}
