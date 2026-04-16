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

package com.semmtech.ui.plugin.widgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public final class GuiFactory {

    public static final int GRID_NO_MARGINS = 1;

    private static GuiFactory instance;

    /**
     * Hidden constructor
     */
    private GuiFactory() {
    }

    /**
     * Returns a new Composite with a GridLayout containing the number of
     * columns specified.
     * 
     * @param parent
     * @param numColumns
     * @return
     */
    public Composite createComposite(Composite parent, int numColumns) {
        Composite composite = new Composite(parent, SWT.NULL);

        // GridLayout
        GridLayout layout = new GridLayout();
        layout.numColumns = numColumns;
        composite.setLayout(layout);

        // GridData
        GridData data = new GridData();
        data.verticalAlignment = GridData.FILL;
        data.horizontalAlignment = GridData.FILL;
        composite.setLayoutData(data);
        return composite;
    }

    public static GuiFactory getInstance() {
        if (instance == null) {
            instance = new GuiFactory();
        }
        return instance;
    }

    public static GridLayout createGridLayout(int numColumns, boolean makeColumnsEqualWidth,
            int style) {
        GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
        if ((style & GRID_NO_MARGINS) != 0) {
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.marginBottom = 0;
            layout.marginTop = 0;
            layout.marginLeft = 0;
            layout.marginRight = 0;
        }
        return layout;
    }
}
