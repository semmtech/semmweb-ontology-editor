package com.semmtech.jena;

import java.util.List;

import jena.schemagen;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class Schemagen extends schemagen {
	private String config;
	private String resourceUri;
	
	private String input;
	private String output;
	private String classname;
	private String packagename;
	
	public Schemagen() {
		
	}
	
	public Schemagen(String config) {
		this(config, null);
	}
	
	public Schemagen(String config, String resourceUri) {
		this.config = config;
		this.resourceUri = resourceUri;
	}
	
	
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getPackagename() {
		return packagename;
	}

	public void setPackagename(String packagename) {
		this.packagename = packagename;
	}

	private String[] getParameters() {
		List<String> parameters = Lists.newArrayList();	
		if (!Strings.isNullOrEmpty(config)) {
			parameters.add(String.format("-c %s", config));
			if (!Strings.isNullOrEmpty(resourceUri)) {
				parameters.add(String.format("-r %s", resourceUri));
			}
		}		
		else {
			if (!Strings.isNullOrEmpty(input)) {
				parameters.add(String.format("-i %s", input));
			}
			if (!Strings.isNullOrEmpty(output)) {
				parameters.add(String.format("-o %s", output));
			}
			if (!Strings.isNullOrEmpty(classname)) {
				parameters.add(String.format("-n %s", classname));
			}
			if (!Strings.isNullOrEmpty(packagename)) {
				parameters.add(String.format("--package %s", packagename));
			}
			parameters.add("--dos true");
		}		
		return StringUtils.join(parameters, " ").split(" ");
	}
	
	public void generate() {		
		main(getParameters());
	}
	
}
