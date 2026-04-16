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

package com.semmtech.plugin.semmweb.sparql.editors;


import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;


/**
 * A text editor which allows users to edit the source of semantic documents
 * (RDF/OWL documents). This class will be extended by different specific text
 * editors, see OWLTextEditor, etc.
 * 
 * http://help.eclipse.org/juno/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%
 * 2Fguide%2Feditors_sourceviewers.htm
 * http://help.eclipse.org/juno/index.jsp?topic
 * =%2Forg.eclipse.platform.doc.isv%2Fguide%2Feditors.htm&cp=2_0_13
 * 
 * http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.
 * isv%2Fguide%2Feditors.htm&cp=2_0_13
 * 
 * @author Mike Henrichs
 * 
 */
public class SparqlTextEditor extends TextEditor {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SparqlTextEditor.class);
    public static final String SPARQL_TEXT_EDITOR_CONTEXT_MENU_ID = "#SparqlTextEditorContext";

    private ColorManager colorManager;

    public SparqlTextEditor() {
        super();
        setEditorContextMenuId(SPARQL_TEXT_EDITOR_CONTEXT_MENU_ID);
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new SparqlViewerConfiguration(colorManager));
        setDocumentProvider(new SparqlDocumentProvider());
    }

    @Override
    public void dispose() {
        colorManager.dispose();
        super.dispose();
    }

    private void updateModifiedFlag() {
        // IFileEditorInput input = (IFileEditorInput) getEditorInput();
        // if (input != null) {
        // IFile file = input.getFile();
        // if (file != null &&
        // CoreResourcePropertiesManager.isPublishedResource(file)) {
        // CoreResourcePropertiesManager.setModified(input.getFile(), true);
        // CoreResourceFileDecorator.refreshAll();
        // }
        // }
    }

    @Override
    public void doSave(IProgressMonitor progressMonitor) {
        super.doSave(progressMonitor);
        updateModifiedFlag();
    }

    @Override
    public void doSaveAs() {
        SaveAsDialog dialog = new SaveAsDialog(getSite().getShell());
        if (dialog.open() == Window.OK) {
            // IPath path = dialog.getResult();
            // String extension = dialog.getExtension();
            //
            // logger.debug("path = " + path);
            // logger.debug("extension = " + extension);
            //
            // IModelProvider provider =
            // CorePlugin.getDefault().getActiveModelProvider();
            // if (provider != null) {
            // Model model = provider.getBaseModel();
            // try {
            // IWorkspaceRoot workspace =
            // ResourcesPlugin.getWorkspace().getRoot();
            // IFile resource = workspace.getFile(path);
            // File file = resource.getLocation().toFile();
            // file.createNewFile();
            // FileOutputStream stream = new FileOutputStream(file);
            // model.write(stream, FileUtils.guessLang(String.format("*.%s",
            // extension)));
            // updateModifiedFlag();
            // resource.getParent().refreshLocal(1, null);
            // }
            // catch (FileNotFoundException ex) {
            // logger.error("FileNotFoundException: " + ex.getMessage());
            // ex.printStackTrace();
            // }
            // catch (IOException ex) {
            // logger.error("IOException: " + ex.getMessage());
            // ex.printStackTrace();
            // }
            // catch (CoreException ex) {
            // logger.error("CoreException: " + ex.getMessage());
            // ex.printStackTrace();
            // }
            // }
        }
    }

    /**
     * This is needed because otherwise non-workspace resources generate a
     * texteditor with ERROR
     * 
     * @param input
     * @return
     */
    private IDocumentProvider createDocumentProvider(IEditorInput input) {
        if (input instanceof IFileEditorInput) {
            return new TextFileDocumentProvider();
        }
        return new TextFileDocumentProvider();
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        setDocumentProvider(createDocumentProvider(input));
        super.doSetInput(input);
    }
}
