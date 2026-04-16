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


import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.MinimalSubclassValidator;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


/**
 * The class contains the logic to create both the Show Imports and Show RDF/OWL
 * menu items. By adding this provider to a parent MenuManager as a
 * IMenuListener the provider will be called when the menu is about to show.
 * 
 * @author Mike Henrichs
 * 
 */
public class ModelVisibilityMenuProvider implements IMenuListener {
    public static final String MENU_SHOW_IMPORTS_ID = "modelVisibility.showImports";
    public static final String MENU_SHOW_RDFOWL_ID = "modelVisibility.showRDFOWL";
    public static final String MENU_SHOW_INFERENCE_ID = "modelVisibility.showInference";
    public static final String GROUP_MODEL_VISIBILITY = "showModels.group";

    private static final List<String> RDF_OWL_URIS = Lists.newArrayList(RDF.getURI(),
            RDFS.getURI(), OWL.getURI(), MinimalSubclassValidator.MODEL_URI,
            OntologyFormEditor.SKOLEMIZED_MODEL_IRI);

    private final boolean hideInferredModel;

    private IModelProvider provider;
    private final List<String> modelUris; // uris of available imports
    private final List<String> rdfOwlUris; // uris of available RDF_OWL_URIS
    private final List<String> inferenceUris; // uris of available inference
                                              // models
    private final Map<String, Boolean> importsState;
    private final Map<String, Boolean> rdfOwlState;
    private final Map<String, Boolean> inferenceState;

    private IModelVisibilityLabelProvider itemLabelProvider;
    private ModelVisibilityListener changeListener;

    /** Calls the constructor with <code>hideInferredModel</code> set to false. */
    public ModelVisibilityMenuProvider() {
        this(false);
    }

    public ModelVisibilityMenuProvider(boolean hideInferredModel) {
        this.hideInferredModel = hideInferredModel;
        this.modelUris = Lists.newArrayList();
        this.rdfOwlUris = Lists.newArrayList();
        this.inferenceUris = Lists.newArrayList();
        this.importsState = Maps.newHashMap();
        this.rdfOwlState = Maps.newHashMap();
        this.inferenceState = Maps.newHashMap();
    }

    /**
     * Sets the model URIs available to the import as well as the URIs which
     * should be marked as visible.
     * 
     * @param provider
     * @param visibleUris
     */
    public void setInput(IModelProvider provider, Collection<String> visibleUris) {
        this.provider = provider;
        if (provider != null) {
            updateAvailableURIs();
            importsState.clear();
            rdfOwlState.clear();
            inferenceState.clear();
            if (visibleUris != null && !visibleUris.isEmpty()) {
                for (String uri : visibleUris) {
                    if (RDF_OWL_URIS.contains(uri)) {
                        rdfOwlState.put(uri, Boolean.TRUE);
                    }
                    else if (uri.equals(IModelProvider.INFERRED_SUBMODEL_URI)) {
                        inferenceState.put(uri, Boolean.TRUE);
                    }
                    else if (modelUris.contains(uri)) {
                        importsState.put(uri, Boolean.TRUE);
                    }
                }
            }
        }
    }

    protected void updateAvailableURIs() {
        modelUris.clear();
        rdfOwlUris.clear();
        inferenceUris.clear();
        for (String uri : provider.getSubModelURIs()) {
            if (RDF_OWL_URIS.contains(uri)) {
                rdfOwlUris.add(uri);
            }
            else if (uri.equals(IModelProvider.INFERRED_SUBMODEL_URI)) {
                inferenceUris.add(uri);
            }
            else {
                modelUris.add(uri);
            }
        }
    }

    /**
     * The change listener is called when the user calls any of the provided
     * actions in either menus.
     * 
     * @param listener
     */
    public void setChangeListener(ModelVisibilityListener listener) {
        this.changeListener = listener;
    }

    /**
     * Set an adapter to transform the label of the import menu options' URI.
     * When creating one such menu option, the adapter's getText() will be
     * called to transform the (full length) sub model's URI to the name desired
     * in the menu option.
     * 
     * @param labelProvider
     */
    public void setMenuItemLabelProvider(IModelVisibilityLabelProvider labelProvider) {
        this.itemLabelProvider = labelProvider;
    }

    @Override
    public void menuAboutToShow(IMenuManager manager) {
        if (provider == null) {
            clearAll(manager);
        }
        else {
            updateAvailableURIs();
            createImportsMenu(manager);
            createRdfOwlMenu(manager);
            if (!hideInferredModel) {
                createInferenceMenu(manager);
            }
        }
    }

    private void clearAll(IMenuManager manager) {
        clearImportsMenu(manager);
        clearRdfOwlMenu(manager);
        clearInferenceMenu(manager);
    }

    private void clearImportsMenu(IMenuManager parent) {
        createMenuManager(parent, "Show Imports", MENU_SHOW_IMPORTS_ID);
    }

    private void clearRdfOwlMenu(IMenuManager parent) {
        createMenuManager(parent, "Show RDF/OWL", MENU_SHOW_RDFOWL_ID);
    }

