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

package com.semmtech.plugin.semmweb.core.actions;


import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.NotImplementedException;
import com.semmtech.plugin.semmweb.core.dialog.BaseRestrictionValidator;
import com.semmtech.plugin.semmweb.core.dialog.RadioSelectionInputDialog;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ModelTransaction;
import com.semmtech.plugin.semmweb.core.model.ResourceStatements;
import com.semmtech.plugin.semmweb.core.model.RestrictionsModel;
import com.semmtech.plugin.semmweb.core.wizards.CreateRestrictionWizard;
import com.semmtech.semantics.ontology.OntClassUtil;
import com.semmtech.semantics.util.JenaUtil;


/*
 * TODO refactor: maybe the methods parameters that are repeated could be passed as class fields (modelProvider, affectedClass ecc..) 
 */
public class EditRestrictionSuite {

    public final static void editRestriction(IModelProvider modelProvider, Shell shell,
            List<Restriction> editRestrictions, Resource affectedClass) {
        if (editRestrictions.isEmpty()) {
            return;
        }

        List<Restriction> relatedRestrictions = editRestrictions;
        Restriction editRestriction = relatedRestrictions.get(0);
        relatedRestrictions.remove(0);

        editRestriction(modelProvider, shell, editRestriction, affectedClass, relatedRestrictions);
    }

    public final static void editRestriction(IModelProvider modelProvider, Shell shell,
            Restriction editRestriction, Resource affectedClass,
            List<Restriction> relatedLocalRestrictions) {

        CreateRestrictionWizard wizard = new CreateRestrictionWizard("Edit Restriction",
                modelProvider, editRestriction, affectedClass, relatedLocalRestrictions);

        wizard.setAnonymousAllowed(true);
        wizard.setAllowOpenEditorOnFinish(false);
        wizard.setLabelProperty(RDFS.label);
        wizard.setAnonymous(true);
        wizard.setSuppressNotify(true);

        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.showPage(wizard.getPage(CreateRestrictionWizard.RESTRICTIONS_PAGE_NAME));

        String transactionDescription = "Due to creation of one or more new restrictions";
        ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
        if (dialog.open() != Window.OK) {
            modelProvider.abortTransaction(transaction);
        }
        else {
            RestrictionsModel restrictionsModel = new RestrictionsModel(modelProvider.getOntModel());

            List<Restriction> oldRestrictionsSet = Lists.newArrayList(relatedLocalRestrictions);
            oldRestrictionsSet.add(editRestriction);
            List<Restriction> newRestrictionsSet = wizard.getRestrictions();

            // Set the affectedClass as subclass to the new restrictions rather
            // than the old ones.
            restrictionsModel.replaceRestrictionSuperclasses(affectedClass, oldRestrictionsSet,
                    newRestrictionsSet);

            // Commit changes to model.
            modelProvider.commitTransaction(transaction);
        }
    }

    public final static void createNarrowRestriction(IModelProvider modelProvider, Shell shell,
            Restriction editRestriction, OntResource affectedClass) {

        CreateRestrictionWizard wizard = new CreateRestrictionWizard("Create Restriction",
                modelProvider, editRestriction, affectedClass, Lists.<Restriction> newArrayList());

        wizard.setAnonymousAllowed(true);
        wizard.setAllowOpenEditorOnFinish(false);
        wizard.setLabelProperty(RDFS.label);
        wizard.setAnonymous(true);
        wizard.setSuppressNotify(true);
        wizard.setValidator(new CustomRestrictionValidator(modelProvider, affectedClass));

        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.showPage(wizard.getPage(CreateRestrictionWizard.RESTRICTIONS_PAGE_NAME));

        String transactionDescription = "Due to creation of one or more new restrictions";
        ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
        if (dialog.open() != Window.OK) {
            modelProvider.abortTransaction(transaction);
        }
        else {
            // Add subClassOf relations to the new restrictions
            ResourceStatements.createResourceAsSubclassStatements(affectedClass,
                    wizard.getRestrictions());

            // Commit changes to model.
            modelProvider.commitTransaction(transaction);
        }
    }

