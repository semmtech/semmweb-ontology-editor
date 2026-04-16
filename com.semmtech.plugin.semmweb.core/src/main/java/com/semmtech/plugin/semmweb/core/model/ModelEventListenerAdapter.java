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


import com.semmtech.plugin.semmweb.core.model.events.ModelActivatedEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.IModelEvent;
import com.semmtech.plugin.semmweb.core.model.events.ModelSavedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelAddedEvent;
import com.semmtech.plugin.semmweb.core.model.events.SubModelRemovedEvent;


/**
 * 
 * @author Sander Stolk
 */
public class ModelEventListenerAdapter implements ModelEventListener {

    @Override
    public void notifyEvent(IModelEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void modelActivated(ModelActivatedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void modelChanged(ModelChangedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subModelAdded(SubModelAddedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subModelRemoved(SubModelRemovedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void namespacePrefixChanged(NamespacePrefixChangedEvent event) {
        // TODO Auto-generated method stub

    }

    @Override
    public void modelSaved(ModelSavedEvent event) {
        // TODO Auto-generated method stub

    }

}
