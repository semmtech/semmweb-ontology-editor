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


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.hp.hpl.jena.reasoner.rulesys.OWLFBRuleReasonerFactory;
import com.hp.hpl.jena.reasoner.rulesys.RDFSRuleReasonerFactory;


/**
 * 
 * @author Sander Stolk
 */
public class InferenceWizardPage extends WizardPage {
    protected final static String TITLE = "Reasoner engine";
    protected final static String DESCRIPTION = "Please select a reasoner engine that should perform the inference.";

    protected Button rdfsButton;
    protected Button owlButton;
    protected String reasonerURI;

    protected InferenceWizardPage(String pageName) {
        super(pageName);
        reasonerURI = RDFSRuleReasonerFactory.URI;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public void createControl(Composite parent) {
        Composite outerComposite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(outerComposite);

        Label reasonerLabel = new Label(outerComposite, SWT.NONE);
        reasonerLabel.setText("The following reasoner engines are available:");
        GridDataFactory.fillDefaults().applyTo(reasonerLabel);

        Button rdfsButton = new Button(outerComposite, SWT.RADIO);
        GridDataFactory.fillDefaults().applyTo(rdfsButton);
        rdfsButton.setText("RDFS");
        rdfsButton.setSelection(reasonerURI == RDFSRuleReasonerFactory.URI);
        rdfsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.widget;
                if (button.getSelection()) {
                    reasonerURI = RDFSRuleReasonerFactory.URI;
                }
            }
        });

        Button owlButton = new Button(outerComposite, SWT.RADIO);
        GridDataFactory.fillDefaults().applyTo(owlButton);
        owlButton.setText("RDFS + OWL");
        owlButton.setSelection(reasonerURI == OWLFBRuleReasonerFactory.URI);
        owlButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Button button = (Button) e.widget;
                if (button.getSelection()) {
                    reasonerURI = OWLFBRuleReasonerFactory.URI;
                    String title = "OWL model inference";
                    String message = "You have opted for model inference using the OWL reasoner. Be aware that even for small models, OWL inference can be a CPU and memory intensive task, which may take a long time or not finish at all.";
                    MessageDialog.openWarning(getShell(), title, message);
                }
            }
        });

        setControl(outerComposite);
    }

    public String getReasonerURI() {
        return reasonerURI;
    }
}
