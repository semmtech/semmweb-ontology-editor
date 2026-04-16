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


import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;


public abstract class AbstractModelValidator implements IModelValidator {
    private final IPreferenceStore preferenceStore;
    private final String validatorId;
    private final Set<IModelValidationListener> listeners;

    protected AbstractModelValidator(String validatorId, IPreferenceStore preferenceStore) {
        this(validatorId, preferenceStore, false);
    }

    protected AbstractModelValidator(String validatorId, IPreferenceStore preferenceStore,
            boolean defaultState) {
        this.validatorId = validatorId;
        this.preferenceStore = preferenceStore;
        this.listeners = Sets.newHashSet();
        if (!preferenceStore.contains(validatorId)) {
            preferenceStore.setDefault(validatorId, defaultState);
        }
    }

    protected void performAddSubModel(Model model, String modelUri) {
        for (IModelValidationListener listener : listeners) {
            listener.submodelAdded(model, modelUri);
        }
    }

    @Override
    public abstract void validateModel(OntModel model);

    @Override
    public abstract String getName();

    @Override
    public abstract String getDescription();

    @Override
    public boolean isEnabled() {
        return preferenceStore.getBoolean(validatorId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        preferenceStore.setValue(validatorId, enabled);
    }

    @Override
    public void addValidationListener(IModelValidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeValidationListener(IModelValidationListener listener) {
        listeners.remove(listener);
    }
}
