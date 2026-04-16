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


import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.resources.CoreResourcePropertiesManager;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


/**
 * 
 * @author Sander Stolk
 */
public class PublishedResourceFileDecorator extends LabelProvider implements ILabelDecorator {
    public static final String ID_DECORATOR = "com.semmtech.plugin.semmweb.core.decorators.publishedResourceFile";

    public PublishedResourceFileDecorator() {
        super();
    }

    @Override
    public Image decorateImage(Image baseImage, Object element) {
        IResource resource = retrieveResource(element);

        if (resource == null) {
            return null;
        }
        if (resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT) {
            return null;
        }

        try {
            Map<String, Integer> decorations = CoreResourcePropertiesManager
                    .findDecorationImageForResource(resource);
            if (decorations.size() > 0) {
                OverlayImageIcon overlayIcon = new OverlayImageIcon(baseImage,
                        CorePlugin.getDefault(), decorations);
                return overlayIcon.getImage();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("static-method")
    private IResource retrieveResource(Object element) {
        if (element instanceof IResource) {
            return (IResource) element;
        }
        if (element instanceof IAdaptable) {
            IAdaptable adaptable = (IAdaptable) element;
            IResource resource = (IResource) adaptable.getAdapter(IResource.class);
            return resource;
        }
        return null;
    }

    @Override
    public String decorateText(String text, Object element) {
        IResource resource = retrieveResource(element);
        if (resource == null) {
            return null;
        }
        if (resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT) {
            return null;
        }

        try {
            String location = CoreResourcePropertiesManager.getSourceLocation(resource);
            String result = text;

            if (CoreResourcePropertiesManager.hasSourceLocation(resource)
                    && CoreResourcePropertiesManager.isModified(resource)) {
                result = "> " + result;
            }

            if (!Strings.isNullOrEmpty(location)) {
                result += " [" + location + "]";
            }

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void refresh() {
        fireLabelProviderChanged(new LabelProviderChangedEvent(this));
    }

    public static PublishedResourceFileDecorator getFileDecorator() {
        IDecoratorManager decoratorManager = CorePlugin.getDefault().getWorkbench()
                .getDecoratorManager();
        if (decoratorManager.getEnabled(ID_DECORATOR)) {
            return (PublishedResourceFileDecorator) decoratorManager
                    .getLabelDecorator(ID_DECORATOR);
        }
        return null;
    }

    public static void refreshAll() {
        PublishedResourceFileDecorator decorator = PublishedResourceFileDecorator
                .getFileDecorator();
        if (decorator != null) {
            decorator.refresh();
        }
    }
}
