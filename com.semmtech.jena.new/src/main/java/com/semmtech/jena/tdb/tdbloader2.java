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

package com.semmtech.jena.tdb;


import java.util.List;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

import tdb.cmdline.CmdTDB;
import tdb.cmdline.CmdTDBGraph;

import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.store.GraphTDB;


public class tdbloader2 extends CmdTDBGraph {
    private boolean showProgress = true;

    static public void main(String... argv) {
        CmdTDB.init();
        TDB.setOptimizerWarningFlag(false);
        new tdbloader2(argv).mainAndExit();
    }

    protected tdbloader2(String[] argv) {
        super(argv);
    }

    @Override
    protected void processModulesAndArgs() {
        super.processModulesAndArgs();
    }

    @Override
    protected String getSummary() {
        return getCommandName() + " [--desc DATASET | -loc DIR] FILE ...";
    }

    @Override
    protected void exec() {
        if (isVerbose()) {
            System.out.println("Java maximum memory: " + Runtime.getRuntime().maxMemory());
            System.out.println(ARQ.getContext());
        }
        if (isVerbose())
            showProgress = true;
        if (isQuiet())
            showProgress = false;

        List<String> urls = getPositional();
        if (urls.size() == 0)
            urls.add("-");

        if (graphName == null) {
            loadQuads(urls);
            return;
        }

        for (String url : urls) {
            Lang lang = RDFLanguages.filenameToLang(url);
            if (lang != null && RDFLanguages.isQuads(lang)) {
                System.err
                        .println("Warning: Quads format given - only the default graph is loaded into the graph for --graph");
                break;
            }
        }

        loadNamedGraph(urls);
    }

    void loadNamedGraph(List<String> urls) {
        GraphTDB graph = getGraph();
        TDBLoader.load(graph, urls, showProgress);
        return;
    }

    void loadQuads(List<String> urls) {
        TDBLoader.load(getDatasetGraphTDB(), urls, showProgress);
        return;
    }
}