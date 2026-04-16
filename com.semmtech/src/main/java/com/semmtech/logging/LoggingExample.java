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

package com.semmtech.logging;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * See http://www.vogella.de/articles/Logging/article.html for examples.
 * 
 * @author Mike Henrichs
 * 
 */
public final class LoggingExample {

    /** The Constant logger. */
    private static final Logger logger = Logger.getLogger(LoggingExample.class.getName());

    /**
     * Instantiates a new logging example.
     */
    private LoggingExample() {
    }

    /**
     * @param args
     *            provided arguments
     */
    public static void main(final String[] args) {
        try {
            SemmtechLogger.setup();
        }
        catch (IOException e) {
            logger.severe(e.getMessage());
        }

        // Set the LogLevel to Severe, only severe Messages will be written
        logger.setLevel(Level.SEVERE);
        logger.severe("Severe message!");
        logger.warning("Warning message");
        logger.info("Info message");
        logger.finest("Really not important");

        // Set the LogLevel to Info, severe, warning and info will be written
        // Finest is still not written
        logger.setLevel(Level.INFO);
        logger.severe("Info message again");
        logger.warning("Warning mesage again");
        logger.info("Last info message");
        logger.finest("Really not important again");
    }

}
