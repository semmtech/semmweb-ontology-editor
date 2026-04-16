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

package com.semmtech.plugin.semmweb.sparql.debug.ui.context;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.semmtech.plugin.semmweb.sparql.SparqlPlugin;
import com.semmtech.plugin.semmweb.sparql.SparqlPluginImages;
import com.semmtech.plugin.semmweb.sparql.debug.ui.SparqlLaunchConfigurationConstants;


public class ContextLaunchConfigurationTab extends AbstractLaunchConfigurationTab implements
        Listener {

    private boolean useCustomContext = false;
    private String username;
    private String password;
    private Label usernameLabel;
    private Text usernameText;
    private Label passwordLabel;
    private Text passwordText;
    private Button customContextCheckbox;

    @Override
    public void createControl(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(top);
        GridLayoutFactory.fillDefaults().margins(8, 8).applyTo(top);

        customContextCheckbox = new Button(top, SWT.CHECK);
        customContextCheckbox.setText("Use custom query context");
        customContextCheckbox.setSelection(useCustomContext);
        customContextCheckbox.addListener(SWT.Selection, this);

        Group contextGroup = new Group(top, SWT.NONE);
        contextGroup.setText("Query Context");
        GridLayoutFactory.fillDefaults().numColumns(2).spacing(5, 4).extendedMargins(8, 8, 5, 12)
                .applyTo(contextGroup);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(contextGroup);

        usernameLabel = new Label(contextGroup, SWT.NONE);
        usernameLabel.setText("User:");
        usernameLabel.setEnabled(useCustomContext);
        GridDataFactory.swtDefaults().hint(80, SWT.DEFAULT).applyTo(usernameLabel);

        usernameText = new Text(contextGroup, SWT.BORDER);
        usernameText.setText(Strings.nullToEmpty(username));
        usernameText.addListener(SWT.Modify, this);
        usernameText.setEnabled(useCustomContext);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(usernameText);

        passwordLabel = new Label(contextGroup, SWT.NONE);
        passwordLabel.setEnabled(useCustomContext);
        passwordLabel.setText("Password:");

        passwordText = new Text(contextGroup, SWT.PASSWORD | SWT.BORDER);
        passwordText.setText(Strings.nullToEmpty(password));
        passwordText.addListener(SWT.Modify, this);
        passwordText.setEnabled(useCustomContext);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(passwordText);

        setControl(top);
    }

    @Override
    public String getName() {
        return "Context";
    }

    @Override
    public Image getImage() {
        return SparqlPlugin.getDefault().getImage(SparqlPluginImages.IMG_CONTEXT);
    }

    @Override
    public void initializeFrom(ILaunchConfiguration config) {
        try {
            useCustomContext = Boolean.parseBoolean(config.getAttribute(
                    SparqlLaunchConfigurationConstants.ATTR_CUSTOM_CONTEXT, "false"));
            username = config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_USER, "");
            password = config.getAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_PASSWORD,
                    "");

            refresh();
        }
        catch (CoreException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private void refresh() {
        // Update Enable of Controls
        usernameLabel.setEnabled(useCustomContext);
        usernameText.setEnabled(useCustomContext);
        passwordLabel.setEnabled(useCustomContext);
        passwordText.setEnabled(useCustomContext);

        customContextCheckbox.setSelection(useCustomContext);
        usernameText.setText(Strings.nullToEmpty(username));
        passwordText.setText(Strings.nullToEmpty(password));
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(SparqlLaunchConfigurationConstants.ATTR_CUSTOM_CONTEXT,
                Boolean.toString(useCustomContext));
        config.setAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_USER, username);
        config.setAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_PASSWORD, password);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy config) {
        config.setAttribute(SparqlLaunchConfigurationConstants.ATTR_CUSTOM_CONTEXT,
                Boolean.toString(false));
        config.setAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_USER, "");
        config.setAttribute(SparqlLaunchConfigurationConstants.ATTR_AUTH_PASSWORD, "");
    }

    @Override
    public void handleEvent(Event event) {
        if (event.type == SWT.Selection) {
            if (event.widget == customContextCheckbox) {
                useCustomContext = customContextCheckbox.getSelection();
                setDirty(true);
                getLaunchConfigurationDialog().updateButtons();
                if (!useCustomContext) {
                    username = "";
                    password = "";
                    refresh();
                }
            }
        }
        else if (event.type == SWT.Modify) {
            if (event.widget == usernameText) {
                username = usernameText.getText();
                setDirty(true);
                getLaunchConfigurationDialog().updateButtons();
            }
            else if (event.widget == passwordText) {
                password = passwordText.getText();
                setDirty(true);
                getLaunchConfigurationDialog().updateButtons();
            }
        }

    }

}
