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

package com.semmtech.ui.plugin;


import java.util.Calendar;

import org.eclipse.swt.widgets.Display;


public class DelayedRunnableExecution implements Runnable {
    public static final int DEFAULT_MAX_DELAY = 1000;
    public static final int DEFAULT_CHECK_INTERVAL = 100;

    private final Runnable runnable;

    private final int maxDelay;
    private final int interval;
    private long lastPoke;
    private boolean running;
    private boolean started;

    public DelayedRunnableExecution(Runnable runnable) {
        this(runnable, DEFAULT_MAX_DELAY);
    }

    public DelayedRunnableExecution(Runnable runnable, int maxDelay) {
        this(runnable, maxDelay, DEFAULT_CHECK_INTERVAL);
    }

    public DelayedRunnableExecution(Runnable runnable, int maxDelay, int interval) {
        this.runnable = runnable;
        this.maxDelay = maxDelay;
        this.interval = interval;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (!started) {
            started = true;
            poke();
            Display.getDefault().timerExec(interval, this);
        }
    }

    public void abort() {
        if (started) {
            started = false;
            lastPoke = -1;
        }
    }

    public void poke() {
        if (started) {
            lastPoke = Calendar.getInstance().getTimeInMillis();
        }
    }

    @Override
    public void run() {
        if (!started) {
            return;
        }
        if (running) {
            return;
        }
        if (lastPoke <= 0) {
            return;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        if (now - lastPoke > maxDelay) {
            running = true;
            Display.getDefault().syncExec(runnable);
            running = false;
            started = false;
        }
        else {
            Display.getDefault().timerExec(interval, this);
        }
    }
}
