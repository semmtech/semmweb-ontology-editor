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


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * The Class HtmlFormatter.
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class HtmlFormatter extends Formatter {

    /** The Constant INITIAL_CAPACITY. */
    private static final int INITIAL_CAPACITY = 1000;

    @Override
    public final String format(final LogRecord rec) {
        StringBuffer buf = new StringBuffer(INITIAL_CAPACITY);

        // Bold any levels >= WARNING
        buf.append("<tr>");
        buf.append("<td>");

        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        }
        else {
            buf.append(rec.getLevel());
        }

        buf.append("</td>");
        buf.append("<td>");
        buf.append(calcDate(rec.getMillis()));
        buf.append(' ');
        buf.append(formatMessage(rec));
        buf.append('\n');
        buf.append("<td>");
        buf.append("</tr>\n");
        return buf.toString();
    }

    /**
     * Calc date.
     * 
     * @param millisecs
     *            the millisecs
     * @return the string
     */
    private static String calcDate(final long millisecs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return dateFormat.format(resultdate);
    }

    /**
     * @param handler
     *            the handler
     * @return the head
     */
    @Override
    public final String getHead(final Handler handler) {
        return "<HTML>\n<HEAD>\n" + (new Date()) + "\n</HEAD>\n<BODY>\n<PRE>\n"
                + "<table border>\n  " + "<tr><th>Time</th><th>Log Message</th></tr>\n";
    }

    /**
     * @param handler
     *            the handler
     * @return the tail
     */
    @Override
    public final String getTail(final Handler handler) {
        return "</table>\n  </PRE></BODY>\n</HTML>\n";
    }

}
