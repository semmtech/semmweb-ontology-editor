package com.semmtech.prefs;

import java.util.prefs.Preferences;

/**
 * See http://www.vogella.de/articles/JavaPreferences/article.html for details.
 * @author Mike Henrichs
 *
 */
public class PreferenceExample {
	
	/** The Constant VALUE_2. */
	private static final int VALUE_2 = 45;
	
	/** The Constant VALUE_1. */
	private static final int VALUE_1 = 50;
	
	/** The prefs. */
	private Preferences prefs;

	/**
	 * Sets the preference.
	 */
	public void setPreference() {
		// This will define a node in which the preferences can be stored
		prefs = Preferences.userRoot().node(this.getClass().getName());
		String id1 = "Test1";
		String id2 = "Test2";
		String id3 = "Test3";

		// First we will get the values
		// Define a boolean value
		System.out.println(prefs.getBoolean(id1, true));
		// Define a string with default "Hello World
		System.out.println(prefs.get(id2, "Hello World"));
		// Define a integer with default 50		
		System.out.println(prefs.getInt(id3, VALUE_1));

		// Now set the values
		prefs.putBoolean(id1, false);
		prefs.put(id2, "Hello Europa");
		prefs.putInt(id3, VALUE_2);

		// Delete the preference settings for the first value
		prefs.remove(id1);

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		PreferenceExample test = new PreferenceExample();
		test.setPreference();
		
	}

}
