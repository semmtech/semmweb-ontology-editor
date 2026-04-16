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

package com.semmtech.plugin.semmweb.core.viewers;


import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.decorators.ModelNodeDecorator;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.ModelEventListener;
import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;


public class DynamicNodeLabelProvider extends ModelNodeLabelProvider implements ModelEventListener {
    private final IModelProvider provider;
    private final ModelNodeDecorator decorator;

    public DynamicNodeLabelProvider(IModelProvider provider) {
        super(provider.getOntModel());
        this.provider = provider;
        this.decorator = new ModelNodeDecorator(provider);
        decorator.addListener(new ILabelProviderListener() {
            @Override
            public void labelProviderChanged(LabelProviderChangedEvent event) {
                fireLabelProviderChanged(new LabelProviderChangedEvent(
                        DynamicNodeLabelProvider.this, event.getElements()));
            }
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        decorator.dispose();
    }

    @Override
    public Image getImage(Object element, InspectOrder order) {
        Image baseImage = super.getImage(element, order);
        Image decoratedImage = decorator.decorateImage(baseImage, element);
        return (decoratedImage == null) ? baseImage : decoratedImage;
    }

    @Override
    public String getText(Object element) {
        String baseText = super.getText(element);
        String decoratedText = decorator.decorateText(baseText, element);
        return (decoratedText == null) ? baseText : decoratedText;
    }

    private void refreshModel() {
        updateModel(provider.getOntModel());
    }

    @Override
    public void notifyEvent(IModelEvent event) {

    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        refreshModel();
    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        ModelChangesCollection changes = event.getModelChanges();
        Model additions = changes.getAdditions();
        Model removals = changes.getRemovals();

        clearAll(additions.listSubjects().toList());
        clearAll(removals.listSubjects().toList());
    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        refreshModel();
    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        refreshModel();
    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        clearAll();
    }

    @Override
    public void modelSaved(ModelSavedEvent event) {

    }
}
