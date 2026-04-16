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


import java.util.List;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.LiteralStatementInputDialog;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreferencePage;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.plugin.semmweb.core.viewers.StatementContentProvider;
import com.semmtech.plugin.semmweb.core.widgets.ResourceLabel;


public class RelabelResourceWizardPage extends WizardPage {
    private static final String PROPERTY_PREDICATE = "predicateProperty";
    private static final String PROPERTY_LABEL = "labelProperty";
    private static final String PROPERTY_LANGUAGE = "languageProperty";

    private OntModel model;
    private Resource resource;

    private ResourceLabel resourceLabel;
    private TableViewer labelViewer;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;

    private List<Statement> addedStatements = Lists.newArrayList();
    private List<Statement> removedStatements = Lists.newArrayList();
    private List<Property> allowedProperties = null;
    private List<Statement> statements = null;

    public RelabelResourceWizardPage(String pageName, OntModel model, Resource resource) {
        super(pageName);
        setTitle("Annotation");
        setDescription("Provide annotation for the selected resource.");
        this.resource = resource;
        this.model = model;
        this.statements = Lists.newArrayList();
        this.addedStatements = Lists.newArrayList();
        this.removedStatements = Lists.newArrayList();

        initProperties();
    }

    private void initProperties() {
        allowedProperties = Lists.newArrayList(model.getProperty(RDFS.label.getURI()),
                model.getProperty(RDFS.comment.getURI()),
                model.getProperty(OWL.versionInfo.getURI()));
    }

