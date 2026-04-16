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
import java.util.Set;

import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelEventListener;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.ui.plugin.views.ViewStates;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * This is a new abstract base class for views, which use the model of the
 * currently active IModelProvider editors.
 * 
 * @author Mike Henrichs
 * 
 */
public abstract class AbstractModelListenerView extends ViewPart implements IPartListener2,
        IPartListener, ModelEventListener {

    private final Set<String> registeredModelURIs;
    private final ViewStates viewStates;

    private IModelProvider modelProvider;
    private IWorkbenchPart previousBroughtToTop;

    private boolean initialized;
    private boolean hidden;
    private boolean stale;
    private final ModelEventListener modelEventListener;
    private String currentModelUri;

    private Composite parent;

    private UndoRedoActionGroup historyActionGroup;

    public AbstractModelListenerView() {
        super();
        registeredModelURIs = Sets.newHashSet();
        viewStates = new ViewStates();
        hidden = false;
        stale = true;

        modelEventListener = new ModelEventListener() {
            @Override
            public void notifyEvent(IModelEvent event) {
                if (isHidden()) {
                    stale = true;
                }
                else {
                    AbstractModelListenerView.this.notifyEvent(event);
                }
            }

            @Override
            public void modelActivated(ModelActivatedEvent event) {
                if (!isHidden()) {
                    AbstractModelListenerView.this.modelActivated(event);
                }
            }

            @Override
            public void modelChanged(ModelChangedEvent event) {
                if (!isHidden()) {
                    AbstractModelListenerView.this.modelChanged(event);
                }
            }

            @Override
            public void subModelAdded(SubModelAddedEvent event) {
                if (!isHidden()) {
                    AbstractModelListenerView.this.subModelAdded(event);
                }
            }

            @Override
            public void subModelRemoved(SubModelRemovedEvent event) {
                if (!isHidden()) {
                    AbstractModelListenerView.this.subModelRemoved(event);
                }
            }

            @Override
            public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
                if (!isHidden()) {
                    AbstractModelListenerView.this.namespacePrefixChanged(event);
                }
            }

            @Override
            public void modelSaved(ModelSavedEvent event) {
                if (!isHidden()) {
                    AbstractModelListenerView.this.modelSaved(event);
                }
            }
        };
    }

    @Override
    public void dispose() {
        if (modelProvider != null) {
            modelProvider.removeModelEventListener(modelEventListener);
        }
        getSite().getWorkbenchWindow().getPartService().removePartListener((IPartListener) this);
        getSite().getWorkbenchWindow().getPartService().removePartListener((IPartListener2) this);
        getSite().setSelectionProvider(null);
        super.dispose();
    }

    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;
        getSite().getWorkbenchWindow().getPartService().addPartListener((IPartListener) this);
        getSite().getWorkbenchWindow().getPartService().addPartListener((IPartListener2) this);
    }

    public Composite getParent() {
        return parent;
    }

    protected void layoutParent(boolean changed, boolean all) {
        if (!Widgets.isNullOrDisposed(parent)) {
            parent.layout(changed, all);
        }
    }

    protected IModelProvider getModelProvider() {
        return modelProvider;
    }

    protected boolean hasModelProvider() {
        return (modelProvider != null);
    }

    protected ModelNodeLabelProvider getLabelProvider() {
        if (hasModelProvider()) {
            return modelProvider.getLabelProvider();
        }
        return null;
    }

    protected String getModelURI() {
        if (hasModelProvider()) {
            return modelProvider.getModelURI();
        }
        return null;
    }

    protected List<String> getSubModelURIs() {
        if (hasModelProvider()) {
            return modelProvider.getSubModelURIs();
        }
        return Lists.newArrayList();
    }

    protected Model getSubModel(String uri) {
        if (hasModelProvider()) {
            return modelProvider.getSubModel(uri);
        }
        return null;
    }

    protected OntModel getOntModel() {
        if (hasModelProvider()) {
            return modelProvider.getOntModel();
        }
        return null;
    }

    protected IUndoContext getUndoContext() {
        if (hasModelProvider()) {
            return modelProvider.getUndoContext();
        }
        return null;
    }

    protected void performUndoRedoOperation(AbstractOperation operation) {
        if (hasModelProvider()) {
            modelProvider.performUndoRedoOperation(operation);
        }
    }

    protected final void setInitialized(boolean initialized) {
        this.initialized = initialized;
        if (initialized && modelProvider != null) {
            modelProvider.addModelEventListener(modelEventListener);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    protected void updateModelProvider() {
        modelProvider = CorePlugin.getDefault().getActiveModelProvider();

        IViewSite site = getViewSite();
        IActionBars actionBars = site.getActionBars();
        // Remove the old undo/redo action handlers (if existent)
        actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), null);
        actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), null);
        // Set new undo/redo action handlers using the desired undoContext
        IUndoContext undoContext = getUndoContext();

        if (historyActionGroup != null) {
            historyActionGroup.dispose();
        }

        if (undoContext != null) {
            historyActionGroup = new UndoRedoActionGroup(site, undoContext, true);
            historyActionGroup.fillActionBars(actionBars);
        }
        actionBars.updateActionBars();
    }

    /**
     * A view is opened: if the editor is an IModelProvider, add the view as
     * ModelEventListener.
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
        if (part instanceof AbstractModelListenerView) {
            if (((AbstractModelListenerView) part).equals(this)) {
                // If this view is opened:
                updateModelProvider();
                if (hasModelProvider()) {
                    if (modelProvider instanceof IWorkbenchPart) {
                        previousBroughtToTop = (IWorkbenchPart) modelProvider;
                    }

                    String modelURI = modelProvider.getModelURI();
                    if (!registeredModelURIs.contains(modelURI)) {
                        registeredModelURIs.add(modelURI);
                        if (isInitialized()) {
                            modelProvider.addModelEventListener(modelEventListener);
                        }
                        currentModelUri = modelURI;
                        // modelActivated event triggered next by partVisible.
                    }
                }
            }
        }
    }

    /**
     * Only do something when a new EditorPart is activated; ViewPart
     * activations are ignored! -> Thus broughttotop should be used!
     */
    @Override
    public void partActivated(IWorkbenchPart part) {
    }

    /**
     * Is called after a new part is activated!
     */
    @Override
    public void partDeactivated(IWorkbenchPart part) {
    }

    /**
     * Is only called when an editor has been changed and thus activated! A
     * wordt gesloten, waardoor editor B geopend wordt: BroughtToTop B -
     * Deactivated A - Activated B - Closed A. A wordt als laatste editor
     * gesloten (geldt ook voor view wordt gesloten), view C wordt geactiveerd:
     * Deactivated A - Activated C - Closed A
     */
    @Override
    public void partBroughtToTop(IWorkbenchPart part) {
        if (part instanceof EditorPart) {
            editorBroughtToTop((EditorPart) part);
        }
    }

    private void editorBroughtToTop(EditorPart part) {
        updateModelProvider();
        String modelUri = getModelURI();
        if (hasModelProvider()) {
            if (!registeredModelURIs.contains(modelUri)) {
                registeredModelURIs.add(modelUri);
                if (isInitialized()) {
                    modelProvider.addModelEventListener(modelEventListener);
                }
            }
        }

        previousBroughtToTop = part;
        if (currentModelUri == null || !currentModelUri.equals(modelUri)) {
            currentModelUri = modelUri;
            ModelActivatedEvent event = null;
            if (hasModelProvider()) {
                event = new ModelActivatedEvent(modelProvider.getOntModel(),
                        "Another model has been activated due to an editor having been brought to top.");
            }
            else {
                event = new ModelActivatedEvent(
                        null,
                        "The current model has been deactivated due to a non-IModelProvider editor having been brought to top.");
            }
            modelEventListener.modelActivated(event);
            modelEventListener.notifyEvent(event);
        }
    }

    /**
     * ViewPart de-activations are ignored, only when an IModelProvider is
     * closed - which is also the part that was previously activated - clear the
     * table;
     */
    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof EditorPart) {
            if (part instanceof IModelProvider) {
                String closedModelUri = ((IModelProvider) part).getModelURI();
                if (registeredModelURIs.contains(closedModelUri)) {
                    registeredModelURIs.remove(closedModelUri);
                }
                viewStates.discardState(closedModelUri);

                if (part == previousBroughtToTop) {
                    modelProvider = null;
                    ModelActivatedEvent event = new ModelActivatedEvent(null,
                            "The current model has been deactivated as its editor had been closed.");
                    modelActivated(event);
                    notifyEvent(event);
                    previousBroughtToTop = null;
                    currentModelUri = null;
                    cleanup();
                }
            }
        }
    }

    @Override
    public abstract void setFocus();

    public void openResource(Resource resource) {
        CorePlugin.getDefault().openResource(resource);
    }

    @Override
    public void notifyEvent(IModelEvent event) {
    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
    }

    @Override
    public void modelSaved(ModelSavedEvent event) {
    }

    protected boolean hasStateParameter(String parameter) {
        if (currentModelUri != null) {
            return viewStates.hasParameter(currentModelUri, parameter);
        }
        return false;
    }

    protected Object getStateParameter(String parameter) {
        if (currentModelUri != null) {
            return viewStates.getParameter(currentModelUri, parameter);
        }
        return null;
    }

    protected void setStateParameter(String parameter, Object object) {
        if (!hasModelProvider()) {
            return;
        }

        if (currentModelUri != null) {
            viewStates.setParameter(currentModelUri, parameter, object);
        }
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {

    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) == this) {
            hidden = true;
        }

    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) == this) {
            hidden = false;
            if (stale) {
                stale = false;
                modelActivated(new ModelActivatedEvent(getOntModel(),
                        "Force refresh of view after it has been made visible."));
            }
        }
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
    }

    /**
     * This method is called when the modelProvider is set to null and should
     * cleanup all the resources used by the view like the view models.
     * <p>
     * NB: the difference with dispose is that dispose clean the SWT widgets
     * this method clean other kind of resources
     */
    protected abstract void cleanup();
}
