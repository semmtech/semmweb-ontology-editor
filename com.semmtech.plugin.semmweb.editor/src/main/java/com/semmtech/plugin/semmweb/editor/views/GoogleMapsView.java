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

package com.semmtech.plugin.semmweb.editor.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;


/**
 * Just a test to see if Google maps could be integrated into the Editor.
 * 
 * @author Mike Henrichs
 * 
 */
public class GoogleMapsView extends ViewPart {

    @Override
    public void createPartControl(Composite parent) {
        // Display display = new Display();
        // final Shell shell = new Shell(display, SWT.SHELL_TRIM);
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());

        Browser browser = new Browser(top, SWT.NONE);
        browser.addTitleListener(new TitleListener() {
            public void changed(TitleEvent event) {
                setPartName(event.title);
            }
        });
        browser.setUrl("https://maps.google.com/maps?q=strekkerweg+75,+amster&hl=nl&ll=52.404304,4.895096&spn=0.011324,0.027874&sll=52.086141,4.389986&sspn=0.091242,0.222988&t=h&hnear=Strekkerweg+75,+Stadsdeel+Noord,+Amsterdam,+Noord-Holland,+Nederland&z=16&iwloc=A");
    }

    @Override
    public void setFocus() {

    }
}
