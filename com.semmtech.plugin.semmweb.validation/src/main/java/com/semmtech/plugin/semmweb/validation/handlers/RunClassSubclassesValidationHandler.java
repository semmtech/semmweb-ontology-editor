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
public class RunClassSubclassesValidationHandler extends AbstractValidationHandler {
    private static Logger logger = Logger.getLogger(RunClassSubclassesValidationHandler.class);
    public static final String ID = "com.semmtech.plugin.semmweb.validation.commands.runClassSubclassesValidation";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        logger.debug("execute called!");
        super.execute(event);

        if (file != null && provider != null && activeResource != null) {
            executeJobWithPreferredInference(new JobCreator() {
                @Override
                public Job createJob() {
                    Job job = new RunClassSubclassesValidationJob();
                    job.setSystem(true);
                    return job;
                }
            });
        }
        return null;
    }

    protected class RunClassSubclassesValidationJob extends Job {
        public final static String NAME = "Run class subclasses validation";

        public RunClassSubclassesValidationJob() {
            super(NAME);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            // get all instances of the current class resource
            List<Resource> subclasses = getSubclasses(activeResource, provider.getOntModel());

            // run resources validation job
            RunResourcesValidationJob job = new RunResourcesValidationJob(file, provider,
                    subclasses);
            job.setUser(true);
            job.schedule();
            return Status.OK_STATUS;
        }

    }

    protected static List<Resource> getSubclasses(Resource resource, Model model) {
        List<Resource> result = Lists.newArrayList();

        Var varSubclass = Var.alloc("subclass");
        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(varSubclass, PathUtil.subClassOfAny, resource);
        ResultSet iter = qb.execSelect(model);
        while (iter.hasNext()) {
            QuerySolution qs = iter.next();
            RDFNode node = qs.get(varSubclass.getName());
            if (node.isResource()) {
                result.add(node.asResource());
            }
        }

        return result;
    }
}
