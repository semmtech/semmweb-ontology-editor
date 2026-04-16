package com.semmtech.semantics.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;

public class SemanticFormatTranslator {
	public static void main(String[] args) {
		translate("file:C:\\Users\\Mike Henrichs\\Dropbox\\Semmtech\\Projecten\\SEMMweb\\OWL Files\\semm\\semm-combined-2012-12-28.ttl", "C:\\Users\\Mike Henrichs\\Dropbox\\Semmtech\\Projecten\\SEMMweb\\OWL Files\\semm\\semm-combined-2012-12-28.owl", FileUtils.langTurtle, FileUtils.langXMLAbbrev);
	}
	
	public static void translate(String inputUrl, String outputUrl, String toLang) {
		String fromLang = FileUtils.guessLang(inputUrl);
		if (inputUrl.endsWith(".owl"))
			fromLang = FileUtils.langXMLAbbrev;
		translate(inputUrl, outputUrl, fromLang, toLang);
	}
	
	public static void translate(String inputUrl, String outputUrl, String fromLang, String toLang) {
		Model model = ModelFactory.createDefaultModel();
		model.read(inputUrl, fromLang);		
		try {
			model.write(new FileOutputStream(new File(outputUrl)), toLang);
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
