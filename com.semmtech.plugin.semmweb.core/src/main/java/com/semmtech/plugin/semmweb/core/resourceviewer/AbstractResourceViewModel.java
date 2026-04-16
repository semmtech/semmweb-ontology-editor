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

package com.semmtech.plugin.semmweb.core.resourceviewer;


import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.FILTER_MODEL_URI;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.Root;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.image;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.isChildOf;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.settingKey;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.settingValue;
import static com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary.text;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
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
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.E_StrLowerCase;
import com.hp.hpl.jena.sparql.expr.ExprVar;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.util.JenaUtil;


/**
 * This is a model that represents a hierarchy of resources that should be shown
 * inside a {@link ResourceTreeViewer} or {@link ResourceTableViewer}. This
 * model exposes a unified interface that provides roots and children to
 * {@link AbstractResourceContentProvider} and labels and images to
 * {@link ResourceLabelProvider}.
 * <p>
 * The subclasses have to provide the implementation of the
 * {@link #buildQuery()} method that retrieves the relation between the
 * resources. Note that at least one Root should be provided. It is possible to
 * use the same ViewModel to show both Trees and Tables.
 * <p>
 * The parameters necessary to customize the creation of the query could be
 * provided through the methods: {@link #addSetting(String, Object)},
 * {@link #getSetting(String)} and {@link #removeSetting(String)}.
 * <p>
 * After the parameters is set the {@link #init()} method have to be called.
 * <p>
 * It is also possible to add listeners to this class. Currently the listeners
 * is not used here but they are in the subclasses.
 * 
 * @see ResourceLabelProvider
 * @see ResourceTreeContentProvider
 * @see ResourceTreeViewer
 * @see ResourceViewModelListener
 * 
 * @author Simone Rondelli
 */
public abstract class AbstractResourceViewModel extends ModelCom {

    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(AbstractResourceViewModel.class);

    /**
     * This class stores the resources and properties necessary to build the
     * view model. The subclasses of can extends this class if more properties
     * are needed.
     */
    public static class Vocabulary {
        public static final String NS = "urn:abstractViewModel#";
        public static final String FILTER_MODEL_URI = "urn:filterModel";

        protected static final Resource resource(String local) {
            return ResourceFactory.createResource(NS + local);
        }

        protected static final Property property(String local) {
            return ResourceFactory.createProperty(NS + local);
        }

        public static final Resource Property = resource("Setting");
        public static final Property settingKey = property("settingKey");
        public static final Property settingValue = property("settingValue");

        public static final Property isChildOf = property("isChildOf");
        public static final Property text = property("text");
        public static final Property image = property("image");
        public static final Resource Root = resource("Root");
    }

    protected Model viewModel;
    protected OntModel currentModel;
    private Model filterModel;

    /**
     * If true returns only those nodes that have children
     */
    private boolean childrenRequired;

    protected List<ResourceViewModelListener> listeners;

    public AbstractResourceViewModel(Model viewModel, OntModel currentModel) {
        super(viewModel.getGraph());
        this.currentModel = currentModel;
        this.viewModel = viewModel;
        listeners = Lists.newArrayList();
        childrenRequired = false;
    }

    /**
     * Runs the query that creates the ViewModel. The created Model is added to
     * this AbstractResourceViewModel from the {@link #init()} method
     * 
     * @param currentModel
     *            Model on which the hierarchy have to be built
     */
    protected Model buildViewModel(OntModel currentModel) {
        Dataset dataset = DatasetFactory.createMem();
        dataset.setDefaultModel(currentModel);

        if (filterModel != null) {
            dataset.addNamedModel(FILTER_MODEL_URI, filterModel);
        }
        else {
            dataset.addNamedModel(FILTER_MODEL_URI, currentModel);
        }

        Query query = buildQuery();
        QueryExecution exec = QueryExecutionFactory.create(query, dataset);

        Model viewModel = exec.execConstruct();
        return viewModel;
    }

