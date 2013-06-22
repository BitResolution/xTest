package com.bitresolution.xtest.commons.reflection

import spock.lang.Specification

class FullyQualifiedClassNameSpec extends Specification {

    def "should parse fully qualified class names"() {
        expect:
        def fqcn = new FullyQualifiedClassName(fullname)
        assert fqcn.fullyQualifiedClassName == fullname
        assert fqcn.className == className
        assert fqcn.packageName.equals(new com.bitresolution.xtest.commons.reflection.Package(packageName))
        assert fqcn.inDefaultPackage == isInDefaultPackage

        where:
        fullname           | className  | packageName | isInDefaultPackage
        "BadClass"         | "BadClass" | ""          | true
        "java.lang.String" | "String"   | "java.lang" | false
    }
}