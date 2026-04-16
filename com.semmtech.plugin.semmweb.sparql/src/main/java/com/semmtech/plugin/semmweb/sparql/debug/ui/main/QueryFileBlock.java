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

package com.semmtech.plugin.semmweb.sparql.debug.ui.main;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import com.semmtech.plugin.semmweb.sparql.debug.ui.Messages;
import com.semmtech.plugin.semmweb.sparql.debug.ui.ResourceSelectionBlock;
import com.semmtech.plugin.semmweb.sparql.debug.ui.SparqlLaunchConfigurationConstants;


public class QueryFileBlock extends ResourceSelectionBlock {

    private final IFile defaultFile;

    public QueryFileBlock(IFile defaultFile) {
        super(IResource.FILE, false);
        setFileExtensions(new String[] { "sparql" });
        this.defaultFile = defaultFile;
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
        String path = ""; //$NON-NLS-1$
        if (defaultFile != null) {
            path = VariablesPlugin
                    .getDefault()
                    .getStringVariableManager()
                    .generateVariableExpression(
                            "workspace_loc", defaultFile.getFullPath().toPortableString()); //$NON-NLS-1$
        }
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE, path);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
        setLaunchConfiguration(configuration);
        try {
            String wd = configuration.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE, (String) null);
            if (wd != null) {
                setText(wd);
            }
        }
        catch (CoreException e) {
            setErrorMessage(Messages.InputFileBlock_Exception_occurred_reading_configuration
                    + e.getStatus().getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
        configuration.setAttribute(SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE, getText());
    }

    @Override
    public String getName() {
        return "InputFileBlock_Name";
    }

    @SuppressWarnings("unused")
    @Override
    protected void textModified() {
        IPath path = null;
        String workingDirPath = getText();
        if (workingDirPath.indexOf("${") >= 0) //$NON-NLS-1$
        {
            IStringVariableManager manager = VariablesPlugin.getDefault()
                    .getStringVariableManager();
            try {
                manager.validateStringVariables(workingDirPath);
                path = new Path(manager.performStringSubstitution(workingDirPath));
            }
            catch (CoreException e) {
            }
        }
        else if (workingDirPath.length() > 0) {
            path = new Path(workingDirPath);
        }
    }

    @Override
    protected String getMessage(int type) {
        switch (type) {
        case ERROR_DIRECTORY_NOT_SPECIFIED:
            return Messages.InputFileBlock_DIRECTORY_NOT_SPECIFIED;
        case ERROR_DIRECTORY_DOES_NOT_EXIST:
            return Messages.InputFileBlock_DIRECTORY_DOES_NOT_EXIST;
        case GROUP_NAME:
            return "SPARQL File";
        case USE_DEFAULT_RADIO:
            return Messages.InputFileBlock_DEFAULT_RADIO;
        case USE_OTHER_RADIO:
            return Messages.InputFileBlock_OTHER_RADIO;
        case DIRECTORY_DIALOG_MESSAGE:
            return Messages.InputFileBlock_DIALOG_MESSAGE;
        case WORKSPACE_DIALOG_MESSAGE:
            return Messages.InputFileBlock_WORKSPACE_DIALOG_MESSAGE;
        case VARIABLES_BUTTON:
            return Messages.InputFileBlock_VARIABLES_BUTTON;
        case FILE_SYSTEM_BUTTON:
            return Messages.InputFileBlock_FILE_SYSTEM_BUTTON;
        case WORKSPACE_BUTTON:
            return Messages.InputFileBlock_WORKSPACE_BUTTON;
        case WORKSPACE_DIALOG_TITLE:
            return Messages.InputFileBlock_WORKSPACE_DIALOG_TITLE;
        case OPENFILES_BUTTON:
            return Messages.InputFileBlock_OPENFILES_BUTTON;
        case OPENFILES_DIALOG_TITLE:
            return Messages.InputFileBlock_OPENFILES_DIALOG;
        }
        return "" + type;
    }

    @Override
    protected void updateResourceText(boolean useDefault) {
    }

}
