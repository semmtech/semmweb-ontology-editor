package com.semmtech.jena.tdb;

import com.semmtech.log4j.StandardConfigurator;

public class TDBBuilderTest {
	
	public static void main(String[] args) {
		StandardConfigurator.configure();
		
		TDBBuilder.generateAssembler();
		TDBBuilder.build("tdb");
	}
}
