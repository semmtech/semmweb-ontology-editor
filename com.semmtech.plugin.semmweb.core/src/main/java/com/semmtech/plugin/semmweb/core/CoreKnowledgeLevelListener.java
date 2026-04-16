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

package com.semmtech.plugin.semmweb.core;


import com.semmtech.plugin.semmweb.core.extensionpoint.IKnowledgeLevelListener;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.UserPreference;


/**
 * Interface called after the knowlegde level has changed.
 * 
 * @author Mike Henrichs
 * 
 */
public class CoreKnowledgeLevelListener implements IKnowledgeLevelListener {

    @Override
    public void updateKnowledgeLevel(String level) {
        if (level.equals(UserPreference.KNOWLEDGE_LEVEL_BEGINNER)
                || level.equals(UserPreference.KNOWLEDGE_LEVEL_INTERMEDIATE)) {
            LabelsPreference.setResourceLabelRendering(LabelsPreference.VALUE_SHOW_READABLE_LABELS);
        }
        else if (level.equals(UserPreference.KNOWLEDGE_LEVEL_EXPERT)) {
            LabelsPreference.setResourceLabelRendering(LabelsPreference.VALUE_SHOW_RESOURCE_QNAMES);
        }

    }

}
