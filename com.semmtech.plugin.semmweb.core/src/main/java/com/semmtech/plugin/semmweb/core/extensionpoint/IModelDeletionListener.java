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

package com.semmtech.plugin.semmweb.core.extensionpoint;


import com.semmtech.plugin.semmweb.core.navigator.IModel;


/**
 * This interface allow other plugins to listen for a model deletion and
 * eventually cancel the deletion of the model. The implementation is as simple
 * as possible, the method {@link #deleteModel(IModel)} just return a boolean
 * that is checked during the deletion. This is fine unless you have few
 * listeners but can be disturbing for the user with the increase of the
 * listeners (think about 5 listeners each of them show a message dialog).
 * <p>
 * Future development can foresee a the returning of an Object which contains
 * the information that have to be shown to the user, and all these information
 * can be shown in a single dialog.
 * 
 * @author Simone Rondelli
 * 
 */
public interface IModelDeletionListener {

    /**
     * Called during the model deletion. It might stop the deletion of the
     * model.
     * 
     * @param model
     *            Model that is about to be deleted
     * @return true if the model can be deleted false otherwise. Keep in mind
     *         that the listeners' results are in AND therefore the deletion can
     *         be stopped by just one of them.
     */
    public boolean deleteModel(IModel model);

}
