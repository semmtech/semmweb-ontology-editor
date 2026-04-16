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

package com.semmtech.plugin.semmweb.validation.jobs;


import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.topbraid.spin.constraints.ConstraintViolation;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.forms.editor.OntologyFormEditor;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.util.ResourcesUtil;
import com.semmtech.plugin.semmweb.validation.markers.Markers;
import com.semmtech.plugin.semmweb.validation.models.ConstraintsModelFactory;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.spin.constraints.SPINConstraints;
import com.semmtech.spin.inference.SPINInferences;
import com.semmtech.ui.plugin.jobs.JobWithMonitor;
import com.semmtech.ui.plugin.jobs.Jobs;
import com.semmtech.ui.plugin.jobs.ResultJob;


/**
 * 
 * @author Sander Stolk
 */
public class RunResourcesValidationJob extends JobWithMonitor {

    private static Logger logger = Logger.getLogger(RunResourcesValidationJob.class);

    private static final String PROBLEMS_VIEW_ID = "org.eclipse.ui.views.ProblemView";
    private static final String SEMMOWL_BASE = "http://www.semmtech.com/spin/semmowl#"; // TODO:
                                                                                        // move
                                                                                        // to
                                                                                        // a
                                                                                        // vocabulary
                                                                                        // class?
    private static final String SPIN_MODELS_PROJECT_FOLDER = "spin";

    protected final IResource file;
    protected final IModelProvider provider;
    protected final List<Resource> resources;

    public RunResourcesValidationJob(IResource file, IModelProvider provider, Resource resource) {
        this(file, provider, Lists.newArrayList(resource));
    }

