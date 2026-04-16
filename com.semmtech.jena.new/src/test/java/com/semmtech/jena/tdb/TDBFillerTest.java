package com.semmtech.jena.tdb;


public class TDBFillerTest {
	
	public static void main(String[] args) {

		TDBUtils.createTDBBacked("src/test/output/dataset.trig", "tdb");
	}
}
