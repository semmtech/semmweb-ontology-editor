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

package com.semmtech.plugin.semmweb.core.editors.text;


/**
 * A special semantic text editor, designed to work with Turtle semantic
 * documents. This text editor will be used to modify RDF/OWL ontology files
 * which are formatted using the "Turtle" lang. these files will preferably have
 * a .ttl extension.
 * 
 * @author Mike Henrichs
 * 
 */
public class TurtleTextEditor extends SemanticTextEditor {
    public static final String TURTLE_EDITOR_CONTEXT_MENU_ID = "#TurtleTextEditorContext";

    /**
     * TODO: Include a color highlighting (see XML editor from example templates
     * plug-in development).
     */
    public TurtleTextEditor() {
        super();
        setEditorContextMenuId(TURTLE_EDITOR_CONTEXT_MENU_ID);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