    @Override
    public void createControl(Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        container.setLayout(new GridLayout());

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        label.setText("This page allows you to provide multiple annotation properties for the selected resource below.");

        resourceLabel = new ResourceLabel(container, SWT.NONE);
        resourceLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        ((GridData) resourceLabel.getLayoutData()).verticalIndent = 5;

        if (resource != null) {
            resourceLabel.setResource(resource);
        }

        if (model != null) {
            resourceLabel.setLabelProvider(new ModelNodeLabelProvider(model));
        }

        Composite innerLabelComposite = new Composite(container, SWT.NONE);
        GridLayout gl_innerLabelComposite = new GridLayout(2, false);
        gl_innerLabelComposite.marginWidth = 0;
        innerLabelComposite.setLayout(gl_innerLabelComposite);
        innerLabelComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createStatementTableControls(innerLabelComposite);

        Composite buttonComposite = new Composite(innerLabelComposite, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(1, false));
        {
            GridData data = new GridData(GridData.FILL_HORIZONTAL);
            data.grabExcessHorizontalSpace = false;
            data.verticalAlignment = GridData.BEGINNING;
            buttonComposite.setLayoutData(data);
        }
        ((GridLayout) buttonComposite.getLayout()).marginRight = 0;

        addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData());
        ((GridData) addButton.getLayoutData()).widthHint = 92;
        addButton.setText("Add...");
        addButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                createLabelStatement();
            }
        });

        editButton = new Button(buttonComposite, SWT.PUSH);
        editButton.setLayoutData(new GridData());
        ((GridData) editButton.getLayoutData()).widthHint = 92;
        editButton.setText("Edit...");
        editButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                editLabelStatement();
            }
        });
        editButton.setEnabled(false);

        deleteButton = new Button(buttonComposite, SWT.PUSH);
        deleteButton.setLayoutData(new GridData());
        ((GridData) deleteButton.getLayoutData()).widthHint = 92;
        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                removeSelectedStatement();
            }
        });

        Link link = new Link(container, SWT.NONE);
        {
            GridData data = new GridData();
            data.verticalIndent = 4;
            link.setLayoutData(data);
        }
        link.setText("Click <a>here</a> to configure language order settings.");
        link.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(getShell(),
                        LanguagesPreferencePage.ID, null, null);
                dialog.open();
            }
        });

        setControl(container);
    }

    private Property[] getDisplayProperties() {
        Property[] result = new Property[] { model.getProperty(RDFS.label.getURI()),
                model.getProperty(RDFS.comment.getURI()),
                model.getProperty(OWL.versionInfo.getURI()) };
        return result;
    }

    private String[] getDisplayLanguages() {
        List<DisplayLanguage> displayLanguages = LanguagesPreference.getDisplayLanguages();
        List<String> languagesList = Lists.newArrayList();
        for (DisplayLanguage language : displayLanguages) {
            // if (language.getCode() != null && language.getCode().equals("")
            // != true) {
            languagesList.add(language.getCode());
            // }
        }
        String[] result = new String[languagesList.size()];
        languagesList.toArray(result);
        return result;
    }

    private void createStatementTableControls(Composite container) {
        labelViewer = new TableViewer(container, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
        labelViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        labelViewer.getTable().setHeaderVisible(true);
        labelViewer.getTable().setLinesVisible(true);
        labelViewer.setContentProvider(new StatementContentProvider());
        labelViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                boolean selected = (labelViewer.getSelection() != null);
                deleteButton.setEnabled(selected);
                editButton.setEnabled(selected);
            }
        });
        labelViewer.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (getSelectedStatement() == null) {
                    createLabelStatement();
                }
                else {
                    editLabelStatement();
                }
            }
        });

        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        Preconditions.checkNotNull(provider);
        final LabelProvider labelProvider = provider.getLabelProvider();

        // Create columns
        TableViewerColumn column = createTableViewerColumn("Property", 0, 125);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                return labelProvider.getText(((Statement) element).getPredicate());
            }

            @Override
            public Image getImage(Object element) {
                return labelProvider.getImage(((Statement) element).getPredicate());
            }
        });
        column = createTableViewerColumn("Literal", 1, 190);
        column.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Statement statement = (Statement) element;
                return labelProvider.getText(statement.getObject());
            }

            @Override
            public Image getImage(Object element) {
                Statement statement = (Statement) element;
                return labelProvider.getImage(statement.getObject());
            }
        });
        column = createTableViewerColumn("Language", 2, 70);
        column.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Statement statement = (Statement) element;
                RDFNode object = statement.getObject();
                if (object.isLiteral()) {
                    String language = statement.getLanguage();
                    return String.format("%s", language);
                }
                return null;
            }

            @Override
            public Image getImage(Object element) {
                return null;
            }
        });

        CellEditor[] editors = new CellEditor[3];
        ComboBoxViewerCellEditor cbPredicateEditor = new ComboBoxViewerCellEditor(
                labelViewer.getTable(), SWT.READ_ONLY);
        cbPredicateEditor.setLabelProvider(new ModelNodeLabelProvider(model));
        cbPredicateEditor.setContentProvider(new IStructuredContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement != null && inputElement instanceof Property[]) {
                    return (Property[]) inputElement;
                }
                return null;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });
        cbPredicateEditor.setInput(getDisplayProperties());
        editors[0] = cbPredicateEditor;

        editors[1] = new TextCellEditor(labelViewer.getTable());

        ComboBoxViewerCellEditor cbLanguageEditor = new ComboBoxViewerCellEditor(
                labelViewer.getTable());
        cbLanguageEditor.setLabelProvider(new LabelProvider());
        cbLanguageEditor.setContentProvider(new IStructuredContentProvider() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement != null && inputElement instanceof String[]) {
                    return (String[]) inputElement;
                }
                return null;
            }

            @Override
            public void dispose() {
            }

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });

        cbLanguageEditor.setInput(getDisplayLanguages());
        editors[2] = cbLanguageEditor;

        labelViewer.setColumnProperties(new String[] { PROPERTY_PREDICATE, PROPERTY_LABEL,
                PROPERTY_LANGUAGE });
        labelViewer.setCellEditors(editors);
        labelViewer.setCellModifier(new ICellModifier() {

            @Override
            public void modify(Object element, String property, Object value) {
                if (element == null) {
                    return;
                }
                Object data = ((TableItem) element).getData();
                int dataPosition = labelViewer.getTable().getSelectionIndex();

                if (data instanceof Statement && property.equals(PROPERTY_PREDICATE)) {
                    Statement statement = (Statement) data;
                    if (value == null) {
                        value = statement.getPredicate();
                    }
                    Statement newStatement = model.createStatement(resource,
                            model.createProperty(value.toString()), statement.getObject());

                    removeStatement(statement);
                    addStatement(newStatement, dataPosition);
                    refreshLabelViewer();
                    labelViewer.setSelection(
                            new StructuredSelection(
                                    statements.get(statements.indexOf(newStatement))), true);
                }
                if (data instanceof Statement
                        && (property.equals(PROPERTY_LABEL) || property.equals(PROPERTY_LANGUAGE))) {
                    Statement statement = (Statement) data;
                    RDFNode literal = statement.getObject();
                    String lang = literal.asLiteral().getLanguage();

                    Statement newStatement = null;
                    if (property.equals(PROPERTY_LABEL)) {
                        String newLabel = (value == null) ? "" : value.toString();
                        newStatement = statement.changeObject(model.createLiteral(newLabel, lang));
                    }
                    else {
                        newStatement = (value == null) ? statement.changeObject(model
                                .createLiteral(literal.asLiteral().getLexicalForm())) : statement
                                .changeObject(model.createLiteral(literal.asLiteral()
                                        .getLexicalForm(), value.toString()));
                    }

                    removeStatement(statement);
                    addStatement(newStatement, dataPosition);
                    refreshLabelViewer();
                    labelViewer.setSelection(
                            new StructuredSelection(
                                    statements.get(statements.indexOf(newStatement))), true);
                }
            }

            @Override
            public Object getValue(Object element, String property) {
                if (element instanceof Statement && property.equals(PROPERTY_PREDICATE)) {
                    Property predicate = ((Statement) element).getPredicate();
                    return predicate;
                }
                if (element instanceof Statement && property.equals(PROPERTY_LABEL)) {
                    String value = ((Statement) element).getString();
                    return value;
                }
                if (element instanceof Statement && property.equals(PROPERTY_LANGUAGE)) {
                    String value = ((Statement) element).getObject().asLiteral().getLanguage();
                    return value;
                }
                return null;
            }

            @Override
            public boolean canModify(Object element, String property) {
                if (property.equals(PROPERTY_PREDICATE) || property.equals(PROPERTY_LABEL)
                        || property.equals(PROPERTY_LANGUAGE))
                    return true;
                return false;
            }
        });

        // if (resource != null) {
        // for (Statement statement : model.listStatements(
        // new SimpleSelector(resource, (Property) null, (RDFNode)
        // null)).toList()) {
        // if (allowedProperties.contains(statement.getPredicate())) {
        // statements.add(statement);
        // }
        // }
        // }

        labelViewer.setInput(statements);
    }

    private void setNewSubjectForStatements(List<Statement> statements, Resource resource) {
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            Property predicate = stmt.getPredicate();
            RDFNode node = stmt.getObject();
            statements.remove(i);
            statements.add(i, model.createStatement(resource, predicate, node));
        }
    }

    public void setResource(Resource resource) {
        this.resource = resource;

        if (resource != null) {
            setNewSubjectForStatements(statements, resource);
            setNewSubjectForStatements(addedStatements, resource);
            setNewSubjectForStatements(removedStatements, resource);
            if (resourceLabel != null) {
                resourceLabel.setResource(resource);
            }
            for (Statement statement : model.listStatements(
                    new SimpleSelector(resource, (Property) null, (RDFNode) null)).toList()) {
                if (allowedProperties.contains(statement.getPredicate())
                        && statements.contains(statement) == false
                        && removedStatements.contains(statement) == false) {
                    statements.add(statement);
                }
            }
        }
        if (labelViewer != null) {
            labelViewer.setInput(statements);
        }
    }

    private Statement getSelectedStatement() {
        if (labelViewer.getSelection() instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) labelViewer.getSelection();
            return (Statement) selection.getFirstElement();
        }
        return null;
    }

    protected void removeSelectedStatement() {
        Statement statement = getSelectedStatement();
        if (statement != null) {
            removeStatement(statement);
            refreshLabelViewer();
            // these buttons are forced to a disabled state, as a null-selection
            // change does not trigger the SelectionChanged listener
            deleteButton.setEnabled(false);
            editButton.setEnabled(false);
        }
    }

    private void removeStatement(Statement statement) {
        if (addedStatements.contains(statement)) {
            addedStatements.remove(statement);
        }

        if (!removedStatements.contains(statement)) {
            removedStatements.add(statement);
        }

        if (statements.contains(statement)) {
            statements.remove(statement);
        }
    }

    private void addStatement(Statement statement, int insertPosition) {
        if (!addedStatements.contains(statement)) {
            addedStatements.add(statement);
        }

        if (removedStatements.contains(statement)) {
            removedStatements.remove(statement);
        }

        if (!statements.contains(statement)) {
            statements.add(insertPosition, statement);
        }
    }

    private void addStatement(Statement statement) {
        if (!addedStatements.contains(statement)) {
            addedStatements.add(statement);
        }

        if (removedStatements.contains(statement)) {
            removedStatements.remove(statement);
        }

        if (!statements.contains(statement)) {
            statements.add(statement);
        }
    }

    protected void editLabelStatement() {
        Statement oldStatement = getSelectedStatement();
        if (oldStatement != null) {
            int oldStatementPosition = labelViewer.getTable().getSelectionIndex();
            String value = oldStatement.getLiteral().getString();
            String language = oldStatement.getLiteral().getLanguage();
            if (language == null)
                language = "";
            Property oldProperty = oldStatement.getPredicate();
            Resource datatype = null;
            if (oldStatement.getLiteral().getDatatypeURI() == null)
                datatype = model.createResource(XSDDatatype.XSDstring.getURI());
            else
                datatype = model.createResource(oldStatement.getLiteral().getDatatypeURI());
            String title = "Edit Label";
            String message = "Please select the property and value which you would like to add to the subject below.";

            LiteralStatementInputDialog dialog = new LiteralStatementInputDialog(getShell(), title,
                    message);

            dialog.setModel(model);
            dialog.setSubject(resource);
            dialog.setProperties(allowedProperties);
            dialog.setSelectedProperty(allowedProperties.indexOf(oldProperty));
            dialog.setValue(value);
            dialog.setLanguage(language);
            dialog.setDatatype(datatype);
            dialog.setDatatypeVisible(false);
            dialog.setLanguageVisible(true);

            if (dialog.open() == Window.OK) {
                Statement newStatement = dialog.createStatement();
                if (newStatement != null) {
                    if (!oldStatement.equals(newStatement)) {
                        removeStatement(oldStatement);
                        addStatement(newStatement, oldStatementPosition);
                    }
                    refreshLabelViewer();
                    labelViewer.setSelection(
                            new StructuredSelection(
                                    statements.get(statements.indexOf(newStatement))), true);
                }
            }
        }
    }

    protected void createLabelStatement() {
        Resource datatype = model.createResource(XSDDatatype.XSDstring.getURI());
        String title = "Create Label";
        String message = "Please select the property and value which you would like to add to the subject below.";
        LiteralStatementInputDialog dialog = new LiteralStatementInputDialog(getShell(), title,
                message);

        dialog.setModel(model);
        dialog.setSubject(resource);
        dialog.setProperties(allowedProperties);
        dialog.setSelectedProperty(0);
        dialog.setDatatype(datatype);
        dialog.setDatatypeVisible(false);
        dialog.setLanguageVisible(true);

        if (dialog.open() == Window.OK) {
            Statement statement = dialog.createStatement();
            if (statement != null) {
                addStatement(statement);
                refreshLabelViewer();
                labelViewer.setSelection(
                        new StructuredSelection(statements.get(statements.indexOf(statement))),
                        true);
            }
        }
    }

    private void refreshLabelViewer() {
        labelViewer.setInput(statements);
    }

    private TableViewerColumn createTableViewerColumn(String title, int index, int bound) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(labelViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(false);
        column.addSelectionListener(getSelectionAdapter(column, index));
        return viewerColumn;
    }

    private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
        SelectionAdapter selectionAdapter = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // / TODO: Add logic for comparator
                // int direction = labelViewer.getTable().getSortDirection();
                // labelViewer.getTable().setSortDirection(dir);
                labelViewer.getTable().setSortColumn(column);
                // labelViewer.refresh();
            }
        };
        return selectionAdapter;
    }

    public List<Statement> getAddedStatements() {
        return addedStatements;
    }

    public List<Statement> getRemovedStatements() {
        return removedStatements;
    }
}
