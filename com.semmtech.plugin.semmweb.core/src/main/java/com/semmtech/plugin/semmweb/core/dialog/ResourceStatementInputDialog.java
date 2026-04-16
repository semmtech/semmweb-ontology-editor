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

package com.semmtech.plugin.semmweb.core.dialog;


import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ResourceNameComparator;
import com.semmtech.ui.plugin.viewers.TableLabelProvider;


/**
 * Allows the user to create a RDF statement between a subject and another
 * resource.
 * 
 * @author Mike Henrichs
 * 
 */
public class ResourceStatementInputDialog extends StatementInputDialog {

    private Resource allowedResourceType;
    private Resource object = null;
    private List<Resource> allowedResources;
    private TableViewer objectViewer;

    public ResourceStatementInputDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData) label.getLayoutData()).widthHint = DIALOG_LABEL_SIZE;
        ((GridData) label.getLayoutData()).verticalAlignment = SWT.TOP;
        ((GridData) label.getLayoutData()).verticalIndent = 5;
        label.setText("Object:");

        // / TODO: Make multiple selection possible
        objectViewer = new TableViewer(composite, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);

        Table table = objectViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        ((GridData) table.getLayoutData()).verticalIndent = 8;
        ((GridData) table.getLayoutData()).heightHint = 180;
        table.setHeaderVisible(true);

        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText("Resource");
        column.setWidth(160);
        column.setResizable(true);
        column.setMoveable(false);

        column = new TableColumn(table, SWT.NONE);
        column.setText("rdf:type");
        column.setWidth(110);
        column.setResizable(true);
        column.setMoveable(false);

        column = new TableColumn(table, SWT.NONE);
        column.setText("rdfs:label");
        column.setWidth(100);
        column.setResizable(true);
        column.setMoveable(false);

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        final LabelProvider labelProvider = provider.getLabelProvider();

        objectViewer.setContentProvider(new ArrayContentProvider());
        objectViewer.setLabelProvider(new TableLabelProvider() {

            @Override
            public String getColumnText(Object element, int columnIndex) {
                if (element instanceof Resource) {
                    Resource resource = (Resource) element;
                    Resource type = resource.getPropertyResourceValue(RDF.type);
                    String label = "";
                    Statement labelStatement = resource.getProperty(RDFS.label);
                    if (labelStatement != null && labelStatement.getObject() != null)
                        label = labelProvider.getText(labelStatement.getObject());
                    switch (columnIndex) {
                    case 0:
                        return labelProvider.getText(element);
                    case 1:
                        return (type == null) ? "" : labelProvider.getText(type);
                    case 2:
                        return label;
                    default:
                        return null;
                    }
                }
                return null;
            }

            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                if (element instanceof Resource) {
                    switch (columnIndex) {
                    case 0:
                        return labelProvider.getImage(element);
                    default:
                        return null;
                    }
                }
                return null;
            }
        });
        objectViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean enabled = false;
                if (objectViewer.getSelection() instanceof IStructuredSelection) {
                    object = (Resource) ((IStructuredSelection) objectViewer.getSelection())
                            .getFirstElement();
                    enabled = true;
                }
                setOKButtonEnabled(enabled);
            }
        });
        if (allowedResources == null) {
            Model baseModel = getModel();
            OntModel inferredModel = ModelFactory.createOntologyModel(
                    OntModelSpec.RDFS_MEM_RDFS_INF, baseModel);
            allowedResources = inferredModel
                    .listSubjectsWithProperty(RDF.type, allowedResourceType).toList();
            Collections.sort(allowedResources, new ResourceNameComparator(labelProvider));
        }
        objectViewer.setInput(allowedResources);
        if (object != null) {
            int index = allowedResources.indexOf(object);
            if (index >= 0)
                objectViewer.getTable().select(index);
        }

        return composite;
    }

    /**
     * Sets the list of allowed resources.
     * 
     * @param allowedResourceList
     *            The list of resources that are shown as possible objects.
     */
    public void setAllowedResources(List<Resource> allowedResourceList) {
        allowedResources = allowedResourceList;
    }

    public void setAllowedResourceType(Resource type) {
        this.allowedResourceType = type;
    }

    public void setObject(Resource object) {
        this.object = object;
        setOKButtonEnabled(true);
    }

    public Resource getObject() {
        return object;
    }

    @Override
    public Statement createStatement() {
        if (object != null)
            return getModel().createStatement(getSubject(), getPredicate(), object);
        return null;
    }
}
