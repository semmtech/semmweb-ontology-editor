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

package com.semmtech.plugin.semmweb.core.nature;


import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.semmtech.plugin.semmweb.core.builders.SemanticProjectBuilder;


public class SemanticProject implements IProjectNature {
    private static Logger logger = Logger.getLogger(SemanticProject.class);

    public static final String NATURE_ID = "com.semmtech.plugin.semmweb.core.semanticProject";

    private IProject project;

    private String getVersion() {
        IProduct product = Platform.getProduct();
        Bundle bundle = product.getDefiningBundle();
        Version version = bundle.getVersion();
        return String
                .format("%s.%s.%s", version.getMajor(), version.getMinor(), version.getMicro());
    }

    public static final QualifiedName INITIAL_VERSION = new QualifiedName(
            "com.semmtech.plugin.semmweb.core.semanticProject", "initialVersion");

    public static final QualifiedName LATEST_VERSION = new QualifiedName(
            "com.semmtech.plugin.semmweb.core.semanticProject", "latestVersion");

    @Override
    public void configure() throws CoreException {
        IProjectDescription description = project.getDescription();

        updateVersionInfo();

        ICommand command = description.newCommand();
        command.setBuilderName(SemanticProjectBuilder.BUILDER_ID);
        setSemanticCommand(description, command);
    }

    private void updateVersionInfo() {
        // Using preferences
        ProjectScope scope = new ProjectScope(project);
        ScopedPreferenceStore store = new ScopedPreferenceStore(scope, NATURE_ID);
        String version = getVersion();
        if (!store.contains("initialVersion")) {
            store.setValue("initialVersion", version);
        }
        store.setValue("latestVersion", version);
        try {
            store.save();
        }
        catch (IOException ex) {
            logger.error("Error trying to save the preferences", ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void deconfigure() throws CoreException {
        // TODO: Undo the set versions
    }

    /**
     * This method could be extracted into a more general Util class; or a
     * abstract superclass which is defined in the com.semmtech.ui.plugin.
     * 
     * @param description
     * @param command
     * @throws CoreException
     */
    private void setSemanticCommand(IProjectDescription description, ICommand command)
            throws CoreException {
        ICommand[] oldBuildSpec = description.getBuildSpec();
        int oldSemanticIndex = getSemanticCommandIndex(oldBuildSpec);
        ICommand[] newBuildSpec;
        if (oldSemanticIndex == -1) {
            newBuildSpec = new ICommand[oldBuildSpec.length + 1];
            System.arraycopy(oldBuildSpec, 0, newBuildSpec, 1, oldBuildSpec.length);
            newBuildSpec[0] = command;
        }
        else {
            oldBuildSpec[oldSemanticIndex] = command;
            newBuildSpec = oldBuildSpec;
        }
        description.setBuildSpec(newBuildSpec);
        project.setDescription(description, null);
    }

    /**
     * See description setSemanticCommand.
     * 
     * @param buildSpec
     * @return
     */
    private int getSemanticCommandIndex(ICommand[] buildSpec) {
        for (int i = 0; i < buildSpec.length; i++) {
            if (buildSpec[i].getBuilderName().equals(SemanticProjectBuilder.BUILDER_ID)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public IProject getProject() {
        return project;
    }

    @Override
    public void setProject(IProject project) {
        this.project = project;
    }

    public static boolean isSemanticProject(IProject project) {
        boolean result = false;
        if (project != null) {
            try {
                result = project.getNature(NATURE_ID) != null;
            }
            catch (CoreException ex) {
            }
        }
        return result;
    }
}
