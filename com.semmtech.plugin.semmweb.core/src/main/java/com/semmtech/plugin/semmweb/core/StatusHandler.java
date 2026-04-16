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

package com.semmtech.plugin.semmweb.core;


import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.AbstractStatusHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import com.semmtech.ui.plugin.EclipseUIPlugin;


/**
 * Global error handler that make the user know that an error has occurred. This
 * basically handle all the exception that are not explicitly handled in the
 * editor.
 * 
 * @author Simone Rondelli
 * 
 */
public class StatusHandler extends AbstractStatusHandler {

    private static Logger logger = Logger.getLogger(StatusHandler.class);

    public StatusHandler() {
    }

    @Override
    public void handle(StatusAdapter statusAdapter, int style) {

        if (statusAdapter.getStatus().matches(IStatus.ERROR) && ((style != StatusManager.NONE))) {
            IStatus status = statusAdapter.getStatus();
            Throwable t = status.getException();

            System.out.println(status);

            if (t == null) {
                logger.error("Uncaught Exception: " + status.getMessage());
            }
            else {
                logger.error("Uncaught Exception: " + status.getMessage(), t);

            }

            String logFile = null;

            Enumeration<?> e = Logger.getRootLogger().getAllAppenders();
            while (e.hasMoreElements()) {
                Appender app = (Appender) e.nextElement();
                if (app instanceof FileAppender) {
                    logFile = ((FileAppender) app).getFile();
                }
            }

            String msg = "An unexpected error has occurred. The editor might be in an inconsistent state. It is strongly suggested to save your work and restart the editor.";

            if (t != null) {
                msg += "\n\nCause: " + t.getMessage();
            }

            if (logFile != null) {
                msg += "\n\nPlease send the file:\n%s\nto Semmtech B.V.";
            }

            final String finalMsg = String.format(msg, logFile);

            EclipseUIPlugin.getStandardDisplay().asyncExec(new Runnable() {

                @Override
                public void run() {
                    MessageDialog.openError(Display.getCurrent().getActiveShell(),
                            "Uncaught Exception", finalMsg);
                }
            });

        }
    }
}
