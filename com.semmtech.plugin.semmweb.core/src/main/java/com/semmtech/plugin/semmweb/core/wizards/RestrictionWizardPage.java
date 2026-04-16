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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.dialog.ResourceSelectionDialog;
import com.semmtech.plugin.semmweb.core.dialog.RestrictionValidator;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.RestrictionResource;
import com.semmtech.plugin.semmweb.core.model.RestrictionsModel;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.widgets.CardinalitySpinner;
import com.semmtech.ui.plugin.widgets.Widgets;


@SuppressWarnings("unused")
public class RestrictionWizardPage extends WizardPage {
    private static Logger logger = Logger.getLogger(RestrictionWizardPage.class);

    private RestrictionValidator validator;
    private LabelProvider labelProvider;
    private OntModel previewModel;
    private List<Resource> properties;

    private Property onProperty;
    private Property typeProperty = OWL.allValuesFrom;
    private RDFNode value;
    private Resource onClass;

    private Restriction oldRestriction;
    private Resource affectedClass;
    private List<Restriction> relatedRestrictions;

    private List<Restriction> createdRestrictions;

    private String onPropertyLabelText = "On Property";
    private String restrictionTypeLabelText = "Restriction Type";
    private String allValuesFromText = "Only";
    private String someValuesFromText = "Some";
    private String hasValueText = "Value";
    private String minCardinalityText = "Minimum";
    private String maxCardinalityText = "Maximum";
    private String cardinalityText = "Cardinality";
    private String onClassLabelText = "Class";

    private String allValuesFormDescription = "All members of this restriction will be restricted on the specified property use, by only allowing instances from the class you provide below.";
    private String someValuesFromDescription = "All members of this restriction will be restricted on the specified property use, by requiring at least one instance from the class you provide below.";
    private String hasValueDescription = "All members of this restriction will be restricted on the specified property use, by allowing only the value entered below.";
    private String minCardinalityDescription = "All members of this restriction will be restricted on the specified property use, by requiring the property to exist at least the minimum number of times defined below.";
    private String maxCardinalityDescription = "All members of this restriction will be restricted on the specified property use, by requiring the property to exist at most the maximum number of times defined below.";
    private String cardinalityDescription = "All members of this restriction will be restricted on the specified property use, by requiring the property to exist exactly the number of times defined below.";

    private Composite container;

    private Button allValuesFromCheckbox;
    private Button someValuesFromCheckbox;
    private Button hasValueCheckbox;
    // private Button minCardinalityCheckbox;
    // private Button maxCardinalityCheckbox;
    private Button cardinalityCheckbox;

    private Button chooseValueButton;
    private Button chooseOnClassButton;

    private Label onPropertyIconLabel;

    private Label descriptionLabel;
    private Label valueLabel;
    private Label valueIconLabel;
    private Label cardinalityLabel;
    private Label onClassLabel;
    private Label onClassIconLabel;

    private Text onPropertyText;
    private Text valueText;
    private Text onClassText;

    CardinalitySpinner cardinalitySpinner;

    public RestrictionWizardPage(String pageName, OntModel model) {
        super(pageName);
        setTitle("Restriction");
        setDescription("Specify the relevant settings for this new restriction.");
        initialize(model);
    }

    private void initialize(OntModel model) {
        previewModel = model;
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        labelProvider = provider.getLabelProvider();

        // / Find better solution; does not include ObjectProperties
        if (properties == null) {
            properties = new ArrayList<>(model.listResourcesWithProperty(RDF.type, RDF.Property)
                    .toList());
        }
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        container.setLayout(new GridLayout(4, false));

        // / Description
        Label label = new Label(container, SWT.WRAP);
        label.setText("Please specify for which property this restriction will be created. Also select the type of restriction you wish to create, and provide a value for this restriction.");
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.widthHint = 1;
        layoutData.heightHint = 34;
        layoutData.horizontalSpan = 4;
        label.setLayoutData(layoutData);

        createOnPropertyControls(container);
        createRestrictionTypeControls(container);
        createDescriptionControls(container);

        setControl(container);
        setPageComplete(false);

        refreshControls();
        validatePage();
    }