    private void clearInferenceMenu(IMenuManager parent) {
        parent.remove(MENU_SHOW_INFERENCE_ID);
    }

    private MenuManager createMenuManager(IMenuManager manager, String text, String id) {
        MenuManager menu = (MenuManager) manager.find(id);
        if (menu == null) {
            menu = new MenuManager(text, id);
            boolean hasGroup = (manager.find(GROUP_MODEL_VISIBILITY) != null);
            if (hasGroup) {
                manager.insertBefore(GROUP_MODEL_VISIBILITY, menu);
            }
            else {
                manager.add(menu);
            }
        }
        menu.removeAll();
        return menu;
    }

    private void createImportsMenu(IMenuManager parent) {
        MenuManager manager = createMenuManager(parent, "Show Imports", MENU_SHOW_IMPORTS_ID);
        if (!modelUris.isEmpty()) {
            for (final String uri : modelUris) {
                String label = uri;
                if (itemLabelProvider != null) {
                    label = itemLabelProvider.getText(uri);
                }
                Action toggleAction = new Action(label, IAction.AS_CHECK_BOX) {
                    @Override
                    public void run() {
                        if (isChecked()) {
                            importsState.put(uri, Boolean.TRUE);
                        }
                        else {
                            importsState.remove(uri);
                        }
                        notifyChange();
                    }
                };
                if (importsState.containsKey(uri) && importsState.get(uri).booleanValue()) {
                    toggleAction.setChecked(true);
                }
                manager.add(toggleAction);
            }
            manager.add(new Separator());
            manager.add(new Action("All", IAction.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    for (String uri : modelUris) {
                        importsState.put(uri, Boolean.TRUE);
                    }
                    notifyChange();
                }
            });
            manager.add(new Action("None", IAction.AS_PUSH_BUTTON) {
                @Override
                public void run() {
                    importsState.clear();
                    notifyChange();
                }
            });
        }
    }

    private void createRdfOwlMenu(IMenuManager parent) {
        MenuManager manager = createMenuManager(parent, "Show RDF/OWL", MENU_SHOW_RDFOWL_ID);
        if (rdfOwlUris.isEmpty()) {
            return;
        }

        for (final String uri : RDF_OWL_URIS) {
            if (!rdfOwlUris.contains(uri)) {
                continue;
            }

            String label = uri;
            if (itemLabelProvider != null) {
                label = itemLabelProvider.getText(uri);
            }
            Action toggleAction = new Action(label, IAction.AS_CHECK_BOX) {
                @Override
                public void run() {
                    if (isChecked()) {
                        rdfOwlState.put(uri, Boolean.TRUE);
                    }
                    else {
                        rdfOwlState.remove(uri);
                    }
                    notifyChange();
                }
            };
            if (rdfOwlState.containsKey(uri) && rdfOwlState.get(uri).booleanValue()) {
                toggleAction.setChecked(true);
            }
            manager.add(toggleAction);
        }
        manager.add(new Separator());
        manager.add(new Action("All", IAction.AS_PUSH_BUTTON) {
            @Override
            public void run() {
                for (String uri : rdfOwlUris) {
                    rdfOwlState.put(uri, Boolean.TRUE);
                }
                notifyChange();
            }
        });
        manager.add(new Action("None", IAction.AS_PUSH_BUTTON) {
            @Override
            public void run() {
                rdfOwlState.clear();
                notifyChange();
            }
        });
    }

    private void createInferenceMenu(IMenuManager parent) {
        // inference menu is actually implemented as a toggle button
        parent.remove(MENU_SHOW_INFERENCE_ID);

        if (!inferenceUris.isEmpty()) {
            String uri = inferenceUris.get(0);

            Action toggleAction = new Action("Show Inference", IAction.AS_CHECK_BOX) {
                @Override
                public String getId() {
                    return MENU_SHOW_INFERENCE_ID;
                }

                @Override
                public void run() {
                    if (isChecked()) {
                        inferenceState.put(IModelProvider.INFERRED_SUBMODEL_URI, Boolean.TRUE);
                    }
                    else {
                        inferenceState.remove(IModelProvider.INFERRED_SUBMODEL_URI);
                    }
                    notifyChange();
                }
            };
            if (inferenceState.containsKey(uri) && inferenceState.get(uri).booleanValue()) {
                toggleAction.setChecked(true);
            }
            parent.insertBefore(GROUP_MODEL_VISIBILITY, toggleAction);
        }
    }

    protected void notifyChange() {
        if (changeListener == null) {
            return;
        }
        List<String> uris = Lists.newArrayList();
        for (String uri : importsState.keySet()) {
            if (importsState.get(uri) != null && importsState.get(uri).booleanValue()) {
                uris.add(uri);
            }
        }
        for (String uri : rdfOwlState.keySet()) {
            if (rdfOwlState.get(uri) != null && rdfOwlState.get(uri).booleanValue()) {
                uris.add(uri);
            }
        }
        for (String uri : inferenceState.keySet()) {
            if (inferenceState.get(uri) != null && inferenceState.get(uri).booleanValue()) {
                uris.add(uri);
            }
        }
        changeListener.visibleModelsChanged(uris);
    }
}
