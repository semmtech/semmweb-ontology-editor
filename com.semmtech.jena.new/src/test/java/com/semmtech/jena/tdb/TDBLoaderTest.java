package com.semmtech.jena.tdb;

import org.apache.log4j.Level;

import com.semmtech.log4j.StandardConfigurator;

public class TDBLoaderTest {

	public static void main(String[] args) {
		StandardConfigurator.configure(Level.DEBUG);
		String parameters = "--tdb src/test/resources/tdb-assemble.ttl http://repo.semmweb.com/ns/mikehenrichs/2014/10/movies/";
		tdbloader2.main(parameters.split(" "));
	}

}