    /**
     * Runs the build query and add all generated triples inside the ViewModel
     */
    public void init() {
        Model viewModel = buildViewModel(currentModel);
        add(viewModel);
    }

    public void addSetting(String key, Object value) {
        List<Resource> settings = listResourcesWithProperty(settingKey, key).toList();
        Resource setting;

        if (settings.isEmpty()) {
            setting = createResource();
        }
        else {
            setting = settings.get(0);
            setting.removeAll(settingKey);
            setting.removeAll(settingValue);
        }

        setting.addLiteral(settingKey, key);
        setting.addLiteral(settingValue, value);
    }

    public Object getSetting(String key) {
        List<Resource> settings = listResourcesWithProperty(settingKey, key).toList();

        if (settings.isEmpty()) {
            return null;
        }

        Resource setting = settings.get(0);
        return setting.getProperty(settingValue).getLiteral().getValue();
    }

    public void removeSetting(String key) {
        List<Resource> settings = listResourcesWithProperty(settingKey, key).toList();

        for (Resource set : settings) {
            remove(set, null, null);
        }
    }

    @Override
    public void close() {
        super.close();

        if (viewModel != null) {
            viewModel.close();
            viewModel = null;
        }

        if (filterModel != null) {
            filterModel.close();
            filterModel = null;
        }

        listeners.clear();
    }

    /**
     * Returns the number of the Root resources
     */
    public int getRootsCount() {
        Var varClass = Var.alloc("class");
        Var varChild = Var.alloc("child");

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(varClass, RDF.type, Root);
        if (childrenRequired) {
            qb.addTriplePattern(varChild, isChildOf, varClass);
        }
        qb.setResultCountVar(varClass, true);
        return qb.execCountSelect(viewModel);
    }

