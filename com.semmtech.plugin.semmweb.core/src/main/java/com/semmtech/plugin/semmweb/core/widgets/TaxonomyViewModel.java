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

package com.semmtech.plugin.semmweb.core.widgets;


import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.Root;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.image;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.isChildOf;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.text;
import static com.semmtech.plugin.semmweb.core.widgets.TaxonomyViewModel.TaxonomyVocabulary.TAXONOMY_MODEL_URI;
import static com.semmtech.plugin.semmweb.core.widgets.TaxonomyViewModel.TaxonomyVocabulary.directInstanceCount;
import static com.semmtech.plugin.semmweb.core.widgets.TaxonomyViewModel.TaxonomyVocabulary.indirectInstanceCount;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Coalesce;
import com.hp.hpl.jena.sparql.expr.E_Subtract;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueInteger;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceViewModelEvent;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceViewModelListener;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;


/**
 * View Model for the TaxonomyView. Provides the async instance count
 * functionality
 * 
 * @author Mike Henrics
 * @author Sander Stolk
 * 
 */
public class TaxonomyViewModel extends AbstractResourceViewModel {

    public static class TaxonomyVocabulary extends Vocabulary {
        public static final String TAXONOMY_MODEL_URI = "urn:taxonomyViewModel";

        public static final Property directInstanceCount = property("directInstanceCount");
        public static final Property indirectInstanceCount = property("indirectInstanceCount");
    }

    public static final String INCLUDE_CHILDLESS_ROOTS = "includeChildlessRoot";
    public static final String COUNT_INSTANCES = "countInstances";

    public static final String INSTANCE_COUNT_CHANGED_EVENT = "instanceCountChangedEvent";

    private final Resource taxonomyRoot;
    private Model instanceCountModel;

    public TaxonomyViewModel(OntModel currentModel, Resource taxonomyRoot) {
        super(ModelFactory.createDefaultModel(), currentModel);
        this.taxonomyRoot = taxonomyRoot;
    }

    @Override
    public void init() {
        super.init();

        if (getCountInstances()) {
            startAsyncInstanceCount();
        }
    }

    public int getDirectInstanceCount(OntResource clazz) {
        if (instanceCountModel != null) {
            Resource resource = instanceCountModel.getResource(clazz.getURI());
            if (resource != null) {
                Statement statement = resource.getProperty(directInstanceCount);
                if (statement != null) {
                    return statement.getObject().asLiteral().getInt();
                }
            }
        }
        return 0;
    }

    public int getIndirectInstanceCount(OntResource clazz) {
        if (instanceCountModel != null) {
            Resource resource = instanceCountModel.getResource(clazz.getURI());
            if (resource != null) {
                Statement statement = resource.getProperty(indirectInstanceCount);
                if (statement != null) {
                    return statement.getObject().asLiteral().getInt();
                }
            }
        }
        return 0;
    }

    public void setCountInstances(boolean count) {
        addSetting(COUNT_INSTANCES, count);
    }

    public boolean getCountInstances() {
        Object res = getSetting(COUNT_INSTANCES);

        if (res instanceof Boolean) {
            return (Boolean) res;
        }

        return true;
    }

    @Override
    public void close() {
        super.close();

        if (instanceCountModel != null) {
            instanceCountModel.close();
            instanceCountModel = null;
        }

    }

    /**
     * Sets the instanceCountModel and notify the listeners.
     * 
     * @param instanceCountModel
     */
    private void setInstanceCountModel(Model instanceCountModel) {
        this.instanceCountModel = instanceCountModel;

        ResourceViewModelEvent event = new ResourceViewModelEvent(INSTANCE_COUNT_CHANGED_EVENT);

        for (ResourceViewModelListener listener : listeners) {
            listener.notifyChange(event);
        }
    }

    public void startAsyncInstanceCount() {
        InstanceCountLoader job = new InstanceCountLoader(this);
        job.setPriority(Job.DECORATE);
        job.schedule();
    }

    @Override
    public Query buildQuery() {
        Var varSuperClass = Var.alloc("superClass");
        Var varRootClassText = Var.alloc("rootClassText");
        Var varRootClassImage = Var.alloc("rootClassImage");

        Var varClass = Var.alloc("class");
        Var varClassText = Var.alloc("classText");
        Var varClassImage = Var.alloc("classImage");

        QueryBuilder qb = QueryBuilder.createConstruct();

        qb.addTriplePattern(varClass, PathUtil.subClassOfAny, taxonomyRoot);
        qb.addTriplePattern(varClass, RDFS.subClassOf, varSuperClass);
        qb.addResultVar(varSuperClass);

        // Retrieve the text and image for the class
        qb.addTriplePatterns(Triples.create(varClass, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varClassText.asNode(), varClassImage.asNode())));

