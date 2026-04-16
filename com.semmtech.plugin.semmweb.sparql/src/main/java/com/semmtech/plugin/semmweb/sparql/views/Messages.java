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

package com.semmtech.plugin.semmweb.sparql.views;


import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.semmtech.plugin.semmweb.sparql.views.messages"; //$NON-NLS-1$
    public static String SparqlView_AllNamespaces;
    public static String SparqlView_AskAnswer;
    public static String SparqlView_AskQueryTitle;
    public static String SparqlView_CreatingQuery;
    public static String SparqlView_DefaultNamespace;
    public static String SparqlView_ExecuteQuery;
    public static String SparqlView_ExecuteQuery_2;
    public static String SparqlView_FoundSolutions;
    public static String SparqlView_PrefixesMenu;
    public static String SparqlView_RetrievingSolutions;
    public static String SparqlView_RunningQueryLocal;
    public static String SparqlView_RunningQueryOnFile;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
