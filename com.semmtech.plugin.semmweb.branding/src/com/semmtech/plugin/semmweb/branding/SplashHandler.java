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

package com.semmtech.plugin.semmweb.branding;


import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.splash.BasicSplashHandler;


/**
 * 
 * @author Mike Henrichs
 */
public class SplashHandler extends BasicSplashHandler {

    public SplashHandler() {
        super();
    }

    @Override
    public void init(Shell splash) {
        super.init(splash);
        String progressRectString = null;
        String messageRectString = null;
        // String foregroundColorString = null;
        IProduct product = Platform.getProduct();

        if (product != null) {
            progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
            messageRectString = product.getProperty(IProductConstants.STARTUP_MESSAGE_RECT);
            // foregroundColorString =
            // product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
        }
        Rectangle progressRect = StringConverter.asRectangle(progressRectString, new Rectangle(10,
                259, 360, 12));
        setProgressRect(progressRect);

        Rectangle messageRect = StringConverter.asRectangle(messageRectString, new Rectangle(10,
                238, 360, 20));
        setMessageRect(messageRect);

        int foregroundColorInteger = 0x6f706c; // off white
        setForeground(new RGB((foregroundColorInteger & 0xFF0000) >> 16,
                (foregroundColorInteger & 0xFF00) >> 8, foregroundColorInteger & 0xFF));

        getContent();

        // TODO Show version of main plug-ins
        // Label label = new Label(composite, SWT.NONE);
        // label.setText("Version: 1.0.0.0");
        // label.setBounds(10, 200, 360, 20);

    }

}
