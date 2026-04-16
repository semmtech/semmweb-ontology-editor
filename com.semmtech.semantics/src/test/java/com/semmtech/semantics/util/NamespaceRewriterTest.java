package com.semmtech.semantics.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileUtils;


public final class NamespaceRewriterTest {
    public NamespaceRewriterTest() {

    }

    @Test
    public void testRewrite() throws IOException {
        NamespaceRewriter rewriter = new NamespaceRewriter();

        String filename = "C:\\Users\\Mike Henrichs\\runtime-semmwebEditor.product\\Default\\Example\\test.ttl";
        String ns1 = "http://www.example.org/test/";
        String ns2 = "http://www.lemantle.com/test#";
        Model original = ModelFactory.createDefaultModel();

        try (FileInputStream fis = new FileInputStream(new File(filename))) {
            original.read(fis, null, FileUtils.langTurtle);
        }

        System.out.println("--- Original:");
        original.write(System.out, FileUtils.langTurtle);

        rewriter.addRewrite(ns1, ns2, true);

        rewriter.rewriteToCopy(original, true);

        System.out.println("--- Rewritten:");
        original.write(System.out, FileUtils.langTurtle);
    }
}
