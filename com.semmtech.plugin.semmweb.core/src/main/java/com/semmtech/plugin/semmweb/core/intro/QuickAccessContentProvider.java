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

package com.semmtech.plugin.semmweb.core.intro;


import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.config.IIntroContentProvider;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


/**
 * 
 * @author Sander Stolk
 */
public class QuickAccessContentProvider implements IIntroContentProvider {
    private static final String CLASS_PROPERTY = "class";

    private List<IAction> newActions;

    @Override
    public void init(IIntroContentProviderSite site) {
        newActions = Lists.newArrayList();
        IConfigurationElement[] configurationElements = Platform
                .getExtensionRegistry()
                .getConfigurationElementsFor("com.semmtech.plugin.semmweb.core.startPageNewActions");

        Map<IContributor, List<IAction>> actionsPerContributor = Maps.newHashMap();
        for (IConfigurationElement element : configurationElements) {
            try {
                IContributor contributor = element.getContributor();
                Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IAction) {
                    IAction action = (IAction) object;
                    if (!Strings.isNullOrEmpty(element.getAttribute("name"))) {
                        action.setText(element.getAttribute("name"));
                    }

                    if (!actionsPerContributor.containsKey(contributor)) {
                        List<IAction> emptyActionsList = Lists.newArrayList();
                        actionsPerContributor.put(contributor, emptyActionsList);
                    }

                    List<IAction> actions = actionsPerContributor.get(contributor);
                    actions.add(action);
                }
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }

        // First get all actions contributed by the core plugin
        List<IContributor> coreContributors = Lists.newArrayList();
        for (IContributor contributor : actionsPerContributor.keySet()) {
            if (contributor.getName().equals(CorePlugin.PLUGIN_ID)) {
                coreContributors.add(contributor);
            }
        }
        for (IContributor coreContributor : coreContributors) {
            newActions.addAll(actionsPerContributor.get(coreContributor));
            actionsPerContributor.remove(coreContributor);
        }
        // Afterwards get all actions contributed by other plugins
        for (IContributor contributor : actionsPerContributor.keySet()) {
            newActions.addAll(actionsPerContributor.get(contributor));
        }
    }

    @Override
    public void createContent(String id, PrintWriter out) {
    }

    @Override
    public void createContent(String id, Composite parent, FormToolkit toolkit) {
        String title = "Quick access to...";
        String description = "This section provides quick access to wizards for creating new projects, files, and so on.";

        // Entire section
        Section outerSection = toolkit.createSection(parent, ExpandableComposite.TWISTIE
                | ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
        outerSection.descriptionVerticalSpacing = 10;
        outerSection.setText(title);
        outerSection.setDescription(description);
        outerSection.setLayoutData(new ColumnLayoutData());
        GridLayoutFactory.fillDefaults().applyTo(outerSection);

        // Content within the section, with vertical spacing of 5px
        Composite contentComposite = toolkit.createComposite(outerSection, SWT.NONE);
        contentComposite.setLayoutData(new ColumnLayoutData());
        outerSection.setClient(contentComposite);
        GridLayoutFactory.fillDefaults().spacing(0, 5).applyTo(contentComposite);

        // List within the section, with no vertical spacing
        Composite listComposite = toolkit.createComposite(contentComposite, SWT.NONE);
        GridLayoutFactory.fillDefaults().margins(3, 5).spacing(0, 0).applyTo(listComposite);

        for (final IAction newAction : newActions) {
            IntroActions.createActionLink(listComposite, newAction, toolkit);
        }
        IntroActions.createActionLink(listComposite, createCloseIntroPartAction(), toolkit);

        outerSection.setExpanded(true);
    }

    private IAction createCloseIntroPartAction() {
        return new Action("Start the Editor") {
            @Override
            public String getDescription() {
                return "Close this Start Page and open the editor.";
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return CorePlugin.getDefault().getImageDescriptor(
                        CorePluginImages.IMG_SEMMTECH_ICON);
            }

            @Override
            public void run() {
                IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                IIntroPart introPart = introManager.getIntro();
                if (introPart != null) {
                    introManager.closeIntro(introPart);
                }
            }
        };
    }

    @Override
    public void dispose() {
    }

}
