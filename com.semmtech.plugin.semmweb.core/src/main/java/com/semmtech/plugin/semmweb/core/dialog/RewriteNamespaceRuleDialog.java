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


import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.semantics.util.NamespaceMapping;
import com.semmtech.semantics.util.NamespaceMappingComparator;
import com.semmtech.semantics.util.NamespaceRewriteRule;
import com.semmtech.ui.plugin.dialog.AbstractMessageInputDialog;


public class RewriteNamespaceRuleDialog extends AbstractMessageInputDialog {

    private List<NamespaceMapping> namespaces;

    private String fromUri;
    private String toUri;
    private boolean updatePrefixMapping;
    private Combo nsCombo;
    private Text uriText;
    private Button updatePrefixCheckbox;

    public RewriteNamespaceRuleDialog(Shell parentShell, String title, String message) {
        super(parentShell, title, message);
        this.showErrorMessage = true;
        this.namespaces = Lists.newArrayList();
    }

    @SuppressWarnings("unused")
    @Override
    protected Control createInputArea(Composite parent) {
        Composite composite = (Composite) super.createInputArea(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.marginTop = 10;
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 6;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        Label label = new Label(composite, SWT.NONE);
        GridData layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        layoutData.widthHint = 95;
        label.setLayoutData(layoutData);
        label.setText("Original:");

        nsCombo = new Combo(composite, SWT.READ_ONLY);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        nsCombo.setLayoutData(layoutData);
        nsCombo.setItems(createNamespaceItems());
        nsCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                fromUri = null;
                int selectedIndex = nsCombo.getSelectionIndex();
                if (selectedIndex > 0 && selectedIndex <= namespaces.size()) {
                    NamespaceMapping selected = namespaces.get(selectedIndex - 1);
                    fromUri = selected.getURI();
                    toUri = fromUri;
                    uriText.setText(toUri);
                    updatePrefixCheckbox.setEnabled(selected.hasPrefix());
                    if (!selected.hasPrefix()) {
                        updatePrefixCheckbox.setSelection(false);
                        updatePrefixCheckbox.setText("Update namespace URI for prefix");
                    }
                    else {
                        updatePrefixCheckbox.setText(String.format(
                                "Update namespace URI for prefix \"%s:\"", selected.getPrefix()));
                    }
                }
                validateInput();
            }
        });
        int selectedIndex = 0;
        if (fromUri != null) {
            for (int i = 0; i < namespaces.size(); i++) {
                NamespaceMapping mapping = namespaces.get(i);
                if (mapping.getURI().equals(fromUri)) {
                    selectedIndex = i + 1;
                    break;
                }
            }
        }
        else {
            if (namespaces.size() == 1) {
                selectedIndex = 1;
                nsCombo.setEnabled(false);
                fromUri = namespaces.get(0).getURI();
                toUri = fromUri;
                validateInput();
            }
        }
        nsCombo.select(selectedIndex);

        label = new Label(composite, SWT.NONE);
        layoutData = new GridData(GridData.BEGINNING, GridData.CENTER, false, false);
        layoutData.widthHint = 95;
        label.setLayoutData(layoutData);
        label.setText("Rewritten URI:");

        uriText = new Text(composite, SWT.BORDER);
        layoutData = new GridData(GridData.FILL, GridData.CENTER, true, false);
        uriText.setLayoutData(layoutData);
        uriText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                toUri = uriText.getText();
                validateInput();
            }
        });
        if (toUri != null) {
            uriText.setText(toUri);
        }

        new Label(composite, SWT.NONE);

        updatePrefixCheckbox = new Button(composite, SWT.CHECK);
        updatePrefixCheckbox.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
                false, 1, 1));
        updatePrefixCheckbox.setText("Update namespace prefix mapping");
        updatePrefixCheckbox.setSelection(updatePrefixMapping);
        if (selectedIndex == 0) {
            updatePrefixCheckbox.setEnabled(false);
        }
        else {
            updatePrefixCheckbox.setEnabled(namespaces.get(selectedIndex - 1).hasPrefix());
        }
        updatePrefixCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updatePrefixMapping = updatePrefixCheckbox.getSelection();
                validateInput();
            }
        });

        applyDialogFont(composite);

        return composite;
    }

    @Override
    protected void validateInput() {
        String errorMessage = null;
        if (fromUri == null) {
            errorMessage = "Please select the original namespace";
        }
        else if (Strings.isNullOrEmpty(toUri)) {
            errorMessage = "New namespace URI cannot be null or empty";
        }
        else if (!toUri.endsWith("/") && !toUri.endsWith("#")) {
            errorMessage = "New namespace URI must end with either a '/' or '#'";
        }
        setErrorMessage(errorMessage);
    }

    private String[] createNamespaceItems() {
        Collections.sort(namespaces, new NamespaceMappingComparator());
        String[] items = new String[namespaces.size() + 1];
        items[0] = "[Select namespace]";
        for (int i = 1; i <= namespaces.size(); i++) {
            items[i] = namespaces.get(i - 1).toString();
        }
        return items;
    }

    public void setNamespaces(Iterable<NamespaceMapping> namespaces) {
        this.namespaces = Lists.newArrayList(namespaces);
    }

    public void addAllNamespaces(Collection<NamespaceMapping> namespaces) {
        this.namespaces.addAll(namespaces);
    }

    public void setUpdatePrefixMapping(boolean update) {
        this.updatePrefixMapping = update;
    }

    public boolean isUpdatePrefixMapping() {
        return updatePrefixMapping;
    }

    public void setRule(NamespaceRewriteRule rule) {
        Preconditions.checkNotNull(rule, "NamespaceRewriteRule cannot be null!");
        fromUri = rule.getFrom();
        toUri = rule.getTo();
        updatePrefixMapping = rule.isUpdatePrefixMap();
    }

    public NamespaceRewriteRule getRule() {
        if (Strings.isNullOrEmpty(fromUri) || Strings.isNullOrEmpty(toUri)) {
            return null;
        }
        return new NamespaceRewriteRule(fromUri, toUri, updatePrefixMapping);
    }
}
