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

package com.semmtech.plugin.semmweb.core.widgets;


import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.forms.editor.AbstractModelResourceContent;
import com.semmtech.plugin.semmweb.core.ui.forms.IEditorFormColors;


/**
 * 
 * @author Sander Stolk
 */
public class AnonymousResourceContentPart extends AbstractPropertyObjectContentPart {
    @SuppressWarnings("unused")
    private Property property;
    @SuppressWarnings("unused")
    private Resource subject;
    private Resource anonymous;

    private FormToolkit toolkit;
    @SuppressWarnings("unused")
    private FormToolkit coloringToolkit;
    private FormColors formColors;
    private IManagedForm managedForm;

    private ToolBar toolbar;
    private MenuManager menuManager;
    private ToolItem menuButton;
    private Label iconLabel;

    public AnonymousResourceContentPart(AbstractModelResourceContent contentParent,
            Composite parent, FormToolkit toolkit, Property property, Resource anonymous) {
        super(contentParent, parent, toolkit);
        this.subject = anonymous;
        this.property = property;
        this.anonymous = anonymous;

        initialize();
        createContent();
    }

    @Override
    public RDFNode getObject() {
        return anonymous;
    }

    public void alterContent(Resource anonymous) {
        this.subject = anonymous;
        this.anonymous = anonymous;
        initialize();
        fillContent();
        refresh();
    }

    private void initialize() {
    }

    private void createContent() {
        // formColors = new RestrictionFormColors(Display.getCurrent());
        // toolkit = new FormToolkit(formColors);
        // toolkit.setBorderStyle(SWT.WRAP | SWT.MULTI);

        setBackground(formColors.getColor(IEditorFormColors.WHITE));
        {
            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 3;
            layout.horizontalSpacing = 3;
            layout.verticalSpacing = 0;
            layout.topMargin = 1;
            layout.rightMargin = 0;
            layout.bottomMargin = 3;
            layout.leftMargin = 2;
            setLayout(layout);
        }

        iconLabel = toolkit.createLabel(this, "", SWT.NONE);
        {
            TableWrapData data = new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP);
            data.heightHint = 18;
            iconLabel.setLayoutData(data);
        }
        iconLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        Composite composite = toolkit.createComposite(this, SWT.BORDER);
        composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));
        composite.setBackground(formColors.getColor(IEditorFormColors.WHITE));
        {
            TableWrapLayout layout = new TableWrapLayout();
            layout.numColumns = 1;
            layout.verticalSpacing = 1;
            layout.topMargin = 0;
            layout.rightMargin = 0;
            layout.leftMargin = 1;
            layout.horizontalSpacing = 8;
            layout.bottomMargin = 10;
            composite.setLayout(layout);
        }
        toolkit.paintBordersFor(composite);

        // / TODO: Solve problem with getResource() always returning resource of
        // the page

        // propertiesPart = new ResourcePropertiesFormPart(this);
        // propertiesPart.createFormContent(composite, managedForm);
        //
        createToolBar(this);

        fillContent();
        refresh();
    }

    private void fillContent() {
        // Retrieve icon
        String imageKey = CorePluginImages.IMG_RDFS_CLASS;
        if (anonymous.hasProperty(RDF.type)) {
            Resource type = anonymous.getPropertyResourceValue(RDF.type);
            imageKey = getModelProvider().getLabelProvider().getInstanceImageKey(type);
        }
        iconLabel.setImage(CorePlugin.getDefault().getImage(imageKey));
    }

    private void createToolBar(Composite parent) {
        toolbar = new ToolBar(parent, SWT.HORIZONTAL | SWT.FLAT);
        toolbar.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.TOP, 1, 1));
        managedForm.getToolkit().adapt(toolbar);
        managedForm.getToolkit().paintBordersFor(toolbar);

        fillToolBar();

        menuButton = new ToolItem(toolbar, SWT.FLAT);
        menuButton.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_ARROW_DOWN));
        menuButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = getShell();
                Menu menu = menuManager.createContextMenu(shell);
                shell.setMenu(menu);
                final ToolItem toolItem = (ToolItem) e.widget;
                final ToolBar toolBar = toolItem.getParent();
                Point point = toolBar.toDisplay(new Point(e.x, e.y + toolItem.getBounds().height));
                menu.setLocation(point.x, point.y);
                menu.setVisible(true);
            }
        });
    }

    private void fillToolBar() {
        menuManager = new MenuManager();
        // / TODO: Add Actions
    }

}
