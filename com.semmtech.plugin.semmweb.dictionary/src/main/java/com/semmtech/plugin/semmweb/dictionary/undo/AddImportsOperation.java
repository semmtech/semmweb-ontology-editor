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

package com.semmtech.plugin.semmweb.dictionary.undo;


import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelChangesCollection;
import com.semmtech.plugin.semmweb.core.model.events.ModelChangedEvent;
import com.semmtech.plugin.semmweb.core.model.events.NamespacePrefixChangedEvent;


public class AddImportsOperation extends AbstractOperation {

    private IModelProvider provider;
    private Ontology baseOntology;
    private List<Import> imports;
    private String description;

    private class Import {
        public String ontologyUri;
        public String prefix;
        public String oldPrefixURI;
        public Resource importOntology;

        Import(String ontologyURI) {
            this.ontologyUri = ontologyURI;
        }
    }

    public AddImportsOperation(IModelProvider provider, Ontology baseOntology,
            Map<String, String> ontologyUriWithPrefix) {
        super("Add Import");
        this.provider = provider;
        this.baseOntology = baseOntology;
        this.imports = Lists.newArrayList();
        Set<String> ontologyUris = ontologyUriWithPrefix.keySet();
        for (String uri : ontologyUris) {
            Import newImport = new Import(uri);
            newImport.prefix = ontologyUriWithPrefix.get(uri);
            if (!Strings.isNullOrEmpty(newImport.prefix)) {
                newImport.oldPrefixURI = provider.getOntModel().getNsPrefixURI(newImport.prefix);
            }
            this.imports.add(newImport);
        }
        this.description = "Imported one or more ontologies";
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if ((provider == null) || (baseOntology == null)) {
            return Status.CANCEL_STATUS;
        }
        OntModel model = provider.getOntModel();
        ModelChangesCollection modelChanges = new ModelChangesCollection();
        List<String> prefixChanges = Lists.newArrayList();

        for (Import newImport : imports) {
            if (Strings.isNullOrEmpty(newImport.ontologyUri)) {
                continue;
            }

            if (!Strings.isNullOrEmpty(newImport.prefix)) {
                model.setNsPrefix(newImport.prefix, newImport.ontologyUri);
                prefixChanges.add(newImport.prefix);
            }

            newImport.importOntology = model.createResource(newImport.ontologyUri);
            baseOntology.addImport(newImport.importOntology);
            modelChanges.add(model.createStatement(baseOntology, OWL.imports,
                    newImport.importOntology));
        }

        if (!modelChanges.isEmpty()) {
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        }
        if (!prefixChanges.isEmpty()) {
            model.notifyEvent(new NamespacePrefixChangedEvent(model, prefixChanges, description));
        }
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        return execute(monitor, info);
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if ((provider == null) || (baseOntology == null)) {
            return Status.CANCEL_STATUS;
        }
        OntModel model = provider.getOntModel();
        ModelChangesCollection modelChanges = new ModelChangesCollection();
        List<String> prefixChanges = Lists.newArrayList();

        for (ListIterator<Import> iter = imports.listIterator(imports.size()); iter.hasPrevious();) {
            Import undoImport = iter.previous();
            if (Strings.isNullOrEmpty(undoImport.ontologyUri)) {
                continue;
            }

            if (undoImport.importOntology != null) {
                baseOntology.removeImport(undoImport.importOntology);
                undoImport.importOntology = null;
                modelChanges.remove(model.createStatement(baseOntology, OWL.imports,
                        undoImport.importOntology));
            }

            if (!Strings.isNullOrEmpty(undoImport.prefix)) {
                if (undoImport.oldPrefixURI != null) {
                    model.setNsPrefix(undoImport.prefix, undoImport.oldPrefixURI);
                }
                else {
                    model.removeNsPrefix(undoImport.prefix);
                }
                prefixChanges.add(undoImport.prefix);
            }
        }

        if (!modelChanges.isEmpty()) {
            model.notifyEvent(new ModelChangedEvent(model, modelChanges, description));
        }
        if (!prefixChanges.isEmpty()) {
            model.notifyEvent(new NamespacePrefixChangedEvent(model, prefixChanges, description));
        }
        return Status.OK_STATUS;
    }

}
