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

package com.semmtech.plugin.semmweb.core.intro;


import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.semmtech.plugin.semmweb.core.Colors;


/**
 * 
 * @author Sander Stolk
 */
public class IntroActions {
    /**
     * Creates an introduction link from an IAction in a parent composite that
     * needs to be layout using a GridLayout.
     */
    public static void createActionLink(final Composite parent, final IAction action,
            FormToolkit toolkit) {
        String actionTitle = action.getText();
        actionTitle = StringUtils.remove(actionTitle, '&');
        actionTitle = StringUtils.stripEnd(actionTitle, ".");
        Color linkColor = Colors.getColor(Colors.SWT_LINK_BLUE);
        ImageHyperlink imageHyperlink = toolkit.createImageHyperlink(parent, SWT.NONE);
        if (action.getImageDescriptor() != null) {
            imageHyperlink.setImage(action.getImageDescriptor().createImage());
        }
        imageHyperlink.setText(actionTitle);
        imageHyperlink.setForeground(linkColor);
        imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
            @Override
            public void linkActivated(HyperlinkEvent e) {
                action.run();
            }
        });

        if (action.getDescription() != null) {
            Label label = toolkit.createLabel(parent, action.getDescription(), SWT.WRAP);
            int indentWidth = 5;
            if (action.getImageDescriptor() == null) {
                indentWidth += 16;
            }
            else {
                indentWidth += action.getImageDescriptor().getImageData().width;
            }
            GridDataFactory.fillDefaults().indent(indentWidth, 0).applyTo(label);
        }
    }
}
