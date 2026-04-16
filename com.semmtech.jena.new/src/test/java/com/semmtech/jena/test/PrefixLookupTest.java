package com.semmtech.jena.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.log4j.StandardConfigurator;

/**
 * The website http://prefix.cc contains a large number of "well-known" prefixes (approx. 1500).
 * 
 * This test shows how all these contexts can be obtained easily using JSON-LD. Using this format to load a model the model's prefix map will be filled with all the prefixes defined on the website. 
 * 
 * @author Mike
 *
 */
public class PrefixLookupTest {
	
	private static Logger logger = Logger.getLogger(PrefixLookupTest.class);

	public static void main(String[] args) {
		StandardConfigurator.configure();
		Model model = RDFDataMgr.loadModel("http://prefix.cc/context", Lang.JSONLD);
		
		Map<String, String> mappings = model.getNsPrefixMap();
		List<String> prefixes = Lists.newArrayList(mappings.keySet());
		Collections.sort(prefixes);
		int index = 1;
		for (String prefix : prefixes) {
			logger.info(String.format("[%4s] %-12s: <%s>", index++, prefix, mappings.get(prefix)));
		}
		
		try {
			RDFDataMgr.write(new FileOutputStream(new File("src/test/output/prefixes.ttl")), model, Lang.TURTLE);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
