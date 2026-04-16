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

package com.semmtech.plugin.semmweb.validation.handlers;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Var;
import com.semmtech.plugin.semmweb.validation.jobs.RunResourcesValidationJob;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;


/**
 * 
 * @author Sander Stolk
 */
public class RunClassInstancesValidationHandler extends AbstractValidationHandler {
    private static Logger logger = Logger.getLogger(RunClassInstancesValidationHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.validation.commands.runClassInstancesValidation";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        super.execute(event);

        if (file != null && provider != null && activeResource != null) {
            executeJobWithPreferredInference(new JobCreator() {
                @Override
                public Job createJob() {
                    Job job = new RunClassInstancesValidationJob();
                    job.setSystem(true);
                    return job;
                }
            });
        }
        return null;
    }

    protected class RunClassInstancesValidationJob extends Job {
        public final static String NAME = "Run class instances validation";

        public RunClassInstancesValidationJob() {
            super(NAME);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            // get all instances of the current class resource
            List<Resource> instances = getInstances(activeResource, provider.getOntModel());

            // run resources validation job
            RunResourcesValidationJob job = new RunResourcesValidationJob(file, provider, instances);
            job.setUser(true);
            job.schedule();
            return Status.OK_STATUS;
        }

    }

    protected static List<Resource> getInstances(Resource resource, Model model) {
        List<Resource> result = Lists.newArrayList();

        Var varInstance = Var.alloc("instance");
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(varInstance, PathUtil.isInstanceOf, resource);
        ResultSet iter = qb.execSelect(model);
        while (iter.hasNext()) {
            QuerySolution qs = iter.next();
            RDFNode node = qs.get(varInstance.getName());
            if (node.isResource()) {
                result.add(node.asResource());
            }
        }

        return result;
    }
}
