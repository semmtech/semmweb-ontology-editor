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

package com.semmtech.plugin.semmweb.core.wizards;


import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;


/**
 * 
 * @author Sander Stolk
 */
public class DownloadOntModelWizardPage extends WizardPage {
    private static final Map<String, String> formatNames = com.semmtech.semantics.util.FileUtils.FORMATS_NAMES;

    private String uri = "";
    private String filename = "";
    private IContainer container;

    private Text fileText;
    private Text urlText;
    private Combo typesCombo;

    protected DownloadOntModelWizardPage(ISelection selection, String uri, String filename) {
        this(getContainerFromSelection(selection), uri, filename);
    }

    protected DownloadOntModelWizardPage(IContainer container, String uri, String filename) {
        super("downloadPage");
        setTitle("Download RDF/OWL Ontology File from Web");
        setDescription("This wizard downloads an ontology file from the web and saves it locally.");
        this.uri = Strings.nullToEmpty(uri);
        this.filename = Strings.nullToEmpty(filename);
        this.container = container;
    }

    private static IContainer getContainerFromSelection(ISelection selection) {
        IStructuredSelection structuredSelection = Selections.toStructured(selection);
        if (structuredSelection == null || structuredSelection.isEmpty()) {
            return null;
        }

        Object selected = structuredSelection.getFirstElement();
        if (selected != null) {
            if (selected instanceof IProject) {
                return calculateModelsFolder((IProject) selected);
            }
            else if (selected instanceof IContainer) {
                return (IContainer) selected;
            }
            else if (selected instanceof IResource) {
                IResource resource = (IResource) selected;
                return resource.getParent();
            }
            else if (selected instanceof ISemanticElement) {
                ISemanticElement node = (ISemanticElement) selected;
                IProject project = node.getProject();
                if (project != null) {
                    return calculateModelsFolder(project);
                }
            }
        }
        return null;
    }

    /**
     * Returns the correct container within the project to which models should
     * be added; this is either the models folder or the project itself. If the
     * project is null, null is returned.
     * 
     * @param project
     * @return
     */
    private static IContainer calculateModelsFolder(IProject project) {
        if (project == null) {
            return null;
        }
        String path = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
        if (!Strings.isNullOrEmpty(path) && project.findMember(path) != null) {
            return (IContainer) project.findMember(path);
        }
        return project;
    }

    @Override
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        top.setLayout(layout);

        layout.numColumns = 3;

        Label label = new Label(top, SWT.WRAP);
        GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        layoutData.widthHint = 510;
        layoutData.heightHint = 34;
        label.setLayoutData(layoutData);

        label.setText("Specify the URL from which to download the ontology and specify how the result should be saved.");

        // Base URI
        label = new Label(top, SWT.NULL);
        label.setText("URL:");
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1);
        layoutData.widthHint = 65;
        label.setLayoutData(layoutData);

        urlText = new Text(top, SWT.BORDER | SWT.SINGLE);
        urlText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
        urlText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                uri = urlText.getText().trim();
                int endIndex = uri.length();
                if (uri.lastIndexOf("#") > 0 && uri.lastIndexOf("#") < endIndex) {
                    endIndex = uri.lastIndexOf("#");
                }
                if (uri.lastIndexOf("?") > 0 && uri.lastIndexOf("?") < endIndex) {
                    endIndex = uri.lastIndexOf("?");
                }
                if (endIndex < 0) {
                    endIndex = 0;
                }
                if (filename == null) {
                    filename = Strings.nullToEmpty(new Path(uri.substring(0, endIndex))
                            .lastSegment());
                    if (fileText != null) {
                        fileText.setText(Strings.nullToEmpty(filename));
                        updateFileExtension();
                    }
                }

                // urlText.setText(uri);
                validatePage();
            }
        });
        urlText.setText(Strings.nullToEmpty(uri));

        if (container == null) {
            label = new Label(top, SWT.NONE);
            layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
            label.setLayoutData(layoutData);
            label.setText("Location:");

            final Composite treeComposite = new Composite(top, SWT.BORDER);
            GridDataFactory.fillDefaults().grab(true, true).span(2, 1).hint(SWT.DEFAULT, 145)
                    .applyTo(treeComposite);
            GridLayoutFactory.fillDefaults().applyTo(treeComposite);

            final CommonFilterViewer viewer = new CommonFilterViewer(treeComposite, SWT.SINGLE
                    | SWT.H_SCROLL | SWT.V_SCROLL);
            GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getTree());

            viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
            viewer.addFilter(new WorkspaceResourcesFilter(IResource.PROJECT, null, false, false,
                    true));
            viewer.expandAll(); // eliminate the arrows in front of projects
            viewer.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                    container = getContainerFromSelection(selection);
                    validatePage();
                }
            });

            if (container != null) {
                viewer.setSelection(new StructuredSelection(container.getProject()), true);
            }
        }

        // Filename
        label = new Label(top, SWT.NULL);
        label.setText("Filename:");

        fileText = new Text(top, SWT.BORDER | SWT.SINGLE);
        fileText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
        fileText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                filename = fileText.getText().trim();
                // fileText.setText(filename);
                validatePage();
            }
        });
        fileText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateFileExtension();
            }
        });
        fileText.setText(Strings.nullToEmpty(filename));

        label = new Label(top, SWT.NULL);
        label.setText("Format:");

        typesCombo = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
        String[] items = new String[formatNames.keySet().size()];
        formatNames.keySet().toArray(items);
        typesCombo.setItems(items);
        typesCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateFileExtension();
                validatePage();
            }
        });
        if (items.length > 0) {
            typesCombo.select(0);
            updateFileExtension();
        }

        setControl(top);
        if (Strings.isNullOrEmpty(uri)) {
            urlText.setFocus();
        }
    }

    protected void updateFileExtension() {
        String extension = getExtension();
        if (filename.lastIndexOf(".") > 0) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        if (filename.length() > 0) {
            filename = String.format("%s.%s", filename, extension);
            fileText.setText(filename);
        }
    }

    protected void validatePage() {
        String errorMessage = null;
        if (container == null) {
            errorMessage = "Folder must be specified";
        }
        else if (!container.isAccessible()) {
            errorMessage = "Project must be writable";
        }
        else if (Strings.isNullOrEmpty(filename.trim())) {
            errorMessage = "Filename must be specified";
        }
        else if (Strings.isNullOrEmpty(uri.trim())) {
            errorMessage = "URI must be specified";
        }
        if (!ResourcesUtil.existsSemanticProjects()) {
            errorMessage = "Create a Project before adding a new Semantic File";
        }
        setErrorMessage(errorMessage);
        setPageComplete(errorMessage == null);
    }

    public String getURL() {
        return uri;
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return formatNames.get(typesCombo.getText());
    }

    public IContainer getFolder() {
        return container;
    }
}
