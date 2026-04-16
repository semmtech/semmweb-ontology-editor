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

package com.semmtech.plugin.semmweb.laces.ldp.preferences;


import java.net.URL;
import java.util.ArrayList;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.laces.ldp.model.LDPServer;


/**
 * 
 * @author Mike Henrichs
 * @author Sander Stolk
 */
public class LDPPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
    public final static String ID = "com.semmtech.plugin.semmweb.laces.ldp.preferences.ldp";

    private String serverUrl;
    private String username;
    private String password;
    private Text serverUrlText;
    private Text usernameText;
    private Text passwordText;

    public LDPPreferencePage() {
        super();

        if (LDPPreference.getServers().size() > 0) {
            LDPServer first = LDPPreference.getServers().get(0);
            serverUrl = first.getServerUrl();
            username = first.getUsername();
            password = first.getPassword();
        }
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected Control createContents(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);

        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(0, 3, 3, 0).applyTo(main);

        Link informationLabel = new Link(main, SWT.NONE);
        informationLabel
                .setText("This is the Laces LDP preference page.\n\n"
                        + "Please create an account on <a href=\"https://hub.laces.tech/\">Laces LDP</a>,\n"
                        + "make an access token in the account settings,\n"
                        + "and fill in the credentials of that access token below.\n\n");
        informationLabel.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
                            .openURL(new URL(e.text));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        GridDataFactory.swtDefaults().span(2, 1).applyTo(informationLabel);

        Label label = new Label(main, SWT.NONE);
        label.setText("Server URL");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).hint(95, SWT.DEFAULT)
                .applyTo(label);

        serverUrlText = new Text(main, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                .applyTo(serverUrlText);
        serverUrlText.setText(Strings.nullToEmpty(serverUrl));
        serverUrlText.setEditable(false);
        serverUrlText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                serverUrl = serverUrlText.getText();
                validatePage();
            }
        });

        label = new Label(main, SWT.NONE);
        label.setText("Token id");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(label);

        usernameText = new Text(main, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                .applyTo(usernameText);
        usernameText.setText(Strings.nullToEmpty(username));
        usernameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                username = usernameText.getText();
                validatePage();
            }
        });

        label = new Label(main, SWT.NONE);
        label.setText("Password");
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).applyTo(label);

        passwordText = new Text(main, SWT.BORDER | SWT.PASSWORD);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                .applyTo(passwordText);
        passwordText.setText(Strings.nullToEmpty(password));
        passwordText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                password = passwordText.getText();
                validatePage();
            }
        });

        return main;
    }

    @SuppressWarnings("null")
    protected void validatePage() {
        String errorMessage = null;
        setValid(errorMessage == null);
    }

    @Override
    public boolean performOk() {
        LDPServer server = new LDPServer(serverUrl, username, password);
        LDPPreference.setServers(Lists.newArrayList(server));

        return true;
    }

    @Override
    protected void performDefaults() {
        LDPPreference.setServers(new ArrayList<LDPServer>());

        serverUrl = null;
        username = null;
        password = null;

        refresh();

        super.performDefaults();
    }

    private void refresh() {
        serverUrlText.setText(Strings.nullToEmpty(serverUrl));
        usernameText.setText(Strings.nullToEmpty(username));
        passwordText.setText(Strings.nullToEmpty(password));
    }

    @Override
    public boolean performCancel() {
        // TODO Auto-generated method stub
        return super.performCancel();
    }
}
