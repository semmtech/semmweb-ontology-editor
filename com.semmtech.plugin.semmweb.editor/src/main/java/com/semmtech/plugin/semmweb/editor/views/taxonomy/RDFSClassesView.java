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

package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.menus.UIElement;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.commands.Commands;
import com.semmtech.plugin.semmweb.core.handlers.SelectedResourceHandler;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;


/**
 * 
 * @author Sander Stolk
 */
public class RDFSClassesView extends AbstractTaxonomyView {

    public static final String ID = "com.semmtech.plugin.semmweb.editor.views.taxonomy.rdfsclasses";
    private static RDFSClassesView singleton;

    public RDFSClassesView() {
        super("Classes");
        singleton = this;
    }

    @Override
    public Resource getTaxonomyResourceType() {
        return RDFS.Class;
    }

    @Override
    public Resource getTaxonomyDefaultRoot() {
        return RDFS.Resource;
    }

    @Override
    public boolean isTaxonomyDefaultRootSelectable() {
        return getVisibleModelURIs().contains(RDFS.getURI());
    }

    @Override
    public List<String> getPropertyChangesTriggeringViewerRefresh() {
        return Lists.newArrayList();
    }

    /**
     * REFACTOR: Change long name!
     */
    @Override
    public List<String> getPropertyChangesTriggeringCompleteRefresh() {
        List<String> list = Lists.newArrayList();

        // TODO: check if these are still wanted/needed
        list.add(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING);
        list.add(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES);

        return list;
    }

    public static class ToggleInstanceCountHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.toggleInstanceCount";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.setShowInstanceCount(!singleton.getShowInstanceCount());
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(singleton.getShowInstanceCount());
            }
        }
    }

    @Override
    protected String getToggleInstanceCountHandlerID() {
        return ToggleInstanceCountHandler.ID;
    }

    public static class ChangeTaxonomyRootHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.changeTaxonomyRoot";

        public static final String PARAMETER_ROOT_RESOURCE_URI = "rdfsclasses.changeTaxonomyRoot.rootResourceURI";
        public static final String PARAMETER_USE_SELECTED = "rdfsclasses.changeTaxonomyRoot.useSelected";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            boolean useSelected = true;
            if (event.getParameter(PARAMETER_USE_SELECTED) != null) {
                useSelected = Boolean.parseBoolean(event.getParameter(PARAMETER_USE_SELECTED));
            }
            String rootResourceUri = event.getParameter(PARAMETER_ROOT_RESOURCE_URI);

            Resource rootResource = null;
            if (rootResourceUri != null) {
                OntModel model = getActiveOntModel(event);
                if (model != null) {
                    rootResource = model.getResource(rootResourceUri);
                }
            }
            else if (useSelected) {
                rootResource = getSelectedResource(event);
            }

            if (rootResource != null) {
                if (singleton != null && singleton.isInitialized()) {
                    singleton.changeRootResource(rootResource);
                }
            }
            return null;
        }
    }

    public static class ChangeTaxonomyRootToTopClassesHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.setTaxonomyRootToTopClasses";

        public static final String PARAMETER_WITH_CHILDREN_ONLY = "rdfsclasses.changeTaxonomyRootToTopClasses.withChildrenOnly";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (HandlerUtil.matchesRadioState(event)) {
                return null;
            }

            boolean allParents = false;
            String currentState = event.getParameter(RadioState.PARAMETER_ID);
            if (SHOW_ALL_TOP_PARENTS_STATE.equals(currentState)) {
                allParents = true;
            }
            HandlerUtil.updateRadioState(event.getCommand(), currentState);
            if (singleton != null && singleton.isInitialized()) {
                singleton.setShowTopClasses(true, allParents, true);
            }
            return null;
        }
    }

    @Override
    protected void setShowTopClasses(String state, boolean performRefresh) {
        super.setShowTopClasses(state, performRefresh);

        String currentState = getShowTopClasses();
        Command command = Commands.getCommand(ChangeTaxonomyRootToTopClassesHandler.ID);
        if (command != null) {
            State commandState = command.getState(RadioState.STATE_ID);
            if (!currentState.equals(commandState)) {
                commandState.setValue(currentState);
            }
        }
    }

    public static class ToggleShowBaseModelHandler extends AbstractHandler implements
            IElementUpdater {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.taxonomy.owlclasses.toggleShowBaseModel";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null) {
                singleton.toggleShowBaseModel();
            }
            return null;
        }

        @Override
        public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
            if (singleton != null) {
                element.setChecked(singleton.showBaseModel());
            }
        }
    }

    public static class RefreshViewHandler extends AbstractHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.refreshView";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.refreshViewer();
            }
            return null;
        }
    }

    public static class CreateSubClassHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createSubClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                Resource selectedResource = getSelectedResource(event);
                singleton.createSubClass(selectedResource);
            }
            return null;
        }
    }

    public static class CreateComplementOfClassHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createComplementOfClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                Resource selectedResource = getSelectedResource(event);
                singleton.createComplementClass(selectedResource);
            }
            return null;
        }
    }

    public static class CreateIntersectionClassHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createIntersectionClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                List<Resource> selectedResources = getSelectedResources(event);
                singleton.createIntersectionClass(selectedResources);
            }
            return null;
        }
    }

    public static class CreateUnionClassHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createUnionClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                List<Resource> selectedResources = getSelectedResources(event);
                singleton.createUnionClass(selectedResources);
            }
            return null;
        }
    }

    public static class CreateEquivalentClassHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createEquivalentClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                Resource selectedResource = getSelectedResource(event);
                singleton.createEquivalentClass(selectedResource);
            }
            return null;
        }
    }

    public static class CreateSiblingHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createSiblingClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                Resource selectedResource = getSelectedResource(event);
                Resource superClass = null;
                if (selectedResource instanceof ResourceTreeData) {
                    superClass = (Resource) ((ResourceTreeData) selectedResource).getParent();
                }
                singleton.createSiblingClass(selectedResource, superClass);
            }
            return null;
        }
    }

    public static class CreateInstanceHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.createInstance";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                Resource selectedResource = getSelectedResource(event);
                singleton.createInstance(selectedResource);
            }
            return null;
        }
    }

    public static class RemoveSubClassHandler extends SelectedResourceHandler {
        public static final String ID = "com.semmtech.plugin.semmweb.editor.commands.rdfsclasses.removeSubClass";

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                Resource selectedResource = getSelectedResource(event);
                Resource superClass = null;
                if (selectedResource instanceof ResourceTreeData) {
                    superClass = (Resource) ((ResourceTreeData) selectedResource).getParent();
                }
                singleton.removeSubClass(selectedResource, superClass);
            }
            return null;
        }
    }

    public static class CollapseAllHandler extends AbstractHandler {

        @Override
        public Object execute(ExecutionEvent event) throws ExecutionException {
            if (singleton != null && singleton.isInitialized()) {
                singleton.collapseAll();
            }
            return null;
        }
    }
}
