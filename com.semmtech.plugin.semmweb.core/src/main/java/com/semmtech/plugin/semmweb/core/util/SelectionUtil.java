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

package com.semmtech.plugin.semmweb.core.util;


public class SelectionUtil {

    /**
     * Returns the selected ISemanticElement from the given
     * IStructuredSelection.
     * 
     * @return List of selected ISemanticElement or an empty list if nothing is
     *         selected
     */
    // public static List<ISemanticElement>
    // getSelectedResources(IStructuredSelection selection) {
    // ArrayList<ISemanticElement> selectedResources = Lists.newArrayList();
    //
    // for (Iterator<?> i = selection.iterator(); i.hasNext();) {
    // Object o = i.next();
    // if (o instanceof ISemanticElement) {
    // selectedResources.add((ISemanticElement) o);
    // }
    // }
    // return selectedResources;
    // }

    /**
     * Returns the selected ISemanticElement from the given ISelection.
     * 
     * @return List of selected ISemanticElement or an empty list if nothing is
     *         selected
     */
    // public static List<ISemanticElement> getSelectedResources(ISelection
    // selection) {
    // if (selection instanceof IStructuredSelection) {
    // return getSelectedResources((IStructuredSelection) selection);
    // }
    // return Lists.newArrayList();
    // }

}
