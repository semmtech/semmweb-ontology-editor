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

package com.semmtech.plugin.semmweb.core.actions;


import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.NewExampleAction;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;
import org.eclipse.ui.navigator.WizardActionGroup;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardRegistry;

import com.semmtech.ui.plugin.navigator.CommonViewerActionProvider;
import com.semmtech.ui.plugin.util.Selections;


/**
 * Action Provider for a custom New menu within the Ontology explorer.
 * 
 * @author Mike Henrichs
 * 
 */
public class CustomNewActionProvider extends CommonViewerActionProvider {

    private static final String FULL_EXAMPLES_WIZARD_CATEGORY = "org.eclipse.ui.Examples";

    private static final String NEW_MENU_NAME = "common.new.menu";//$NON-NLS-1$

    private ActionFactory.IWorkbenchAction showDlgAction;

    private IAction newSemanticProjectAction;

    private IAction newExampleAction;

    private WizardActionGroup newWizardActionGroup;

    private boolean contribute = false;

    private IWorkbenchWindow window;

    @Override
    public void init(ICommonActionExtensionSite anExtensionSite) {
        super.init(anExtensionSite);

        if (anExtensionSite.getViewSite() instanceof ICommonViewerWorkbenchSite) {
            window = ((ICommonViewerWorkbenchSite) anExtensionSite.getViewSite())
                    .getWorkbenchWindow();
            showDlgAction = ActionFactory.NEW.create(window);
            newSemanticProjectAction = new NewSemanticProjectAction(window);
            newExampleAction = new NewExampleAction(window);
            newWizardActionGroup = new WizardActionGroup(window, PlatformUI.getWorkbench()
                    .getNewWizardRegistry(), WizardActionGroup.TYPE_NEW,
                    anExtensionSite.getContentService());

            contribute = true;
        }
    }

    /**
     * Adds a submenu to the given menu with the name "group.new" see
     * {@link ICommonMenuConstants#GROUP_NEW}). The submenu contains the
     * following structure:
     * 
     * <ul>
     * <li>a set of context sensitive wizard shortcuts (as defined by
     * <b>org.eclipse.ui.navigator.commonWizard</b>),</li>
     * <li>another separator,</li>
     * <li>a generic "Other" new wizard shortcut action</li>
     * </ul>
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        IMenuManager submenu = new MenuManager("New", NEW_MENU_NAME);
        if (!contribute) {
            return;
        }

        Object selected = Selections.retrieveFirst(getSelection());
        // IServiceLocator locator = getServiceLocator();

        // if selected == null means that the white area of project navigator
        // has been selected
        if (selected != null && !(selected instanceof IProject) && !(selected instanceof IFolder)) {
            return;
        }

        // Add new project wizard shortcut
        submenu.add(newSemanticProjectAction);
        submenu.add(new Separator());

        // fill the menu from the commonWizard contributions
        newWizardActionGroup.setContext(getContext());
        newWizardActionGroup.fillContextMenu(submenu);

        submenu.add(new Separator(ICommonMenuConstants.GROUP_ADDITIONS));

        // if there are examples, then add them to the end of the menu
        if (hasExamples()) {
            submenu.add(new Separator());
            submenu.add(newExampleAction);
        }

        // Add other ..
        submenu.add(new Separator());
        submenu.add(showDlgAction);

        // append the submenu after the GROUP_NEW group.
        menu.insertAfter(ICommonMenuConstants.GROUP_NEW, submenu);
    }

    /**
     * Return whether or not any examples are in the current install.
     * 
     * @return True if there exists a full examples wizard category.
     */
    private boolean hasExamples() {
        IWizardRegistry newRegistry = PlatformUI.getWorkbench().getNewWizardRegistry();
        IWizardCategory category = newRegistry.findCategory(FULL_EXAMPLES_WIZARD_CATEGORY);
        return category != null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.actions.ActionGroup#dispose()
     */
    @Override
    public void dispose() {
        if (showDlgAction != null) {
            showDlgAction.dispose();
            showDlgAction = null;
        }
        super.dispose();
    }

}
