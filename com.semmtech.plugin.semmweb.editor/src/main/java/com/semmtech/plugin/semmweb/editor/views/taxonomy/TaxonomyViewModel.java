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

package com.semmtech.plugin.semmweb.editor.views.taxonomy;


import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_Coalesce;
import com.hp.hpl.jena.sparql.expr.E_IsURI;
import com.hp.hpl.jena.sparql.expr.E_NotExists;
import com.hp.hpl.jena.sparql.expr.E_StrLowerCase;
import com.hp.hpl.jena.sparql.expr.E_Subtract;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueInteger;
import com.hp.hpl.jena.sparql.expr.nodevalue.NodeValueNode;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.semantics.util.JenaUtil;


public class TaxonomyViewModel extends ModelCom {

    public static final String FILTER_MODEL_URI = "urn:filterModel";
    public static final String TAXONOMY_MODEL_URI = "urn:taxonomyViewModel";

    private Model viewModel;
    private Model instanceCountModel;
    private OntModel currentModel;

    private List<TaxonomyViewModelListener> listeners;

    public static final class Vocabulary {
        public static final String NS = "urn:abstractTaxonomyView#";

        protected static final Resource resource(String local) {
            return ResourceFactory.createResource(NS + local);
        }

        protected static final Property property(String local) {
            return ResourceFactory.createProperty(NS + local);
        }

        public static final Property isChildOf = property("isChildOf");
        public static final Property directInstanceCount = property("directInstanceCount");
        public static final Property indirectInstanceCount = property("indirectInstanceCount");
        public static final Property text = property("text");
        public static final Property image = property("image");
        public static final Resource Root = resource("Root");
    }

    public TaxonomyViewModel(Model viewModel, OntModel currentModel) {
        super(viewModel.getGraph());
        this.viewModel = viewModel;
        this.currentModel = currentModel;
        listeners = Lists.newArrayList();
    }

    public int getDirectInstanceCount(OntClass clazz) {
        if (instanceCountModel != null) {
            Resource resource = instanceCountModel.getResource(clazz.getURI());
            if (resource != null) {
                Statement statement = resource.getProperty(Vocabulary.directInstanceCount);
                if (statement != null) {
                    return statement.getObject().asLiteral().getInt();
                }
            }
        }
        return 0;
    }

    public int getIndirectInstanceCount(OntClass clazz) {
        if (instanceCountModel != null) {
            Resource resource = instanceCountModel.getResource(clazz.getURI());
            if (resource != null) {
                Statement statement = resource.getProperty(Vocabulary.indirectInstanceCount);
                if (statement != null) {
                    return statement.getObject().asLiteral().getInt();
                }
            }
        }
        return 0;
    }

