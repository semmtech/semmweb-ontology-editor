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


import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.base.Preconditions;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.widgets.ResourceLabel;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


/**
 * The StatementInputDialog extends the AbstractMessageInputDialog.
 * 
 * This dialog can be used to RDF create statements. The statements will be
 * created within the provided model, using the provided subject. User may
 * choose which predicate is used for the new statement (depending on the number
 * of properties provided; if only one property is supplied, this property will
 * be used as predicate for the new statement).
 * 
 * However this abstract dialog class will be further implemented in the
 * ResourceStatementDialog or LiteralStatementInputDialog the first of this
 * allows users to create statements between a subject and a resource; the
 * second creates a statement between a subject and a literal.
 * 
 * @author Mike Henrichs
 * 
 */
public abstract class StatementInputDialog extends AbstractMessageInputDialog {

    private Model model;
    private Resource subject;
    private List<Property> properties;
    private int selectedPropertyIndex = -1;

    private ResourceLabel subjectLabel;
    private ResourceLabel predicateLabel;
    private TableViewer propertiesViewer;

    protected StatementInputDialog(Shell parentShell) {
        super(parentShell);
    }

    protected StatementInputDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);

        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        ((GridData) label.getLayoutData()).widthHint = DIALOG_LABEL_SIZE;
        ((GridData) label.getLayoutData()).verticalIndent = 1;
        label.setText("Subject:");

        subjectLabel = new ResourceLabel(composite, SWT.NONE);
        if (getModel() != null) {
            subjectLabel.setLabelProvider(new ModelNodeLabelProvider(getModel()));
        }
        subjectLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
        if (getSubject() != null) {
            subjectLabel.setResource(getSubject());
        }

        if (properties.size() > 0) {
            label = new Label(composite, SWT.NONE);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
            ((GridData) label.getLayoutData()).widthHint = DIALOG_LABEL_SIZE;
            ((GridData) label.getLayoutData()).verticalIndent = 5;
            label.setText("Predicate:");

            if (properties.size() > 1) {
                createPropertiesTable(composite);
            }
            else if (properties.size() == 1) {
                predicateLabel = new ResourceLabel(composite, SWT.NONE);
                predicateLabel.setResource(properties.get(0));
                predicateLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
                ((GridData) predicateLabel.getLayoutData()).verticalIndent = 5;
            }
        }

        return composite;
    }

    private void createPropertiesTable(Composite inner) {
        propertiesViewer = new TableViewer(inner, SWT.SINGLE | SWT.BORDER);
        propertiesViewer.getTable().setLayoutData(new GridData());
        ((GridData) propertiesViewer.getTable().getLayoutData()).heightHint = 60;
        propertiesViewer.setContentProvider(new IStructuredContentProvider() {
            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement instanceof List) {
                    return ((List<?>) inputElement).toArray();
                }
                return null;
            }
        });
        propertiesViewer.getTable().addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (propertiesViewer.getSelection() instanceof IStructuredSelection) {
                    Property selectedProperty = (Property) ((IStructuredSelection) propertiesViewer
                            .getSelection()).getFirstElement();
                    selectedPropertyIndex = properties.indexOf(selectedProperty);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        propertiesViewer.getTable().setHeaderVisible(true);
        propertiesViewer.getTable().setLinesVisible(true);

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        final LabelProvider labelProvider = provider.getLabelProvider();

        TableViewerColumn viewerColumn = new TableViewerColumn(propertiesViewer, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText("Property");
        column.setWidth(200);
        column.setResizable(false);
        column.setMoveable(false);
        viewerColumn.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return labelProvider.getText(element);
            }

            @Override
            public Image getImage(Object element) {
                return labelProvider.getImage(element);
            }
        });
        propertiesViewer.setInput(properties);
        propertiesViewer.getTable().setSelection(selectedPropertyIndex);
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public void setSubject(Resource subject) {
        this.subject = subject;
    }

    public Resource getSubject() {
        return subject;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public void setSelectedProperty(int index) {
        this.selectedPropertyIndex = index;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public Property getPredicate() {
        return properties.get(selectedPropertyIndex);
    }

    public abstract Statement createStatement();
}
