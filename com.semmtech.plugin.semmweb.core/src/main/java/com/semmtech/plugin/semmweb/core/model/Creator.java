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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.semmtech.ui.plugin.WorkbenchAdapter;


public final class Creator implements Comparable<Creator>, IAdaptable {
    private Person person;
    private Organisation organisation;

    public Creator() {
        this(null, null);
    }

    public Creator(Person person) {
        this(person, null);
    }

    public Creator(Organisation organisation) {
        this(null, organisation);
    }

    public Creator(Person person, Organisation organisation) {
        this.person = person;
        this.organisation = organisation;
    }

    public Person getPerson() {
        return person;
    }

    @JsonIgnore
    public boolean hasPerson() {
        return (person != null);
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    @JsonIgnore
    public boolean hasOrganisation() {
        return (organisation != null);
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public int hashCode() {
        return String.format("%s;%s;", (person != null ? person.getLastName() : ""),
                (organisation != null ? organisation.getName() : "")).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Creator)) {
            return false;
        }
        return equals((Creator) obj);
    }

    public boolean equals(Creator other) {
        if (person != null && other.getPerson() == null || person == null
                && other.getPerson() != null) {
            return false;
        }
        else if (person != null && other.getPerson() != null && !person.equals(other.getPerson())) {
            return false;
        }

        if (organisation != null && other.getOrganisation() == null || organisation == null
                && other.getOrganisation() != null) {
            return false;
        }
        else if (organisation != null && other.getOrganisation() != null
                && !organisation.equals(other.getOrganisation())) {
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
    public int compareTo(Creator other) {
        if (other == null) {
            return 1;
        }
        int compare = 0;
        if (getPerson() != null && other.getPerson() == null) {
            compare = -1;
        }
        else if (getPerson() == null && other.getPerson() != null) {
            compare = 1;
        }
        else if (getPerson() != null && other.getPerson() != null) {
            compare = getPerson().compareTo(other.getPerson());
        }
        if (compare == 0) {
            if (getOrganisation() != null && other.getOrganisation() == null) {
                compare = 1;
            }
            else if (getOrganisation() == null && other.getOrganisation() != null) {
                compare = -1;
            }
            else if (getOrganisation() != null && other.getOrganisation() != null) {
                compare = getOrganisation().compareTo(other.getOrganisation());
            }
        }
        return compare;
    }

    private static final WorkbenchAdapter workbenchAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            Creator creator = (Creator) o;
            Person person = creator.getPerson();
            Organisation organisation = creator.getOrganisation();
            if (person != null && organisation != null) {
                return String.format("%s %s (%s), %s", person.getFirstName(), person.getLastName(),
                        person.getEmail(), organisation.getName());
            }
            else if (person != null) {
                return String.format("%s %s (%s)", person.getFirstName(), person.getLastName(),
                        person.getEmail());
            }
            else if (organisation != null) {
                return organisation.getName();
            }
            return null;
        }
    };

}
