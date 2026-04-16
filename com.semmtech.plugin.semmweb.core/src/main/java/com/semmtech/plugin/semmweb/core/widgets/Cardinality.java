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

package com.semmtech.plugin.semmweb.core.widgets;


public class Cardinality {
    /**
     * Unbounded cardinality (0..n)
     */
    public static Cardinality unbounded() {
        return new Cardinality(0, true);
    }

    /**
     * One cardinality (1..1)
     */
    public static Cardinality one() {
        return new Cardinality(1, 1);
    }

    /**
     * Optional cardinality (0..1)
     */
    public static Cardinality optional() {
        return new Cardinality(0, 1);
    }

    /**
     * Some cardinality (1..n)
     */
    public static Cardinality some() {
        return new Cardinality(1, true);
    }

    private boolean unbounded;
    private int min;
    private int max;

    public Cardinality(int cardinality) {
        this(cardinality, cardinality, false);
    }

    public Cardinality(int min, int max) {
        this(min, max, false);
    }

    public Cardinality(int lower, boolean unbounded) {
        this(lower, lower + 1, unbounded);
    }

    public Cardinality(int lower, int upper, boolean unbounded) {
        this.min = lower;
        this.max = upper;
        this.unbounded = unbounded;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public boolean getUnbounded() {
        return unbounded;
    }

    public void setUnbounded(boolean unbounded) {
        this.unbounded = unbounded;
    }

    @Override
    public String toString() {
        return toString("to");
    }

    public String toString(String separator) {
        if (unbounded) {
            return String.format("%d %s n", min, separator);
        }

        return String.format("%d %s %d", min, separator, max);
    }

    @Override
    public int hashCode() {
        String hashable = String
                .format("Cardinality:%d;%d;%s;", getMin(), getMax(), getUnbounded());
        return hashable.hashCode();
    }

    public boolean equals(Cardinality other) {
        if (getMin() != other.getMin())
            return false;
        if (getUnbounded() != other.getUnbounded())
            return false;
        if (!getUnbounded() && (getMax() != other.getMax()))
            return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Cardinality))
            return false;
        return equals((Cardinality) obj);
    }

    public static boolean isStricter(Cardinality other, Cardinality initial) {
        if (other.getMin() < initial.getMin())
            return false;
        if (other.getUnbounded() && !initial.getUnbounded())
            return false;
        else if (!other.getUnbounded() && !initial.getUnbounded()
                && other.getMax() > initial.getMax())
            return false;
        return true;
    }
}
