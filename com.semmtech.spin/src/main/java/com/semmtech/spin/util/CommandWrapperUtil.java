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

package com.semmtech.spin.util;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.SPINQueryFinder;

import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;


/**
 * 
 * @author Sander Stolk
 */
public class CommandWrapperUtil {
    public static CommandWrapper find(String uri, Model model) {
        Map<Resource, List<CommandWrapper>> class2Query = Maps.newHashMap();

        StmtIterator typeStmtIter = model.listStatements(null, RDF.type, model.createResource(uri));
        while (typeStmtIter.hasNext()) {
            Statement typeStatement = typeStmtIter.next();

            Resource subject = typeStatement.getSubject();
            StmtIterator classStmtIter = model.listStatements(null, null, subject);
            while (classStmtIter.hasNext()) {
                Statement classStatement = classStmtIter.next();

                SPINQueryFinder.add(class2Query, classStatement, model, false, true);

                if (!class2Query.isEmpty()) {
                    Iterator<List<CommandWrapper>> iter = class2Query.values().iterator();
                    while (iter.hasNext()) {
                        List<CommandWrapper> list = iter.next();
                        if (!list.isEmpty()) {
                            return list.get(0);
                        }
                    }
                }
            }
        }

        return null;
    }
}
