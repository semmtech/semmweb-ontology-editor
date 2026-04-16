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
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.widgets.ModelResourceInstancesContentPart;
import com.semmtech.semantics.ontology.OntResourceUtil;


/**
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */
public class ModelResourceInstancesContent extends AbstractModelResourceContent {

    private ModelResourceInstancesContentPart instancesPart;

    public ModelResourceInstancesContent(ModelResourceFormPage page) {
        super(page);
    }

    @Override
    public String getTitle() {
        return "Instances";
    }

    @Override
    public Image getImage() {
        return CorePlugin.getDefault().getImage(CorePluginImages.IMG_INSTANCES);
    }

    @Override
    public boolean isViewable() {
        return OntResourceUtil.isClass(getResource());
    }

    @Override
    protected Control createContent(Composite parent) {
        FormToolkit toolkit = getToolkit();
        Composite outerComposite = toolkit.createComposite(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().margins(0, 0).spacing(5, 0).applyTo(outerComposite);
        GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                .applyTo(outerComposite);

        instancesPart = new ModelResourceInstancesContentPart(this, outerComposite, getToolkit());
        GridDataFactory.fillDefaults().grab(true, true).indent(0, 0).applyTo(instancesPart);

        return outerComposite;
    }

    @Override
    public void refresh() {
        super.refresh();
        instancesPart.refresh();
    }

    @Override
    public void notifyEvent(IModelEvent event) {
        if (!(event instanceof ModelSavedEvent)) {
            refresh();
        }
    }
}
