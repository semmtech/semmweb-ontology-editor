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

package com.semmtech.plugin.semmweb.core.model;


public class RestrictionType {
    public static final int NONE = 0;

    public static final int ALL_VALUES_FROM = 1;
    public static final int SOME_VALUE_FROM = 2;
    public static final int HAS_VALUE = 4;
    public static final int MIN_CARDINALITY = 8;
    public static final int MAX_CARDINALITY = 16;
    public static final int CARDINALITY = 32;

    public static final int QUALIFIED_CARDINALITY = 64;
    public static final int MIN_QUALIFIED_CARDINALITY = 128;
    public static final int MAX_QUALIFIED_CARDINALITY = 256;

    private RestrictionType() {
    }

    /**
     * TODO: Implement
     * 
     * @param label
     * @return
     */
    public static int parseRestrictionType(String label) {
        return RestrictionType.ALL_VALUES_FROM;
    }
}
