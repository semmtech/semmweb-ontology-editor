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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.viewers.WorkspaceResourcesFilter;
import com.semmtech.ui.plugin.util.Selections;
import com.semmtech.ui.plugin.viewers.CommonFilterViewer;


/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (owl).
 */

public class OntologyFileWizardPage extends WizardPage {
    private static final Map<String, String> formatNames = com.semmtech.semantics.util.FileUtils.FORMATS_NAMES;

    private static final String PAGE_NAME = "filePage";
    private static final String PAGE_TITLE = "RDF/OWL Ontology File";
    private static final String PAGE_DESCRIPTION = "This wizard creates an ontology that can be shared using SEMMweb.";

    private IContainer defaultContainer;
    private IContainer container;

    private String filename;
    private String extension;
    private Text filenameText;
    private Combo formatCombo;

    private boolean showDefaultLocationCheckbox = true;
    private boolean useDefault;

    private Button defaultCheckbox;

    private Composite treeComposite;
    private CommonFilterViewer viewer;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public OntologyFileWizardPage(ISelection selection) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        defaultContainer = getContainerFromSelection(selection);
        container = defaultContainer;
        filename = "";
    }

    private IContainer getContainerFromSelection(ISelection selection) {
        IStructuredSelection structuredSelection = Selections.toStructured(selection);
        if (structuredSelection == null || structuredSelection.isEmpty()) {
            return null;
        }

        Object selected = structuredSelection.getFirstElement();
        if (selected != null) {
            if (selected instanceof IProject) {
                return SemanticProjectUtils.getModelsFolder((IProject) selected);
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
                    return SemanticProjectUtils.getModelsFolder(project);
                }
            }
        }
        return null;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 8;
        top.setLayout(layout);

        Label label = new Label(top, SWT.WRAP);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 500;
        label.setLayoutData(layoutData);
        label.setText("Specify the location and format of the RDF/OWL file used to store the ontology locally.");

        label = new Label(top, SWT.NONE);
        GridDataFactory.swtDefaults().span(2, 1).hint(SWT.DEFAULT, 5).applyTo(label);

        if (showDefaultLocationCheckbox && defaultContainer != null) {
            defaultCheckbox = new Button(top, SWT.CHECK);
            defaultCheckbox.setSelection(useDefault);
            GridDataFactory.swtDefaults().span(2, 1).applyTo(defaultCheckbox);
            defaultCheckbox.setText("Use default location");
            defaultCheckbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    useDefault = defaultCheckbox.getSelection();
                    treeComposite.setEnabled(!useDefault);
                    viewer.getTree().setEnabled(!useDefault);
                }
            });

            label = new Label(top, SWT.NONE);
            GridDataFactory.swtDefaults().span(2, 1).hint(SWT.DEFAULT, 0).applyTo(label);
        }

        layoutData = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1);
        layoutData.widthHint = 120;

        if (showDefaultLocationCheckbox || defaultContainer == null) {
            label = new Label(top, SWT.NONE);
            label.setLayoutData(layoutData);
            label.setText("Location:");

            treeComposite = new Composite(top, SWT.BORDER);
            treeComposite.setEnabled(!useDefault);
            GridDataFactory.fillDefaults().grab(true, true).span(2, 1).hint(SWT.DEFAULT, 145)
                    .applyTo(treeComposite);
            GridLayoutFactory.fillDefaults().applyTo(treeComposite);

            viewer = new CommonFilterViewer(treeComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
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
                    setPageComplete(validatePage());
                }
            });
            viewer.getTree().setEnabled(!useDefault);

            if (container != null) {
                viewer.setSelection(new StructuredSelection(container.getProject()), true);
            }
        }

        label = new Label(top, SWT.NONE);
        GridDataFactory.swtDefaults().span(2, 1).hint(SWT.DEFAULT, 1).applyTo(label);

        label = new Label(top, SWT.NONE);
        label.setText("Filename:");
        GridDataFactory.swtDefaults().hint(65, SWT.DEFAULT).align(SWT.BEGINNING, SWT.CENTER)
                .applyTo(label);

        filenameText = new Text(top, SWT.BORDER);
        filenameText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1,
                1));
        filenameText.setText(filename == null ? "" : filename);
        filenameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                filename = filenameText.getText();
                setPageComplete(validatePage());
            }
        });
        filenameText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateExtension();
            }
        });

        label = new Label(top, SWT.NONE);
        label.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 1));
        label.setText("Format:");

        formatCombo = new Combo(top, SWT.DROP_DOWN | SWT.READ_ONLY);
        formatCombo.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false,
                1, 1));

        String[] items = new String[formatNames.keySet().size()];
        formatNames.keySet().toArray(items);
        formatCombo.setItems(items);

        formatCombo.select(0);
        extension = formatNames.get(formatCombo.getText());

        formatCombo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                extension = formatNames.get(formatCombo.getText());
                updateExtension();
                setPageComplete(validatePage());
            }
        });

        setPageComplete(validatePage());
        setControl(top);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        filenameText.setFocus();
    }

    public void setUseDefault(boolean useDefault) {
        this.useDefault = useDefault;
    }

    public void showUseDefaultCheckbox(boolean show) {
        showDefaultLocationCheckbox = show;
    }

    private IContainer getCurrentContainer() {
        if (useDefault && defaultContainer != null) {
            return defaultContainer;
        }
        return container;
    }

    private boolean validatePage() {
        String errorMessage = null;

        IContainer container = getCurrentContainer();
        if (container == null) {
            errorMessage = "Please select a container for the new file";
        }
        else if (Strings.isNullOrEmpty(filename)) {
            errorMessage = "Filename cannot be empty";
        }
        else {
            IFile file = container.getFile(new Path(filename));
            if (file.exists()) {
                errorMessage = "File already exists";
            }
        }
        setErrorMessage(errorMessage);
        return (errorMessage == null);
    }

    public String getFolder() {
        IContainer container = getCurrentContainer();
        if (container != null) {
            return container.getFullPath().toString();
        }
        return null;
    }

    public IProject getProject() {
        IContainer container = getCurrentContainer();
        if (container != null) {
            if (container instanceof IProject) {
                return (IProject) container;
            }

            return container.getProject();
        }
        return null;
    }

    public void setContainer(IContainer container) {
        this.defaultContainer = getContainerFromSelection(new StructuredSelection(container));
        this.container = defaultContainer;
    }

    /**
     * Insert a filename in the text filed only if there isn't one
     */
    public void suggestFilename(String filename) {
        if (filenameText != null && !filenameText.isDisposed()
                && Strings.isNullOrEmpty(this.filename)) {
            filenameText.setText(filename);
            this.filename = filename;
        }
    }

    public String getFilename() {
        return filename;
    }

    public String getExtension() {
        return extension;
    }

    private void updateExtension() {
        if (filename.lastIndexOf(".") > 0) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        if (filename.length() > 0) {
            filename = String.format("%s.%s", filename, extension);
            filenameText.setText(filename);
        }
    }
}