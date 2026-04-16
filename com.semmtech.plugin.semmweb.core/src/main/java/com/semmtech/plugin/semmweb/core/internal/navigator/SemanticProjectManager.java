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

package com.semmtech.plugin.semmweb.core.internal.navigator;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.google.common.collect.Maps;
import com.semmtech.plugin.semmweb.core.navigator.IImport;
import com.semmtech.plugin.semmweb.core.navigator.IImportCollection;
import com.semmtech.plugin.semmweb.core.navigator.IModel;
import com.semmtech.plugin.semmweb.core.navigator.IModelCollection;
import com.semmtech.plugin.semmweb.core.navigator.INamespace;
import com.semmtech.plugin.semmweb.core.navigator.INamespaceCollection;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticElement;
import com.semmtech.plugin.semmweb.core.navigator.ISemanticProject;


public class SemanticProjectManager {

    private static final Logger logger = Logger.getLogger(SemanticProjectManager.class);

    private static final String JSON_FILE = ".models";

    private static final String JSON_FIELD_MODELS = "models";
    private static final String JSON_FIELD_LOCATION_URL = "locationUrl";
    private static final String JSON_FIELD_MODEL_NAME = "name";
    private static final String JSON_FIELD_NAMESPACE_PREFIX = "prefix";
    private static final String JSON_FIELD_NAMESPACE_URI = "namespaceUri";
    private static final String JSON_FIELD_IMPORTS = "imports";
    private static final String JSON_FIELD_IMPORT_URI = "uri";
    private static final String JSON_FIELD_IMPORTED_BY = "importedBy";
    private static final String JSON_FIELD_ONTOLOGY_URI = "ontologyUri";
    private static final String JSON_FIELD_NAMESPACES = "namespaces";

    private final static Map<IProject, SemanticProjectManager> managers = Maps.newHashMap();

    private final IProject project;

    /**
     * With the volatile keyword we make sure that the read value of the build
     * will be always updated for all the threads that tries to access it.
     */
    private volatile SemanticProject build;

    private SemanticProjectManager(IProject project) {
        this.project = project;
    }

    /**
     * Retrieves the SemanticProject from the locally stored JSON file; if this
     * file is not present an empty SemanticProject will be created. If no
     * project is available for this manager, null is returned.
     * 
     * @return
     */
    public synchronized SemanticProject obtainProject() {
        // Attempt to retrieve models from previous build
        if (build != null) {
            return build;
        }

        build = new SemanticProject(null, project);
        return build;
    }

    public synchronized void clear() {
        build = null;
    }

    public ModelCollection getModelCollection() {
        SemanticProject semanticProject = obtainProject();
        if (semanticProject.getChildrenByType(ISemanticElement.MODEL_COLLECTION).size() > 0) {
            for (ISemanticElement element : semanticProject
                    .getChildrenByType(ISemanticElement.MODEL_COLLECTION)) {
                if (element instanceof ModelCollection) {
                    return (ModelCollection) element;
                }
            }
        }
        return null;
    }

