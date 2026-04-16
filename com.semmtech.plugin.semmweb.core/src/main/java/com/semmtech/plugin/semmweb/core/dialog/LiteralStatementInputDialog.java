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


import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.viewers.ModelNodeLabelProvider;
import com.semmtech.ui.plugin.viewers.StructuredContentProvider;


/**
 * Allows the user to create a RDF statement between a subject and a literal.
 * 
 * The literal created can have a datatype and/or language (xml:lang)
 * 
 * @author Mike Henrichs
 * 
 */
public class LiteralStatementInputDialog extends StatementInputDialog {
    private LabelProvider labelProvider = null;
    private static List<String> languages = null;
    private static String previousLanguage = "nl";
    private static List<Resource> datatypes = null;
    private static Resource previousDatatype = null;

    private String value;
    private String language;
    private String typeUri;

    private boolean languageVisible = true;
    private boolean datatypeVisible = true;
    private List<DisplayLanguage> displayLanguages = LanguagesPreference.getDisplayLanguages();
    private Text valueText;
    private ComboViewer languageComboViewer;
    private ComboViewer typeComboViewer;

    public LiteralStatementInputDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
    }

    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);

        // Instead use model if present
        if (getModel() == null) {
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            Preconditions.checkNotNull(provider);
            labelProvider = provider.getLabelProvider();
        }
        else {
            labelProvider = new ModelNodeLabelProvider(getModel());
        }
        if (datatypes == null) {
            datatypes = Lists.newArrayList();
            TypeMapper mapper = new TypeMapper();
            XSDDatatype.loadXSDSimpleTypes(mapper);
            Model model = getModel();
            for (Iterator<RDFDatatype> iter = mapper.listTypes(); iter.hasNext();) {
                RDFDatatype type = iter.next();
                datatypes.add(model.createResource(type.getURI()));
            }
            if (previousDatatype == null) {
                previousDatatype = model.createResource(XSDDatatype.XSDstring.getURI());
            }
        }

        createLiteralValueControls(composite);
        createLiteralLanguageControls(composite);
        createDatatypeControls(composite);

        return composite;
    }

    private void createLiteralValueControls(Composite container) {
        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        ((GridData) label.getLayoutData()).widthHint = DIALOG_LABEL_SIZE;
        ((GridData) label.getLayoutData()).verticalIndent = 6;
        label.setText("Value:");

        valueText = new Text(container, SWT.SINGLE | SWT.BORDER);
        valueText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        ((GridData) valueText.getLayoutData()).verticalIndent = 6;
        valueText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                value = valueText.getText();
                validateInput();
            }
        });
        if (value != null) {
            valueText.setText(value);
        }
    }

    private void createLiteralLanguageControls(Composite container) {
        if (!languageVisible) {
            return;
        }

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        ((GridData) label.getLayoutData()).widthHint = DIALOG_LABEL_SIZE;
        ((GridData) label.getLayoutData()).verticalIndent = 1;
        label.setText("Language:");

        languageComboViewer = new ComboViewer(container, SWT.BORDER);
        languageComboViewer.getCombo().setLayoutData(new GridData(40, 20));

        if (languages == null) {
            languages = Lists.newArrayList();
            for (DisplayLanguage language : displayLanguages) {
                languages.add(language.getCode());
            }
        }
        else {
            // / If list of languages have already been composed; check if all
            // displayLanguages are available in the list
            for (DisplayLanguage language : displayLanguages) {
                if (!languages.contains(language.getCode())) {
                    languages.add(language.getCode());
                }
            }
        }

        String[] languageItems = new String[languages.size()];
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i) == null) {
                languageItems[i] = "";
            }
            else {
                languageItems[i] = languages.get(i);
            }
        }

        languageComboViewer.getCombo().setItems(languageItems);
        languageComboViewer.getCombo().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                language = languageComboViewer.getCombo().getText();
                validateInput();
            }
        });
        if (previousLanguage != null) {
            languageComboViewer.getCombo().select(languages.indexOf(previousLanguage));
        }
    }

    private void createDatatypeControls(Composite container) {
        if (!datatypeVisible) {
            return;
        }

        Label label = new Label(container, SWT.NONE);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        ((GridData) label.getLayoutData()).widthHint = DIALOG_LABEL_SIZE;
        ((GridData) label.getLayoutData()).verticalIndent = 1;
        label.setText("Datatype:");

        typeComboViewer = new ComboViewer(container, SWT.BORDER | SWT.READ_ONLY);
        typeComboViewer.getCombo().setLayoutData(new GridData());
        typeComboViewer.setLabelProvider(labelProvider);
        typeComboViewer.setContentProvider(new StructuredContentProvider() {

            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement == datatypes) {
                    return datatypes.toArray();
                }
                return null;
            }
        });
        typeComboViewer.setInput(datatypes);
        typeComboViewer.getCombo().setVisible(datatypeVisible);
        typeComboViewer.getCombo().addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (typeComboViewer.getSelection() instanceof IStructuredSelection) {
                    Object selectedObject = ((IStructuredSelection) typeComboViewer.getSelection())
                            .getFirstElement();
                    if (selectedObject instanceof Resource) {
                        typeUri = ((Resource) selectedObject).getURI();
                    }
                }
                validateInput();
            }
        });
        if (previousDatatype != null) {
            typeComboViewer.getCombo().select(datatypes.indexOf(previousDatatype));
        }
    }

    @Override
    protected void okPressed() {
        previousLanguage = language;
        if (!languages.contains(previousLanguage) && previousLanguage != null) {
            languages.add(previousLanguage);
        }
        previousDatatype = getModel().createResource(typeUri);
        super.okPressed();
    }

    @Override
    protected void validateInput() {

    }

    public void setLanguageVisible(boolean visible) {
        languageVisible = visible;
    }

    public void setDatatypeVisible(boolean visible) {
        datatypeVisible = visible;
    }

    @SuppressWarnings("static-method")
    public void setDatatype(Resource datatype) {
        previousDatatype = datatype;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setLanguage(String language) {
        previousLanguage = language;
    }

    /**
     * Returns a Literal which can be either typed (if a XSD data type is
     * selected) or a language (xml:lang attribute) when data type is xsd:string
     * and language is selected.
     * 
     * @return
     */
    public Literal getLiteral() {
        Model model = getModel();
        Literal literal = null;
        if (value == null) {
            return null;
        }
        if (typeUri != null && !typeUri.equals(XSDDatatype.XSDstring.getURI())) {
            literal = model.createTypedLiteral(value, typeUri);
        }
        else if (language != null && language.length() > 0) {
            literal = model.createLiteral(value, language);
        }
        else {
            literal = model.createLiteral(value);
        }
        return literal;
    }

    @Override
    public Statement createStatement() {
        Literal literal = getLiteral();
        if (literal != null) {
            return getModel().createStatement(getSubject(), getPredicate(), literal);
        }
        return null;
    }
}