    public RunResourcesValidationJob(IResource file, IModelProvider provider,
            List<Resource> resources) {
        super("Resource Validation");
        this.file = file;
        this.provider = provider;
        this.resources = Lists.newArrayList();

        if (resources != null) {
            for (Resource resource : resources) {
                if (resource != null) {
                    this.resources.add(resource.inModel(provider.getOntModel()));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        if (resources == null || resources.isEmpty()) {
            return Status.OK_STATUS;
        }

        // TODO: Select desired SPIN constraints instead of all known

        // TODO: Separate the constraint models into different sets.
        // Currently the CreateConstraintsModelJob forms too great a
        // bottleneck to do so, however.
        String[][] constraintsModelSets = new String[][] { ConstraintsModelFactory.DEFAULT_MODEL_SET };

        String monitorText = String.format("Running validation on the %s.",
                (resources.size() == 1 ? "resource" : "resources"));
        startMonitorUpdate(monitor, monitorText, 3 * constraintsModelSets.length);

        long startTime = System.currentTimeMillis();

        for (String[] constraintsModelSet : constraintsModelSets) {
            updateSubTask("Validating using checks from the constraints model set:\n"
                    + Arrays.toString(constraintsModelSet));

            // Create a constraints model
            // - step 1. check for additional spin model files in the project
            List<IFile> additionalModels = Lists.newArrayList();
            IProject project = file.getProject();
            if (project != null) {
                IFolder folder = project.getFolder(SPIN_MODELS_PROJECT_FOLDER);

                if (folder.exists()) {
                    try {
                        for (IResource member : folder.members()) {
                            if (member instanceof IFile
                                    && ResourcesUtil.supportedByEditor(member,
                                            OntologyFormEditor.ID)) {
                                additionalModels.add((IFile) member);
                            }
                        }
                    }
                    catch (CoreException e) {
                        logger.error("Error while searching spin models in the project " + project,
                                e);
                    }
                }
            }
            // - step 2. create the constraints model
            CreateConstraintsModelJob constrJob = new CreateConstraintsModelJob(
                    constraintsModelSet,
                    additionalModels.toArray(new IFile[additionalModels.size()]));
            constrJob.setSystem(true);
            final OntModel constraintsModel = (OntModel) Jobs.getResult(constrJob, monitor);
            if (constraintsModel == null) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }
            addWorked(1);

            logger.debug("SPIN constraints model has been created.");

            // TODO: Currently the inference rules are contained in the
            // constraints models. These rules should be moved to their own
            // models and put in a separate inference project
            // (com.semmtech.spin.inference?). This particular task of running
            // selective inference can then be moved up, turning to constraints
            // models only afterwards.
            final OntModel rulesModel = constraintsModel;
            RunSelectiveInferenceJob infJob = new RunSelectiveInferenceJob(resources, rulesModel);
            infJob.setSystem(true);
            final OntModel inferredModel = (OntModel) Jobs.getResult(infJob, monitor);
            if (inferredModel == null) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }
            addWorked(1);

            logger.debug("SPIN selective inference has been run.");

            ConstraintsValidationJob valJob = new ConstraintsValidationJob(resources,
                    inferredModel, constraintsModel);
            valJob.setSystem(true);
            final List<ConstraintViolation> violations = (List<ConstraintViolation>) Jobs
                    .getResult(valJob, monitor);
            if (violations == null) {
                monitor.done();
                stopMonitorUpdate();
                return Status.CANCEL_STATUS;
            }

            logger.debug("SPIN constraints have been checked for violations.");

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    Markers.generate(file, violations);

                    // Open the Problems view
                    try {
                        CorePlugin.getActivePage().showView(PROBLEMS_VIEW_ID);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            logger.debug("Markers have been created to display the constraint violations.");

            addWorked(1);
        }

        long endTime = System.currentTimeMillis();
        logger.debug(String.format("Validating the resources took %d milliseconds.", endTime
                - startTime));

        monitor.done();
        stopMonitorUpdate();
        return Status.OK_STATUS;
    }

    private class CreateConstraintsModelJob extends ResultJob {
        protected final String[] constraintsModelSet;
        protected final IFile[] additionalModels;

        public CreateConstraintsModelJob(String[] constraintsModelSet, IFile[] additionalModels) {
            super("Create Constraints Model");
            this.constraintsModelSet = constraintsModelSet;
            this.additionalModels = additionalModels;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            OntModel constraintsModel = ConstraintsModelFactory.create(constraintsModelSet,
                    additionalModels);
            returnResult(constraintsModel);
            return Status.OK_STATUS;
        }
    }

    private class RunSelectiveInferenceJob extends ResultJob {
        protected final List<Resource> resources;
        protected final Model rulesModel;

        public RunSelectiveInferenceJob(List<Resource> resources, Model rulesModel) {
            super("Run Selective Inference");
            this.resources = resources;
            this.rulesModel = rulesModel;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            OntModel inferredModel = ModelFactory.createOntologyModel(OntModelSpec.RDFS_MEM);
            if (resources != null && !resources.isEmpty()) {
                Model queryModel = resources.get(0).getModel();
                Model unionModel = ModelFactory.createUnion(queryModel, rulesModel);

                for (Resource resource : resources) {
                    // ensuring validation of individuals is performed
                    List<Resource> owlClassTypes = findClassTypes(resource, OWL.Class, queryModel);
                    for (Resource owlClassType : owlClassTypes) {
                        String queryURI = SEMMOWL_BASE + "SubclassOfThingRule";
                        SPINInferences.runQueryOnInstance(queryURI, unionModel, inferredModel,
                                owlClassType, false);
                    }

                    // ensuring validation of restrictions can cope with oneOf
                    List<Resource> qualifiedClasses = findRestrictionQualifiedClasses(resource,
                            queryModel);
                    for (Resource qualifiedClass : qualifiedClasses) {
                        String queryURI = SEMMOWL_BASE + "OneOfRule";
                        SPINInferences.runQueryOnInstance(queryURI, unionModel, inferredModel,
                                qualifiedClass, false);
                    }
                }
            }
            returnResult(inferredModel);
            return Status.OK_STATUS;
        }

        protected List<Resource> findClassTypes(Resource resource, Resource classType,
                Model queryModel) {
            List<Resource> result = Lists.newArrayList();

            Var varClass = Var.alloc("class");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(resource, PathUtil.isInstanceOf, varClass);
            qb.addTriplePattern(varClass, PathUtil.isInstanceOf, classType);
            qb.addResultVar(varClass);
            ResultSet iter = qb.execSelect(queryModel);
            while (iter.hasNext()) {
                try {
                    Resource clazz = iter.next().getResource(varClass.getName());
                    result.add(clazz);
                }
                catch (Exception e) {
                    // result contains not a resource but a literal; ignore.
                }
            }
            return result;
        }

        protected List<Resource> findRestrictionQualifiedClasses(Resource resource, Model queryModel) {
            List<Resource> result = Lists.newArrayList();

            Var varRestriction = Var.alloc("restriction");
            Var varQualifiedClass = Var.alloc("qualifiedClass");

            QueryBuilder qb = QueryBuilder.createSelect(true);

            // qb.addTriplePattern(resource, PathUtil.isInstanceOf,
            // varRestriction);
            ElementUnion union = new ElementUnion();
            ElementGroup eg = new ElementGroup();
            eg.addTriplePattern(Triples.create(resource, PathUtil.isInstanceOf, varRestriction));
            union.addElement(eg);
            eg = new ElementGroup();
            eg.addTriplePattern(Triples.create(resource, PathUtil.subClassOfAny, varRestriction));
            union.addElement(eg);
            qb.addPattern(union);

            qb.addTriplePattern(varRestriction, RDF.type, OWL.Restriction);

            union = new ElementUnion();
            eg = new ElementGroup();
            eg.addTriplePattern(Triples.create(varRestriction, OWL2.onClass, varQualifiedClass));
            union.addElement(eg);
            eg = new ElementGroup();
            eg.addTriplePattern(Triples
                    .create(varRestriction, OWL.allValuesFrom, varQualifiedClass));
            union.addElement(eg);
            eg = new ElementGroup();
            eg.addTriplePattern(Triples.create(varRestriction, OWL.someValuesFrom,
                    varQualifiedClass));
            union.addElement(eg);

            qb.addPattern(union);
            qb.addResultVar(varQualifiedClass);
            ResultSet iter = qb.execSelect(queryModel);
            while (iter.hasNext()) {
                try {
                    Resource qualifiedClass = iter.next().getResource(varQualifiedClass.getName());
                    result.add(qualifiedClass);
                }
                catch (Exception e) {
                    // result contains not a resource but a literal; ignore.
                }
            }
            return result;
        }
    }

    private class ConstraintsValidationJob extends ResultJob {
        protected final List<Resource> resources;
        protected final Model constraintsModel;
        protected final Model inferredModel;

        public ConstraintsValidationJob(List<Resource> resources, Model inferredModel,
                Model constraintsModel) {
            super("Validation");
            this.resources = resources;
            this.constraintsModel = constraintsModel;
            this.inferredModel = inferredModel;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            List<ConstraintViolation> violations = Lists.newArrayList();
            if (resources != null && !resources.isEmpty()) {
                List<Resource> checkResources = resources;

                if (inferredModel != null) {
                    // Ensure the checkResources use a unified model containing
                    // their normal model and the inferredModel.
                    List<Resource> unionResources = Lists.newArrayList();
                    Model unionModel = ModelFactory.createUnion(resources.get(0).getModel(),
                            inferredModel);
                    for (Resource resource : resources) {
                        Resource unionResource = resource.inModel(unionModel);
                        unionResources.add(unionResource);
                    }
                    checkResources = unionResources;
                }

                for (Resource resource : checkResources) {
                    List<ConstraintViolation> resourceViolations = SPINConstraints.check(resource,
                            constraintsModel, null);
                    if (resourceViolations != null) {
                        violations.addAll(resourceViolations);
                    }
                }
            }
            returnResult(violations);
            return Status.OK_STATUS;
        }
    }

}
