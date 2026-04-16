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

package com.semmtech.plugin.semmweb.sparql.forms.editor;


import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.semmtech.plugin.semmweb.sparql.editors.SparqlTextEditor;


public class SparqlQueryFormEditor extends FormEditor {
    public static final String ID = "com.semmtech.plugin.semmweb.sparql.editors.sparqlQueryEditor";

    private int editorPageIndex;
    private SparqlTextEditor textEditor;
    private String filename;

    @Override
    protected void addPages() {
        try {
            textEditor = new SparqlTextEditor();
            textEditor.addPropertyListener(new IPropertyListener() {
                @Override
                public void propertyChanged(Object source, int propId) {
                    // / When the DIRTY_FLAG changes from dirty to non-dirty
                    // (after a save)
                    // if (propId == PROP_DIRTY && !isDirty())
                    // readModelFromEditor();
                }
            });

            editorPageIndex = addPage(textEditor, getEditorInput());
            setPageText(editorPageIndex, "Syntax");
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        if (!isDirty()) {
            return;
        }
        textEditor.doSave(monitor);
    }

    @Override
    public void doSaveAs() {
        textEditor.doSaveAs();
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        if (input instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput) input;
            filename = fileEditorInput.getPath().lastSegment();
            setInput(input);
        }
        else if (input instanceof FileStoreEditorInput) {
            // Create a linked resource in an existing project, which points
            // to a file elsewhere in the file system.

            FileStoreEditorInput storeInput = (FileStoreEditorInput) input;
            URI uri = storeInput.getURI();
            File file = null;
            try {
                IFileStore location = EFS.getLocalFileSystem().getStore(uri);
                file = location.toLocalFile(EFS.NONE, null);
                setInput(input);
                filename = file.getAbsolutePath();
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }

        setSite(site);
        setPartName(filename);
    }
}
