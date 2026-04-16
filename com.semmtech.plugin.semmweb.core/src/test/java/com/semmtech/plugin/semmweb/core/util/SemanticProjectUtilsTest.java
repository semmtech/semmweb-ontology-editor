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
package com.semmtech.plugin.semmweb.core.util;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IProject;
import org.junit.Ignore;
import org.junit.Test;

//import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;


public class SemanticProjectUtilsTest {

    @Test
    public void testGetSemanticProject() {
        // Create mock project:
        IProject mockedProject = mock(IProject.class);

        // Stub required methods:

        // Get a semantic project for the project:
        ISemanticProject project = SemanticProjectUtils.getSemanticProject(mockedProject);

        assertNotNull(project);
    }

    @Test
    public void testGetModelCollectionWithNoModelCollection() {
        // Create mock project:
        IProject mockedProject = mock(IProject.class);

        // Stub required methods:

        // Get a semantic project for the project:
        IModelCollection modelCollection = SemanticProjectUtils.getModelCollection(mockedProject);

        assertNull(modelCollection);
    }

    @Test
    @Ignore("First get to know SemanticProject workings better [Eelke van der Horst]")
    public void testGetModelCollectionWithModelCollection() {
        // Create mock project:
        IProject mockedProject = mock(IProject.class);

        // Stub required methods:

        // Get a semantic project for the project:
        IModelCollection modelCollection = SemanticProjectUtils.getModelCollection(mockedProject);

        assertNull(modelCollection);
    }

    @Test
    @Ignore("First get to know SemanticProject workings better [Eelke van der Horst]")
    public void testGetModels() {
        // Build a tree starting from project

        fail("Not yet implemented");
    }

    @Test
    @Ignore("First get to know SemanticProject workings better [Eelke van der Horst]")
    public void testGetAllSpecs() {
        fail("Not yet implemented");

        // WorkspaceOntologySpec spec1 = WorkspaceOntologySpec.RDFS;
    }

}
