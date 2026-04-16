package com.semmtech;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Note that when overriding a function (or method), that function is also used
 * by non-overwritten code in the superclass. Thus in the snippet that follows,
 * the function getThreeTimesSeven() declared in SuperClass does not necessarily
 * use the getThree() and getSeven() declared in that class, using any
 * overriding functions in SubClass instead.
 */
public class OverrideSnippet {

    private class SuperClass {
        public SuperClass() {
        }

        public int getThree() {
            return 3;
        }

        public int getSeven() {
            return 7;
        }

        public int getThreeTimesSeven() {
            return getThree() * getSeven();
        }
    }

    private class SubClass extends SuperClass {
        public SubClass() {
        }

        @Override
        public int getThree() {
            return 3 * 100;
        }
    }

    @Test
    public void testOverride() {
        SuperClass superClass = new SuperClass();
        assertEquals(3, superClass.getThree());
        assertEquals(7, superClass.getSeven());
        assertEquals(3 * 7, superClass.getThreeTimesSeven());

        SubClass subClass = new SubClass();
        assertEquals(300, subClass.getThree());
        assertEquals(7, subClass.getSeven());
        assertEquals(300 * 7, subClass.getThreeTimesSeven());

        SuperClass s = new SubClass();
        assertEquals(300, s.getThree());
        assertEquals(7, s.getSeven());
        assertEquals(300 * 7, s.getThreeTimesSeven());
    }
}
