package com.semmtech;

import java.util.Properties;

public final class EnvironmentExample {

	/**
	 * Instantiates a new main.
	 */
	private EnvironmentExample() {
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            provided arguments
	 */
	public static void main(final String[] args) {
		System.out.println("Executing com.semmtech.program.Main.main()...");

		testEnvironment();

		System.out.println("[Done]");
	}

	/**
	 * Test environment.
	 */
	private static void testEnvironment() {
		try {
			Properties p = Environment.getEnvironmentVariables();
			System.out.println("the current value of TBC_WORKSPACE is : " + p.getProperty("TBC_WORKSPACE"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
