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


import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.ScrolledForm;


public final class Widgets {

    private Widgets() {
    }

    /**
     * Returns true if the widget is null or has been disposed; otherwise false.
     * 
     * @param widget
     * @return
     */
    public static boolean isNullOrDisposed(Widget widget) {
        if (widget == null) {
            return true;
        }
        if (widget.isDisposed()) {
            return true;
        }
        return false;
    }

    /**
     * Disposes the given widget if not null and not alreay disposed.
     * 
     * @param widget
     */
    public static void disposeIfExists(Widget widget) {
        if (!isNullOrDisposed(widget)) {
            widget.dispose();
        }
    }

    /**
     * If the viewer is null or the child control of the viewer is null or
     * disposed, true is returned; otherwise false is returned
     * 
     * @param viewer
     * @return
     */
    public static boolean isNullOrDisposedViewer(Viewer viewer) {
        if (viewer == null) {
            return true;
        }
        return isNullOrDisposed(viewer.getControl());
    }

    /**
     * Returns the first Control (either <code>control</code> itself or one of
     * its parents) that matches the specified class type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T retrieveFirstControlOfType(Control control, Class<T> type) {
        if (control == null) {
            return null;
        }
        if (type.isInstance(control)) {
            return (T) control;
        }
        return retrieveFirstControlOfType(control.getParent(), type);
    }

    /**
     * Returns the first parent of <code>control</code> that matches the
     * specified class type.
     */
    public static <T> T retrieveFirstParentOfType(Control control, Class<T> type) {
        if (control == null) {
            return null;
        }
        return retrieveFirstControlOfType(control.getParent(), type);
    }

    public static void layoutControlUpToScrollableParent(Control control) {
        control.pack(true);
        Control prevControl = control;
        for (Composite composite = control.getParent(); composite != null; composite = composite
                .getParent()) {
            composite.changed(new Control[] { prevControl });
            composite.layout(true, false);
            if (composite instanceof ScrolledForm) {
                ((ScrolledForm) composite).reflow(false);
                return;
            }
            prevControl = composite;
        }
    }
}
