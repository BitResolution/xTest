package com.bitresolution.xtest.core.phases.parse;

import com.bitresolution.xtest.commons.reflection.FullyQualifiedClassName;
import com.bitresolution.xtest.commons.reflection.Package;

public interface GraphFactory {

    TestGraph from(Class<?> klass) throws TestGraphException;

    TestGraph from(FullyQualifiedClassName name) throws ClassNotFoundException, TestGraphException;

    TestGraph from(Package name) throws TestGraphException;
}
