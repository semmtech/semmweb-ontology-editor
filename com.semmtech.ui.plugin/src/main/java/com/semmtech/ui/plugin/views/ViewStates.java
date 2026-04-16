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

package com.semmtech.ui.plugin.views;


import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * 
 * @author Sander Stolk
 */
public class ViewStates {

    private final List<ViewState> states;

    protected class ViewState {
        private final String stateName;
        private final Map<String, Object> stateObjects;

        public ViewState(String stateName) {
            this.stateName = stateName;
            this.stateObjects = Maps.newHashMap();
        }

        public String getName() {
            return stateName;
        }

        public void setParameter(String parameter, Object object) {
            stateObjects.put(parameter, object);
        }

        public Object getParameter(String parameter) {
            return stateObjects.get(parameter);
        }

        public boolean containsParameter(String parameter) {
            return stateObjects.containsKey(parameter);
        }
    }

    public ViewStates() {
        states = Lists.newArrayList();
    }

    /** Discards the current state with the specified name. */
    public void discardState(String stateName) {
        int index = findState(stateName);
        if (index != -1) {
            states.remove(index);
        }
    }

    /**
     * Sets the specified parameter with the object for the state with the given
     * name. If the parameter already exists in the state, it is overwritten. If
     * no state with the given name exists yet, a new state is created with that
     * name to set the parameter in.
     */
    public void setParameter(String stateName, String parameter, Object object) {
        if (stateName == null) {
            return;
        }
        int index = findState(stateName);
        if (index == -1) {
            index = states.size();
            states.add(index, new ViewState(stateName));
        }
        states.get(index).setParameter(parameter, object);
    }

    public boolean hasParameter(String stateName, String parameter) {
        for (ViewState state : states) {
            if (state.getName().equals(stateName)) {
                return state.containsParameter(parameter);
            }
        }
        return false;
    }

    /**
     * Gets the object of the specified parameter for the state with the given
     * name. If the parameter or the state does not yet exist, null is returned.
     */
    public Object getParameter(String stateName, String parameter) {
        int index = findState(stateName);
        if (index != -1) {
            return states.get(index).getParameter(parameter);
        }
        return null;
    }

    private int findState(String stateName) {
        if (stateName == null) {
            return -1;
        }
        for (int i = 0; i < states.size(); i++) {
            ViewState state = states.get(i);
            if (state.getName().equals(stateName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the state with the given name. Null is returned if no such state
     * exists.
     */
    protected ViewState getState(String stateName) {
        int index = findState(stateName);
        return (index != -1) ? states.get(index) : null;
    }
}
