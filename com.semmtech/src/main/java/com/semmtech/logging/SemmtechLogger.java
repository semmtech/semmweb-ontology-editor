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
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * The Class SemmtechLogger.
 * 
 * @author Mike Henrichs
 */
public final class SemmtechLogger {

    /** The file txt. */
    private static FileHandler fileTxt;

    /** The formatter txt. */
    private static SimpleFormatter formatterTxt;

    /** The file html. */
    private static FileHandler fileHTML;

    /** The formatter html. */
    private static Formatter formatterHTML;

    /**
     * Instantiates a new semmtech logger.
     */
    private SemmtechLogger() {
    }

    /**
     * Setup.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void setup() throws IOException {
        // Create Logger
        Logger logger = Logger.getLogger("");
        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler("target/log/Logging.txt");
        fileHTML = new FileHandler("target/log/Logging.html");

        // Create txt Formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);
        // Create HTML Formatter
        formatterHTML = new HtmlFormatter();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
    }
}
