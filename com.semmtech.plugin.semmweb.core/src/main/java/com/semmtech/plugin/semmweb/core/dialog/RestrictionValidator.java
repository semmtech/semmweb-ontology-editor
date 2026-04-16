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

package com.semmtech.plugin.semmweb.core.dialog;


import com.hp.hpl.jena.ontology.Restriction;


/**
 * The IRestrictionValidator is the interface for restriction consistency
 * validators.
 */
public interface RestrictionValidator {
    /**
     * Validates the given restriction if consistent with possible other
     * restrictions. Returns an error message to display if the new restriction
     * causes inconsistency. Returns <code>null</code> if there are no
     * inconsistencies. Note that the empty string is not treated the same as
     * <code>null</code>; it indicates an error state but with no message to
     * display.
     * 
     * @param restriction
     *            the restriction to be tested
     * @return an error message or <code>null</code> if no error
     */
    public String isConsistent(Restriction restriction);

    /**
     * Checks if the restrictions is valid.
     * 
     * @param restriction
     *            the restriction to be validated
     * @return an error message or <code>null</code> if no error
     */
    public String isValid(Restriction restriction);
}
