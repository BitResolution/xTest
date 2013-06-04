package com.bitresolution.xtest.core.execution;

public abstract class Fixture implements Comparable<Fixture> {

    private final Integer index;

    public Fixture(Integer index) {
        this.index = index;
    }

    @Override
    public int compareTo(Fixture that) {
        return this.index.compareTo(that.index);
    }
}
