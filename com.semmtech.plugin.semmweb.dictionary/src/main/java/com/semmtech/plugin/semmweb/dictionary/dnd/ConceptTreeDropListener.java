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

package com.semmtech.plugin.semmweb.dictionary.dnd;


import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Shell;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.dialog.LiteralStatementInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.ResourceStatementInputDialog;
import com.semmtech.plugin.semmweb.core.dialog.StatementInputDialog;
import com.semmtech.plugin.semmweb.core.dnd.PropertyTransfer;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.widgets.trees.ResourceTreeData;
import com.semmtech.plugin.semmweb.dictionary.DictionaryPlugin;


public class ConceptTreeDropListener extends ViewerDropAdapter {
    private Logger logger = Logger.getLogger(ConceptTreeDropListener.class);
    private int dropLocation;
    private ResourceTreeData targetData;

    private boolean forceCheckDomain = false;

    public ConceptTreeDropListener(Viewer viewer) {
        super(viewer);
    }

    @Override
    public void drop(DropTargetEvent event) {
        dropLocation = this.determineLocation(event);
        Object target = determineTarget(event);
        String targetName = "[empty]";
        if (target != null)
            targetName = target.toString();
        if (target instanceof ResourceTreeData) {
            targetData = (ResourceTreeData) target;
            targetName = "node '" + targetData.getURI() + "'";
        }
        else
            targetData = null;

        String translatedLocation = "";
        switch (dropLocation) {
        case LOCATION_BEFORE:
            translatedLocation = "Dropped before the target ";
            break;
        case LOCATION_AFTER:
            translatedLocation = "Dropped after the target ";
            break;
        case LOCATION_ON:
            translatedLocation = "Dropped on the target ";
            break;
        case LOCATION_NONE:
            translatedLocation = "Dropped into nothing ";
            break;
        }
        logger.debug(translatedLocation);
        logger.debug("The drop was done on the element: " + targetName);
        super.drop(event);
    }

    /**
     * This method performs the actual drop
     */
    @Override
    public boolean performDrop(Object data) {
        if (targetData != null) {
            if (data instanceof Property) {
                Property property = (Property) data;
                Resource resource = null;
                OntModel model = null;
                switch (dropLocation) {
                case LOCATION_BEFORE:
                case LOCATION_AFTER:
                    break;
                case LOCATION_ON:
                    resource = targetData;
                    break;
                case LOCATION_NONE:
                    break;
                }
                IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
                if (provider != null) {
                    model = provider.getOntModel();
                }
                if (model != null && resource != null) {
                    Shell shell = DictionaryPlugin.getDefault().getWorkbench()
                            .getActiveWorkbenchWindow().getShell();

                    String title = "Create Statement";
                    String message = "Create statement with given subject and predicate.";
                    StatementInputDialog dialog = null;

                    Resource range = null;
                    for (Statement rangeStatement : model.listStatements(
                            new SimpleSelector(property, RDFS.range, (RDFNode) null)).toList()) {
                        if (rangeStatement.getObject() != null
                                && !rangeStatement.getObject().isLiteral()) {
                            range = (Resource) rangeStatement.getObject();
                            break;
                        }
                    }
                    if (range == null) {
                        dialog = new LiteralStatementInputDialog(shell, title, message);
                        ((LiteralStatementInputDialog) dialog).setDatatypeVisible(true);
                        ((LiteralStatementInputDialog) dialog).setLanguageVisible(true);
                    }
                    else if (range.equals(RDFS.Literal))
                        dialog = new LiteralStatementInputDialog(shell, title, message);
                    else if (range.equals(XSD.nonNegativeInteger)) {
                        dialog = new LiteralStatementInputDialog(shell, title, message);
                        ((LiteralStatementInputDialog) dialog).setDatatypeVisible(true);
                        ((LiteralStatementInputDialog) dialog).setLanguageVisible(false);
                        ((LiteralStatementInputDialog) dialog).setDatatype(XSD.nonNegativeInteger);
                    }
                    else {
                        dialog = new ResourceStatementInputDialog(shell, title, message);
                        ((ResourceStatementInputDialog) dialog).setAllowedResourceType(range);
                    }
                    dialog.setModel(model);
                    dialog.setSubject(resource);
                    dialog.setProperties(Arrays.asList(property));
                    dialog.setSelectedProperty(0);

                    if (dialog.open() == 0) {
                        Statement statement = dialog.createStatement();
                        if (statement != null && provider != null) {
                            String transactionDescription = "Added new statement due to drop "
                                    + "of property on SKOS concept";
                            ModelTransaction transaction = provider
                                    .createTransaction(transactionDescription);
                            model.add(statement);
                            provider.commitTransaction(transaction);
                        }
                    }
                    return true;
                }
            }
            // if (data instanceof String) {
            // Resource resource = model.getResource((String) data);
            // Resource parent = null;
            // switch (dropLocation) {
            // case LOCATION_BEFORE :
            // parent = (Resource)targetData.getParent();
            // break;
            // case LOCATION_AFTER :
            // parent = (Resource)targetData.getParent();
            // break;
            // case LOCATION_ON :
            // parent = (Resource)targetData;
            // break;
            // case LOCATION_NONE :
            // break;
            // }
            // if (parent != null) {
            // logger.debug("Parent in drop is '" + parent.getURI() + "'");
            // model.add(resource, RDFS.subClassOf, parent);
            // /// Postpone update! - let drag listener perform notifyEvent, so
            // no notifyEvent here
            // return true;
            // }
            // }
        }
        return false;
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        if (PropertyTransfer.getInstance().isSupportedType(transferType)) {
            int location = getCurrentLocation();
            if (location != LOCATION_ON)
                return false;

            Property property = (Property) PropertyTransfer.getInstance()
                    .nativeToJava(transferType);
            boolean valid = !forceCheckDomain;
            if (!valid) {
                Resource domain = property.getPropertyResourceValue(RDFS.domain);
                if (domain != null) {
                    Resource resource = (Resource) target;
                    OntModel inferredModel = ModelFactory.createOntologyModel(
                            OntModelSpec.RDFS_MEM_RDFS_INF, resource.getModel());
                    List<Resource> validResources = inferredModel.listSubjectsWithProperty(
                            RDF.type, domain).toList();
                    valid = validResources.contains(resource);
                }
            }
            return true;
        }
        return false;
    }

}