    public void createOnClassControls(Composite parent) {
        if (Widgets.isNullOrDisposed(onClassLabel) == false) {
            return;
        }

        onClassLabel = new Label(parent, SWT.NONE);
        onClassLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        onClassLabel.setText(onClassLabelText);

        onClassIconLabel = new Label(parent, SWT.NONE);
        onClassIconLabel.setLayoutData(new GridData(16, 16));

        onClassText = new Text(parent, SWT.BORDER);
        onClassText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        onClassText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                onChooseOnClassClicked();
            }
        });
        onClassText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.character == SWT.DEL) {
                    setOnClass(null);
                    refreshControls();
                    validatePage();
                }
                else if (event.keyCode != SWT.SHIFT) {
                    MessageDialog.openInformation(getShell(), "Disabled",
                            "Please use the button next to this text field to choose the value.");
                    event.doit = false;
                }
            }
        });

        chooseOnClassButton = new Button(parent, SWT.NONE);
        chooseOnClassButton.setText("...");
        chooseOnClassButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                onChooseOnClassClicked();
            }
        });
    }

    public void disposeOnClassControls() {
        if (Widgets.isNullOrDisposed(onClassLabel) == false) {
            onClassLabel.dispose();
            onClassText.dispose();
            onClassIconLabel.dispose();
            chooseOnClassButton.dispose();
        }
    }

    protected void onChooseOnClassClicked() {
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        OntModel currentModel = provider.getOntModel();
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), "Choose Class",
                "Choose a class to be used as qualification.");
        dialog.clearAll();

        dialog.setHierarchicalProperties(Arrays
                .asList(new Property[] { RDFS.subClassOf, RDF.type }));
        dialog.setRootResources(Arrays.asList(new Resource[] { RDFS.Resource }));
        dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDFS.Class, OWL.Class }));

        dialog.setModel(currentModel);

        if (dialog.open() == Window.OK) {
            setOnClass(dialog.getFirstSelectedResource());
            refreshControls();
            validatePage();
        }
    }

    public void createDescriptionControls(Composite parent) {
        descriptionLabel = new Label(parent, SWT.WRAP);
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 4, 1);
        layoutData.widthHint = 560;
        layoutData.heightHint = 35;
        layoutData.horizontalSpan = 4;
        layoutData.verticalIndent = 5;
        descriptionLabel.setLayoutData(layoutData);
    }

    public void createValueControls(Composite parent) {
        if (Widgets.isNullOrDisposed(valueLabel) == false) {
            return;
        }

        valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

        valueIconLabel = new Label(parent, SWT.NONE);
        valueIconLabel.setLayoutData(new GridData(16, 16));

        valueText = new Text(parent, SWT.BORDER);
        valueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        valueText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (chooseValueButton.getEnabled()) {
                    onChooseValueClicked();
                }
            }
        });
        valueText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (allValuesFromCheckbox.getSelection() || someValuesFromCheckbox.getSelection()) {
                    if (event.character == SWT.DEL) {
                        setValue(null);
                        refreshControls();
                        validatePage();
                    }
                    else if (event.keyCode != SWT.SHIFT) {
                        MessageDialog
                                .openInformation(getShell(), "Disabled",
                                        "Please use the button next to this text field to choose the value.");
                        event.doit = false;
                    }
                }
                else if (value != null && value.isResource()) {
                    setValue(null);
                    refreshControls();
                    validatePage();
                }
            }
        });
        valueText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (value == null && valueText.getText().length() > 0) {
                    value = createLiteralValue(previewModel);
                }
                else if (value != null && value.isLiteral() && valueText.getText().length() > 0) {
                    if (!value.asLiteral().getLexicalForm().equals(valueText.getText())) {
                        value = createLiteralValue(previewModel);
                    }
                }
                validatePage();
            }
        });

        chooseValueButton = new Button(parent, SWT.NONE);
        chooseValueButton.setText("...");
        chooseValueButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                onChooseValueClicked();
            }
        });
    }

    public void disposeValueControls() {
        if (Widgets.isNullOrDisposed(valueLabel) == false) {
            valueLabel.dispose();
            valueText.dispose();
            valueIconLabel.dispose();
            chooseValueButton.dispose();
        }
    }

    private int getHighestMinimumCardinality(List<Restriction> restrictions) {
        int result = -1;
        if (restrictions == null) {
            return result;
        }
        for (Restriction restriction : restrictions) {
            int minCardinality = RestrictionResource.getMinCardinality(restriction);
            if (minCardinality > result) {
                result = minCardinality;
            }
        }
        return result;
    }

    private int getLowestMaximumCardinality(List<Restriction> restrictions) {
        int result = -1;
        if (restrictions == null) {
            return result;
        }
        for (Restriction restriction : restrictions) {
            int maxCardinality = RestrictionResource.getMaxCardinality(restriction);
            if (result == -1 || (maxCardinality >= 0 && maxCardinality < result)) {
                result = maxCardinality;
            }
        }
        return result;
    }

    public void createCardinalityControls(Composite parent) {
        if (Widgets.isNullOrDisposed(cardinalityLabel) == false) {
            cardinalityLabel.setVisible(true);
            cardinalitySpinner.setVisible(true);
            return;
        }

        cardinalityLabel = new Label(parent, SWT.NONE);
        cardinalityLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
        cardinalityLabel.setText(cardinalityText);

        cardinalitySpinner = new CardinalitySpinner(parent, SWT.BORDER, false);
        cardinalitySpinner.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1));
        cardinalitySpinner.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validatePage();
            }
        });

        if (oldRestriction != null) {
            relatedRestrictions.add(oldRestriction);
        }
        int initialLimit = getHighestMinimumCardinality(relatedRestrictions);
        if (initialLimit > -1) {
            cardinalitySpinner.setMin(initialLimit);
        }
        initialLimit = getLowestMaximumCardinality(relatedRestrictions);
        if (initialLimit > -1) {
            cardinalitySpinner.setMax(initialLimit);
        }
        if (oldRestriction != null) {
            relatedRestrictions.remove(oldRestriction);
        }

        container.layout(true, true);
    }

    public void disposeCardinalityControls() {
        if (Widgets.isNullOrDisposed(cardinalityLabel) == false) {
            cardinalityLabel.dispose();
            cardinalitySpinner.dispose();
        }
    }

    protected void onChooseValueClicked() {
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        OntModel currentModel = provider.getOntModel();
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(),
                "Choose Restriction Value", "Choose a value to be used with this restriction.");
        dialog.clearAll();

        dialog.setHierarchicalProperties(Arrays
                .asList(new Property[] { RDFS.subClassOf, RDF.type }));
        dialog.setRootResources(Arrays.asList(new Resource[] { RDFS.Resource }));
        boolean individualsAllowed = hasValueCheckbox.getSelection();
        if (individualsAllowed) {
            dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDFS.Class, OWL.Class,
                    OWL.Thing }));
        }
        else {
            dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDFS.Class, OWL.Class }));
        }

        dialog.setModel(currentModel);

        if (dialog.open() == Window.OK) {
            setValue(dialog.getFirstSelectedResource());
            refreshControls();
            validatePage();
        }
    }

    public void createRestrictionTypeControls(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        layoutData.verticalIndent = 2;
        label.setLayoutData(layoutData);
        label.setText(restrictionTypeLabelText);

        new Label(parent, SWT.NONE);

        Composite restrictionTypeComposite = new Composite(parent, SWT.NONE);
        layoutData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        layoutData.verticalIndent = 2;
        restrictionTypeComposite.setLayoutData(layoutData);
        GridLayout layout = new GridLayout(1, false);
        layout.marginBottom = 4;
        layout.verticalSpacing = 4;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        restrictionTypeComposite.setLayout(layout);

        allValuesFromCheckbox = new Button(restrictionTypeComposite, SWT.RADIO);
        allValuesFromCheckbox.setImage(CorePlugin.getDefault().getImage(
                CorePluginImages.IMG_OWL_ALL_VALUES_FROM));
        allValuesFromCheckbox.setText(allValuesFromText);
        allValuesFromCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (((Button) e.getSource()).getSelection() == true) {
                    onRestrictionTypeChanged();
                }
            }
        });

        someValuesFromCheckbox = new Button(restrictionTypeComposite, SWT.RADIO);
        someValuesFromCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
        someValuesFromCheckbox.setImage(CorePlugin.getDefault().getImage(
                CorePluginImages.IMG_OWL_SOME_VALUES_FROM));
        someValuesFromCheckbox.setText(someValuesFromText);
        someValuesFromCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (((Button) e.getSource()).getSelection() == true) {
                    onRestrictionTypeChanged();
                }
            }
        });

        hasValueCheckbox = new Button(restrictionTypeComposite, SWT.RADIO);
        hasValueCheckbox.setImage(CorePlugin.getDefault().getImage(
                CorePluginImages.IMG_OWL_HAS_VALUE));
        hasValueCheckbox.setText(hasValueText);
        hasValueCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (((Button) e.getSource()).getSelection() == true) {
                    onRestrictionTypeChanged();
                }
            }
        });

        cardinalityCheckbox = new Button(restrictionTypeComposite, SWT.RADIO);
        cardinalityCheckbox.setImage(CorePlugin.getDefault().getImage(
                CorePluginImages.IMG_OWL_CARDINALITY));
        cardinalityCheckbox.setText(cardinalityText);
        cardinalityCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (((Button) e.getSource()).getSelection() == true) {
                    onRestrictionTypeChanged();
                }
            }
        });

        new Label(parent, SWT.NONE);
    }

    private void createOnPropertyControls(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        GridData layoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        layoutData.widthHint = (LabelsPreference.showReadableLabels() ? 90 : 130);
        label.setLayoutData(layoutData);
        label.setText(onPropertyLabelText);

        onPropertyIconLabel = new Label(parent, SWT.NONE);
        onPropertyIconLabel.setLayoutData(new GridData(16, 16));

        onPropertyText = new Text(parent, SWT.BORDER);
        onPropertyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        onPropertyText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                onChooseOnPropertyClicked();
            }
        });
        onPropertyText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.character == SWT.DEL) {
                    setOnProperty(null);
                    refreshControls();
                    validatePage();
                }
                else if (event.keyCode != SWT.SHIFT) {
                    MessageDialog
                            .openInformation(getShell(), "Disabled",
                                    "Please use the button next to this text field to choose the property.");
                    event.doit = false;
                }
            }
        });
        onPropertyText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // PlatformUI.getWorkbench().getHelpSystem().setHelp(onPropertyText,
        // ContextIds.MESSAGE);

        createChooseButton(parent, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                onChooseOnPropertyClicked();
            }
        });
    }

    protected void onChooseOnPropertyClicked() {
        IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
        OntModel currentModel = provider.getOntModel();

        String title = "Select Restriction Property";
        String message = "Please select a property for which the restriction will be created.";
        ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), title, message);
        dialog.setModel(currentModel);

        dialog.clearAll();
        dialog.setHierarchicalProperties(Arrays.asList(new Property[] { RDFS.subPropertyOf,
                RDFS.subClassOf, RDF.type }));
        dialog.setRootResources(Arrays.asList(new Resource[] { RDF.Property }));
        dialog.setAllowedResourceTypes(Arrays.asList(new Resource[] { RDF.Property }));
        dialog.setHierarchicalViewSelected(false);

        if (dialog.open() == Window.OK) {
            Resource selected = dialog.getFirstSelectedResource();

            if (selected == null) {
                return;
            }

            onProperty = dialog.getFirstSelectedResource().as(Property.class);
            onPropertyText.setText(labelProvider.getText(onProperty));
            onPropertyIconLabel.setImage(labelProvider.getImage(onProperty));
            validatePage();
        }
    }

    private Button createChooseButton(Composite parent, SelectionListener listener) {
        Button button = new Button(parent, SWT.BUTTON1);
        button.setText("...");
        button.setLayoutData(new GridData(GridData.END));
        button.addSelectionListener(listener);
        return button;
    }

    protected void onRestrictionTypeChanged() {
        Property previousProperty = typeProperty;
        if (allValuesFromCheckbox.getSelection()) {
            typeProperty = OWL.allValuesFrom;
        }
        else if (someValuesFromCheckbox.getSelection()) {
            typeProperty = OWL.someValuesFrom;
        }
        else if (hasValueCheckbox.getSelection()) {
            typeProperty = OWL.hasValue;
        }
        else if (cardinalityCheckbox.getSelection()) {
            typeProperty = OWL.cardinality;
        }

        if (previousProperty.equals(OWL.allValuesFrom)
                || previousProperty.equals(OWL.someValuesFrom)) {
            if (typeProperty.equals(OWL.minCardinality) || typeProperty.equals(OWL.maxCardinality)
                    || typeProperty.equals(OWL.cardinality)) {
                if (value != null) {
                    setOnClass(value.asResource());
                }
                setValue(null);
            }
        }
        else if (previousProperty.equals(OWL.hasValue)) {
            boolean isClass = false;
            if (value != null && value.isResource()) {
                isClass = (previewModel.getOntClass(value.asResource().getURI()) != null);
            }
            if (!isClass) {
                setValue(null);
            }
            else if (typeProperty.equals(OWL.minCardinality)
                    || typeProperty.equals(OWL.maxCardinality)
                    || typeProperty.equals(OWL.cardinality)) {
                setOnClass(value.asResource());
                setValue(null);
            }
        }
        else if (previousProperty.equals(OWL.minCardinality)
                || previousProperty.equals(OWL2.minQualifiedCardinality)
                || previousProperty.equals(OWL.maxCardinality)
                || previousProperty.equals(OWL2.maxQualifiedCardinality)
                || previousProperty.equals(OWL.cardinality)
                || previousProperty.equals(OWL2.qualifiedCardinality)) {
            if (typeProperty.equals(OWL.minCardinality) && typeProperty.equals(OWL.maxCardinality)
                    && typeProperty.equals(OWL.cardinality)) {
                setValue(null);
            }
            else if (typeProperty.equals(OWL.allValuesFrom)
                    || typeProperty.equals(OWL.someValuesFrom) || typeProperty.equals(OWL.hasValue)) {
                if (onClass != null) {
                    setValue(onClass);
                }
                setOnClass(null);
            }
        }

        refreshControls();
        validatePage();
    }

    /**
     * Updates the content of the controls based on the set fields.
     */
    private void refreshControls() {
        onPropertyText.setText((onProperty != null) ? labelProvider.getText(onProperty) : "");
        onPropertyIconLabel.setImage((onProperty != null) ? labelProvider.getImage(onProperty)
                : null);

        allValuesFromCheckbox.setSelection(typeProperty.equals(OWL.allValuesFrom));
        someValuesFromCheckbox.setSelection(typeProperty.equals(OWL.someValuesFrom));
        hasValueCheckbox.setSelection(typeProperty.equals(OWL.hasValue));
        cardinalityCheckbox.setSelection(typeProperty.equals(OWL.cardinality)
                || typeProperty.equals(OWL2.qualifiedCardinality)
                || typeProperty.equals(OWL.minCardinality)
                || typeProperty.equals(OWL2.minQualifiedCardinality)
                || typeProperty.equals(OWL.maxCardinality)
                || typeProperty.equals(OWL2.maxQualifiedCardinality));

        boolean showValue = (cardinalityCheckbox.getSelection() == false);
        boolean showCardinality = (cardinalityCheckbox.getSelection() == true);
        boolean showOnClass = (cardinalityCheckbox.getSelection() == true);

        if (showValue == false) {
            disposeValueControls();
        }
        if (showCardinality == false) {
            disposeCardinalityControls();
        }
        if (showOnClass == false) {
            disposeOnClassControls();
        }

        if (showValue == true) {
            createValueControls(container);
            valueText.setText((value != null) ? labelProvider.getText(value) : "");
            valueIconLabel.setImage((value != null) ? labelProvider.getImage(value) : null);
        }
        if (showCardinality == true) {
            createCardinalityControls(container);
        }
        if (showOnClass == true) {
            createOnClassControls(container);
            onClassText.setText((onClass != null) ? labelProvider.getText(onClass) : "");
            onClassIconLabel.setImage((onClass != null) ? labelProvider.getImage(onClass) : null);
        }

        if (showCardinality == false && showValue == true) {
            // ensuring the form obtains a high enough height when first
            // initializing
            createCardinalityControls(container);
            cardinalityLabel.setVisible(false);
            cardinalitySpinner.setVisible(false);
        }

        if (allValuesFromCheckbox.getSelection()) {
            descriptionLabel.setText(allValuesFormDescription);
            valueLabel.setText(allValuesFromText);
        }
        else if (someValuesFromCheckbox.getSelection()) {
            descriptionLabel.setText(someValuesFromDescription);
            valueLabel.setText(someValuesFromText);
        }
        else if (hasValueCheckbox.getSelection()) {
            descriptionLabel.setText(hasValueDescription);
            valueLabel.setText(hasValueText);
        }
        else if (cardinalityCheckbox.getSelection()) {
            descriptionLabel.setText(cardinalityDescription);
        }

        container.layout(true, true);
    }

    /**
     * Validates the current wizard page, and informs user of invalid input(s).
     */
    private void validatePage() {
        setPageComplete(false);
        if (onProperty == null) {
            setErrorMessage("Please provide the property on which restriction will apply.");
            setPageComplete(false);
        }
        else if ((allValuesFromCheckbox.getSelection() || someValuesFromCheckbox.getSelection())
                && valueText.getText().isEmpty()) {
            setErrorMessage("This restriction requires a class to be specified of which the instances are possible values for the selected property.");
            setPageComplete(false);
        }
        else if (hasValueCheckbox.getSelection() && valueText.getText().isEmpty()) {
            setErrorMessage("This restriction requires a value to be specified for the selected property.");
            setPageComplete(false);
        }
        else if (cardinalityCheckbox.getSelection() && cardinalitySpinner.getUnbounded() == true
                && cardinalitySpinner.getMin() == 0) {
            setErrorMessage("A cardinality restriction requires either an upper bound or a lower bound greater than 0.");
            setPageComplete(false);
        }
        else if (validator != null) {
            String errorMessage = null;
            List<Restriction> restrictions = createRestrictionsStatements(previewModel);
            if (restrictions != null) {
                for (Resource resource : restrictions) {
                    Restriction restriction = resource.as(Restriction.class);
                    errorMessage = validator.isValid(restriction);

                    if (errorMessage == null) {
                        errorMessage = validator.isConsistent(restriction);
                    }

                    if (errorMessage != null) {
                        break;
                    }
                }
            }
            setErrorMessage(errorMessage);
            setPageComplete(errorMessage == null);
        }
        else {
            setErrorMessage(null);
            setPageComplete(true);
        }
    }

    private RDFNode createLiteralValue(OntModel model) {
        return model.createTypedLiteral(valueText.getText());
    }

    public List<Restriction> createRestrictionsStatements(OntModel model) {
        Preconditions.checkNotNull(onProperty);

        if (createdRestrictions != null) {
            for (Resource restriction : createdRestrictions) {
                restriction.removeProperties();
            }
            createdRestrictions.clear();
        }

        RestrictionsModel restrictionsModel = new RestrictionsModel(model);
        createdRestrictions = Lists.newArrayList();

        if (hasValueCheckbox.getSelection() && value != null) {
            Restriction newRestriction = model.createHasValueRestriction(null, onProperty, value);
            createdRestrictions.add(newRestriction);
            return createdRestrictions;
        }

        Resource onClass = null;
        int minCardinality = 0;
        int maxCardinality = -1;

        if (allValuesFromCheckbox.getSelection() && (value != null)) {
            onClass = value.asResource();
        }
        else if (someValuesFromCheckbox.getSelection() && (value != null)) {
            minCardinality = 1;
            onClass = value.asResource();
        }
        else if (cardinalityCheckbox.getSelection()) {
            minCardinality = cardinalitySpinner.getMin();
            if (!cardinalitySpinner.getUnbounded()) {
                maxCardinality = cardinalitySpinner.getMax();
            }
            onClass = this.onClass;
        }

        createdRestrictions = restrictionsModel.createRestrictions(onProperty, minCardinality,
                maxCardinality, onClass);
        return createdRestrictions;
    }

    public void setOnProperty(Property onProperty) {
        this.onProperty = onProperty;
    }

    public Property getOnProperty() {
        return onProperty;
    }

    public void setTypeProperty(Property typeProperty) {
        this.typeProperty = typeProperty;
    }

    public Property getTypeProperty() {
        return typeProperty;
    }

    public void setOnClass(Resource onClass) {
        this.onClass = onClass;
    }

    public Resource getOnClass() {
        return onClass;
    }

    public void setValue(RDFNode value) {
        this.value = value;
    }

    public RDFNode getValue() {
        return value;
    }

    public RestrictionValidator getValidator() {
        return validator;
    }

    public void setValidator(RestrictionValidator validator) {
        this.validator = validator;
    }

    public List<Restriction> getNewRestrictions() {
        return createdRestrictions;
    }

    public void setOldRestriction(Restriction oldRestriction) {
        this.oldRestriction = oldRestriction;
    }

    public void setAffectedClass(Resource affectedClass) {
        this.affectedClass = affectedClass;
    }

    public void setRelatedRestrictions(List<Restriction> relatedRestrictions) {
        this.relatedRestrictions = relatedRestrictions;
    }
}
