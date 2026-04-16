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

package com.semmtech.plugin.semmweb.core.operations;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceDocumentManagerConfiguration;
import com.semmtech.plugin.semmweb.core.jena.ontology.WorkspaceOntologySpec;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;
import com.semmtech.plugin.semmweb.core.preferences.DocumentManagerPreference;


/**
 * 
 * @author Sander Stolk
 */
public class AddImportOperation extends ModelOperation {

    private static Logger logger = Logger.getLogger(AddImportOperation.class);

    private final static String description = "Add import";

    private Resource sourceOntology;
    private String sourceUri;
    private String sourcePrefix;
    private String importUri;
    private String importPrefix;
    private String oldPrefixedImportURI;
    private String oldPrefixedSourceURI;
    private Resource importOntology;
    private boolean priorSourceOntologyStatementExisted;
    private boolean priorImportStatementExisted;

    /**
     * If this file is not null will be added as alternate URL of the imported
     * ontology URI
     */
    private IFile alternateFile;

    /** The argument <code>importPrefix</code> may be null. */
    public AddImportOperation(Resource ontology, String importUri, String importPrefix) {
        super(description);
        this.sourceOntology = ontology;
        this.importUri = importUri;
        this.importPrefix = importPrefix;
        this.alternateFile = null;
    }

    /**
     * The argument <code>sourcePrefix</code> may be null. The argument
     * <code>importPrefix</code> may be null.
     */
    public AddImportOperation(String sourceUri, String sourcePrefix, String importUri,
            String importPrefix) {
        super(description);
        this.sourceUri = sourceUri;
        this.sourcePrefix = sourcePrefix;
        this.importUri = importUri;
        this.importPrefix = importPrefix;
    }

    public void setAlternateFile(IFile alternateFile) {
        this.alternateFile = alternateFile;
    }

    @Override
    public boolean execute(OntModel model) {
        if ((sourceOntology == null && sourceUri == null) || (importUri == null)) {
            return false;
        }

        if (!Strings.isNullOrEmpty(sourcePrefix)) {
            this.oldPrefixedSourceURI = model.getNsPrefixURI(sourcePrefix);
        }
        if (!Strings.isNullOrEmpty(importPrefix)) {
            if (importPrefix.equals(sourcePrefix)) {
                // ignore prefix; can't have the same prefix referring to two
                // namespaces
                importPrefix = null;
            }
            else {
                this.oldPrefixedImportURI = model.getNsPrefixURI(importPrefix);
            }
        }

        ModelChangesCollection modelChanges = new ModelChangesCollection();

        // Ensure source ontology is marked as being of type owl:Ontology
        if (sourceOntology == null) {
            sourceOntology = model.createResource(sourceUri);
        }
        Statement sourceOntologyStatement = model.createStatement(sourceOntology, RDF.type,
                OWL.Ontology);
        priorSourceOntologyStatementExisted = model.contains(sourceOntologyStatement);
        if (!priorSourceOntologyStatementExisted) {
            model.add(sourceOntologyStatement);
            modelChanges.add(sourceOntologyStatement);
        }

        // Add the import of the ontology
        importOntology = model.createResource(importUri);
        Statement importStatement = model.createStatement(sourceOntology, OWL.imports,
                importOntology);
        priorImportStatementExisted = model.contains(importStatement);
        if (!priorImportStatementExisted) {
            model.add(importStatement);
            modelChanges.add(importStatement);
        }

        if (!modelChanges.isEmpty()) {
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        }

        List<String> prefixesChanged = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(sourcePrefix)) {
            model.setNsPrefix(sourcePrefix, sourceUri);
            prefixesChanged.add(sourcePrefix);
        }
        if (!Strings.isNullOrEmpty(importPrefix)) {
            model.setNsPrefix(importPrefix, importUri);
            prefixesChanged.add(importPrefix);
        }
        if (!prefixesChanged.isEmpty()) {
            model.notifyEvent(new NamespacePrefixChangedEvent(model, prefixesChanged, description));
        }

        if (alternateFile != null) {
            DocumentManagerPreference documentManagerPreference = DocumentManagerPreference
                    .fromProject(alternateFile.getProject());

            WorkspaceDocumentManagerConfiguration ontConf = documentManagerPreference
                    .getDocumentManagerConfig();

            String altUrl = String.format("file:///%s", alternateFile.getLocation().toOSString())
                    .replace("\\", "/");

            WorkspaceOntologySpec spec = ontConf.getOntologySpec(importUri);
            if (spec == null) {
                spec = new WorkspaceOntologySpec(importUri);
            }
            spec.setPrefix(importPrefix);
            spec.setWorkspaceAltURL(altUrl);
            ontConf.addOntologySpec(spec);

            documentManagerPreference.setDocumentManagerConfig(ontConf);

            try {
                documentManagerPreference.save();
            }
            catch (Exception ex) {
                logger.error("Exception occured trying to save document manager preferences", ex);
            }
        }

        return true;
    }

    @Override
    public boolean undo(OntModel model) {
        if ((sourceOntology == null) || (importUri == null) || (importOntology == null)) {
            return false;
        }

        ModelChangesCollection modelChanges = new ModelChangesCollection();

        Statement importStatement = model.createStatement(sourceOntology, OWL.imports,
                importOntology);
        if (!priorImportStatementExisted) {
            model.remove(importStatement);
            importOntology = null;
            modelChanges.remove(importStatement);
        }

        Statement sourceOntologyStatement = model.createStatement(sourceOntology, OWL.imports,
                importOntology);
        if (!priorSourceOntologyStatementExisted) {
            model.remove(sourceOntologyStatement);
            sourceOntology = null;
            modelChanges.remove(sourceOntologyStatement);
        }

        if (!modelChanges.isEmpty()) {
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        }

        List<String> prefixesChanged = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(importPrefix)) {
            if (oldPrefixedImportURI != null) {
                model.setNsPrefix(importPrefix, oldPrefixedImportURI);
            }
            else {
                model.removeNsPrefix(importPrefix);
            }
            prefixesChanged.add(importPrefix);
        }
        if (!Strings.isNullOrEmpty(sourcePrefix)) {
            if (oldPrefixedSourceURI != null) {
                model.setNsPrefix(sourcePrefix, oldPrefixedSourceURI);
            }
            else {
                model.removeNsPrefix(sourcePrefix);
            }
            prefixesChanged.add(sourcePrefix);
        }
        model.notifyEvent(new NamespacePrefixChangedEvent(model, prefixesChanged, description));

        if (alternateFile != null) {
            DocumentManagerPreference documentManagerPreference = DocumentManagerPreference
                    .fromProject(alternateFile.getProject());

            WorkspaceDocumentManagerConfiguration ontConf = documentManagerPreference
                    .getDocumentManagerConfig();

            ontConf.setWorkspaceAltURL(importUri, null);

            documentManagerPreference.setDocumentManagerConfig(ontConf);

            try {
                documentManagerPreference.save();
            }
            catch (Exception ex) {
                logger.error("Exception occured trying to save document manager preferences", ex);
            }
        }

        return true;
    }

}
