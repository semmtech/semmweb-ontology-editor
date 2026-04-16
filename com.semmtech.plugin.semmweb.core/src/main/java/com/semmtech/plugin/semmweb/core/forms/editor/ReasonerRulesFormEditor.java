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

package com.semmtech.plugin.semmweb.core.forms.editor;


import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.hp.hpl.jena.reasoner.rulesys.ClauseEntry;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.semmtech.plugin.semmweb.core.editors.text.RulesTextEditor;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


public class ReasonerRulesFormEditor extends FormEditor {

    private static final Logger logger = Logger.getLogger(ReasonerRulesFormEditor.class);

    private String filename;
    private String path;
    private RulesTextEditor textEditor;
    private int editorPageIndex;
    private TableViewer tableViewer;

    @Override
    protected void addPages() {
        try {
            // / Create page with table
            Composite parent = new Composite(getContainer(), SWT.NONE);
            parent.setLayout(new FillLayout());

            // GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
            // gridData.horizontalSpan = 2;
            // Label label = new Label(parent, SWT.NONE);
            // label.setLayoutData(gridData);
            // label.setText("Information will be displayed on this page of the editor");

            tableViewer = new TableViewer(parent);
            Table table = tableViewer.getTable();
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            TableColumn column = new TableColumn(table, SWT.NONE);
            column.setText("Name");
            column.setWidth(100);

            column = new TableColumn(table, SWT.NONE);
            column.setText("Head");
            column.setWidth(400);

            column = new TableColumn(table, SWT.NONE);
            column.setText("Body");
            column.setWidth(400);

            column = new TableColumn(table, SWT.NONE);
            column.setText("Direction");
            column.setWidth(100);

            tableViewer.setContentProvider(new ArrayContentProvider() {
                @Override
                public Object[] getElements(Object inputElement) {
                    if (inputElement instanceof Collection<?>) {
                        return ((Collection<?>) inputElement).toArray();
                    }
                    return null;
                }
            });
            tableViewer.setLabelProvider(new TableLabelProvider() {
                @Override
                public String getColumnText(Object element, int columnIndex) {
                    if (element instanceof Rule) {
                        Rule rule = (Rule) element;
                        String text = "";
                        if (columnIndex == 0) {
                            if (rule.getName() == null) {
                                return "";
                            }
                            return rule.getName().toString();
                        }
                        else if (columnIndex == 1) {
                            if (rule.getBody() == null || rule.getBody().length == 0) {
                                return "";
                            }
                            for (ClauseEntry entry : rule.getBody()) {
                                text += ((text == "") ? "" : " ") + "(" + entry.toString() + ")";
                            }
                            return text;
                        }
                        else if (columnIndex == 2) {
                            if (rule.getHead() == null || rule.getHead().length == 0) {
                                return "";
                            }

                            for (ClauseEntry entry : rule.getHead()) {

                                text += ((text == "") ? "" : " ++ ") + entry.toString();
                            }
                            return text;
                        }
                        else if (columnIndex == 3) {
                            if (rule.isAxiom()) {
                                return "axiom";
                            }
                            else if (rule.isBackward()) {
                                return "backward";
                            }
                            else if (rule.isMonotonic()) {
                                return "monotonic";
                            }

                            return "";
                        }
                    }
                    return null;
                }
            });

            int index = addPage(parent);
            setPageText(index, "Rules");

            // / Create and add TextEditor variant
            textEditor = new RulesTextEditor();
            // textEditor.addPropertyListener(new IPropertyListener() {
            // @Override
            // public void propertyChanged(Object source, int propId) {
            // /// When the DIRTY_FLAG changes from dirty to non-dirty (after a
            // save)
            // if (propId == PROP_DIRTY && !isDirty())
            // readModelFromEditor();
            // }
            // });
            editorPageIndex = addPage(textEditor, getEditorInput());
            setPageText(editorPageIndex, "Source");
            List<Rule> rules = Rule.rulesFromURL("file:" + path);
            tableViewer.setInput(rules);
        }
        catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    @SuppressWarnings("unused")
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        // InputStream inputStream = null;

        if (input instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput) input;
            File file = new File(fileEditorInput.getPath().toString());
            // try {
            // inputStream = new FileInputStream(file);
            // }
            // catch (FileNotFoundException e) {
            // e.printStackTrace();
            // }
            path = fileEditorInput.getPath().toString();
            filename = fileEditorInput.getPath().lastSegment();
            setInput(input);
        }
        else if (input instanceof FileStoreEditorInput) {
            // / Create a linked resource in an existing project, which points
            // to a file elsewhere
            // / in the file system.
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IProject project = workspace.getRoot().getProject("External Files");
            try {
                if (!project.exists()) {
                    project.create(null);
                }
                if (!project.isOpen()) {
                    project.open(null);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            FileStoreEditorInput storeInput = (FileStoreEditorInput) input;
            URI uri = storeInput.getURI();
            // / TODO: Not sure if correct...?
            path = uri.toString();
            File file = null;
            try {
                IFileStore store = EFS.getLocalFileSystem().getStore(uri);
                file = store.toLocalFile(EFS.NONE, null);
                // inputStream = new FileInputStream(file);

                IPath location = new Path(file.getPath());
                IFile projectFile = project.getFile(location.lastSegment());
                projectFile.createLink(location, IResource.NONE, null);
                FileEditorInput editorInput = new FileEditorInput(projectFile);
                setInput(editorInput);
                filename = location.lastSegment();
            }
            catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
        // if (input instanceof IURIEditorInput) {
        // ModelProviderRegistry.register(getModelURI(), (IModelProvider)this);
        // }
        setSite(site);
        setPartName(filename);

        // / TODO: Init rules table
    }

    /**
     * Returns the text of the TextEditor of this editor.
     * 
     * @return
     */
    @SuppressWarnings("unused")
    private String getEditorText() {
        return textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get();
    }

    /**
     * Create a toolkit that shares colors between editors.
     */
    @Override
    protected FormToolkit createToolkit(Display display) {
        return new FormToolkit(display);
    }
}
