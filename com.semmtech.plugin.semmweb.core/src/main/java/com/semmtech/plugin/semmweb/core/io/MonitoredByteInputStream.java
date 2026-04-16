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

package com.semmtech.plugin.semmweb.core.io;


import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;


public class MonitoredByteInputStream extends ByteArrayInputStream {
    private final IProgressMonitor monitor;

    public MonitoredByteInputStream(byte[] buf, IProgressMonitor monitor) {
        super(buf);
        this.monitor = monitor;
    }

    @Override
    public synchronized int read() {
        monitor.worked(1);
        System.out.println("worked 1");
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        monitor.worked(b.length);
        System.out.println("worked " + b.length);
        return super.read(b);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) {
        monitor.worked(len);
        System.out.println("worked on " + len + "; with off is " + off + "; on array with length "
                + b.length);
        return super.read(b, off, len);
    }
}
