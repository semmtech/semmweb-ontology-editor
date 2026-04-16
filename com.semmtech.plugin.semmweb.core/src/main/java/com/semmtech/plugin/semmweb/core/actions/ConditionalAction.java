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

package com.semmtech.plugin.semmweb.core.actions;


import java.util.List;

import org.eclipse.jface.action.Action;

import com.google.common.collect.Lists;


/**
 * 
 * @author Sander Stolk
 * @author Simone Rondelli
 */
public abstract class ConditionalAction extends Action {
    protected final List<IRunCondition> runConditions;

    public ConditionalAction() {
        this.runConditions = Lists.newArrayList();
    }

    @Override
    abstract public void run();

    protected boolean satisfiesRunConditions() {
        for (IRunCondition condition : runConditions) {
            if (!condition.isSatisfied(this)) {
                return false;
            }
        }
        return true;
    }

    public void addRunCondition(IRunCondition condition) {
        runConditions.add(condition);
    }

    public void removeRunCondition(IRunCondition condition) {
        runConditions.remove(condition);
    }
}
