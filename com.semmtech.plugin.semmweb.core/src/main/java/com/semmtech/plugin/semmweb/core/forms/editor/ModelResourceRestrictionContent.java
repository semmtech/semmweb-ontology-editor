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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.widgets.ModelResourceRestrictionsContentPart;
import com.semmtech.semantics.ontology.OntResourceUtil;
import com.semmtech.ui.plugin.widgets.Widgets;


public class ModelResourceRestrictionContent extends AbstractModelResourceContent {
    private static final Logger logger = Logger.getLogger(ModelResourceRestrictionContent.class);

    private ModelResourceRestrictionsContentPart restrictionsPart;
    private Image addRestrictionImage;

    public ModelResourceRestrictionContent(ModelResourceFormPage page) {
        super(page);
    }

    @Override
    public String getTitle() {
        return "Instance Behaviour";
    }

    @Override
    public Image getImage() {
        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_OWL_RESTRICTION);
    }

    @Override
    public boolean isViewable() {
        return OntResourceUtil.isClass(getResource());
    }

    @Override
    public void dispose() {
        super.dispose();
        if (addRestrictionImage != null) {
            addRestrictionImage.dispose();
        }
    }

    @Override
    protected Control createContent(Composite parent) {
        FormToolkit toolkit = getToolkit();
        Composite outerComposite = toolkit.createComposite(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(5, 0).applyTo(outerComposite);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                .applyTo(outerComposite);

        createRestrictionsPart(outerComposite);

        return outerComposite;
    }

    private void createRestrictionsPart(Composite parent) {
        restrictionsPart = new ModelResourceRestrictionsContentPart(this, parent, getToolkit());
        GridDataFactory.fillDefaults().grab(true, true).indent(0, 0).applyTo(restrictionsPart);
    }

    @Override
    public void notifyEvent(IModelEvent event) {
        updateContent();
    }

    @Override
    public void updateContent() {
        if (!Widgets.isNullOrDisposed(restrictionsPart)) {
            restrictionsPart.refresh();
        }
        refresh();
    }

    /**
     * Refreshes the composite (layout every descendant).
     */
    public void refresh() {
        logger.debug("(" + getResource().toString() + ") refresh called!");
        super.refresh();
    }

}
