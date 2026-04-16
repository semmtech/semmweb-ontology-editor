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

package com.semmtech.plugin.semmweb.core.ui;


import org.eclipse.ui.IEditorInput;

import com.hp.hpl.jena.rdf.model.Resource;


/**
 * Input used to edit a single Resource offered through a particular Model (see
 * Model URI).
 * 
 * @author Mike Henrichs
 * 
 */
public interface IResourceEditorInput extends IEditorInput {
    /**
     * Returns the Resource of this input.
     * 
     * @return
     */
    public Resource getResource();

    /**
     * Returns the URI of this Resource
     * 
     * @return
     */
    public String getURI();

    /**
     * Returns the URI of the Model, used to idetify the undelying Model.
     * 
     * @return
     */
    public String getModelURI();
}
