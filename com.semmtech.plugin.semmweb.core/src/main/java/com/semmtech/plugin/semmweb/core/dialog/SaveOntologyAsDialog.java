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

package com.semmtech.plugin.semmweb.core.dialog;


import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.semmtech.plugin.semmweb.core.util.SemanticProjectUtils;
import com.semmtech.plugin.semmweb.core.viewers.ProjectResourceFilter;
import com.semmtech.semantics.util.FileUtils;
import com.semmtech.ui.plugin.ide.ResourceAndContainerGroup;


/**
 * A standard "Save As" dialog which solicits a path from the user. The
 * <code>getResult</code> method returns the path. Note that the folder at the
 * specified path might not exist and might need to be created.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @see org.eclipse.ui.dialogs.ContainerGenerator
 * @noextend This class is not intended to be subclassed by clients.
 */
public class SaveOntologyAsDialog extends TitleAreaDialog {
    private static final String WINDOW_TITLE = "Save As";
    private static final String DIALOG_TITLE = "Save As";
    private static final String DIALOG_MESSAGE = "Save ontology file to another location and/or format.";

    private String windowTitle;
    private String title;
    private String message;

    private IFile originalFile = null;

    private String originalName = null;

    private IPath result;
    private String extension;

    // widgets
    private ResourceAndContainerGroup resourceGroup;

    private Button okButton;

    /**
     * Image for title area
     */
    private Image dialogImage;

    /**
     * If true the dialog will show only the Projects folder and the returned
     * path is always the Models folder of the selected project
     */
    private boolean showOnlyProjects;

    /**
     * If this variable is set the project (if exists) will be automatically
     * selected in the list.
     */
    private IProject selectedProject;

    /**
     * Creates a new Save As dialog for no specific file.
     * 
     * @param parentShell
     *            the parent shell
     */
    public SaveOntologyAsDialog(Shell parentShell) {
        this(parentShell, WINDOW_TITLE, DIALOG_TITLE, DIALOG_MESSAGE);
    }

    public SaveOntologyAsDialog(Shell parentShell, String windowTitle, String title, String message) {
        super(parentShell);

        this.windowTitle = windowTitle;
        this.title = title;
        this.message = message;
        this.showOnlyProjects = false;

        setShellStyle(getShellStyle() | SWT.SHEET);
    }

    /*
     * (non-Javadoc) Method declared in Window.
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(windowTitle);
    }

    /*
     * (non-Javadoc) Method declared in Window.
     */
    @Override
    protected Control createContents(Composite parent) {
        Control contents = super.createContents(parent);

        initializeControls();
        validatePage();
        resourceGroup.setFocus();
        setTitle(title);
        setMessage(message);

        return contents;
    }

    /**
     * The <code>SaveAsDialog</code> implementation of this <code>Window</code>
     * method disposes of the banner image when the dialog is closed.
     */
    @Override
    public boolean close() {
        if (dialogImage != null) {
            dialogImage.dispose();
        }
        return super.close();
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        // top level composite
        Composite parentComposite = (Composite) super.createDialogArea(parent);

        // create a composite with standard margins and spacing
        Composite composite = new Composite(parentComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setFont(parentComposite.getFont());

        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                setDialogComplete(validatePage());
            }
        };

        resourceGroup = new ResourceAndContainerGroup(composite, listener, "File name:", "File",
                false, FileUtils.FORMATS_NAMES, 180);

        resourceGroup.setAllowExistingResources(true);

        if (showOnlyProjects) {
            resourceGroup.addViewerFilter(new ProjectResourceFilter());
        }