    public int getRootClassCount(boolean childrenRequired) {
        if (viewModel != null) {
            Var varClass = Var.alloc("class");
            Var varChild = Var.alloc("child");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varClass, RDF.type, Vocabulary.Root);
            if (childrenRequired) {
                qb.addTriplePattern(varChild, Vocabulary.isChildOf, varClass);
            }
            qb.setResultCountVar(varClass, true);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public OntClass getRootClass(boolean childrenRequired, int index) {
        if ((currentModel != null) && (viewModel != null) && (index >= 0)) {
            Var varClass = Var.alloc("class");
            Var varChild = Var.alloc("child");
            Var varText = Var.alloc("text");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varClass, RDF.type, Vocabulary.Root);
            if (childrenRequired) {
                qb.addTriplePattern(varChild, Vocabulary.isChildOf, varClass);
            }
            qb.addTriplePattern(varClass, Vocabulary.text, varText);
            qb.addResultVar(varClass);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varClass.getName());
                OntClass clazz = JenaUtil.asOntClass(resource, currentModel);
                return clazz;
            }
        }
        return null;
    }

    public List<OntClass> getRootClasses(boolean childrenRequired) {
        List<OntClass> result = Lists.newArrayList();
        if ((currentModel != null) && (viewModel != null)) {
            Var varClass = Var.alloc("class");
            Var varChild = Var.alloc("child");
            Var varText = Var.alloc("text");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varClass, RDF.type, Vocabulary.Root);
            if (childrenRequired) {
                qb.addTriplePattern(varChild, Vocabulary.isChildOf, varClass);
            }
            qb.addTriplePattern(varClass, Vocabulary.text, varText);
            qb.addResultVar(varClass);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varClass.getName());
                OntClass clazz = JenaUtil.asOntClass(resource, currentModel);
                result.add(clazz);
            }
        }
        return result;
    }

    public int getChildClassCount(OntClass clazz) {
        if ((viewModel != null) && (clazz != null)) {
            Var varChildClass = Var.alloc("childClass");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varChildClass, Vocabulary.isChildOf, clazz);
            qb.setResultCountVar(varChildClass);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    public OntClass getChildClass(OntClass clazz, int index) {
        if ((currentModel != null) && (viewModel != null) && (clazz != null) && (index >= 0)) {
            Var varChildClass = Var.alloc("childClass");
            Var varText = Var.alloc("text");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varChildClass, Vocabulary.isChildOf, clazz);
            qb.addTriplePattern(varChildClass, Vocabulary.text, varText);
            qb.addResultVar(varChildClass);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varChildClass.getName());
                OntClass childClass = JenaUtil.asOntClass(resource, currentModel);
                return childClass;
            }
        }
        return null;
    }

    public String getText(Resource resource) {
        if ((viewModel != null) && (resource != null)) {
            Resource viewModelResource = resource.isURIResource() ? viewModel
                    .createResource(resource.getURI()) : viewModel.createResource(resource.getId());
            Statement statement = viewModelResource.getProperty(Vocabulary.text);
            if (statement != null) {
                return statement.getObject().asLiteral().getString();
            }
        }
        return null;
    }

    public DeltaTaxonomyViewModel difference(TaxonomyViewModel viewModel) {
        return new DeltaTaxonomyViewModel(difference((Model) viewModel), currentModel);
    }

    public void addViewModelChangeListener(TaxonomyViewModelListener list) {
        listeners.add(list);
    }

    private void setInstanceCountModel(Model instanceCountModel) {
        this.instanceCountModel = instanceCountModel;

        TaxonomyViewModelEvent event = new TaxonomyViewModelEvent(
                TaxonomyViewModelEvent.INSTANCE_COUNT_CHANGED);

        for (TaxonomyViewModelListener listener : listeners) {
            listener.notifyChange(event);
        }
    }

    private Model getViewModel() {
        return viewModel;
    }

    private OntModel getCurrentModel() {
        return currentModel;
    }

    /**
     * Creates a new TaxonomyViewModel and returns that instance.
     * 
     * <pre>
     * <code>
     *     CONSTRUCT {
     *         ?class rdf:type ?type .
     *         ?class <urn:view#text> ?classText .
     *         ?class <urn:view#image> ?classImage .
     *         ?class <urn:view#isChildOf> ?superClass .
     *         ?class <urn:view#directInstanceCount> ?directInstanceCount .
     *         ?class <urn:view#indirectInstanceCount> ?indirectInstanceCount .
     *     }
     *     WHERE {
     *       {
     *         { 
     *           { 
     *             { 
     *               { 
     *                 { 
     *                   { 
     *                     {
     *                       {
     *                         SELECT DISTINCT  ?class (<urn:abstractTaxonomyView#Root> AS ?type)
     *                         WHERE
     *                           { { { { 
     *                                 ?class  <isInstanceOf>  <taxonomyResourceType> . }
     *                                 GRAPH <urn:filterModel> {
     *                                     { ?subClass  rdfs:subClassOf  ?class . }
     *                                     UNION
     *                                     { ?class  rdf:type  ?classType .
     *                                       FILTER NOT EXISTS {?subClass  rdfs:subClassOf  ?class . }
     *                                     }
     *                               } }
     *                               FILTER NOT EXISTS {
     *                                 GRAPH <urn:filterModel> {
     *                                       ?class  rdfs:subClassOf  ?rootSuperClass .
     *                                       FILTER isURI(?rootSuperClass)
     *                                 }
     *                                 ?rootSuperClass  <isInstanceOf>  <taxonomyResourceType>
     *                               }
     *                               
     *                             }
     *                           }
     *                       }
     *                       UNION
     *                       {
     *                         SELECT DISTINCT  ?class ?superClass
     *                         WHERE {
     *                           ?class  <isInstanceOf>  <taxonomyResourceType> . 
     *                           GRAPH <urn:filterModel> {
     *                               ?class  rds:subClassOf  ?superClass .
     *                               FILTER isURI(?superClass)
     *                           }
     *                         }
     *                       }
     *                     }
     *                     OPTIONAL { 
     *                       SELECT DISTINCT  (count(distinct ?instance) AS ?instanceCountAgg) ?class
     *                       WHERE {
     *                         ?instance  <isInstanceOf>  ?class .
     *                         FILTER isURI(?instance)
     *                       }
     *                       GROUP BY ?class
     *                     }
     *                   }
     *                   BIND(coalesce(?instanceCountAgg, 0) AS ?instanceCount)
     *                 }
     *                 OPTIONAL {
     *                   SELECT DISTINCT  (count(distinct ?instance) AS ?directInstanceCountAgg) ?class
     *                   WHERE {
     *                     ?instance  rdf:type  ?class . 
     *                     FILTER isURI(?instance)
     *                   }
     *                   GROUP BY ?class
     *                 }
     *               }
     *               BIND(coalesce(?directInstanceCountAgg, 0) AS ?directInstanceCount)
     *             }
     *             BIND(( ?instanceCount - ?directInstanceCount ) AS ?indirectInstanceCount)
     *           }
     *           ?class  <labelProvider>  ?_0 .
     *           ?_0  rdf:first  ?classText ;
     *                rdf:rest  ?_1 .
     *           ?_1  rdf:first  ?classImage ;
     *                rdf:rest  rdf:nil .
     *         }
     *         FILTER isURI(?class)
     *       }
     *     }
     *     </code>
     * </pre>
     */
    public static TaxonomyViewModel create(OntModel model, OntModel filterModel,
            Resource taxonomyResourceType, boolean includeChildlessRoots, boolean countInstances) {
        if (model == null) {
            return null;
        }
        if (filterModel == null) {
            filterModel = model;
        }
        final String filterModelUri = "urn:filterModel";
        Dataset dataset = DatasetFactory.createMem();
        dataset.setDefaultModel(model);
        dataset.addNamedModel(filterModelUri, filterModel);

        Query query = buildViewModelQuery(taxonomyResourceType, includeChildlessRoots);
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);

        Model viewModel = exec.execConstruct();
        TaxonomyViewModel taxonomyViewModel = new TaxonomyViewModel(viewModel, model);

        if (countInstances) {
            taxonomyViewModel.startAsyncInstanceCount();
        }

        return taxonomyViewModel;
    }

    public void startAsyncInstanceCount() {
        InstanceCountLoader job = new InstanceCountLoader(this);
        job.setPriority(Job.DECORATE);
        job.schedule();
    }

    public static Query buildViewModelQuery(Resource taxonomyResourceType,
            boolean includeChildlessRoots) {

        Var varClass = Var.alloc("class");
        Var varType = Var.alloc("type");
        Var varClassType = Var.alloc("classType");
        Var varSuperClass = Var.alloc("superClass");
        Var varSubClass = Var.alloc("subClass");
        Var varRootSuperClass = Var.alloc("rootSuperClass"); // non-existent
        Var varClassText = Var.alloc("classText");
        Var varClassImage = Var.alloc("classImage");

        QueryBuilder qb = QueryBuilder.createConstruct();

        // Create union of two sub queries
        ElementUnion union = new ElementUnion();

        // First sub query: get all root classes, which will have ?type ==
        // Vocabulary.Root.
        QueryBuilder subqb = QueryBuilder.createSelect(true);
        subqb.addTriplePattern(varClass, PathUtil.isInstanceOf, taxonomyResourceType);

        Triple subClassPattern = Triples.create(varSubClass, RDFS.subClassOf, varClass);
        if (includeChildlessRoots) {
            ElementGroup root1 = new ElementGroup();
            root1.addTriplePattern(subClassPattern);
            ElementGroup root2 = new ElementGroup();
            root2.addTriplePattern(Triples.create(varClass, RDF.type, varClassType));
            ElementGroup filter = new ElementGroup();
            filter.addTriplePattern(subClassPattern);
            root2.addElementFilter(new ElementFilter(new E_NotExists(filter)));
            ElementUnion rootClassUnion = new ElementUnion();
            rootClassUnion.addElement(root1);
            rootClassUnion.addElement(root2);
            ElementNamedGraph graphElement = new ElementNamedGraph(
                    NodeFactory.createURI(FILTER_MODEL_URI), rootClassUnion);
            subqb.addPattern(graphElement);
        }
        else {
            subqb.addTriplePattern(subClassPattern);
        }

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(Triples.create(varClass, RDFS.subClassOf, varRootSuperClass));
        eg.addElementFilter(new ElementFilter(new E_IsURI(new ExprVar(varRootSuperClass.getName()))));
        ElementNamedGraph graphElement = new ElementNamedGraph(
                NodeFactory.createURI(FILTER_MODEL_URI), eg);
        eg = new ElementGroup();
        eg.addElement(graphElement);
        eg.addTriplePattern(Triples.create(varRootSuperClass, PathUtil.isInstanceOf,
                taxonomyResourceType));
        ElementFilter ef = new ElementFilter(new E_NotExists(eg));
        subqb.addPattern(ef);

        subqb.addResultVar(varClass);
        subqb.getProject().add(varType, new NodeValueNode(Vocabulary.Root.asNode()));
        ElementSubQuery subQuery = new ElementSubQuery(subqb.getQuery());
        union.addElement(subQuery);

        // Second sub query: get the remaining classes and their super
        // classes.
        subqb = QueryBuilder.createSelect(true);
        subqb.addTriplePattern(varClass, PathUtil.isInstanceOf, taxonomyResourceType);

        eg = new ElementGroup();
        eg.addTriplePattern(Triples.create(varClass, RDFS.subClassOf, varSuperClass));
        eg.addElementFilter(new ElementFilter(new E_IsURI(new ExprVar(varSuperClass.getName()))));
        graphElement = new ElementNamedGraph(NodeFactory.createURI(FILTER_MODEL_URI), eg);
        subqb.addPattern(graphElement);

        subqb.addResultVars(varClass, varSuperClass);
        subQuery = new ElementSubQuery(subqb.getQuery());
        union.addElement(subQuery);

        qb.addPattern(union);

        // Retrieve the text and image for the class
        qb.addTriplePatterns(Triples.create(varClass, LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varClassText.asNode(), varClassImage.asNode())));
        qb.addFilterIsURI(varClass);

        // Set the Construct pattern
        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(varClass, RDF.type, varType));
        bgp.add(Triples.create(varClass, Vocabulary.text, varClassText));
        bgp.add(Triples.create(varClass, Vocabulary.image, varClassImage));
        bgp.add(Triples.create(varClass, Vocabulary.isChildOf, varSuperClass));
        // bgp.add(Triples.create(varClass, Vocabulary.directInstanceCount,
        // varDirectInstanceCount));
        // bgp.add(Triples
        // .create(varClass, Vocabulary.indirectInstanceCount,
        // varIndirectInstanceCount));
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
        eg.addTriplePattern(Triples.create(varClass, Vocabulary.text, varText));

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
        bgp.add(Triples.create(varClass, Vocabulary.directInstanceCount, varDirectInstanceCount));
        bgp.add(Triples
                .create(varClass, Vocabulary.indirectInstanceCount, varIndirectInstanceCount));
        Template constructTemplate = new Template(bgp);
        qb.setConstructTemplate(constructTemplate);

        return qb.getQuery();
    }

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

                // It could happens that the model is closed during this method
                // call this try-catch block is needed
                try {
                    Dataset dataset = DatasetFactory.create(currentModel);
                    dataset.addNamedModel(TAXONOMY_MODEL_URI, viewModel);

                    Query countQuery = TaxonomyViewModel.buildCountInstancesQuery();
                    QueryExecution exec = QueryExecutionFactory.create(countQuery, dataset);

                    Model instanceCountModel = exec.execConstruct();
                    taxonomyViewModel.setInstanceCountModel(instanceCountModel);
                }
                catch (Exception ex) {
                    logger.warn("Error while count instances: " + ex.getMessage());
                    return Status.CANCEL_STATUS;
                }
            }

            return Status.OK_STATUS;
        }
    }

    @Override
    public void close() {
        super.close();
        if (instanceCountModel != null) {
            instanceCountModel.close();
        }

        if (viewModel != null) {
            viewModel.close();
        }
    }
}
