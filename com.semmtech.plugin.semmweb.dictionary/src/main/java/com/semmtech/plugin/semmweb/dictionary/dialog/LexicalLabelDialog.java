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

package com.semmtech.plugin.semmweb.dictionary.dialog;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.hp.hpl.jena.rdf.model.Property;
import com.semmtech.semantics.vocabulary.SKOS;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


public class LexicalLabelDialog extends AbstractMessageInputDialog {
    private static final String DIALOG_TITLE = "Lexcial Label";
    private static final String DIALOG_MESSAGE = "Please provide a lexical label, by specifying the type and content.";

    private Text text;
    private Combo combo;
    private Property property = SKOS.prefLabel;
    private String label = null;
    private String lang = null;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     */
    public LexicalLabelDialog(Shell parentShell) {
        super(parentShell, DIALOG_TITLE, DIALOG_MESSAGE);
    }

    @SuppressWarnings("unused")
    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout gl_composite = new GridLayout(2, false);
        gl_composite.marginWidth = 0;
        gl_composite.horizontalSpacing = 0;
        composite.setLayout(gl_composite);

        Label lblType = new Label(composite, SWT.NONE);
        GridData gd_lblType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblType.widthHint = 90;
        lblType.setLayoutData(gd_lblType);
        lblType.setText("Type:");

        Composite typeComposite = new Composite(composite, SWT.NONE);
        typeComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Button prefRadio = new Button(typeComposite, SWT.RADIO);
        prefRadio.setText("preferred");

        Button altRadio = new Button(typeComposite, SWT.RADIO);
        altRadio.setText("alternative");

        Button hiddenRadio = new Button(typeComposite, SWT.RADIO);
        hiddenRadio.setText("hidden");

        Button notationRadio = new Button(typeComposite, SWT.RADIO);
        notationRadio.setText("notation");

        Label lblLabel = new Label(composite, SWT.NONE);
        lblLabel.setText("Label:");

        Composite labelComposite = new Composite(composite, SWT.NONE);
        GridLayout gl_labelComposite = new GridLayout(2, false);
        gl_labelComposite.horizontalSpacing = 0;
        gl_labelComposite.verticalSpacing = 0;
        gl_labelComposite.marginWidth = 0;
        gl_labelComposite.marginHeight = 0;
        labelComposite.setLayout(gl_labelComposite);
        GridData gd_labelComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_labelComposite.heightHint = 24;
        labelComposite.setLayoutData(gd_labelComposite);

        text = new Text(labelComposite, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        combo = new Combo(labelComposite, SWT.NONE);
        combo.setItems(new String[] { "nl", "en", "de", "fr" });
        GridData gd_combo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_combo.horizontalIndent = 5;
        combo.setLayoutData(gd_combo);
        new Label(labelComposite, SWT.NONE);
        new Label(labelComposite, SWT.NONE);

        if (label != null)
            text.setText(label);
        if (lang != null)
            combo.setText(lang);
        if (property != null) {
            prefRadio.setSelection(property.getURI().equals(SKOS.prefLabel.getURI()));
            altRadio.setSelection(property.getURI().equals(SKOS.altLabel.getURI()));
            hiddenRadio.setSelection(property.getURI().equals(SKOS.hiddenLabel.getURI()));
            notationRadio.setSelection(property.getURI().equals(SKOS.notation.getURI()));
        }

        prefRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                property = SKOS.prefLabel;
            }
        });
        altRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                property = SKOS.altLabel;
            }
        });
        hiddenRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                property = SKOS.hiddenLabel;
            }
        });
        notationRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                property = SKOS.notation;
            }
        });
        text.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                label = text.getText();
            }
        });
        combo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                lang = combo.getText();
            }
        });

        return composite;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLanguage(String lang) {
        this.lang = lang;
    }

    public String getLanguage() {
        return lang;
    }

    public void setProperty(Property prop) {
        this.property = prop;
    }

    public Property getProperty() {
        return property;
    }
}
