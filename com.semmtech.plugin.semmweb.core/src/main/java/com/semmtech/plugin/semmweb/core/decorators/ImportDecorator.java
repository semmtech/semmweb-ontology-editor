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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IDecoratorManager;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.markers.ImportProblem;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


/**
 * 
 * @author Sander Stolk
 */
public class ImportDecorator extends LabelProvider implements ILabelDecorator {
    public static final String ID_DECORATOR = "com.semmtech.plugin.semmweb.core.decorators.semanticElement.import";

    public ImportDecorator() {
        super();
    }

    @Override
    public Image decorateImage(Image baseImage, Object element) {
        if (baseImage == null) {
            return null;
        }

        if (element instanceof IImport) {
            IImport immport = (IImport) element;
            IModel model = (IModel) immport.getAncestor(ISemanticElement.MODEL);
            boolean importError = false;

            if (model != null) {
                IResource file = model.getResource();

                if (file != null) {

                    List<ImportProblem> problems = ImportProblem.find(file, model.isWorkingCopy());
                    for (ImportProblem problem : problems) {
                        if (problem.getLocation().equals(immport.getURI())) {
                            importError = true;
                        }
                    }
                }
            }

            if (!importError && immport.isDirect()) {
                return null;
            }

            OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault());

            if (importError) {
                icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_ERROR,
                        OverlayImageIcon.BOTTOM_LEFT);
            }

            if (!immport.isDirect()) {
                icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_INHERITED,
                        OverlayImageIcon.TOP_LEFT);
            }

            return icon.createImage();
        }

        return null;
    }

    @Override
    public String decorateText(String text, Object element) {
        if (element instanceof IImport) {
            IImport immport = (IImport) element;
            String uri = immport.getURI();
            if (!text.contains(uri)) {
                return String.format("%s [%s]", text, uri);
            }
        }
        return null;
    }

    public void refresh() {
        fireLabelProviderChanged(new LabelProviderChangedEvent(this));
    }

    public static ImportDecorator getFileDecorator() {
        IDecoratorManager decoratorManager = CorePlugin.getDefault().getWorkbench()
                .getDecoratorManager();
        if (decoratorManager.getEnabled(ID_DECORATOR)) {
            return (ImportDecorator) decoratorManager.getLabelDecorator(ID_DECORATOR);
        }
        return null;
    }

    public static void refreshAll() {
        ImportDecorator decorator = ImportDecorator.getFileDecorator();
        if (decorator != null) {
            decorator.refresh();
        }
    }
}
