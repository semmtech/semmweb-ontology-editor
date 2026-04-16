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

package com.semmtech.plugin.semmweb.laces.ldp.actions;


import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import com.semmtech.plugin.semmweb.core.actions.SemanticProjectActionProvider;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.plugin.semmweb.core.util.PublicationProviderUtil;
import com.semmtech.plugin.semmweb.laces.ldp.handlers.PublishOntologyHandler;
import com.semmtech.plugin.semmweb.laces.ldp.handlers.UpdateOntologyHandler;
import com.semmtech.ui.plugin.navigator.CommonViewerActionProvider;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Creates Target and Source menus
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class PublicationActionProvider extends CommonViewerActionProvider {

    @Override
    public void fillContextMenu(IMenuManager parent) {
        IMenuManager publishMenu = parent
                .findMenuUsingPath(SemanticProjectActionProvider.MENU_PUBLISH);

        IMenuManager sourceMenu = parent
                .findMenuUsingPath(SemanticProjectActionProvider.MENU_FETCH);

        if (publishMenu == null) {
            publishMenu = new MenuManager("Publish", SemanticProjectActionProvider.MENU_PUBLISH);
            parent.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, publishMenu);
        }

        if (sourceMenu == null) {
            sourceMenu = new MenuManager("Fetch", SemanticProjectActionProvider.MENU_FETCH);
            parent.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, sourceMenu);
        }

        ISelection selection = getSelection();
        if (selection.isEmpty() && Selections.toStructured(selection).size() != 1) {
            return;
        }

        Object selected = Selections.retrieveFirst(selection);
        IResource resource = null;

        if (selected instanceof IResource) {
            resource = (IResource) selected;
        }
        else if (selected instanceof IModel) {
            // TODO: Prefer to use the Adapter structure
            IModel model = (IModel) selected;
            resource = model.getResource();
        }
        else {
            return;
        }

        if (resource == null) {
            return;
        }

        // Publish
        if (CoreResourcePropertiesManager.hasSourceLocation(resource)
                && PublicationProviderUtil.getPublisherFor(CoreResourcePropertiesManager
                        .getSourceLocation(resource)) != null) {
            // Target -> Publish
            CommandContributionItem publish = createCommand("publishOntology",
                    PublishOntologyHandler.ID, "Republish");
            publishMenu.add(publish);
        }
        else {
            // Target -> Publish...
            CommandContributionItem share = createCommand("republishOntology",
                    PublishOntologyHandler.ID, "Publish...");
            publishMenu.add(share);
        }

        // Fetch
        if (CoreResourcePropertiesManager.hasSourceLocation(resource)) {
            // Fetch -> Update
            CommandContributionItem update = createCommand("updateOntology",
                    UpdateOntologyHandler.ID, "Update");
            sourceMenu.add(update);
        }

    }
}