    /**
     * Return the Root resource at the given index
     */
    public OntResource getRoot(int index) {
        if (index >= 0) {
            Var varClass = Var.alloc("class");
            Var varChild = Var.alloc("child");
            Var varText = Var.alloc("text");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varClass, RDF.type, Root);

            if (childrenRequired) {
                qb.addTriplePattern(varChild, isChildOf, varClass);
            }

            qb.addTriplePattern(varClass, text, varText);
            qb.addResultVar(varClass);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varClass.getName());
                OntResource clazz = JenaUtil.asOntResource(resource, currentModel);
                return clazz;
            }
        }
        return null;
    }

    /**
     * Returns all the roots resources
     */
    public List<OntResource> getRoots() {
        List<OntResource> result = Lists.newArrayList();
        Var varClass = Var.alloc("class");
        Var varChild = Var.alloc("child");
        Var varText = Var.alloc("text");

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addTriplePattern(varClass, RDF.type, Root);

        if (childrenRequired) {
            qb.addTriplePattern(varChild, isChildOf, varClass);
        }

        qb.addTriplePattern(varClass, text, varText);
        qb.addResultVar(varClass);
        qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);

        ResultSet iter = qb.execSelect(viewModel);
        while (iter.hasNext()) {
            QuerySolution qs = iter.next();
            Resource resource = qs.getResource(varClass.getName());
            OntResource clazz = JenaUtil.asOntResource(resource, currentModel);
            result.add(clazz);
        }
        return result;
    }

    /**
     * Return the number of the children under all roots. This method is
     * particularly useful in the table view where we need to know the number of
     * all elements excluding the root/s
     * 
     * @see #getChild(int)
     */
    public int getChildCount() {
        Var varChild = Var.alloc("child");
        Var varType = Var.alloc("type");

        QueryBuilder qb = QueryBuilder.createSelect(false);
        qb.addTriplePattern(varChild, Vocabulary.isChildOf, varType);

        qb.setResultCountVar(varChild, true);
        return qb.execCountSelect(viewModel);
    }

    /**
     * Return the number of children under the given root
     */
    public int getChildCount(OntResource clazz) {
        if (clazz != null) {
            Var varChildClass = Var.alloc("childClass");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varChildClass, isChildOf, clazz);
            qb.setResultCountVar(varChildClass);
            return qb.execCountSelect(viewModel);
        }
        return 0;
    }

    /**
     * Return the child resource at the given index between all the children
     * without taking in account the roots
     * 
     * @see #getChildCount()
     */
    public Resource getChild(int index) {
        if (index >= 0) {
            Var varChild = Var.alloc("child");
            Var varType = Var.alloc("type");
            Var varText = Var.alloc("text");

            QueryBuilder qb = QueryBuilder.createSelect(true);
            qb.addTriplePattern(varChild, Vocabulary.isChildOf, varType);
            qb.addTriplePattern(varChild, Vocabulary.text, varText);

            qb.addResultVar(varChild);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varChild.getName());
                OntResource ontResource = JenaUtil.asOntResource(resource, currentModel);
                return ontResource;
            }
        }
        return null;
    }

    /**
     * Return the child at the given index under the given root
     */
    public OntResource getChild(OntResource clazz, int index) {
        if ((clazz != null) && (index >= 0)) {
            Var varChildClass = Var.alloc("childClass");
            Var varText = Var.alloc("text");

            QueryBuilder qb = QueryBuilder.createSelect(false);
            qb.addTriplePattern(varChildClass, isChildOf, clazz);
            qb.addTriplePattern(varChildClass, text, varText);
            qb.addResultVar(varChildClass);
            qb.addOrderBy(new E_StrLowerCase(new ExprVar(varText)), Query.ORDER_ASCENDING);
            qb.setLimit(1);
            qb.setOffset(index);

            ResultSet iter = qb.execSelect(viewModel);
            while (iter.hasNext()) {
                QuerySolution qs = iter.next();
                Resource resource = qs.getResource(varChildClass.getName());
                OntResource childClass = JenaUtil.asOntResource(resource, currentModel);
                return childClass;
            }
        }
        return null;
    }

    /**
     * Return the text for the given resource
     */
    public String getText(Resource resource) {
        if (resource != null) {
            Resource viewModelResource = resource.isURIResource() ? viewModel
                    .createResource(resource.getURI()) : viewModel.createResource(resource.getId());
            Statement statement = viewModelResource.getProperty(text);
            if (statement != null) {
                return statement.getObject().asLiteral().getString();
            }
        }
        return null;
    }

    /**
     * Return the image for the given resource
     */
    public String getImage(Resource resource) {
        if (resource != null) {
            Resource viewModelResource = resource.isURIResource() ? viewModel
                    .createResource(resource.getURI()) : viewModel.createResource(resource.getId());
            Statement statement = viewModelResource.getProperty(image);
            if (statement != null) {
                return statement.getObject().asLiteral().getString();
            }
        }
        return null;
    }

    public DeltaResourceViewModel difference(AbstractResourceViewModel viewModel) {
        return new DeltaResourceViewModel(difference((Model) viewModel), getCurrentModel());
    }

    public Model getViewModel() {
        return viewModel;
    }

    public OntModel getCurrentModel() {
        return currentModel;
    }

    public void addViewModelChangeListener(ResourceViewModelListener list) {
        listeners.add(list);
    }

    public void removeViewModelChangeListener(ResourceViewModelListener list) {
        listeners.remove(list);
    }

    public void setFilterModel(Model filterModel) {
        this.filterModel = filterModel;
    }

    public boolean isChildrenRequired() {
        return childrenRequired;
    }

    public void setChildrenRequired(boolean childrenRequired) {
        this.childrenRequired = childrenRequired;
    }

    /**
     * Create the query that will be executed to build the View Model. Assuming
     * that the namesapce of the elements is ns:
     * 
     * Every element have to be at least one ns:Root except the Roots itself.
     * 
     * Roots and children are related with the property ns:isChildOf. Every
     * element must have ns:text, ns:image
     * 
     */
    public abstract Query buildQuery();
}
