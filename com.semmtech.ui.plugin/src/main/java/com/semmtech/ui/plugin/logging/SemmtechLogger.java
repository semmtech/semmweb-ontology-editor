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

package com.semmtech.ui.plugin.logging;


import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


public class SemmtechLogger {

    private Logger log4j;

    private SemmtechLogger(Class<?> clazz) {
        this.log4j = Logger.getLogger(clazz);
    }

    public static SemmtechLogger getLogger(Class<?> clazz) {
        return new SemmtechLogger(clazz);
    }

    public void autoError(Shell shell, String title, String action, Throwable ex) {
        String message = String.format("An error occurred while %s:", action);

        log4j.error(message, ex);

        if (shell != null) {
            MessageDialog.openError(shell, title == null ? "Error" : title,
                    String.format("%s \n\n -%s" + ex.getMessage(), message, ex.getMessage()));
        }
    }

    public void error(Shell shell, String title, String message, Throwable ex) {
        log4j.error(message, ex);

        if (shell != null) {
            MessageDialog.openError(shell, title == null ? "Error" : title, message);
        }
    }

    public void error(Shell shell, String title, String message) {
        log4j.error(message);

        if (shell != null) {
            MessageDialog.openError(shell, title == null ? "Error" : title, message);
        }
    }

    public void error(Shell shell, String message, Throwable ex) {
        error(shell, null, message, ex);
    }

    public void error(Shell shell, String message) {
        error(shell, null, message);
    }

    public void error(String message, Throwable ex) {
        error(null, null, message, ex);
    }

    public void error(String message) {
        error(null, null, message);
    }

    public void warn(Shell shell, String title, String message, Throwable ex) {
        log4j.warn(message, ex);

        if (shell != null) {
            MessageDialog.openWarning(shell, title == null ? "Warning" : title, message);
        }
    }

    public void warn(Shell shell, String title, String message) {
        log4j.warn(message);

        if (shell != null) {
            MessageDialog.openWarning(shell, title == null ? "Warning" : title, message);
        }
    }

    public void warn(Shell shell, String message, Throwable ex) {
        warn(shell, null, message, ex);
    }

    public void warn(Shell shell, String message) {
        warn(shell, null, message);
    }

    public void warn(String message, Throwable ex) {
        warn(null, null, message, ex);
    }

    public void warn(String message) {
        warn(null, null, message);
    }

    public void info(Shell shell, String title, String message, Throwable ex) {
        log4j.info(message, ex);

        if (shell != null) {
            MessageDialog.openInformation(shell, title == null ? "Information" : title, message);
        }
    }

    public void info(Shell shell, String title, String message) {
        log4j.info(message);

        if (shell != null) {
            MessageDialog.openInformation(shell, title == null ? "Information" : title, message);
        }
    }

    public void info(Shell shell, String message, Throwable ex) {
        info(shell, null, message, ex);
    }

    public void info(Shell shell, String message) {
        info(shell, null, message);
    }

    public void info(String message, Throwable ex) {
        info(null, null, message, ex);
    }

    public void info(String message) {
        info(null, null, message);
    }

    public void debug(String message, Throwable ex) {
        log4j.debug(message, ex);
    }

    public void debug(String message) {
        log4j.debug(message);
    }

    public void trace(String message, Throwable ex) {
        log4j.trace(message, ex);
    }

    public void trace(String message) {
        log4j.trace(message);
    }
}
