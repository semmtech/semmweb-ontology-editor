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

package com.semmtech.log4j;


import java.io.OutputStreamWriter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;


/**
 * 
 * @author Mike Henrichs
 */
public class StandardConfigurator {

    public static void configure() {
        Logger root = Logger.getRootLogger();

        root.getLoggerRepository().resetConfiguration();

        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setWriter(new OutputStreamWriter(System.out));
        consoleAppender.setThreshold(Level.INFO);
        consoleAppender.setName("console");
        consoleAppender.setLayout(new PatternLayout(
                com.semmtech.log4j.PatternLayout.STANDARD_PATTERN));

        root.addAppender(consoleAppender);
    }

    public static void configure(Priority threshold) {
        Logger root = Logger.getRootLogger();

        root.getLoggerRepository().resetConfiguration();

        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setWriter(new OutputStreamWriter(System.out));
        consoleAppender.setThreshold(threshold);
        consoleAppender.setName("console");
        consoleAppender.setLayout(new PatternLayout(
                com.semmtech.log4j.PatternLayout.STANDARD_PATTERN));

        root.addAppender(consoleAppender);
    }
}
