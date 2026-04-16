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

package com.semmtech.plugin.semmweb.core.decorators;


import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.markers.SemanticProblem;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


/**
 * 
 * @author Sander Stolk
 */
public class ModelNodeDecorator extends LabelProvider implements ILabelDecorator {
    // public static final String ID_DECORATOR =
    // "com.semmtech.plugin.semmweb.core.decorators.modelNode";

    protected IModelProvider provider;
    protected IResource file;

    private IResourceChangeListener markerChangeListener;

    public ModelNodeDecorator(IModelProvider provider) {
        super();
        this.provider = provider;
    }

    @Override
    public void dispose() {
        if (markerChangeListener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(markerChangeListener);
        }
        provider = null;
    }

    @Override
    public Image decorateImage(Image baseImage, Object element) {
        if (baseImage == null) {
            return null;
        }

        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            IResource file = getFile();
            if (file != null) {
                List<SemanticProblem> problems = SemanticProblem.find(file, resource);
                if (!problems.isEmpty()) {
                    OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault());
                    icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_WARNING,
                            OverlayImageIcon.BOTTOM_LEFT);
                    Image decoratedImage = icon.createImage();
                    return decoratedImage;
                }
            }
        }

        return null;
    }

    @Override
    public String decorateText(String text, Object element) {
        return null;
    }

    protected IResource getFile() {
        if (file == null && provider instanceof OntologyFormEditor) {
            OntologyFormEditor editor = (OntologyFormEditor) provider;
            file = editor.getResource();

            if (file != null) {
                addMarkerChangeListener(file);
            }
        }
        return file;
    }

    private void addMarkerChangeListener(final IResource file) {
        markerChangeListener = new IResourceChangeListener() {
            @Override
            public void resourceChanged(IResourceChangeEvent event) {
                Set<RDFNode> affectedNodes = Sets.newHashSet();
                IMarkerDelta markerDeltas[] = event.findMarkerDeltas(SemanticProblem.TYPE, true);
                for (IMarkerDelta markerDelta : markerDeltas) {
                    IResource res = markerDelta.getResource();
                    if (res.equals(file)) {
                        String rdfResource = (String) markerDelta
                                .getAttribute(SemanticProblem.ATTRIBUTE_RDF_RESOURCE);
                        if (!Strings.isNullOrEmpty(rdfResource)) {
                            Resource affectedResource = provider.getOntModel().getResource(
                                    rdfResource);
                            affectedNodes.add(affectedResource);
                        }
                    }
                }
                if (!affectedNodes.isEmpty()) {
                    refresh(affectedNodes.toArray(new RDFNode[affectedNodes.size()]));
                }
            }
            // }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(markerChangeListener,
                IResourceChangeEvent.POST_CHANGE);
    }

    public void refresh(RDFNode[] nodes) {
        if (nodes == null) {
            refreshAll();
        }
        else {
            fireLabelProviderChanged(new LabelProviderChangedEvent(this, nodes));
        }
    }

    public void refreshAll() {
        fireLabelProviderChanged(new LabelProviderChangedEvent(this));
    }
}
