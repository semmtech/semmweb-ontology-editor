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


import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.semmtech.plugin.semmweb.core.internal.navigator.ModelCollection;
import com.semmtech.plugin.semmweb.core.internal.navigator.SemanticProjectManager;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.nature.SemanticProject;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.INamespace;
import com.semmtech.plugin.semmweb.core.navigator.IParent;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;
import com.semmtech.plugin.semmweb.core.preferences.ModelsFolderPreference;


/**
 * Utility class for SemanticProjects
 * 
 * @author Eelke van der Horst
 * 
 */
public class SemanticProjectUtils {

    private static Logger logger = Logger.getLogger(SemanticProjectUtils.class);

    /**
     * Private constructor to prevent initialization of this utility class.
     */
    private SemanticProjectUtils() {

    }

    /**
     * Returns the model files contained in the models folder of the given
     * project
     * 
     * @return files in project models folder
     */
    public static List<IFile> getModelFiles(IProject project) {
        Preconditions.checkNotNull(project);

        IFolder modelsDir = getModelsFolder(project);
        List<IFile> files = Lists.newArrayList();

        try {
            for (IResource res : modelsDir.members(false)) {
                if (res instanceof IFile) {
                    files.add((IFile) res);
                }
            }
        }
        catch (CoreException e) {
            logger.error(
                    "Error while retrieving models file list for project: " + project.getName(), e);
        }

        return files;
    }

    /**
     * Returns the file associated to given name in the given project if and
     * only if the file exists in the models folder
     * 
     * @return IFile or null
     */
    public static IFile getModelFile(IProject project, String fileName) {
        Preconditions.checkNotNull(project);
        Preconditions.checkNotNull(fileName);

        IFolder modelsFolder = getModelsFolder(project);

        if (modelsFolder == null) {
            return null;
        }

        IFile modelFile = modelsFolder.getFile(fileName);

        if (modelFile.exists()) {
            return modelFile;
        }

        return null;
    }

    /**
     * Returns the models folder of given project
     * 
     * @return project IFolder or null
     */
    public static IFolder getModelsFolder(IProject project) {
        Preconditions.checkNotNull(project);

        String modelsPath = ModelsFolderPreference.fromProject(project).getModelsFolderPath();
        return (IFolder) project.findMember(modelsPath);
    }

    /**
     * Returns true if the project has Semantic Nature
     */
    public static boolean isSemanticProject(IProject project) {
        Preconditions.checkNotNull(project);

        try {
            IProjectNature semanticNature = project.getNature(SemanticProject.NATURE_ID);

            if (semanticNature != null) {
                return true;
            }
        }
        catch (CoreException e) {
            logger.error("Error retreiving semantic nature for project:" + project, e);
        }
        return false;
    }

    /**
     * Return the ISemanticProject associated to the given IProject
     * 
     * @return ISemanticProject or null
     */
    public static ISemanticProject getSemanticProject(IProject project) {
        Preconditions.checkNotNull(project);

        SemanticProjectManager manager = SemanticProjectManager.getSemanticProjectManager(project);
        return manager.obtainProject();
    }

    /**
     * Returns the IModelCollection associated to IModel
     * 
     * @param project
     * @return
     */
    public static IModelCollection getModelCollection(IProject project) {
        Preconditions.checkNotNull(project);

        ISemanticProject semanticProject = getSemanticProject(project);

        // NOTE: Currently it is assumed that each Semantic project will
        // only have a single Models collection
        for (ISemanticElement element : semanticProject
                .getChildrenByType(ISemanticElement.MODEL_COLLECTION)) {
            if (element instanceof ModelCollection) {
                return (ModelCollection) element;
            }
        }

        return null;
    }

    /**
     * Returns the models associated to the given project
     */
    public static List<IModel> getModels(IProject project) {
        Preconditions.checkNotNull(project);

        List<IModel> models = Lists.newArrayList();

        IModelCollection modelCollection = getModelCollection(project);

        if (modelCollection != null) {
            for (ISemanticElement res : modelCollection.getChildrenByType(ISemanticElement.MODEL)) {
                if (res instanceof IModel) {
                    models.add((IModel) res);
                }
            }
        }

        return models;
    }

    /**
     * Gets the list of WorkspaceOntologySpec_s_ that are already known to the
     * project.
     * 
     * @param project
     * @return
     */
    public static List<WorkspaceOntologySpec> getKnownSpecs(IProject project) {
        if (project != null) {
            DocumentManagerPreference prefs = DocumentManagerPreference.fromProject(project);
            if (prefs != null) {
                WorkspaceDocumentManagerConfiguration configuration = prefs
                        .getDocumentManagerConfig();
                if (configuration != null) {
                    List<WorkspaceOntologySpec> knownSpecs = Lists.newArrayList();
                    knownSpecs.addAll(configuration.listWorkspaceOntologySpecs());
                    return knownSpecs;
                }
            }
        }
        return null;
    }

    /**
     * Traverses the ISemanticProject tree to returns all
     * namespace-prefix-localcopy entries (WorkspaceOntologySpecs) found in the
     * project's models.
     * 
     * @param project
     * @return
     */
    public static List<WorkspaceOntologySpec> getAllSpecs(IProject project) {
        List<WorkspaceOntologySpec> specs = Lists.newArrayList();

        List<IModel> models = getModels(project);

        for (IModel model : models) {
            List<ISemanticElement> namespaceCollections = model
                    .getChildrenByType(ISemanticElement.NAMESPACE_COLLECTION);

            for (ISemanticElement namespaceCollection : namespaceCollections) {
                IParent namespaces = (IParent) namespaceCollection;

                if (namespaces == null) {
                    continue;
                }

                ISemanticElement[] namespacesChild = namespaces.getChildren();

                if (namespacesChild == null) {
                    continue;
                }

                for (ISemanticElement ns : namespacesChild) {
                    INamespace namespace = (INamespace) ns;

                    String prefix = namespace.getPrefix();
                    String uri = namespace.getURI();

                    WorkspaceOntologySpec spec = new WorkspaceOntologySpec(uri);
                    spec.setPrefix(prefix);
                    specs.add(spec);
                }
            }
        }

        // Get a list with duplcates (and NULLs) removed:
        List<WorkspaceOntologySpec> result = ImmutableSet.copyOf(
                Iterables.filter(specs, Predicates.notNull())).asList();

        return result;
    }

    /**
     * Return true if the passed path denote a Semantic Project directory
     * 
     * @param projectDirPath
     *            Absolute project dir path
     */
    public static boolean isSemanticProjectDir(String projectDirPath) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(projectDirPath));

        // Java 7 Path do not confuse with eclipse IPath
        java.nio.file.Path projectDir = java.nio.file.Paths.get(projectDirPath);

        if (!Files.exists(projectDir) || !Files.isDirectory(projectDir)) {
            return false;
        }

        try {
            // search the .project file
            for (java.nio.file.Path f : Files.newDirectoryStream(projectDir)) {
                if (IProjectDescription.DESCRIPTION_FILE_NAME.equals(f.getFileName().toString())) {

                    // Check if the project has Semantic Nature
                    IWorkspace workspace = ResourcesPlugin.getWorkspace();
                    IProjectDescription description;
                    description = workspace.loadProjectDescription(new Path(f.toAbsolutePath()
                            .toString()));

                    for (String nature : description.getNatureIds()) {
                        if (SemanticProject.NATURE_ID.equals(nature)) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (IOException | CoreException e) {
            logger.error("Error while retrieving project nature: " + projectDirPath, e);
        }
        return false;
    }

}
