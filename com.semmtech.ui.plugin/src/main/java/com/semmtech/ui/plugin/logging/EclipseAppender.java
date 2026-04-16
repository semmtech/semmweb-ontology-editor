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


import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

import com.google.common.base.Preconditions;


/**
 * This class convert the log4j logging events in EclipseRCP Log (IStatus).
 * Using the ILog interface we have the logs printed both in the "Error Log"
 * View inside the editor and in the console.
 * 
 * @author Simone Rondelli
 * 
 */
public class EclipseAppender extends AppenderSkeleton {
    private static final String DEFAULT_LAYOUT = "%m";

    private ILog eclipseLogger;

    public EclipseAppender(Bundle bundle) {
        this(bundle, DEFAULT_LAYOUT);
    }

    public EclipseAppender(Bundle bundle, String layout) {
        Preconditions.checkArgument(bundle != null,
                "Bundle must be not null to initialize the EclipseAppender!");

        this.eclipseLogger = Platform.getLog(bundle);
        setLayout(new PatternLayout(layout));
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void append(LoggingEvent event) {
        if (Level.ERROR.equals(event.getLevel())) {
            eclipseLog(IStatus.ERROR, event);
        }
        else if (Level.WARN.equals(event.getLevel())) {
            eclipseLog(IStatus.WARNING, event);
        }
        else if (Level.INFO.equals(event.getLevel())) {
            eclipseLog(IStatus.INFO, event);
        }
    }

    private void eclipseLog(int severity, LoggingEvent event) {
        String message = layout.format(event);
        Throwable ex = event.getThrowableInformation() == null ? null : event
                .getThrowableInformation().getThrowable();

        String pluginId = ClassNameToPluginIdMapper.retrievePluginId(event.getLocationInformation()
                .getClassName());

        eclipseLogger.log(new Status(severity, pluginId, message, ex));
    }
}
