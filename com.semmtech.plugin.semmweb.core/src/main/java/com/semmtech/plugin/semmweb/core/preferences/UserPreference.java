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

package com.semmtech.plugin.semmweb.core.preferences;


import com.fasterxml.jackson.core.type.TypeReference;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.Person;
import com.semmtech.ui.plugin.preference.BasePreference;


public class UserPreference extends BasePreference {

    public static final String PREFERENCE_PERSON = "com.semmtech.plugin.semmweb.core.preferences.user.person";
    public static final String PREFERENCE_KNOWLEDGE_LEVEL = "com.semmtech.plugin.semmweb.core.preferences.user.knowledgeLevel";

    public static final String KNOWLEDGE_LEVEL_UNKOWN = "unknown";
    public static final String KNOWLEDGE_LEVEL_BEGINNER = "beginner";
    public static final String KNOWLEDGE_LEVEL_INTERMEDIATE = "intermediate";
    public static final String KNOWLEDGE_LEVEL_EXPERT = "expert";

    private static final UserPreference instance = new UserPreference();

    private UserPreference() {
        super(CorePlugin.getDefault().getPreferenceStore());
    }

    public static String getKnowledgeLevel() {
        return instance.getPreferenceStore().getString(PREFERENCE_KNOWLEDGE_LEVEL);
    }

    public static boolean hasKnowledgeLevel(String... levels) {
        String knowledgeLevel = getKnowledgeLevel();
        if (knowledgeLevel == null) {
            return false;
        }
        for (String level : levels) {
            if (knowledgeLevel.equals(level)) {
                return true;
            }
        }
        return false;
    }

    public static void setKnowledgeLevel(String level) {
        instance.getPreferenceStore().setValue(PREFERENCE_KNOWLEDGE_LEVEL, level);
    }

    public static Person getPerson() {
        return instance.getValueUsingJSON(PREFERENCE_PERSON, new TypeReference<Person>() {
        }, null);
    }

    public static void setPerson(Person person) {
        instance.storeValueUsingJSON(person, PREFERENCE_PERSON);
    }
}
