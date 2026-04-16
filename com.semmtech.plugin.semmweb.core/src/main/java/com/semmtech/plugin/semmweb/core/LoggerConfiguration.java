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


import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.collections4.EnumerationUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.core.runtime.Platform;

import com.semmtech.ui.plugin.logging.EclipseAppender;


/**
 * 
 * @author Sander Stolk
 */
public class LoggerConfiguration {

    @SuppressWarnings("unchecked")
    public static boolean init() {
        Properties props = new Properties();

        try (InputStream configStream = LoggerConfiguration.class.getClassLoader()
                .getResourceAsStream("semmweb.log4j.properties")) {
            props.load(configStream);

            // for some enigmatic reason log4j doesn't work with the log file
            // configured in the log4j.properties (the file is created but not
            // written), to make it work is needed to explicitly set the
            // absolute path of the file
            File log4jFile = new File(Platform.getInstallLocation().getURL().getFile()
                    + File.separator + "log" + File.separator + "semmweb.log");
            props.setProperty("log4j.appender.fileAppender.File", log4jFile.getAbsolutePath());

            LogManager.resetConfiguration();
            PropertyConfigurator.configure(props);

            EclipseAppender eclipseAppender = new EclipseAppender(CorePlugin.getDefault()
                    .getBundle());

            LogManager.getRootLogger().addAppender(eclipseAppender);

            // Make sure that only Trace and Debug messages are printed to
            // console because the other levels are printed by the
            // EclipseAppender
            for (Object app : EnumerationUtils.toList(LogManager.getRootLogger().getAllAppenders())) {
                if (app instanceof ConsoleAppender) {
                    final ConsoleAppender consoleAppender = (ConsoleAppender) app;
                    consoleAppender.addFilter(new Filter() {

                        @Override
                        public int decide(LoggingEvent event) {
                            Level level = event.getLevel();
                            if ((Level.DEBUG.equals(level) || Level.TRACE.equals(level))) {
                                return Filter.ACCEPT;
                            }
                            return Filter.DENY;
                        }
                    });
                }
            }

            return true;
        }
        catch (final Exception e) {
            // No logger was set. Returning false.
            e.printStackTrace();
        }

        return false;
    }
}
