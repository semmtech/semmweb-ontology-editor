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

package com.semmtech.plugin.semmweb.sparql.actions;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.semmtech.plugin.semmweb.sparql.debug.ui.SparqlLaunchConfigurationConstants;


public class LaunchSparqlFileAction extends Action {
    private final ISelectionProvider selectionProvider;
    private IResource selected;

    public LaunchSparqlFileAction(String text, ISelectionProvider selectionProvider) {
        super(text);
        this.selectionProvider = selectionProvider;
    }

    @Override
    public boolean isEnabled() {
        ISelection selection = selectionProvider.getSelection();
        selected = null;
        if (!selection.isEmpty()) {
            IStructuredSelection structured = (IStructuredSelection) selection;
            if (structured.size() == 1 && structured.getFirstElement() instanceof IResource) {
                selected = (IResource) structured.getFirstElement();
                // SPARQL files only
                String extension = selected.getFileExtension();
                if (extension != null && (extension.equals("rq") || extension.equals("sparql"))) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        String name = selected.getName();
        IPath path = selected.getFullPath();
        String wspacePath = "${workspace_loc:" + path.toString() + "}";

        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String groupId = "org.eclipse.debug.ui.launchGroup.run";

        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        ILaunchConfigurationType configType = manager
                .getLaunchConfigurationType(SparqlLaunchConfigurationConstants.ID_SPARQL_QUERY);

        try {
            // try to open LCDialog on an existing config for the selected query
            ILaunchConfiguration[] currConfigs = manager.getLaunchConfigurations(configType);
            for (int i = 0; i < currConfigs.length; i++) {
                if (currConfigs[i].getName().equals(name)
                        && currConfigs[i].getAttribute(
                                SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE, "").equals(
                                wspacePath)) {
                    DebugUITools
                            .openLaunchConfigurationDialog(shell, currConfigs[i], groupId, null);
                    return;
                }
            }

            // else, try to open LCDialog on a new config for the selected query
            ILaunchConfigurationWorkingCopy newConfig = configType.newInstance(null,
                    manager.generateLaunchConfigurationName(name));
            newConfig.setAttribute(SparqlLaunchConfigurationConstants.ATTR_QUERY_FILE, wspacePath);
            newConfig.doSave();
            DebugUITools.openLaunchConfigurationDialog(shell, newConfig, groupId, null);
            return;
        }
        catch (CoreException e) {
            e.printStackTrace();
        }

        // if above options fail, open LCDialog on the SPARQL config type
        IStructuredSelection selection = new StructuredSelection(configType);
        DebugUITools.openLaunchConfigurationDialogOnGroup(shell, selection, groupId);
    }
}
