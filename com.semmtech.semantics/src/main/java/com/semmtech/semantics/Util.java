/********************************************************************************
 * Copyright (c) 2011-2016, 2026 Semmtech B.V., Hoofddorp.
 *    ___  _____ __  __ __  __ _____ _____ ___ _   _ 
 *   / __|| ____|  \/  |  \/  |_   _| ____/ __| | | |
 *   \__ \|  _| | |\/| | |\/| | | | |  _|| |  | |_| |
 *    __) | |___| |  | | |  | | | | | |__| |__|  _  |
 *   |___/|_____|_|  |_|_|  |_| |_| |_____\___|_| |_| B.V.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package com.semmtech.semantics;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


/**
 * @author Mike Henrichs
 * 
 */
public class Util {
    public static void printStatements(Model model) {
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();
            System.out.println(String.format("%s %s %s", subject.toString(), predicate.toString(),
                    ((object instanceof Resource) ? object.toString() : "\"" + object.toString()
                            + "\"")));
        }
    }

    public static String createResourceLocalName(String name) {
        String localName = "";
        String[] parts = name.split(" ");
        for (int i = 0; i < parts.length; i++)
            localName += com.semmtech.Util.firstToUpper(parts[i]);
        return localName;
    }

    public static String createPropertyLocalName(String name) {
        String localName = "";
        String[] parts = name.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if (i == 0)
                localName += com.semmtech.Util.firstToLower(parts[0]);
            else
                localName += com.semmtech.Util.firstToUpper(parts[i]);
        }
        return localName;
    }

    public static boolean validOWLLocalName(String localName) {
        for (int i = 0; i < localName.length(); i++)
            if (!Character.isLetter(localName.charAt(i)))
                return false;
        return true;
    }

    public static void saveOWL(Model model, String filename, String base, String lang) {
        // / Save a copy of the GTF model
        RDFWriter writer = model.getWriter(lang);
        writer.setProperty("showXMLDeclaration", "true");
        writer.setProperty("tab", "2");
        writer.setProperty("relativeURIs", "");
        writer.setProperty("xmlbase", base);

        // / Create output
        try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(filename), "UTF8")) {
            writer.write(model, out, base);
            out.flush();
            out.close();
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
