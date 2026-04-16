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

package com.semmtech;


import static java.lang.System.currentTimeMillis;

import java.util.Map;

import com.google.common.collect.Maps;


/**
 * 
 * @author Simone Rondelli
 */
public class TimeTester {

    private long totTime;
    private long startTime;
    private long partialTime;

    private Map<Object, Long> subStep;

    public TimeTester() {
        totTime = 0;
        subStep = Maps.newHashMap();
    }

    public void start() {
        startTime = currentTimeMillis();
    }

    public void startSub(Object id) {
        subStep.put(id, currentTimeMillis());
    }

    public long step() {
        return step(null);
    }

    public long subStep(Object id) {
        return subStep(id, null);
    }

    public long step(String text) {
        partialTime = 0;
        long step = currentTimeMillis() - startTime;
        totTime += step;

        if (text != null) {
            System.out.println(String.format("%s: %d", text, step));
        }

        return step;
    }

    public long subStep(Object id, String text) {
        long startTime = subStep.get(id);
        long step = currentTimeMillis() - startTime;
        totTime += step;

        if (text != null) {
            System.out.println(String.format("[sub] %s: %d", text, step));
        }

        return step;
    }

    public long partStep() {
        return partStep(null);
    }

    public long partStep(String text) {
        if (partialTime == 0) {
            partialTime = startTime;
        }

        long currTime = System.currentTimeMillis();
        long step = currTime - partialTime;
        partialTime = currTime;

        if (text != null) {
            System.out.println(String.format("%s: %d", text, step));
        }
        return step;
    }

    public long step(int tries) {
        return step(null, tries);
    }

    public long step(String text, int tries) {
        long step = step() / tries;

        if (text != null) {
            System.out.println(String.format("%s: %d", text, step));
        }
        return step;
    }

    public long getTotTime() {
        return totTime;
    }

    public void reset() {
        totTime = 0;
    }
}
