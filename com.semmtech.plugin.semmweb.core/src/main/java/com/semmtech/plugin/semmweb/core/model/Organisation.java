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


import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.model.IWorkbenchAdapter3;

import com.semmtech.ui.plugin.WorkbenchAdapter;


public final class Organisation implements Comparable<Organisation>, IAdaptable {
    private String name;
    private String acronym;

    public Organisation() {

    }

    public Organisation(String name, String acronym) {
        this.name = name;
        this.acronym = acronym;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Organisation)) {
            return false;
        }
        return equals((Organisation) obj);
    }

    public boolean equals(Organisation other) {
        if (!name.equals(other.getName())) {
            return false;
        }
        if (!acronym.equals(other.getAcronym())) {
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        if (adapter == IWorkbenchAdapter.class || adapter == IWorkbenchAdapter2.class
                || adapter == IWorkbenchAdapter3.class) {
            return workbenchAdapter;
        }
        return null;
    }

    @Override
    public int compareTo(Organisation other) {
        if (other == null) {
            return 1;
        }
        int compare = name.compareToIgnoreCase(other.getName());
        if (compare != 0) {
            return compare;
        }
        return acronym.compareToIgnoreCase(other.getAcronym());
    }

    private final static WorkbenchAdapter workbenchAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            return ((Organisation) o).getName();
        }
    };
}
