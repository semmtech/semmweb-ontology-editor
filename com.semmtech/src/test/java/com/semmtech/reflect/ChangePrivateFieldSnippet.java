package com.semmtech.reflect;


import java.lang.reflect.Field;

import org.junit.Test;


public class ChangePrivateFieldSnippet {

    public ChangePrivateFieldSnippet() {

    }

    class Super {
        private int value = 5;

        public int getValue() {
            return value;
        }
    }

    public class Sub extends Super {

        public Sub() {
            super();
            Field[] fields = this.getClass().getSuperclass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.getName().equals("value")) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    field.setInt(this, 10);
                }
                catch (IllegalArgumentException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testReflect() {
        Super s1 = new Super();
        System.out.println("s1.getValue() = " + s1.getValue());

        Super s2 = new Sub();
        System.out.println("s2.getValue() = " + s2.getValue());
    }

}
