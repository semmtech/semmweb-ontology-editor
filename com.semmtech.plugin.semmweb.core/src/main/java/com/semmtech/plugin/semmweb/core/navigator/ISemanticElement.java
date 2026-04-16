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

package com.semmtech.plugin.semmweb.core.navigator;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;


public interface ISemanticElement extends IAdaptable {

    static final int SEMANTIC_MODEL = 1;
    /**
     * Constant representing a Semantic project. A Semantic element with this
     * type can be safely cast to ISemanticProject.
     */
    static final int SEMANTIC_PROJECT = 2;
    static final int MODEL_COLLECTION = 3;
    static final int MODEL = 4;
    static final int IMPORT = 5;
    static final int IMPORT_COLLECTION = 6;
    static final int NAMESPACE = 7;
    static final int NAMESPACE_COLLECTION = 8;

    IProject getProject();

    ISemanticProject getSemanticProject();

    /**
     * Returns this element's kind encoded as an integer. This is a handle-only
     * method.
     * 
     * @return
     */
    int getElementType();

    String getId();

    String[] getPath();

    /**
     * Returns the name of this element. This is a handle-only method.
     * 
     * @return
     */
    String getElementName();

    /**
     * Returns the first ancestor of this Semantic element that has the given
     * type. Returns null if no such an ancestor can be found. This is a
     * handle-only method.
     * 
     * @param type
     * @return
     */
    ISemanticElement getAncestor(int type);

    /**
     * Returns the element directly containing this element, or null if this
     * element has no parent. This is a handle-only method.
     * 
     * @return
     */
    ISemanticElement getParent();
}
