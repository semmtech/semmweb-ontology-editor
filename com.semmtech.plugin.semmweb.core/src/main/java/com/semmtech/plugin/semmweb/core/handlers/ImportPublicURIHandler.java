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

package com.semmtech.plugin.semmweb.core.handlers;


import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;
import com.semmtech.plugin.semmweb.core.wizards.DownloadOntModelWizard;
import com.semmtech.ui.plugin.util.Selections;


public class ImportPublicURIHandler extends AbstractHandler {

    public static final String ID = "com.semmtech.plugin.semmweb.core.commands.importPublicURI";

    public static final String PARAMETER_PUBLIC_URI = "publicURI";
    public static final String PARAMETER_FILENAME = "filename";

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(ImportPublicURIHandler.class);

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String publicUri = event.getParameter(PARAMETER_PUBLIC_URI);
        String filename = event.getParameter(PARAMETER_FILENAME);

        ISelection selection = HandlerUtil.getCurrentSelection(event);
        ISemanticElement node = Selections.retrieveFirstAsType(selection, ISemanticElement.class);

        if (node == null || node.getProject() == null) {
            return null;
        }

        IProject project = node.getProject();
        String modelsPath = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
        IFolder folder = (IFolder) project.findMember(modelsPath);

        if (folder == null || !folder.exists()) {
            return null;
        }

        // FIXME: Should probably download from the External Alt URL, if
        // available. If that's not available, the publicUri will do. Also, if
        // the user changes the download url in the wizard, it might be a good
        // idea to adjust the External Alt URL afterwards.
        DownloadOntModelWizard wizard = new DownloadOntModelWizard(publicUri, folder, filename);
        Shell parentShell = HandlerUtil.getActiveShell(event);
        WizardDialog dialog = new WizardDialog(parentShell, wizard);
        dialog.create();
        if (dialog.open() == Window.OK) {
            DocumentManagerPreference preference = DocumentManagerPreference.fromProject(project);
            WorkspaceDocumentManagerConfiguration config = preference.getDocumentManagerConfig();
            WorkspaceOntologySpec spec = config.getOntologySpec(publicUri);
            if (spec == null) {
                spec = new WorkspaceOntologySpec(publicUri);
            }
            spec.setWorkspaceAltURL(wizard.getLocationURI());
            config.addOntologySpec(spec);
            preference.setDocumentManagerConfig(config);
            try {
                preference.save();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public static CommandContributionItem createCommand(IServiceLocator serviceLocator,
            String label, String imageKey, String publicUri, String fileName) {

        CommandContributionItemParameter commandParam = new CommandContributionItemParameter(
                serviceLocator, "importPublicURI", ImportPublicURIHandler.ID, SWT.PUSH);

        Map<String, String> parameters = Maps.newHashMap();
        parameters.put(ImportPublicURIHandler.PARAMETER_PUBLIC_URI, publicUri);
        parameters.put(ImportPublicURIHandler.PARAMETER_FILENAME, fileName);

        commandParam.label = label;
        commandParam.parameters = parameters;
        commandParam.icon = CorePlugin.getDefault().getImageDescriptor(imageKey);

        return new CommandContributionItem(commandParam);
    }

}
