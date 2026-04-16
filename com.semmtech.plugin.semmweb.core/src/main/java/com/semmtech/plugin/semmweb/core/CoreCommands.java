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


/**
 * Default SEMMweb core commands.
 * 
 * @author Mike Henrichs
 * 
 */
public interface CoreCommands {
    static final String DUMMY_ID = "com.semmtech.plugin.semmweb.core.commands.dummy";

    static final String ADD_IMPORT_ID = "com.semmtech.plugin.semmweb.core.commands.addImport";
    static final String EDIT_SELECTED_RESOURCE_ID = "com.semmtech.plugin.semmweb.core.commands.editSelectedResource";
    static final String CREATE_RESOURCE_ID = "com.semmtech.plugin.semmweb.core.commands.createResource";
    static final String CREATE_PROPERTY_ID = "com.semmtech.plugin.semmweb.core.commands.createProperty";
    static final String DELETE_SELECTED_RESOURCE_ID = "com.semmtech.plugin.semmweb.core.commands.deleteSelectedResource";
    static final String CREATE_RESTRICTION_ID = "com.semmtech.plugin.semmweb.core.commands.createRestriction";
    static final String CREATE_QUALIFICATION_ID = "com.semmtech.plugin.semmweb.core.commands.createQualification";
}
