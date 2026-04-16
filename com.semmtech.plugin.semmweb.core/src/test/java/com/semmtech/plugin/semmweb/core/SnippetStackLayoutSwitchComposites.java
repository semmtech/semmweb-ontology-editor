package com.semmtech.plugin.semmweb.core;


/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
//package org.eclipse.swt.snippets;
/*
 * StackLayout example snippet: use a StackLayout to switch between Composites.
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


/**
 * 
 * @author Sander Stolk
 */
public class SnippetStackLayoutSwitchComposites {

    static int pageNum = -1;

    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout());

        // create the button that will switch between the pages
        Button pageButton = new Button(shell, SWT.PUSH);
        GridDataFactory.fillDefaults().applyTo(pageButton);
        pageButton.setText("Push");

        // create the composite that the pages will share
        final Composite contentPanel = new Composite(shell, SWT.BORDER);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true)
                .applyTo(contentPanel);
        final StackLayout layout = new StackLayout();
        contentPanel.setLayout(layout);

        // create the first page's content
        final Composite page0 = new Composite(contentPanel, SWT.NONE);
        // page0.setBackground(new Color(shell.getDisplay(), 120, 0, 0));
        page0.setLayout(new RowLayout());
        Label label = new Label(page0, SWT.NONE);
        label.setText("Label on page 1");
        label.pack();

        // create the second page's content
        final Composite page1 = new Composite(contentPanel, SWT.NONE);
        page1.setLayout(new GridLayout());
        Button button = new Button(page1, SWT.NONE);
        button.setText("Button on page 2");
        GridDataFactory.fillDefaults().hint(300, 1000).applyTo(button);
        button.pack();

        pageButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                pageNum = ++pageNum % 2;
                layout.topControl = pageNum == 0 ? page0 : page1;
                contentPanel.layout();
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}