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


import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.ui.forms.RestrictionFormColors;
import com.semmtech.plugin.semmweb.core.widgets.ModelResourcePropertiesContentPart;
import com.semmtech.ui.plugin.widgets.Widgets;


/**
 * 
 * @author Sander Stolk
 */
public class ModelResourcePropertiesContent extends AbstractModelResourceContent {
    private static final Logger logger = Logger.getLogger(ModelResourcePropertiesContent.class);

    private ModelResourcePropertiesContentPart propertiesPart;

    public ModelResourcePropertiesContent(ModelResourceFormPage page) {
        super(page);
    }

    @Override
    public String getTitle() {
        return "Properties";
    }

    @Override
    public Image getImage() {
        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_RDF_PROPERTY);
    }

    @Override
    public boolean isViewable() {
        return true;
    }

    @Override
    protected Control createContent(Composite parent) {
        FormToolkit toolkit = getToolkit();
        Composite outerComposite = toolkit.createComposite(parent, SWT.NONE);
        outerComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB,
                TableWrapData.FILL_GRAB));

        FormColors formColors = new RestrictionFormColors(Display.getCurrent());
        toolkit = new FormToolkit(formColors);
        toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(5, 0).applyTo(outerComposite);
        createPropertiesPart(outerComposite);

        return outerComposite;
    }

    /**
     * Creates section, composite and its content for representing a resource's
     * properties.
     */
    private void createPropertiesPart(Composite parent) {
        propertiesPart = new ModelResourcePropertiesContentPart(this, parent, getToolkit());
        GridDataFactory.fillDefaults().grab(true, true).indent(0, 0).applyTo(propertiesPart);
    }

    @Override
    public void fillToolBar(IToolBarManager toolBarManager) {
        IAction addPropertyAction = new Action("Add Property...") {
            @Override
            public void run() {
                if (propertiesPart != null) {
                    propertiesPart.executeAddProperty();
                }
            }
        };
        addPropertyAction.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
                CorePlugin.PLUGIN_ID, CorePluginImages.IMG_ADD_PLUS));
        toolBarManager.add(addPropertyAction);
        toolBarManager.update(true);
    }

    @Override
    public void notifyEvent(IModelEvent event) {
        updateContent();
    }

    @Override
    public void updateContent() {
        if (!Widgets.isNullOrDisposed(propertiesPart)) {
            propertiesPart.setRedraw(false);
            propertiesPart.refreshProperties();
            propertiesPart.refresh();
            propertiesPart.setRedraw(true);
        }
    }

    /**
     * Refreshes the composite (layout every descendant).
     */
    public void refresh() {
        logger.debug("(" + getResource().toString() + ") refresh called!");
        super.refresh();
    }

}