    public void storeProject(SemanticProject semanticProject) {
        build = semanticProject;
        try {
            IFile file = project.getFile(JSON_FILE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            write(semanticProject, stream);
            if (file.exists()) {
                file.setContents(new ByteArrayInputStream(stream.toByteArray()), IResource.FORCE,
                        null);
            }
            else {
                file.create(new ByteArrayInputStream(stream.toByteArray()), IResource.FORCE, null);
            }
        }
        catch (CoreException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static void write(ISemanticProject semanticProject, OutputStream stream) {
        Writer writer = new OutputStreamWriter(stream);
        JsonFactory factory = new JsonFactory();

        try (JsonGenerator generator = factory.createJsonGenerator(writer)) {
            generator.setPrettyPrinter(new DefaultPrettyPrinter());
            generator.writeStartObject(); // Project

            for (ISemanticElement child : semanticProject
                    .getChildrenByType(ISemanticElement.MODEL_COLLECTION)) {
                writeModelsCollection((IModelCollection) child, generator);
            }
            generator.writeEndObject();
        }
        catch (IOException ex) {
            logger.error("Error occured trying to encode ModelCollection!", ex);
        }
    }

    private static void writeModelsCollection(IModelCollection collection, JsonGenerator generator)
            throws JsonGenerationException, IOException {
        generator.writeArrayFieldStart(JSON_FIELD_MODELS);
        for (ISemanticElement child : collection.getChildrenByType(ISemanticElement.MODEL)) {
            writeModel((IModel) child, generator);
        }
        generator.writeEndArray();
    }

    private static void writeModel(IModel model, JsonGenerator generator)
            throws JsonGenerationException, IOException {
        generator.writeStartObject(); // Model
        generator.writeStringField(JSON_FIELD_MODEL_NAME, model.getName());
        generator.writeStringField(JSON_FIELD_LOCATION_URL, model.getLocationURL());
        for (ISemanticElement child : model.getChildren()) {
            if (child instanceof IImportCollection) {
                writeImports((IImportCollection) child, generator);
            }
            else if (child instanceof INamespaceCollection) {
                writeNamespaces((INamespaceCollection) child, generator);
            }
        }
        generator.writeEndObject();
    }

    private static void writeNamespaces(INamespaceCollection collection, JsonGenerator generator)
            throws JsonGenerationException, IOException {
        generator.writeArrayFieldStart(JSON_FIELD_NAMESPACES);
        for (ISemanticElement child : collection.getChildrenByType(ISemanticElement.NAMESPACE)) {
            INamespace namespace = (INamespace) child;
            generator.writeStartObject();
            generator.writeStringField(JSON_FIELD_NAMESPACE_URI, namespace.getURI());
            if (namespace.getPrefix() != null) {
                generator.writeStringField(JSON_FIELD_NAMESPACE_PREFIX, namespace.getPrefix());
            }
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

    private static void writeImports(IImportCollection collection, JsonGenerator generator)
            throws JsonGenerationException, IOException {
        generator.writeArrayFieldStart(JSON_FIELD_IMPORTS);
        for (ISemanticElement child : collection.getChildrenByType(ISemanticElement.IMPORT)) {
            IImport immport = (IImport) child;
            generator.writeStartObject();
            generator.writeStringField(JSON_FIELD_IMPORT_URI, immport.getURI());
            // generator.writeBooleanField(JSON_FIELD_IMPORT_DISABLED,
            // immport.isDisabled());
            generator.writeArrayFieldStart(JSON_FIELD_IMPORTED_BY);
            for (String ontologyUri : immport.getImportedByOntologyURIs()) {
                generator.writeStartObject();
                generator.writeStringField(JSON_FIELD_ONTOLOGY_URI, ontologyUri);
                generator.writeEndObject();
            }
            generator.writeEndArray();
            generator.writeEndObject();
        }
        generator.writeEndArray();
    }

    public SemanticProject read(IFile file) {
        SemanticProject semanticProject = new SemanticProject(null, project);
        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createJsonParser(file.getContents())) {
            ModelCollection modelCollection = new ModelCollection(semanticProject);
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if (JSON_FIELD_MODELS.equals(parser.getCurrentName())) {
                    parser.nextToken();
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        Model model = new Model(modelCollection);
                        ImportCollection importCollection = new ImportCollection(model);
                        NamespaceCollection namespaceCollection = new NamespaceCollection(model);
                        while (parser.nextToken() != JsonToken.END_OBJECT) {
                            if (JSON_FIELD_MODEL_NAME.equals(parser.getCurrentName())) {
                                parser.nextToken();
                                model.setName(parser.getText());
                            }
                            else if (JSON_FIELD_LOCATION_URL.equals(parser.getCurrentName())) {
                                parser.nextToken();
                                model.setLocationURL(parser.getText());
                            }
                            else if (JSON_FIELD_IMPORTS.equals(parser.getCurrentName())) {

                                parser.nextToken();
                                while (parser.nextToken() != JsonToken.END_ARRAY) {
                                    Import immport = new Import(importCollection);
                                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                                        if (JSON_FIELD_IMPORT_URI.equals(parser.getCurrentName())) {
                                            parser.nextToken();
                                            immport.setURI(parser.getText());
                                        }
                                        // else if
                                        // (JSON_FIELD_IMPORT_DISABLED.equals(parser
                                        // .getCurrentName())) {
                                        // parser.nextToken();
                                        // immport.setDisabled(parser.getBooleanValue());
                                        // }
                                        else if (JSON_FIELD_IMPORTED_BY.equals(parser
                                                .getCurrentName())) {

                                            parser.nextToken();
                                            while (parser.nextToken() != JsonToken.END_ARRAY) {
                                                while (parser.nextToken() != JsonToken.END_OBJECT) {
                                                    if (JSON_FIELD_ONTOLOGY_URI.equals(parser
                                                            .getCurrentName())) {
                                                        parser.nextTextValue();
                                                        immport.addImportedByOntologyUri(parser
                                                                .getText());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    immport.setImportType(semanticProject.getImportType(immport
                                            .getURI()));
                                }
                            }
                            else if (JSON_FIELD_NAMESPACES.equals(parser.getCurrentName())) {

                                parser.nextToken();
                                while (parser.nextToken() != JsonToken.END_ARRAY) {
                                    Namespace namespace = new Namespace(namespaceCollection);
                                    while (parser.nextToken() != JsonToken.END_OBJECT) {
                                        if (JSON_FIELD_NAMESPACE_URI
                                                .equals(parser.getCurrentName())) {
                                            parser.nextToken();
                                            namespace.setURI(parser.getText());
                                        }
                                        else if (JSON_FIELD_NAMESPACE_PREFIX.equals(parser
                                                .getCurrentName())) {
                                            parser.nextToken();
                                            namespace.setPrefix(parser.getText());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            logger.error(String.format(
                    "An error occured trying to read from the .models file of project: %s", file
                            .getProject().getName()), ex);
        }
        return semanticProject;
    }

    public synchronized static SemanticProjectManager getSemanticProjectManager(IProject project) {
        if (!managers.containsKey(project)) {
            managers.put(project, new SemanticProjectManager(project));
        }
        return managers.get(project);
    }

}
