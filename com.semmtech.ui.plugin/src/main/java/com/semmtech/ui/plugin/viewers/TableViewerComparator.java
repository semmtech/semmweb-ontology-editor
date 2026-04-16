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

package com.semmtech.ui.plugin.viewers;


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;


public abstract class TableViewerComparator extends ViewerComparator {
    protected static final int DESCENDING = 1;

    protected int columnIndex;
    protected int direction = DESCENDING;

    protected TableViewerComparator() {
        this.columnIndex = 0;
        this.direction = DESCENDING;
    }

    public int getDirection() {
        return (direction == 1) ? SWT.DOWN : SWT.UP;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumn(int column) {
        if (column == columnIndex) {
            direction = 1 - direction;
        }
        else {
            columnIndex = column;
            direction = DESCENDING;
        }
    }

    @Override
    public abstract int compare(Viewer viewer, Object e1, Object e2);

}
