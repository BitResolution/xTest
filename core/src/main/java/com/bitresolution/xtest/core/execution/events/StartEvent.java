package com.bitresolution.xtest.core.execution.events;

public class StartEvent extends TestExecutorEvent {
    public StartEvent(Object source) {
        super(source);
    }

    public static StartEvent start(Object source) {
        return new StartEvent(source);
    }
}
