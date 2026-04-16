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

package com.semmtech.io;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;


/**
 * When writing to this stream, the content is appended to a StringBuffer. By
 * calling the toString() method the complete content is returned as a string.
 * 
 * @author Mike Henrichs
 */
public class StringOutputStream extends OutputStream {

    private final String encoding;

    public StringOutputStream() {
        this("UTF-8");
    }

    public StringOutputStream(String encoding) {
        this.encoding = encoding;
    }

    /** The buffer. */
    private StringBuffer buffer = new StringBuffer();

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public final void write(final byte[] b) throws IOException {
        buffer.append(new String(b, Charset.forName(encoding)));
    }

    public final void write(String s) {
        buffer.append(s);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public final void write(final byte[] b, final int off, final int len) throws IOException {
        buffer.append(new String(b, off, len, Charset.forName(encoding)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public final void write(final int b) throws IOException {
        buffer.append(Integer.toString(b));
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        buffer.delete(0, buffer.length());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        return buffer.toString();
    }
}
