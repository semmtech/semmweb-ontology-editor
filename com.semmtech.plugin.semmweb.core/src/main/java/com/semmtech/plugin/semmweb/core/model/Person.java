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


public final class Person implements Comparable<Person>, IAdaptable {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String faxNumber;

    public Person() {
    }

    public Person(String lastName, String firstName, String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    @Override
    public int hashCode() {
        return lastName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Person)) {
            return false;
        }
        return equals((Person) obj);
    }

    public boolean equals(Person other) {
        if (!lastName.equals(other.getLastName())) {
            return false;
        }
        if (!firstName.equals(other.getFirstName())) {
            return false;
        }
        if (!email.equals(other.getEmail())) {
            return false;
        }

        if (phoneNumber == null && other.getPhoneNumber() != null || phoneNumber != null
                && other.getPhoneNumber() == null) {
            return false;
        }
        else if (phoneNumber != null && other.getPhoneNumber() != null
                && !phoneNumber.equals(other.getPhoneNumber())) {
            return false;
        }

        if (faxNumber == null && other.getFaxNumber() != null || faxNumber != null
                && other.getFaxNumber() == null) {
            return false;
        }
        else if (faxNumber != null && other.getFaxNumber() != null
                && !faxNumber.equals(other.getFaxNumber())) {
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(Person other) {
        if (other == null) {
            return 1;
        }
        int compare = lastName.compareToIgnoreCase(other.getLastName());
        if (compare != 0) {
            return compare;
        }
        compare = firstName.compareToIgnoreCase(other.getFirstName());
        if (compare != 0) {
            return compare;
        }
        return email.compareToIgnoreCase(other.getEmail());
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

    private WorkbenchAdapter workbenchAdapter = new WorkbenchAdapter() {
        @Override
        public String getLabel(Object o) {
            Person person = (Person) o;
            return String.format("%s %s (%s)", person.getFirstName(), person.getLastName(),
                    person.getEmail());
        }
    };

}
