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

package com.semmtech.plugin.semmweb.core.forms.editor;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hp.hpl.jena.ontology.OntResource;
import com.semmtech.plugin.semmweb.core.dnd.DndUtils;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelEventListener;
import com.semmtech.plugin.semmweb.core.model.ModelEventListenerAdapter;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;
import com.semmtech.ui.plugin.ControlProvider;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public abstract class AbstractModelResourceContent extends ModelEventListenerAdapter implements
        ControlProvider {
    private static final String CLASS_PROPERTY = "class";

    protected final ModelResourceFormPage page;
    private boolean initialized;
    protected IToolBarManager toolBarManager;
    protected Control contentControl;
    private final ModelEventListener modelEventListener;
    private boolean stale;

    public AbstractModelResourceContent(ModelResourceFormPage page) {
        this.page = page;
        this.initialized = false;
        this.stale = true;

        this.modelEventListener = new ModelEventListener() {
            @Override
            public void notifyEvent(IModelEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.notifyEvent(event);
                }
                else if (!(event instanceof ModelSavedEvent)) {
                    stale = true;
                }
            }

            @Override
            public void modelActivated(ModelActivatedEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.modelActivated(event);
                }
            }

            @Override
            public void modelChanged(ModelChangedEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.modelChanged(event);
                }
            }

            @Override
            public void subModelAdded(SubModelAddedEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.subModelAdded(event);
                }
            }

            @Override
            public void subModelRemoved(SubModelRemovedEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.subModelRemoved(event);
                }
            }

            @Override
            public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.namespacePrefixChanged(event);
                }
            }

            @Override
            public void modelSaved(ModelSavedEvent event) {
                if (isInitialized() && contentControl.getVisible()) {
                    AbstractModelResourceContent.this.modelSaved(event);
                }
            }
        };

        getModelProvider().addModelEventListener(modelEventListener);
    }

    public void dispose() {
        IModelProvider modelProvider = getModelProvider();
        if ((modelProvider != null) && (modelEventListener != null)) {
            modelProvider.removeModelEventListener(modelEventListener);
        }

        Widgets.disposeIfExists(contentControl);

        if (toolBarManager != null) {
            toolBarManager.removeAll();
        }
    }

    /**
     * Returns whether this content should be displayed for a particular
     * resource.
     */
    abstract public boolean isViewable();

    /**
     * Returns the title of this content. Subclasses should overwrite this
     * function if they have one available.
     */
    public String getTitle() {
        return null;
    }

    /**
     * Returns the image of this content. Subclasses should overwrite this
     * function if they have one available.
     */
    public Image getImage() {
        return null;
    }

    /**
     * Creates the content for this composite. This method is called when this
     * composite gets focus for the first time. Subclasses should implement this
     * method.
     */
    abstract protected Control createContent(Composite parent);

    /** Returns true after the content has been created. */
    public boolean isInitialized() {
        if (Widgets.isNullOrDisposed(contentControl)) {
            return false;
        }
        return initialized;
    }

    public IEditorPart getEditor() {
        return page.getEditor();
    }

    public IModelProvider getModelProvider() {
        if (page.getEditor() instanceof IModelProvider) {
            return (IModelProvider) page.getEditor();
        }
        return null;
    }

    public OntResource getResource() {
        return page.getResource();
    }

    public FormToolkit getToolkit() {
        return page.getToolkit();
    }

    public IProject getProject() {
        return page.getProject();
    }

    public Control getControl() {
        if (Widgets.isNullOrDisposed(contentControl)) {
            contentControl = createContent(page.getContentComposite());
        }
        else if (stale) {
            updateContent();
        }

        if (toolBarManager != null) {
            toolBarManager.removeAll();
            toolBarManager.update(true);
            internalFillToolBar(toolBarManager);
            toolBarManager.update(true);
        }

        stale = false;
        initialized = true;
        return contentControl;
    }

    public void setToolBarManager(IToolBarManager toolBarManager) {
        this.toolBarManager = toolBarManager;
    }

    protected void internalFillToolBar(IToolBarManager toolBarManager) {
        fillToolBar(toolBarManager);

        IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(
                        "com.semmtech.plugin.semmweb.core.resourceContentToolBarActions");
        for (IConfigurationElement element : configurationElements) {
            try {
                Object object = element.createExecutableExtension(CLASS_PROPERTY);
                if (object instanceof IAction) {
                    IAction action = (IAction) object;
                    toolBarManager.add(action);
                    toolBarManager.update(true);
                }
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method fills the toolBarManager set with setToolBarManager.
     * Subclasses should overwrite this method if they have any actions to add.
     */
    public void fillToolBar(IToolBarManager toolBarManager) {
    }

    protected void setDragSupportOfResource(Control control) {
        DndUtils.addDragSupport(control, getResource());
    }

    public void updateContent() {
        refresh();
    }

    public void refresh() {
        if (!Widgets.isNullOrDisposed(contentControl)) {
            contentControl.setRedraw(false);
            Widgets.layoutControlUpToScrollableParent(contentControl);
            contentControl.setRedraw(true);
        }
    }
}
