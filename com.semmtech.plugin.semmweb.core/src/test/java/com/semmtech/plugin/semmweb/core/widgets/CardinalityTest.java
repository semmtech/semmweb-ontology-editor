package com.semmtech.plugin.semmweb.core.widgets;

public class CardinalityTest {
	public static void main(String[] args) {
		Cardinality c0n = new Cardinality(0, true);
		Cardinality c15 = new Cardinality(1, 5);
		Cardinality c25 = new Cardinality(2, 5);
		Cardinality c34 = new Cardinality(3, 4);
		Cardinality c47 = new Cardinality(4, 7);
		
		System.out.println("{4, 7} lies within {0, n} = " + Cardinality.isStricter(c47, c0n));
		System.out.println("{0, n} lies within {4, 7} = " + Cardinality.isStricter(c0n, c47));
		System.out.println("{2, 5} lies within {1, 5} = " + Cardinality.isStricter(c25, c15));
		System.out.println("{3, 4} lies within {1, 5} = " + Cardinality.isStricter(c34, c15));
		System.out.println("{1, 5} lies within {3, 4} = " + Cardinality.isStricter(c15, c34));
		
		
	}
}
