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

package com.semmtech.plugin.semmweb.editor.perspectives;


import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import com.semmtech.plugin.semmweb.core.preferences.UserPreference;
import com.semmtech.plugin.semmweb.core.views.ProjectNavigatorView;
import com.semmtech.plugin.semmweb.core.wizards.NewOntologyFileWizard;
import com.semmtech.plugin.semmweb.core.wizards.NewSemanticProjectWizard;
import com.semmtech.plugin.semmweb.editor.views.NetworkNavigatorView;
import com.semmtech.plugin.semmweb.editor.views.classrestrictions.ClassRestrictionsView;
import com.semmtech.plugin.semmweb.editor.views.instances.InstancesView;
import com.semmtech.plugin.semmweb.editor.views.properties.PropertiesView;
import com.semmtech.plugin.semmweb.editor.views.taxonomy.OWLClassesView;
import com.semmtech.plugin.semmweb.editor.views.taxonomy.RDFSClassesView;
import com.semmtech.plugin.semmweb.editor.views.triples.TriplesView;


/**
 * Wiping unwanted actions and preferences:
 * http://stackoverflow.com/questions/2451628
 * /run-and-search-appear-in-rcp-menubar
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class OntologyPerspectiveFactory implements IPerspectiveFactory {
    public static final String ID_PERSPECTIVE = "com.semmtech.plugin.semmweb.editor.perspectives.editor";

    private static final String PROBLEMS_VIEW_ID = "org.eclipse.ui.views.ProblemView";
    private static final String SERVERS_VIEW_ID = "com.semmtech.plugin.semmweb.laces.ldp.views.servers";

    @Override
    public void createInitialLayout(IPageLayout layout) {
        defineActions(layout);
        defineLayout(layout);
    }

    private void defineActions(IPageLayout layout) {
        String knowledgeLevel = UserPreference.getKnowledgeLevel();

        layout.addNewWizardShortcut(NewOntologyFileWizard.ID);
        layout.addNewWizardShortcut(NewSemanticProjectWizard.ID);

        layout.addShowViewShortcut(ProjectNavigatorView.ID);
        layout.addShowViewShortcut(RDFSClassesView.ID);
        layout.addShowViewShortcut(OWLClassesView.ID);
        layout.addShowViewShortcut(PropertiesView.ID);
        layout.addShowViewShortcut(NetworkNavigatorView.ID);
        layout.addShowViewShortcut(SERVERS_VIEW_ID);

        if (knowledgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_EXPERT)) {
            layout.addShowViewShortcut(TriplesView.ID);
            layout.addShowViewShortcut(InstancesView.ID);
            layout.addShowViewShortcut(ClassRestrictionsView.ID);
        }
    }

    private void defineLayout(IPageLayout layout) {
        String editorArea = layout.getEditorArea();
        String knowledgeLevel = UserPreference.getKnowledgeLevel();

        IFolderLayout topLeft = layout.createFolder("left", IPageLayout.LEFT, (float) 0.35,
                editorArea);
        topLeft.addView(OWLClassesView.ID);
        topLeft.addView(RDFSClassesView.ID);
        layout.addView(InstancesView.ID, IPageLayout.BOTTOM, (float) 0.5, OWLClassesView.ID);
        layout.addView(ProjectNavigatorView.ID, IPageLayout.BOTTOM, (float) 0.35, InstancesView.ID);

        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.54,
                editorArea);
        right.addView(PropertiesView.ID);
        IFolderLayout middleRight = layout.createFolder("middleright", IPageLayout.BOTTOM,
                (float) 0.5, PropertiesView.ID);
        middleRight.addView(NetworkNavigatorView.ID);
        middleRight.addView(ClassRestrictionsView.ID);
        layout.addView(SERVERS_VIEW_ID, IPageLayout.BOTTOM, (float) 0.35, NetworkNavigatorView.ID);

        if (knowledgeLevel.equals(UserPreference.KNOWLEDGE_LEVEL_EXPERT)) {
            IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.675,
                    editorArea);
            bottom.addView(TriplesView.ID);
            bottom.addView(PROBLEMS_VIEW_ID);
        }

    }
}
