package com.semmtech.semantics.ontology;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ ExtendedOntResourceTest.class, ExtendedOntProperty.class,
        ExtendedOntClassTest.class })
public class AllOntologyTests {
}
