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


import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;


/**
 * A text editor which allows users to edit the source of semantic documents
 * (RDF/OWL documents). This class will be extended by different specific text
 * editors, see OWLTextEditor, etc.
 * 
 * @author Mike Henrichs
 * 
 */
public class SemanticTextEditor extends TextEditor {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SemanticTextEditor.class);
    public static final String SEMANTIC_TEXT_EDITOR_CONTEXT_MENU_ID = "#SemanticTextEditorContext";

    public SemanticTextEditor() {
        super();
        setEditorContextMenuId(SEMANTIC_TEXT_EDITOR_CONTEXT_MENU_ID);
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);
    }

    @Override
    protected void installEncodingSupport() {
        super.installEncodingSupport();
        refreshEncoding();
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        refreshEncoding();
    }

    protected void refreshEncoding() {
        IEncodingSupport encodingSupport = (IEncodingSupport) getAdapter(IEncodingSupport.class);
        IEditorInput input = getEditorInput();

        if (encodingSupport != null && input instanceof FileEditorInput) {
            IFile file = ((FileEditorInput) input).getFile();
            try {
                String encoding = file.getCharset();
                encodingSupport.setEncoding(encoding);
            }
            catch (CoreException ex) {
                ex.printStackTrace();
            }
        }
    }
}
