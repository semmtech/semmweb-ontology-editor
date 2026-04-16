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

package com.semmtech.plugin.semmweb.core.views;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;

import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.handlers.OpenSemanticModelHandler;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.ui.plugin.util.Selections;


/**
 * See tutorial on CommonNavigator at
 * http://www.modumind.com/2007/04/25/common-navigator-tutorial-1-hello-world/
 * 
 * @author Mike Henrichs
 * 
 */
public class ProjectNavigatorView extends CommonNavigator implements IPropertyChangeListener {

    private static Logger logger = Logger.getLogger(ProjectNavigatorView.class);

    public static final String ID = "com.semmtech.plugin.semmweb.core.views.projectNavigator";

    private IContextActivation activateContext;

    private ProjectNavigatorHelpContextProvider helpContextProvider;

    @Override
    public void init(IViewSite aSite, IMemento aMemento) throws PartInitException {
        super.init(aSite, aMemento);

        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void createPartControl(Composite aParent) {
        super.createPartControl(aParent);
        getCommonViewer().getTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                TreeItem item = getCommonViewer().getTree().getItem(new Point(e.x, e.y));
                if (item == null) {
                    getCommonViewer().setSelection(new StructuredSelection());
                }
            }
        });

        // The activation/deactivation of the navigator context is used by the
        // CTRL+C/V key bindings to make the binding enabled only when the user
        // is in the project navigator view. Otherwise the simple copy/paste
        // with CTRL+C/V won't work in the other views
        getCommonViewer().getControl().addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {

                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        if (activateContext == null) {
                            return;
                        }

                        ((IContextService) PlatformUI.getWorkbench().getService(
                                IContextService.class)).deactivateContext(activateContext);
                    }
                });
            }

            @Override
            public void focusGained(FocusEvent e) {
                PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        activateContext = ((IContextService) PlatformUI.getWorkbench().getService(
                                IContextService.class))
                                .activateContext("com.semmtech.plugin.semmweb.core.context.navigatorContext");
                    }
                });
            }
        });
    }

    @Override
    protected Object getInitialInput() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    @Override
    protected void handleDoubleClick(DoubleClickEvent anEvent) {
        ISelection selection = anEvent.getSelection();

        if (Selections.hasFirstOfType(selection, IModel.class)) {
            IModel model = Selections.retrieveFirstAsType(selection, IModel.class);
            OpenSemanticModelHandler.openModel(model);
        }
        else {
            super.handleDoubleClick(anEvent);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        List<String> list = Lists.newArrayList();
        list.add(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING);
        list.add(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES);

        String property = event.getProperty();
        if (list.contains(property)) {
            try {
                ResourcesPlugin.getWorkspace().getRoot()
                        .refreshLocal(IResource.DEPTH_INFINITE, null);
            }
            catch (CoreException e) {
                logger.error("Error while refreshing the workspace after relabel", e);
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class key) {
        if (key.equals(IContextProvider.class)) {
            if (helpContextProvider == null) {
                helpContextProvider = new ProjectNavigatorHelpContextProvider(getCommonViewer());
            }
            return helpContextProvider;
        }
        return super.getAdapter(key);
    }
}
