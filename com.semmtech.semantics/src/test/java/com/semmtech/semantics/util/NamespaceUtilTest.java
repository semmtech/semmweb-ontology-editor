package com.semmtech.semantics.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.util.FileUtils;


public class NamespaceUtilTest {
    public NamespaceUtilTest() {

    }

    @Test
    public void testNamespaces() throws IOException {
        Model model = ModelFactory.createDefaultModel();

        try (FileInputStream fis = new FileInputStream(
                new File(
                        "C:\\Users\\Mike Henrichs\\runtime-semmwebEditor.product\\Default\\Example\\test.ttl"))) {
            model.read(fis, null, FileUtils.langTurtle);
        }

        System.out.println("\n--- Model:\n");
        model.write(System.out, FileUtils.langTurtle);

        System.out.println("\n--- listNamespaces:\n");
        for (NsIterator iter = model.listNameSpaces(); iter.hasNext();) {
            String ns = iter.next();
            System.out.println(ns);
        }

        System.out.println("\n--- Namespaces:\n");
        List<NamespaceMapping> namespaces = NamespaceUtil.getNamespaceMappings(model, true);
        for (NamespaceMapping mapping : namespaces) {
            String prefix = mapping.getPrefix();
            System.out.println(String.format("%s%s", mapping.getURI(), (prefix != null ? " <- "
                    + prefix + ":" : "")));
        }
    }
}
