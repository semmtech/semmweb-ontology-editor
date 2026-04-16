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

package com.semmtech.plugin.semmweb.dictionary.wizards;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;


public class CreateSKOSConceptWizardPage extends WizardPage {

    @SuppressWarnings("unused")
    private OntModel model;
    private Composite container;

    public CreateSKOSConceptWizardPage(String pageName, OntModel model) {
        this(pageName, model, null);
    }

    public CreateSKOSConceptWizardPage(String pageName, OntModel model, Resource type) {
        super(pageName);
        setTitle("Concept");
        setDescription("Create a new concept.");

        this.model = model;
    }

    @SuppressWarnings("unused")
    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.VERTICAL));

        Label label = new Label(container, SWT.NONE);
        label.setText("Scheme");

        new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);

        createLexicalLabelsControls(container);

        new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);

        label = new Label(container, SWT.NONE);
        label.setText("Notes");

        new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);

        label = new Label(container, SWT.NONE);
        label.setText("Semantic Relations");

        new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);

        label = new Label(container, SWT.NONE);
        label.setText("Collection");

        setControl(container);
    }

    private void createLexicalLabelsControls(Composite parent) {
        final Composite inner = new Composite(parent, SWT.NONE);
        inner.setLayout(new GridLayout(2, false));

        Label label = new Label(inner, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        label.setText("Lexical Labels");

        ToolBar toolbar = new ToolBar(inner, SWT.HORIZONTAL | SWT.FLAT);
        toolbar.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1,
                1));

        // / Delete Action
        MenuManager addMenu = new MenuManager("Add");
        Action prefLabelAction = new Action() {
            @Override
            public void run() {
                // LiteralStatementInputWidget widget = new
                // LiteralStatementInputWidget(inner, SWT.NONE);
                // widget.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,
                // false, 2, 1));
                // widget.setModel(model);
                // container.layout(true, true);
            }
        };
        prefLabelAction.setText("Preferred Label");
        Action altLabelAction = new Action() {
            @Override
            public void run() {
                MessageDialog.openInformation(getShell(), "TODO", "TODO!");
            }
        };
        altLabelAction.setText("Alternative Label");
        Action hiddenLabelAction = new Action() {
            @Override
            public void run() {
                MessageDialog.openInformation(getShell(), "TODO", "TODO!");
            }
        };
        hiddenLabelAction.setText("Hidden Label");
        Action notationAction = new Action() {
            @Override
            public void run() {
                MessageDialog.openInformation(getShell(), "TODO", "TODO!");
            }
        };
        notationAction.setText("Notation");
        addMenu.add(prefLabelAction);
        addMenu.add(altLabelAction);
        addMenu.add(hiddenLabelAction);
        addMenu.add(notationAction);

        final MenuManager menuManager = new MenuManager();
        menuManager.add(addMenu);

        ToolItem addButton = new ToolItem(toolbar, SWT.DROP_DOWN);
        addButton.setText("Add...");
        addButton.addSelectionListener(new SelectionAdapter() {
            Menu menu = null;

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (menu == null) {
                    menu = menuManager.createContextMenu(getShell());
                    getShell().setMenu(menu);
                }
                if (e.detail == SWT.ARROW) {
                    final ToolItem toolItem = (ToolItem) e.widget;
                    final ToolBar toolBar = toolItem.getParent();
                    Point point = toolBar.toDisplay(new Point(e.x, e.y));
                    menu.setLocation(point.x, point.y);
                    menu.setVisible(true);
                }
                else {
                    MessageDialog.openQuestion(getShell(), "Test", "Clicked on the button!");
                }
            }
        });

        ToolItem editButton = new ToolItem(toolbar, SWT.PUSH);
        editButton.setText("Edit...");

    }

}
