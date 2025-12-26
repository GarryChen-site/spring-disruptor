package com.garry.springlifecycle.fsm.persistence;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.garry.springlifecycle.fsm.statefulj.persistence.StateFieldAccessor;
import com.garry.springlifecycle.fsm.statefulj.persistence.annotations.State;
import com.garry.springlifecycle.fsm.tools.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by andrewhall on 7/24/16.
 */
public class StateFieldAccessorTest {

    static class StatefulClass1 {

        @State
        private String state;

    }

    static class StatefulClass2 {

        @State(accessorType = State.AccessorType.METHOD)
        private String state;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }

    static class StatefulClass3 {

        @State(accessorType = State.AccessorType.METHOD, getMethodName = "nonBeanGetState", setMethodName = "nonBeanSetState")
        private String state;

        public String nonBeanGetState() {
            return state;
        }

        public void nonBeanSetState(String state) {
            this.state = state;
        }
    }

    static class StatefulClass4 {

        @State(accessorType = State.AccessorType.METHOD)
        private String state;
    }

    static class StatefulClass5 {

        @State(accessorType = State.AccessorType.METHOD)
        private String state;

        public String nonBeanGetState() {
            return state;
        }

    }

    static class StatefulClass6 {

        @State(accessorType = State.AccessorType.PROPERTY)
        private String state;
    }

    @Test
    public void testStatePersistentStatefulClass1() {
        StateFieldAccessor<StatefulClass1> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass1.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass1.class, State.class));
        assertNotNull(stateFieldAccessor.getField());
        assertEquals(null, stateFieldAccessor.getGetMethod());
        assertEquals(null, stateFieldAccessor.getSetMethod());

        Field stateField = stateFieldAccessor.getField();
        assertEquals("state", stateField.getName());
    }

    @Test
    public void testStatePersistentStatefulClass2() {
        StateFieldAccessor<StatefulClass2> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass2.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass2.class, State.class));
        assertNotNull(stateFieldAccessor.getField());
        assertEquals("getState", stateFieldAccessor.getGetMethod().getName());
        assertEquals("setState", stateFieldAccessor.getSetMethod().getName());

        Field stateField = stateFieldAccessor.getField();
        assertEquals("state", stateField.getName());
    }

    @Test
    public void testStatePersistentStatefulClass3() {
        StateFieldAccessor<StatefulClass3> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass3.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass3.class, State.class));
        assertNotNull(stateFieldAccessor.getField());
        assertEquals("nonBeanGetState", stateFieldAccessor.getGetMethod().getName());
        assertEquals("nonBeanSetState", stateFieldAccessor.getSetMethod().getName());

        Field stateField = stateFieldAccessor.getField();
        assertEquals("state", stateField.getName());
    }

    @Test
    public void testStatePersistentStatefulClass4() {
        assertThrows(RuntimeException.class, () -> new StateFieldAccessor(
                StatefulClass4.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass4.class, State.class)));
    }

    @Test
    public void testStatePersistentStatefulClass5() {
        assertThrows(RuntimeException.class, () -> new StateFieldAccessor(
                StatefulClass5.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass5.class, State.class)));
    }

    @Test
    public void testGetterSetterStatefulClass1() {
        StateFieldAccessor<StatefulClass1> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass1.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass1.class, State.class));
        StatefulClass1 stateful = new StatefulClass1();
        assertNull(stateFieldAccessor.getValue(stateful));
        stateFieldAccessor.setValue(stateful, "testValue");
        assertEquals("testValue", stateFieldAccessor.getValue(stateful));
    }

    @Test
    public void testGetterSetterStatefulClass2() {
        StateFieldAccessor<StatefulClass2> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass2.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass2.class, State.class));
        StatefulClass2 stateful = new StatefulClass2();

        StatefulClass2 spy = Mockito.spy(stateful);
        assertNull(stateFieldAccessor.getValue(spy));
        Mockito.verify(spy).getState();

        stateFieldAccessor.setValue(spy, "testValue");
        Mockito.verify(spy).setState("testValue");
        assertEquals("testValue", stateFieldAccessor.getValue(spy));
    }

    @Test
    public void testGetterSetterStatefulClass3() {
        StateFieldAccessor<StatefulClass3> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass3.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass3.class, State.class));
        StatefulClass3 stateful = new StatefulClass3();

        StatefulClass3 spy = Mockito.spy(stateful);
        assertNull(stateFieldAccessor.getValue(spy));
        Mockito.verify(spy).nonBeanGetState();

        stateFieldAccessor.setValue(spy, "testValue");
        Mockito.verify(spy).nonBeanSetState("testValue");
        assertEquals("testValue", stateFieldAccessor.getValue(spy));
    }

    @Test
    public void testStatePersistentStatefulClass6() {
        StateFieldAccessor<StatefulClass6> stateFieldAccessor = new StateFieldAccessor(
                StatefulClass6.class,
                ReflectionUtils.getFirstAnnotatedField(StatefulClass6.class, State.class));
        assertNotNull(stateFieldAccessor.getField());
        assertEquals(null, stateFieldAccessor.getGetMethod());
        assertEquals(null, stateFieldAccessor.getSetMethod());

        Field stateField = stateFieldAccessor.getField();
        assertEquals("state", stateField.getName());
    }

}
