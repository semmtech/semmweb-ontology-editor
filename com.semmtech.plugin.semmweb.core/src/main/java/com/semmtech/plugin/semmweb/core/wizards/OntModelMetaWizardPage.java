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


import java.util.Calendar;
import java.util.TimeZone;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;


@SuppressWarnings("unused")
public class OntModelMetaWizardPage extends WizardPage {

    private static final String LOCKED_IMAGE = CorePluginImages.IMG_LOCKED;
    private static final String UNLOCKED_IMAGE = CorePluginImages.IMG_UNLOCKED_GREY;

    private Text labelText;
    private Text commentText;
    private Composite rdfmetaComposite;
    private Button rdfmetaCheckbox;
    private Composite container;
    private ExpandableComposite expandableComposite;

    protected OntModelMetaWizardPage() {
        super("metaPage");
        setTitle("Additional Annotations");
        setDescription("Provide additional meta-information for this ontology.");
    }

    public void createEmptyControl(Composite parent) {
        container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        initialize();
        dialogChanged();
        setControl(container);
    }

    @Override
    public void createControl(Composite parent) {
        container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);

        Label label = new Label(container, SWT.NONE);
        label.setText("Please select the meta-information types.");
        label.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1));

        GridData checkboxData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 1, 1);
        checkboxData.horizontalIndent = 3;
        checkboxData.verticalIndent = 3;

        rdfmetaCheckbox = new Button(container, SWT.CHECK);
        rdfmetaCheckbox.setText("RDF Metadata");
        rdfmetaCheckbox.setLayoutData(checkboxData);
        rdfmetaCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (rdfmetaCheckbox.getSelection()) {
                    GridData data = new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1);
                    data.verticalIndent = 3;

                    Label label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("URI:");
                    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1));

                    GridLayout layout = new GridLayout(2, false);
                    layout.marginHeight = 0;
                    layout.marginWidth = 0;
                    layout.horizontalSpacing = 4;
                    layout.verticalSpacing = 0;

                    Composite composite = new Composite(rdfmetaComposite, SWT.NONE);
                    composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true,
                            false, 1, 1));
                    composite.setLayout(layout);

                    final Text uriText = new Text(composite, SWT.BORDER);
                    uriText.setEnabled(false);
                    uriText.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true,
                            false, 1, 1));

                    ToolBar toolbar = new ToolBar(composite, SWT.HORIZONTAL);
                    final ToolItem item = new ToolItem(toolbar, SWT.CHECK);
                    item.setImage(CorePlugin.getDefault().getImage(LOCKED_IMAGE));
                    item.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            uriText.setEnabled(item.getSelection());
                            item.setImage(CorePlugin.getDefault().getImage(
                                    item.getSelection() ? UNLOCKED_IMAGE : LOCKED_IMAGE));
                        }
                    });

                    label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("Name:");
                    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1));

                    Text text = new Text(rdfmetaComposite, SWT.BORDER);
                    text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,
                            1, 1));

                    label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("Acronym:");
                    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1));

                    text = new Text(rdfmetaComposite, SWT.BORDER);
                    text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,
                            1, 1));

                    label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("Description:");
                    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1));

                    text = new Text(rdfmetaComposite, SWT.BORDER);
                    text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,
                            1, 1));
                    ((GridData) text.getLayoutData()).heightHint = 40;

                    data = new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 1, 1);
                    data.verticalIndent = 2;
                    label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("Creation Date:");
                    label.setLayoutData(data);

                    layout = new GridLayout(3, false);
                    layout.marginHeight = 0;
                    layout.marginWidth = 0;
                    layout.horizontalSpacing = 4;
                    layout.verticalSpacing = 0;

                    composite = new Composite(rdfmetaComposite, SWT.NONE);
                    composite.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING,
                            true, false, 1, 1));
                    composite.setLayout(layout);

                    final DateTime datetime = new DateTime(composite, SWT.DATE | SWT.LONG);
                    datetime.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING,
                            true, false, 1, 1));

                    final DateTime time = new DateTime(composite, SWT.TIME | SWT.LONG);
                    time.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true,
                            false, 1, 1));

                    Button calendar = new Button(composite, SWT.PUSH);
                    calendar.setImage(CorePlugin.getDefault().getImage(
                            CorePluginImages.IMG_DATETIME));
                    data = new GridData(22, 23);
                    calendar.setLayoutData(data);
                    calendar.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                            datetime.setDate(calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH));
                            time.setTime(calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
                        }
                    });

                    label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("Version:");
                    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1));

                    text = new Text(rdfmetaComposite, SWT.BORDER);
                    text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,
                            1, 1));

                    label = new Label(rdfmetaComposite, SWT.NONE);
                    label.setText("Language:");
                    label.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false,
                            false, 1, 1));

                    text = new Text(rdfmetaComposite, SWT.BORDER);
                    text.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false,
                            1, 1));

                    ((GridData) rdfmetaComposite.getLayoutData()).heightHint = 215;
                }
                else {
                    ((GridData) rdfmetaComposite.getLayoutData()).heightHint = 0;
                }

                container.getShell().layout(true, true);
                container.getShell().pack(true);
            }
        });

        GridData subData = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
        GridLayout subLayout = new GridLayout(2, false);
        subData.heightHint = 0;
        subLayout.marginLeft = 25;
        subLayout.horizontalSpacing = 13;

        rdfmetaComposite = new Composite(container, SWT.NULL);
        rdfmetaComposite.setLayoutData(subData);
        rdfmetaComposite.setLayout(subLayout);

        initialize();
        dialogChanged();
        setControl(container);
    }

    // public void createOldControl(Composite parent) {
    // Composite container = new Composite(parent, SWT.NULL);
    // GridLayout layout = new GridLayout();
    // container.setLayout(layout);
    //
    // layout.numColumns = 2;
    // layout.verticalSpacing = 9;
    //
    // /// Container
    // GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    // data.widthHint = 90;
    // Label label = new Label(container, SWT.NULL);
    // label.setText("rdfs:label:");
    // label.setLayoutData(data);
    //
    // labelText = new Text(container, SWT.BORDER | SWT.SINGLE);
    // labelText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // labelText.addModifyListener(new ModifyListener() {
    // public void modifyText(ModifyEvent e) {
    // //dialogChanged();
    // }
    // });
    //
    // data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
    // label = new Label(container, SWT.NULL);
    // label.setText("rdfs:comment:");
    // label.setLayoutData(data);
    //
    // commentText = new Text(container, SWT.BORDER | SWT.SINGLE);
    // commentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // commentText.addModifyListener(new ModifyListener() {
    // public void modifyText(ModifyEvent e) {
    // //dialogChanged();
    // }
    // });
    //
    // initialize();
    // dialogChanged();
    // setControl(container);
    // }

    private void dialogChanged() {

    }

    private void initialize() {

    }

    public String getLabel() {
        return null;
    }

    public String getComment() {
        return null;
    }
}