    public final static void editRestrictionAnonymousCheck(IModelProvider modelProvider,
            Shell shell, Restriction oldRestriction, Resource affectedClass,
            List<Restriction> relatedLocalRestrictions) {
        boolean performEdit = oldRestriction.isAnon();
        String title = "Edit Restriction";

        if (!oldRestriction.isAnon()) {
            String message = "The selected restriction is not anonymous, and thus may be used to restrict other classes. Do you wish to continue editing this restriction?";
            Map<String, String> options = Maps.newLinkedHashMap();
            options.put("change", "Change non-anonymous restriction anyway");
            options.put("noChange", "Do not change the non-anonymous restriction");
            options.put("duplicate",
                    "Duplicate the restriction into an anonymous restriction and edit");

            RadioSelectionInputDialog dialog = new RadioSelectionInputDialog(shell, title, message,
                    options);
            if (dialog.open() == Window.OK) {
                String selected = dialog.getSelected();
                if (selected.equals("change")) {
                    performEdit = true;
                }
                else if (selected.equals("duplicate")) {
                    // TODO: Duplicate the restriction
                    throw new NotImplementedException("The duplicate operation is not implemented");
                }
            }
        }
        if (performEdit) {
            editRestriction(modelProvider, shell, oldRestriction, affectedClass,
                    relatedLocalRestrictions);
        }
    }

    /**
     * @param onProperty
     */
    public final static void createRestriction(IModelProvider modelProvider, Shell shell,
            Property onProperty, OntResource affectedClass) {
        CreateRestrictionWizard wizard = new CreateRestrictionWizard("New Restriction",
                modelProvider, null);

        wizard.setAnonymousAllowed(true);
        wizard.setAllowOpenEditorOnFinish(false);
        wizard.setLabelProperty(RDFS.label);
        wizard.setOnProperty(onProperty);
        wizard.setAnonymous(true);
        wizard.setSuppressNotify(true);
        wizard.setValidator(new CustomRestrictionValidator(modelProvider, affectedClass));

        WizardDialog dialog = new WizardDialog(shell, wizard);
        dialog.create();
        dialog.showPage(wizard.getPage(CreateRestrictionWizard.RESTRICTIONS_PAGE_NAME));

        String transactionDescription = "Due to creation of one or more new restrictions";
        ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
        if (dialog.open() != Window.OK) {
            modelProvider.abortTransaction(transaction);
        }
        else {
            // Add subClassOf relations to the new restrictions
            ResourceStatements.createResourceAsSubclassStatements(affectedClass,
                    wizard.getRestrictions());

            // Commit changes to model
            modelProvider.commitTransaction(transaction);
        }
    }

    public static final void removeRestrictionAsSuperclass(IModelProvider modelProvider,
            Shell shell, Restriction restriction, Resource resource) {

        List<Resource> superClasses = Lists.newArrayList();
        superClasses.add(restriction.asResource());

        String transactionDescription = "Due to removal of restrictions";
        ModelTransaction transaction = modelProvider.createTransaction(transactionDescription);
        createRemoveResourceAsSubclassStatements(modelProvider.getOntModel(), resource,
                superClasses);
        modelProvider.commitTransaction(transaction);
    }

    private static void createRemoveResourceAsSubclassStatements(OntModel ontModel,
            Resource resource, List<Resource> superClasses) {
        if ((resource != null) && (superClasses != null)) {
            for (Resource superClass : superClasses) {
                ontModel.remove(resource, RDFS.subClassOf, superClass);
                if (superClass.isAnon()) {
                    superClass.removeProperties();
                }
            }
        }
    }

    // private boolean equal(RDFNode i1, RDFNode i2) {
    // if (i1 == null && i2 == null) {
    // return true;
    // }
    // if (i1 == null || i2 == null) {
    // return false;
    // }
    // return i1.equals(i2);
    // }

    private static class CustomRestrictionValidator extends BaseRestrictionValidator {

        private OntResource resource;
        private IModelProvider modelProvider;

        public CustomRestrictionValidator(IModelProvider modelProvider, OntResource resource) {
            this.resource = resource;
            this.modelProvider = modelProvider;
        }