        qb.addTriplePatterns(Triples.create(taxonomyRoot, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varRootClassText.asNode(), varRootClassImage.asNode())));

        // Set the Construct pattern
        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(taxonomyRoot, RDF.type, Root.asNode()));
        bgp.add(Triples.create(taxonomyRoot, text, varRootClassText));
        bgp.add(Triples.create(taxonomyRoot, image, varRootClassImage));
        bgp.add(Triples.create(varClass, text, varClassText));
        bgp.add(Triples.create(varClass, image, varClassImage));
        bgp.add(Triples.create(varClass, isChildOf, varSuperClass));

        Template constructTemplate = new Template(bgp);
        qb.setConstructTemplate(constructTemplate);

        return qb.getQuery();
    }

    public static Query buildCountInstancesQuery() {
        QueryBuilder qb = QueryBuilder.createConstruct();

        Var varClass = Var.alloc("class");
        Var varText = Var.alloc("text");
        Var varInstance = Var.alloc("instance");
        Var varInstanceCountAgg = Var.alloc("instanceCountAgg");
        Var varInstanceCount = Var.alloc("instanceCount");
        Var varDirectInstanceCountAgg = Var.alloc("directInstanceCountAgg");
        Var varDirectInstanceCount = Var.alloc("directInstanceCount");
        Var varIndirectInstanceCount = Var.alloc("indirectInstanceCount");

        QueryBuilder optqb = QueryBuilder.createSelect(true);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(Triples.create(varClass, text, varText));

        ElementNamedGraph graphElement = new ElementNamedGraph(
                NodeFactory.createURI(TAXONOMY_MODEL_URI), eg);
        qb.addPattern(graphElement);

        optqb.addTriplePattern(varInstance, PathUtil.isInstanceOf, varClass);
        optqb.addFilterIsURI(varInstance);
        optqb.addGroupBy(varClass);
        optqb.setResultCountVar(varInstance, varInstanceCountAgg, true);
        optqb.addResultVar(varClass);
        ElementSubQuery subQuery = new ElementSubQuery(optqb.getQuery());
        ElementOptional optional = new ElementOptional(subQuery);
        qb.addPattern(optional);

        // Coalesce the instance count with 0 (to ensure it'll always be set)
        List<Expr> coalesceList = Lists.newArrayList();
        coalesceList.add(new ExprVar(varInstanceCountAgg));
        coalesceList.add(new NodeValueInteger(0));
        qb.addPattern(new ElementBind(varInstanceCount, new E_Coalesce(new ExprList(coalesceList))));

        // Retrieve the direct instance count
        optqb = QueryBuilder.createSelect(true);
        optqb.addTriplePattern(varInstance, RDF.type, varClass);
        optqb.addFilterIsURI(varInstance);
        optqb.addGroupBy(varClass);
        optqb.setResultCountVar(varInstance, varDirectInstanceCountAgg, true);
        optqb.addResultVar(varClass);
        subQuery = new ElementSubQuery(optqb.getQuery());
        optional = new ElementOptional(subQuery);
        qb.addPattern(optional);

        // Coalesce the instance count with 0 (to ensure it'll always be set)
        coalesceList = Lists.newArrayList();
        coalesceList.add(new ExprVar(varDirectInstanceCountAgg));
        coalesceList.add(new NodeValueInteger(0));
        qb.addPattern(new ElementBind(varDirectInstanceCount, new E_Coalesce(new ExprList(
                coalesceList))));

        // Retrieve the indirect instance count by subtracting the direct one
        // from the total
        qb.addPattern(new ElementBind(varIndirectInstanceCount, new E_Subtract(new ExprVar(
                varInstanceCount), new ExprVar(varDirectInstanceCount))));
        //
        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(varClass, directInstanceCount, varDirectInstanceCount));
        bgp.add(Triples.create(varClass, indirectInstanceCount, varIndirectInstanceCount));
        Template constructTemplate = new Template(bgp);
        qb.setConstructTemplate(constructTemplate);

        return qb.getQuery();
    }

    /**
     * Job that load asynchronously the instanceCount.
     * 
     * @author Simone
     */
    private static class InstanceCountLoader extends Job {

        private static Logger logger = Logger
                .getLogger(TaxonomyViewModel.InstanceCountLoader.class);

        private TaxonomyViewModel taxonomyViewModel;

        public InstanceCountLoader(TaxonomyViewModel taxonomyViewModel) {
            super("Count Instances");
            this.taxonomyViewModel = taxonomyViewModel;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            Model viewModel = taxonomyViewModel.getViewModel();
            Model currentModel = taxonomyViewModel.getCurrentModel();

            // make sure that for each model in the editor the instances is
            // counted only once, otherwise a ConcurrentModificationException
            // could be thrown
            synchronized (currentModel) {
                // It could happens that the model is being closed during this
                // method execution, so this try-catch block is needed
                try {
                    Dataset dataset = DatasetFactory.create(currentModel);
                    dataset.addNamedModel(TAXONOMY_MODEL_URI, viewModel);

                    Query countQuery = TaxonomyViewModel.buildCountInstancesQuery();
                    QueryExecution exec = QueryExecutionFactory.create(countQuery, dataset);

                    Model instanceCountModel = exec.execConstruct();
                    taxonomyViewModel.setInstanceCountModel(instanceCountModel);
                }
                catch (Exception ex) {
                    logger.warn("Error while count instances: " + ex.getMessage(), ex);
                    return Status.CANCEL_STATUS;
                }
            }
            return Status.OK_STATUS;
        }
    }

}
