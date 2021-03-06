package com.bitresolution.xtest.phases.sources;

import com.bitresolution.succor.junit.category.Unit;
import com.bitresolution.succor.reflection.FullyQualifiedClassName;
import com.bitresolution.succor.reflection.PackageName;
import com.bitresolution.xtest.examples.*;
import com.google.common.collect.Sets;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@org.junit.experimental.categories.Category(Unit.class)
public class XTestAnnotatedSourceSpec {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPackageNames() {
        new XTestAnnotatedSource(null);
    }

    @Test
    public void shouldReturnEmptySetIfNoAnnotatedClassesFound() {
        //given
        PackageName packageName = new PackageName("com.bitresolution.xtest.core.phases.sources");
        XTestAnnotatedSource source = new XTestAnnotatedSource(packageName);

        //when
        Set<FullyQualifiedClassName> classes = source.getClasses();

        //then
        assertThat(classes, is(Collections.EMPTY_SET));
    }

    @Test
    public void shouldFindAllAnnotatedClassesInPackage() {
        //given
        PackageName packageName = new PackageName("com.bitresolution.xtest.examples");
        XTestAnnotatedSource source = new XTestAnnotatedSource(packageName);

        //when
        Set<FullyQualifiedClassName> classes = source.getClasses();

        //then
        Set<FullyQualifiedClassName> expected = Sets.newHashSet(
                new FullyQualifiedClassName(TestNodeClassWithNoTestNodeMethodsExample.class),
                new FullyQualifiedClassName(TestNodeEmptyClassExample.class),
                new FullyQualifiedClassName(TestNodeExceptionHandlingMethodExample.class),
                new FullyQualifiedClassName(TestNodeMultipleMethodExample.class),
                new FullyQualifiedClassName(TestNodeSingleMethodExample.class)
        );
        assertThat(classes, is(expected));
    }


    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(XTestAnnotatedSource.class)
                .usingGetClass()
                .verify();

    }
}
