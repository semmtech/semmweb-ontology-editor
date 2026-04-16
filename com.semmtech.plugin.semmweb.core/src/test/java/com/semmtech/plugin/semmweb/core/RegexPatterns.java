package com.semmtech.plugin.semmweb.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPatterns {

	public static final String EXAMPLE_TEST = "This is my small example string which I'm going to use for pattern matching. {@en-US}";

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("(\\{\\@)(\\w+\\-?\\w*)(\\}$)");
		// In case you would like to ignore case sensitivity you could use this
		// statement
		// Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(EXAMPLE_TEST);
		String lang = EXAMPLE_TEST.replaceAll("(\\{\\@)(\\w+)(\\}$)", "$3");
		System.out.println("lang = '" + lang + "'");
		// Check all occurance
		while (matcher.find()) {
			System.out.println("Start index: " + matcher.start());
			System.out.println("End index: " + matcher.end() + " ");
			System.out.println(matcher.group());
			System.out.println("group('2') = " + matcher.group(2));
		}
		// Now create a new pattern and matcher to replace whitespace with tabs
//		Pattern replace = Pattern.compile("\\s+");
//		Matcher matcher2 = replace.matcher(EXAMPLE_TEST);
//		System.out.println(matcher2.replaceAll("\t"));
	}

}