        @Override
        public String isConsistent(Restriction restriction) {
            String errorMessage = isValid(restriction);
            if (errorMessage != null) {
                return errorMessage;
            }

            // / Check cardinality restrictions
            Property onProperty = restriction.getPropertyResourceValue(OWL.onProperty).as(
                    Property.class);

            for (ExtendedIterator<Restriction> iter = OntClassUtil.listRestrictions(
                    JenaUtil.asOntClass(resource), onProperty, false); iter.hasNext()
                    && errorMessage == null;) {
                Restriction existing = iter.next();

                if (existing.hasProperty(OWL.minCardinality)) {
                    Optional<Integer> min = getPropertyInteger(existing, OWL.minCardinality);
                    for (Property p : ImmutableSet.of(OWL.minCardinality, OWL.maxCardinality,
                            OWL.cardinality)) {
                        Optional<Integer> value = getPropertyInteger(restriction, p);
                        if (isSmaller(value, min)) {
                            errorMessage = String
                                    .format("Existing restriction(s) already require a minimum cardinality of %d",
                                            min.get().intValue());
                        }
                    }
                }
                else if (existing.hasProperty(OWL.maxCardinality)) {
                    Optional<Integer> max = getPropertyInteger(existing, OWL.maxCardinality);
                    for (Property p : ImmutableSet.of(OWL.minCardinality, OWL.maxCardinality,
                            OWL.cardinality)) {
                        Optional<Integer> value = getPropertyInteger(restriction, p);
                        if (isBigger(value, max)) {
                            errorMessage = String
                                    .format("Existing restriction(s) already require a maximum cardinality of %d",
                                            max.get().intValue());
                        }
                    }
                }
                else if (existing.hasProperty(OWL.cardinality)) {
                    Optional<Integer> cardinality = getPropertyInteger(existing, OWL.maxCardinality);
                    for (Property p : ImmutableSet.of(OWL.minCardinality, OWL.maxCardinality,
                            OWL.cardinality)) {
                        Optional<Integer> value = getPropertyInteger(restriction, p);
                        if (isBigger(value, cardinality) || isSmaller(value, cardinality)) {
                            errorMessage = String
                                    .format("Existing restriction(s) already requires an exact cardinality of %d",
                                            cardinality.get().intValue());
                        }
                    }
                }
            }

            // / Check QCR Restrictions
            if (restriction.hasProperty(OWL2.onClass)) {
                Resource onClass = restriction.getPropertyResourceValue(OWL2.onClass);

                for (ExtendedIterator<Restriction> iter = OntClassUtil.listRestrictions(
                        JenaUtil.asOntClass(resource), onProperty, false); iter.hasNext()
                        && errorMessage == null;) {
                    Restriction existing = iter.next();
                    // TODO: Also check if the onclass on existing and new are
                    // subclasses of each other; in a step-for-step fashion
                    if (existing.hasProperty(OWL2.onClass, onClass)
                            && existing.hasProperty(OWL2.minQualifiedCardinality)) {
                        Optional<Integer> min = getPropertyInteger(existing,
                                OWL2.minQualifiedCardinality);
                        for (Property p : ImmutableSet.of(OWL2.minQualifiedCardinality,
                                OWL2.maxQualifiedCardinality, OWL2.qualifiedCardinality)) {
                            Optional<Integer> value = getPropertyInteger(restriction, p);
                            if (isSmaller(value, min)) {
                                errorMessage = String
                                        .format("Existing restriction(s) require a minimum of %d %s instances",
                                                min.get().intValue(), modelProvider
                                                        .getLabelProvider().getText(onClass));
                            }
                        }
                    }
                    else if (existing.hasProperty(OWL2.onClass, onClass)
                            && existing.hasProperty(OWL2.maxQualifiedCardinality)) {
                        Optional<Integer> max = getPropertyInteger(existing, OWL.maxCardinality);
                        for (Property p : ImmutableSet.of(OWL2.minQualifiedCardinality,
                                OWL2.maxQualifiedCardinality, OWL2.qualifiedCardinality)) {
                            Optional<Integer> value = getPropertyInteger(restriction, p);
                            if (isBigger(value, max)) {
                                errorMessage = String
                                        .format("Existing restriction(s) require a maximum of %d %s instances",
                                                max.get().intValue(), modelProvider
                                                        .getLabelProvider().getText(onClass));
                            }
                        }
                    }
                    else if (existing.hasProperty(OWL2.onClass, onClass)
                            && existing.hasProperty(OWL2.qualifiedCardinality)) {
                        Optional<Integer> cardinality = getPropertyInteger(existing,
                                OWL.maxCardinality);
                        for (Property p : ImmutableSet.of(OWL2.minQualifiedCardinality,
                                OWL2.maxQualifiedCardinality, OWL2.qualifiedCardinality)) {
                            Optional<Integer> value = getPropertyInteger(restriction, p);
                            if (isBigger(value, cardinality) || isSmaller(value, cardinality)) {
                                errorMessage = String.format(
                                        "Existing restriction(s) require exactly %d %s instances",
                                        cardinality.get().intValue(), modelProvider
                                                .getLabelProvider().getText(onClass));
                            }
                        }
                    }
                    else if (existing.hasProperty(OWL.minCardinality)) {
                        Optional<Integer> min = getPropertyInteger(existing, OWL.minCardinality);
                        for (Property p : ImmutableSet.of(OWL.minCardinality, OWL.maxCardinality,
                                OWL.cardinality)) {
                            Optional<Integer> value = getPropertyInteger(restriction, p);
                            if (isSmaller(value, min)) {
                                errorMessage = String
                                        .format("Existing restriction(s) already require a minimum cardinality of %d",
                                                min.get().intValue());
                            }
                        }
                    }
                    else if (existing.hasProperty(OWL.maxCardinality)) {
                        Optional<Integer> max = getPropertyInteger(existing, OWL.maxCardinality);
                        for (Property p : ImmutableSet.of(OWL.minCardinality, OWL.maxCardinality,
                                OWL.cardinality)) {
                            Optional<Integer> value = getPropertyInteger(restriction, p);
                            if (isBigger(value, max)) {
                                errorMessage = String
                                        .format("Existing restriction(s) already require a maximum cardinality of %d",
                                                max.get().intValue());
                            }
                        }
                    }
                    else if (existing.hasProperty(OWL.cardinality)) {
                        Optional<Integer> cardinality = getPropertyInteger(existing,
                                OWL.maxCardinality);
                        for (Property p : ImmutableSet.of(OWL.minCardinality, OWL.maxCardinality,
                                OWL.cardinality)) {
                            Optional<Integer> value = getPropertyInteger(restriction, p);
                            if (isBigger(value, cardinality) || isSmaller(value, cardinality)) {
                                errorMessage = String
                                        .format("Existing restriction(s) already requires an exact cardinality of %d",
                                                cardinality.get().intValue());
                            }
                        }
                    }
                }
            }
            return errorMessage;
        }

        private boolean isSmaller(Optional<Integer> a, Optional<Integer> b) {
            if (!a.isPresent()) {
                return false;
            }
            if (!b.isPresent()) {
                return false;
            }
            return (a.get().intValue() < b.get().intValue());
        }

        private boolean isBigger(Optional<Integer> a, Optional<Integer> b) {
            if (!a.isPresent()) {
                return false;
            }
            if (!b.isPresent()) {
                return false;
            }
            return (a.get().intValue() > b.get().intValue());
        }

        private Optional<Integer> getPropertyInteger(Restriction restriction, Property property) {
            Optional<Integer> integer = Optional.absent();
            if (restriction.hasProperty(property)) {
                Literal literal = restriction.getPropertyValue(property).asLiteral();
                if (literal.getDatatypeURI() == null
                        || literal.getDatatypeURI().equals(XSD.integer.getURI())
                        || literal.getDatatypeURI().equals(XSD.xint.getURI())) {
                    integer = Optional.fromNullable(new Integer(literal.getInt()));
                }
            }
            return integer;
        }
    }
}
