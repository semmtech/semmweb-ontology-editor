package com.semmtech.plugin.semmweb.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;

public class JsonTests {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/// Test to see the working of JSON - will be used to store Preferences of Eclipse!
		DisplayLanguage nl = new DisplayLanguage("nl", "Nederlands");
		DisplayLanguage empty = new DisplayLanguage("", "International");
		DisplayLanguage en = new DisplayLanguage("en", "English");
		
		List<DisplayLanguage> languages = Lists.newArrayList();
		languages.add(nl);
		languages.add(empty);
		languages.add(en);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValue(new File("D://languages.json"), languages);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
		
		languages = null;
		try {
			languages = mapper.readValue(new File("D://languages.json"), new TypeReference<List<DisplayLanguage>>() { });
			for (DisplayLanguage lang : languages)
				System.out.println("lang.getName() = '" + lang.getName() + "'; lang.getCode() = '" + lang.getCode() +"'");
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
