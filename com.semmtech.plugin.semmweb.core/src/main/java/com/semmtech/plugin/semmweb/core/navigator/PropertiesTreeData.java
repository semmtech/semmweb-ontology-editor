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

package com.semmtech.plugin.semmweb.core.navigator;


import org.eclipse.core.resources.IFile;


/**
 * http://scribbledideas.blogspot.nl/2006/05/building-common-navigator-based-
 * viewer_22.html
 * 
 * @author Mike Henrichs
 * 
 */
public class PropertiesTreeData {
    private IFile container;
    private String name;
    private String value;

    /**
     * Create a property with the given name and value contained by the given
     * file.
     * 
     * @param aName
     *            The name of the property.
     * @param aValue
     *            The value of the property.
     * @param aFile
     *            The file that defines this property.
     */
    public PropertiesTreeData(String aName, String aValue, IFile aFile) {
        name = aName;
        value = aValue;
        container = aFile;
    }

    /**
     * The name of this property.
     * 
     * @return The name of this property.
     */
    public String getName() {
        return name;
    }

    /**
     * Return the value of the property in the file.
     * 
     * @return The value of the property in the file.
     */
    public String getValue() {
        return value;
    }

    /**
     * The IFile that defines this property.
     * 
     * @return The IFile that defines this property.
     */
    public IFile getFile() {
        return container;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof PropertiesTreeData
                && ((PropertiesTreeData) obj).getName().equals(name);
    }

    public String toString() {
        StringBuffer toString = new StringBuffer(getName()).append(":").append(getValue()); //$NON-NLS-1$
        return toString.toString();
    }
}
