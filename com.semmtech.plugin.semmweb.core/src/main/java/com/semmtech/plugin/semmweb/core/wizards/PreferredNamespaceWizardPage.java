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

package com.semmtech.plugin.semmweb.core.wizards;


import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.NamespaceURIValidator;
import com.semmtech.plugin.semmweb.core.dialog.WorkspaceOntologySpecDialog;


public class PreferredNamespaceWizardPage extends WizardPage {

    private static final String PREFIX_EMPTY = "<empty>";
    private static final String PREFIX_BASE = "<base>";
    private static final String PREFIX_NEW = "<new>";
    private static final String PREFIX_CREATE = "<create>";

    private int previousSelected = -1;

    private final OntModel model;
    private Model baseModel;
    private List<String> prefixes;

    private Composite container;
    private Combo ontologyCombo;

    private final String baseUri;
    private String newUri;
    private String preferredUri;

    public PreferredNamespaceWizardPage(String pageName, OntModel model) {
        super(pageName);
        setTitle("Preferred namespace");
        setDescription("Set the preferred namespace to create new resources in.");

        this.model = model;
        this.baseUri = CorePlugin.getDefault().getActiveModelProvider().getBaseURI();

        setPageComplete(true);
    }

    public void setBaseModel(Model baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(2, false));

        Label label = new Label(container, SWT.WRAP);
        label.setText("Please specify the namespace within which you are likely to add new resources the most. This namespace will be set as the model's base URI and will be selected by default when creating new resources.");
        GridDataFactory.fillDefaults().hint(500, SWT.DEFAULT).span(2, 1).applyTo(label);

        label = new Label(container, SWT.NONE);
        GridDataFactory.swtDefaults().span(2, 1).hint(SWT.DEFAULT, 6).applyTo(label);

        label = new Label(container, SWT.NONE);
        GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).hint(70, SWT.DEFAULT)
                .applyTo(label);
        label.setText("Preferred:");

        ontologyCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
        ontologyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (baseModel == null) {
            baseModel = model;
        }
        prefixes = Lists.newArrayList(baseModel.getNsPrefixMap().keySet());
        Collections.sort(prefixes);
        if (!Strings.isNullOrEmpty(baseUri) && baseModel.getNsURIPrefix(baseUri) == null) {
            prefixes.add(0, PREFIX_BASE);
        }
        prefixes.add(0, PREFIX_EMPTY);
        prefixes.add(PREFIX_CREATE);

        String[] namespaceItems = new String[prefixes.size()];
        previousSelected = 0;
        for (int i = 0; i < prefixes.size(); i++) {
            String prefix = prefixes.get(i);
            if (PREFIX_EMPTY.equals(prefix)) {
                namespaceItems[i] = ""; // "[Select preferred namespace]";
            }
            else if (PREFIX_BASE.equals(prefix)) {
                namespaceItems[i] = String.format("<%s>", baseUri);
                previousSelected = i;
            }
            else if (PREFIX_CREATE.equals(prefix)) {
                namespaceItems[i] = "[Create namespace...]";
            }
            else {
                String uri = baseModel.getNsPrefixURI(prefix);
                if (uri.equals(preferredUri)) {
                    previousSelected = i;
                }
                namespaceItems[i] = String.format("%s: <%s>", prefix, uri);
            }
        }
        ontologyCombo.setItems(namespaceItems);
        ontologyCombo.select(previousSelected);
        ontologyCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selected = ontologyCombo.getSelectionIndex();
                String prefix = prefixes.get(selected);

                if (PREFIX_EMPTY.equals(prefix)) {
                    preferredUri = null;
                }
                else if (PREFIX_BASE.equals(prefix)) {
                    preferredUri = baseUri;
                }
                else if (PREFIX_NEW.equals(prefix)) {
                    preferredUri = newUri;
                }
                else if (PREFIX_CREATE.equals(prefix)) {
                    createNewNamespace();
                }
                else {
                    preferredUri = baseModel.getNsPrefixURI(prefix);
                }

                previousSelected = selected;
            }
        });
        setControl(container);
    }

    protected void createNewNamespace() {
        WorkspaceOntologySpecDialog dialog = new WorkspaceOntologySpecDialog(getShell(),
                "New Namespace", "Specify the URI for the new namespace below.",
                new NamespaceURIValidator());
        dialog.setHidePrefix(true);
        dialog.setHideAltURL(true);
        if (dialog.open() == Window.OK) {
            newUri = dialog.getPublicURI();
            int index = prefixes.indexOf(PREFIX_NEW);
            List<String> list = Lists.newArrayList(ontologyCombo.getItems());
            if (index < 0) {
                if (prefixes.contains(PREFIX_BASE)) {
                    index = prefixes.indexOf(PREFIX_BASE) + 1;
                }
                else {
                    index = 1;
                }
                prefixes.add(index, PREFIX_NEW);
                list.add(index, String.format("<%s>", newUri));
            }
            else {
                list.set(index, String.format("<%s>", newUri));
            }
            String[] array = new String[list.size()];
            list.toArray(array);
            ontologyCombo.setItems(array);
            previousSelected = index;
            preferredUri = newUri;
        }
        ontologyCombo.select(previousSelected);
    }

    public String getPreferredNamespaceURI() {
        return preferredUri;
    }

    public void setPreferredNamespaceURI(String uri) {
        this.preferredUri = uri;
    }

    // TODO: Add validators to the dialog to create new namespace URIs, based on
    // methods below:

    // private String isValidNamespace(String namespace) {
    // if (Strings.isNullOrEmpty(namespace)) {
    // return "No namespace was specified.";
    // }
    // if (!namespace.startsWith("<") || !namespace.endsWith(">")) {
    // return
    // "A namespace needs to be indicated with a '<' at the start and a '>' at the end.";
    // }
    // return isValidPublicUri(namespace.substring(1, namespace.length() - 1));
    // }
    //
    // private String isValidPublicUri(String uri) {
    // if (Strings.isNullOrEmpty(uri)) {
    // return "The URI of the namespace still needs to be specified.";
    // }
    // char lastChar = uri.charAt(uri.length() - 1);
    // if ((lastChar != '/') && (lastChar != '#')) {
    // return "The URI of the namespace needs to end in a '/' or '#'.";
    // }
    // return null;
    // }
}