        return parentComposite;
    }

    /**
     * Returns the full path entered by the user.
     * <p>
     * Note that the file and container might not exist and would need to be
     * created. See the <code>IFile.create</code> method and the
     * <code>ContainerGenerator</code> class.
     * </p>
     * 
     * @return the path, or <code>null</code> if Cancel was pressed
     */
    public IPath getResult() {
        return result;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Initializes the controls of this dialog.
     */
    private void initializeControls() {
        if (showOnlyProjects) {
            if (selectedProject != null) {
                // resourceGroup.setSelectedContainer(selectedProject);
            }

            if (originalName != null) {
                resourceGroup.setResource(originalName);
            }
        }
        else {
            if (originalFile != null) {
                resourceGroup.setContainerFullPath(originalFile.getParent().getFullPath());
                resourceGroup.setResource(originalFile.getName());
            }
            else if (originalName != null) {
                resourceGroup.setResource(originalName);
            }
        }

        setDialogComplete(validatePage());
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    @Override
    protected void okPressed() {

        IPath path;

        if (showOnlyProjects) {
            IContainer selected = resourceGroup.getSelectedContainer();
            if (selected instanceof IProject) {
                IProject project = (IProject) selected;
                IFolder modelsDir = SemanticProjectUtils.getModelsFolder(project);
                path = modelsDir.getFullPath();
            }
            else {
                path = resourceGroup.getContainerFullPath();
            }
        }
        else {
            path = resourceGroup.getContainerFullPath();
        }

        // Get new path.
        path = path.append(resourceGroup.getResource());
        extension = resourceGroup.getExtension();

        // If the user does not supply a file extension and if the save
        // as dialog was provided a default file name append the extension
        // of the default filename to the new name
        if (path.getFileExtension() == null) {
            if (originalFile != null && originalFile.getFileExtension() != null) {
                path = path.addFileExtension(originalFile.getFileExtension());
            }
            else if (originalName != null) {
                int pos = originalName.lastIndexOf('.');
                if (++pos > 0 && pos < originalName.length()) {
                    path = path.addFileExtension(originalName.substring(pos));
                }
            }
        }

        // If the path already exists then confirm overwrite.
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        if (file.exists()) {
            String[] buttons = new String[] { IDialogConstants.YES_LABEL,
                    IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL };
            String question = String.format(
                    "The file \'%s\' already exists. Do you want to replace the existing file?",
                    path.toString());
            MessageDialog d = new MessageDialog(getShell(), "Question", null, question,
                    MessageDialog.QUESTION, buttons, 0) {
                @Override
                protected int getShellStyle() {
                    return super.getShellStyle() | SWT.SHEET;
                }
            };

            int overwrite = d.open();
            switch (overwrite) {
            case 0: // Yes
                break;
            case 1: // No
                return;
            case 2: // Cancel
            default:
                cancelPressed();
                return;
            }
        }

        // Store path and close.
        result = path;
        close();
    }

    /**
     * Sets the completion state of this dialog and adjusts the enable state of
     * the Ok button accordingly.
     * 
     * @param value
     *            <code>true</code> if this dialog is compelete, and
     *            <code>false</code> otherwise
     */
    protected void setDialogComplete(boolean value) {
        okButton.setEnabled(value);
    }

    /**
     * Sets the original file to use.
     * 
     * @param originalFile
     *            the original file
     */
    public void setOriginalFile(IFile originalFile) {
        this.originalFile = originalFile;
    }

    /**
     * Set the original file name to use. Used instead of
     * <code>setOriginalFile</code> when the original resource is not an IFile.
     * Must be called before <code>create</code>.
     * 
     * @param originalName
     *            default file name
     */
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    /**
     * Returns whether this page's visual components all contain valid values.
     * 
     * @return <code>true</code> if valid, and <code>false</code> otherwise
     */
    private boolean validatePage() {
        if (!resourceGroup.areAllValuesValid()) {
            if (!resourceGroup.getResource().equals("")) {
                setErrorMessage(resourceGroup.getProblemMessage());
            }
            else {
                setErrorMessage(null);
            }
            return false;
        }

        String resourceName = resourceGroup.getResource();
        IWorkspace workspace = ResourcesPlugin.getWorkspace();

        // Do not allow a closed project to be selected
        IPath fullPath = resourceGroup.getContainerFullPath();
        if (fullPath != null) {
            String projectName = fullPath.segment(0);
            IStatus isValidProjectName = workspace.validateName(projectName, IResource.PROJECT);
            if (isValidProjectName.isOK()) {
                IProject project = workspace.getRoot().getProject(projectName);
                if (!project.isOpen()) {
                    setErrorMessage("Close project message");
                    return false;
                }
            }
        }

        IStatus result = workspace.validateName(resourceName, IResource.FILE);
        if (!result.isOK()) {
            setErrorMessage(result.getMessage());
            return false;
        }

        setErrorMessage(null);
        return true;
    }

    /*
     * (non-Javadoc) TODO: Check purpose and workings and implement
     * 
     * @see org.eclipse.jface.window.Dialog#getDialogBoundsSettings()
     * 
     * @since 3.2
     */
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        IDialogSettings settings = null; // IDEWorkbenchPlugin.getDefault().getDialogSettings();
        // IDialogSettings section =
        // settings.getSection(DIALOG_SETTINGS_SECTION);
        // if (section == null) {
        // section = settings.addNewSection(DIALOG_SETTINGS_SECTION);
        // }
        return settings;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    public void setShowOnlyProjects(boolean showOnlyProjects) {
        this.showOnlyProjects = showOnlyProjects;
    }

    public void setSelectedProject(IProject selectedProject) {
        this.selectedProject = selectedProject;
    }
}