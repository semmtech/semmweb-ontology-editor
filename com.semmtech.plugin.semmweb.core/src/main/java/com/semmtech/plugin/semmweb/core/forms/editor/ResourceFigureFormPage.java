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


import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.draw2d.ResourceFigure;


public class ResourceFigureFormPage extends FormPage {

    public ResourceFigureFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    protected Resource getResource() {
        if (getEditor() instanceof IResourceProvider)
            return ((IResourceProvider) getEditor()).getResource();
        return null;
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        FormToolkit toolkit = managedForm.getToolkit();
        managedForm.getForm().getBody().setLayout(new FillLayout(SWT.VERTICAL));
        Composite composite = toolkit.createComposite(managedForm.getForm().getBody());
        composite.setLayout(new FillLayout());
        FigureCanvas canvas = new FigureCanvas(composite);
        LightweightSystem lws = new LightweightSystem(canvas);
        Figure contents = new Figure();
        XYLayout contentsLayout = new XYLayout();
        contents.setLayoutManager(contentsLayout);

        Font labelFont = new Font(null, "Segoe UI", 10, SWT.BOLD);
        Label label = new Label("semm:PhysicalObject", CorePlugin.getDefault().getImage(
                CorePluginImages.IMG_OWL_CLASS));
        label.setFont(labelFont);

        final ResourceFigure resourceFigure = new ResourceFigure(label);

        resourceFigure.getLiteralsCompartment().add(
                new Label("rdfs:label = \"fysiek object {@nl}\"", CorePlugin.getDefault().getImage(
                        CorePluginImages.IMG_RDF_PROPERTY)));
        resourceFigure.getLiteralsCompartment().add(
                new Label("rdfs:comment = \"Dit is een commentaar regel. {@nl}\"", CorePlugin
                        .getDefault().getImage(CorePluginImages.IMG_RDF_PROPERTY)));

        contentsLayout.setConstraint(resourceFigure, new Rectangle(10, 10, -1, -1));
        contents.add(resourceFigure);

        lws.setContents(contents);

    }
}
