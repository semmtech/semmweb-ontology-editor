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

package com.semmtech.plugin.semmweb.core.viewers;


import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.path.P_Link;
import com.hp.hpl.jena.sparql.path.P_OneOrMore1;
import com.hp.hpl.jena.sparql.path.Path;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.model.DisplayLanguage;
import com.semmtech.plugin.semmweb.core.preferences.LabelsPreference;
import com.semmtech.plugin.semmweb.core.preferences.LanguagesPreference;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.plugin.semmweb.core.sparql.NamespaceLabelProviderPropertyFunction;
import com.semmtech.semantics.query.QuerySolutions;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.vocabulary.SEMM;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


/**
 * NB: Every time that the resource has to be used as a key of a map remember to
 * use:
 * 
 * <p>
 * <code>resource = resource.inModel(model);<code>
 * <p>
 * 
 * This because the resource could be an instance of ResourceImpl, ResourceTreeData or others
 * implementation that has different hasCode() and equals() method.
 * 
 * TODO: A better alternative should be a custom implementation of "ResourceMap" that 
 * automatically transform the resource
 */
public class ModelNodeLabelProvider extends LabelProvider implements IPropertyChangeListener {
    protected static Logger logger = Logger.getLogger(ModelNodeLabelProvider.class);

    protected Map<Resource, String> resourceLabels;
    protected Map<Resource, String> resourceImageKeys;
    protected Map<Resource, String> instanceImageKeys;
    protected Map<RDFNode, String> namespaceLabels;
    protected Table<Resource, Resource, Boolean> instances;
    protected Table<Resource, Resource, Boolean> instancesOfA;
    protected Model model;

    public ModelNodeLabelProvider(Model model) {
        this.model = model;
        this.resourceLabels = Maps.newHashMapWithExpectedSize(250);
        this.resourceImageKeys = Maps.newHashMapWithExpectedSize(250);
        this.instanceImageKeys = Maps.newHashMapWithExpectedSize(250);
        this.namespaceLabels = Maps.newHashMapWithExpectedSize(50);
        this.instances = HashBasedTable.create(330, 12);
        this.instancesOfA = HashBasedTable.create(330, 12);

        CorePlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
    }

    @Override
    public void dispose() {
        CorePlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
        clearAll();
        super.dispose();
    }

    protected void updateModel(Model model) {
        this.model = model;
        clearAll();
    }

    public void refresh() {
        clearAll();
    }

    protected void clearAll() {
        clearResourceLabels();
        clearInstances();
        clearResourceImages();
        clearInstanceImages();
        clearNamespaceLabels();
    }

    protected void clearAll(Collection<Resource> affected) {
        clearResourceLabels(affected);
        clearInstances(affected);
        clearResourceImages(affected);
        clearInstanceImages(affected);
        clearNamespaceLabels(affected);
    }

    protected void clearResourceLabels() {
        resourceLabels.clear();
    }

    protected void clearResourceLabels(Collection<Resource> affected) {
        for (Resource resource : affected) {
            resource = resource.inModel(model);
            resourceLabels.remove(resource);
        }
    }

    protected void clearInstances() {
        instances.clear();
        instancesOfA.clear();
    }

    protected void clearInstances(Collection<Resource> affected) {
        for (Resource resource : affected) {
            resource = resource.inModel(model);
            instances.row(resource).clear();
            instancesOfA.row(resource).clear();
        }
    }

    protected void clearResourceImages() {
        resourceImageKeys.clear();
    }

    protected void clearResourceImages(Collection<Resource> affected) {
        for (Resource resource : affected) {
            resource = resource.inModel(model);
            resourceImageKeys.remove(resource);
        }
    }

    protected void clearInstanceImages() {
        instanceImageKeys.clear();
    }

    protected void clearInstanceImages(Collection<Resource> affected) {
        for (Resource resource : affected) {
            resource = resource.inModel(model);
            instanceImageKeys.remove(resource);
        }
    }

    protected void clearNamespaceLabels() {
        namespaceLabels.clear();
    }

    protected void clearNamespaceLabels(Collection<? extends RDFNode> affected) {
        for (RDFNode node : affected) {
            node = node.inModel(model);
            namespaceLabels.remove(node);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (property.equals(LabelsPreference.PREFERENCE_RESOURCE_LABEL_RENDERING)
                || property.equals(LanguagesPreference.PREFERENCE_DISPLAY_LANGUAGES)) {
            clearResourceLabels();
        }
    }

    public boolean isRDFProperty(Resource resource) {
        resource = resource.inModel(model);
        if (isInstanceOf(resource, RDF.Property)) {
            return true;
        }
        boolean isProperty = false;

        Var resourceVar = Var.alloc("resource");
        QueryBuilder builder = QueryBuilder.createAsk().addTriplePattern(Node.ANY, resourceVar,
                Node.ANY);
        QueryExecution execution = QueryExecutionFactory.create(builder.getQuery(), model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(resourceVar.getName(), resource);
        execution.setInitialBinding(bindings);
        isProperty = execution.execAsk();

        instances.put(resource, RDF.Property, new Boolean(isProperty));

        return isProperty;
    }

    public boolean isRDFList(Resource resource) {
        resource = resource.inModel(model);
        if (isInstanceOf(resource, RDF.List)) {
            return true;
        }
        boolean isList = false;

        Var resourceVar = Var.alloc("resource");
        Query query = QueryFactory.create();
        query.setQueryAskType();
        ElementTriplesBlock element = new ElementTriplesBlock(BasicPattern.wrap(Lists
                .newArrayList(new Triple(resourceVar, RDF.first.asNode(), Node.ANY))));
        query.setQueryPattern(element);

        QueryExecution execution = QueryExecutionFactory.create(query, model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(resourceVar.getName(), resource);
        execution.setInitialBinding(bindings);
        isList = execution.execAsk();

        instances.put(resource, RDF.List, new Boolean(isList));

        return isList;
    }

    public boolean isInstanceOf(Resource resource, Resource clazz) {
        resource = resource.inModel(model);
        clazz = clazz.inModel(model);

        if (instances.contains(resource, clazz)) {
            return instances.get(resource, clazz).booleanValue();
        }
        boolean instanceOf = false;

        Var resourceVar = Var.alloc("resource");
        Var classVar = Var.alloc("class");
        Query query = QueryFactory.create();
        query.setQueryAskType();
        ElementPathBlock element = new ElementPathBlock();
        Path path = PathUtil.getPath(PathUtil.IS_INSTANCE_OF);
        element.addTriplePath(new TriplePath(resourceVar, path, classVar));
        query.setQueryPattern(element);

        QueryExecution execution = QueryExecutionFactory.create(query, model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(resourceVar.getName(), resource);
        bindings.add(classVar.getName(), clazz);
        execution.setInitialBinding(bindings);
        instanceOf = execution.execAsk();
        instances.put(resource, clazz, new Boolean(instanceOf));

        return instanceOf;
    }

    public boolean isInstanceOfA(Resource resource, Resource type) {
        resource = resource.inModel(model);
        type = type.inModel(model);

        if (instancesOfA.contains(resource, type)) {
            return instancesOfA.get(resource, type).booleanValue();
        }
        boolean result = false;

        Var resourceVar = Var.alloc("resource");
        Var classVar = Var.alloc("class");
        Var typeVar = Var.alloc("type");
        Query query = QueryFactory.create();
        query.setQueryAskType();
        ElementPathBlock element = new ElementPathBlock();
        Path path = PathUtil.getPath(PathUtil.IS_INSTANCE_OF);
        element.addTriplePath(new TriplePath(resourceVar, path, classVar));
        element.addTriple(new Triple(classVar, RDF.type.asNode(), typeVar));
        query.setQueryPattern(element);

        QueryExecution execution = QueryExecutionFactory.create(query, model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(resourceVar.getName(), resource);
        bindings.add(typeVar.getName(), type);
        execution.setInitialBinding(bindings);
        result = execution.execAsk();
        instancesOfA.put(resource, type, new Boolean(result));

        return result;
    }

    public boolean hasType(Resource resource) {
        resource = resource.inModel(model);

        Var resourceVar = Var.alloc("resource");
        Query query = QueryFactory.create();
        query.setQueryAskType();
        ElementTriplesBlock element = new ElementTriplesBlock(BasicPattern.wrap(Lists
                .newArrayList(new Triple(resourceVar, RDF.type.asNode(), Node.ANY))));
        query.setQueryPattern(element);

        QueryExecution execution = QueryExecutionFactory.create(query, model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(resourceVar.getName(), resource);
        execution.setInitialBinding(bindings);

        return execution.execAsk();
    }

    public boolean isSubClassOf(Resource resource, Resource clazz) {
        resource = resource.inModel(model);
        clazz = clazz.inModel(model);

        Var resourceVar = Var.alloc("resource");
        Var classVar = Var.alloc("class");
        Query query = QueryFactory.create();
        query.setQueryAskType();

        ElementPathBlock element = new ElementPathBlock();
        Path path = new P_OneOrMore1(new P_Link(RDFS.subClassOf.asNode()));
        element.addTriplePath(new TriplePath(resourceVar, path, classVar));
        query.setQueryPattern(element);

        QueryExecution execution = QueryExecutionFactory.create(query, model);
        QuerySolutionMap bindings = new QuerySolutionMap();
        bindings.add(resourceVar.getName(), resource);
        bindings.add(classVar.getName(), clazz);
        execution.setInitialBinding(bindings);

        return execution.execAsk();
    }

    /**
     * Returns a image for the given element, if the element is not an instance
     * of a RDFNode null is returned.
     */
    @Override
    public Image getImage(Object element) {
        return getImage(element, InspectOrder.CLASS_PROPERTY_INDIVIDUAL);
    }

    public Image getImage(Object element, InspectOrder order) {
        if (model == null) {
            return null;
        }
        if (element == null) {
            return null;
        }
        if (!(element instanceof RDFNode)) {
            return null;
        }
        String imageKey = null;
        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            imageKey = getResourceImageKey(resource, order);
        }
        else if (element instanceof Literal) {
            Literal literal = ((Literal) element);
            imageKey = getLiteralImageKey(literal);
            if (literal.getLexicalForm().length() == 0) {
                return CorePlugin.getDefault().getDecoratedImage(imageKey,
                        CorePluginImages.IMG_OVERLAY_EMPTY, OverlayImageIcon.BOTTOM_RIGHT);
            }
        }
        if (imageKey != null) {
            return CorePlugin.getDefault().getImage(imageKey);
        }
        return null;
    }

    /**
     * Returns the text for the given element, if this element not an instance
     * of a Resource null is returned.
     */
    @Override
    public String getText(Object element) {
        if (model == null || element == null) {
            return null;
        }
        if (element instanceof Resource) {
            Resource resource = (Resource) element;
            return getResourceTextFromSPARQL(resource);
        }
        else if (element instanceof Literal) {
            return getLiteralText((Literal) element);
        }
        return null;
    }

    public String getNamespaceText(Object element) {
        if (model == null || element == null) {
            return null;
        }
        if (element instanceof RDFNode) {
            RDFNode node = (RDFNode) element;
            return getNamespaceTextFromSPARQL(node);
        }
        return null;
    }

    protected static String getLiteralText(Literal literal) {
        String value = literal.getString();
        String language = literal.getLanguage();

        if (language != null && language.length() > 0) {
            return String.format("%s {@%s}", value, language);
        }
        return String.format("%s", value);
    }

    protected static String getLiteralImageKey(Literal literal) {

        RDFDatatype datatype = literal.getDatatype();
        String imageKey = CorePluginImages.IMG_XSD_LITERAL;

        if (datatype != null && !datatype.getURI().equals(XSD.xstring.getURI())) {
            if (datatype.getURI().equals(XSD.integer.getURI())
                    || datatype.getURI().equals(XSD.xint.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_INTEGER;
            }
            else if (datatype.getURI().equals(XSD.xdouble.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_DOUBLE;
            }
            else if (datatype.getURI().equals(XSD.xfloat.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_FLOAT;
            }
            else if (datatype.getURI().equals(XSD.date.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_DATE;
            }
            else if (datatype.getURI().equals(XSD.dateTime.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_DATETIME;
            }
            else if (datatype.getURI().equals(XSD.xboolean.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_BOOLEAN;
            }
            else if (datatype.getURI().equals(XSD.anyURI.getURI())) {
                imageKey = CorePluginImages.IMG_XSD_ANYURI;
            }
        }
        else if (literal.getLanguage() != null && literal.getLanguage().length() > 0) {
            imageKey = CorePluginImages.IMG_XSD_STRING;
            String lang = literal.getLanguage().toLowerCase();
            for (DisplayLanguage language : LanguagesPreference.getDisplayLanguages()) {
                if (language.getCode() != null && language.getCode().equals(lang)) {
                    imageKey = language.getImageKey();
                    break;
                }
            }
        }
        else if (datatype != null && datatype.getURI().equals(XSD.xstring.getURI())) {
            imageKey = CorePluginImages.IMG_XSD_STRING;
        }

        return imageKey;
    }

    /**
     * Returns the correct imageKey for a resource if this resource is an
     * instance of rdf:Property; otherwise null.
     * 
     * @param resource
     * @return
     */
    protected String getPropertyImageKey(Resource resource) {
        String imageKey = null;
        if (isRDFProperty(resource)) {
            imageKey = CorePluginImages.IMG_RDF_PROPERTY;
        }
        return imageKey;
    }

    /**
     * Returns the correct imageKey for a resource if this resource is an
     * instance of owl:Thing; otherwise null.
     * 
     * @param resource
     * @return
     */
    protected String getIndividualImageKey(Resource resource) {
        String imageKey = null;
        if (isInstanceOf(resource, OWL.Thing) || isInstanceOfA(resource, OWL.Class)) {
            imageKey = CorePluginImages.IMG_OWL_INDIVIDUAL;
        }
        else if (isInstanceOf(resource, SEMM.Role)) {
            imageKey = CorePluginImages.IMG_SEMM_ROLE;
        }
        return imageKey;
    }

    /**
     * Returns the correct imageKey for a resource if this resource is an
     * instance of rdfs":Class (in order of owl:Restriction, owl:Class,
     * rdfs:Class); otherwise null.
     * 
     * @param resource
     * @return
     */
    protected String getClassImageKey(Resource resource) {
        String imageKey = null;
        if (isInstanceOf(resource, OWL.Restriction)) {
            if (resource.hasProperty(OWL.someValuesFrom)) {
                imageKey = CorePluginImages.IMG_OWL_SOME_VALUES_FROM;
            }
            else if (resource.hasProperty(OWL.allValuesFrom)) {
                imageKey = CorePluginImages.IMG_OWL_ALL_VALUES_FROM;
            }
            else if (resource.hasProperty(OWL.hasValue)) {
                imageKey = CorePluginImages.IMG_OWL_HAS_VALUE;
            }
            else if (resource.hasProperty(OWL.minCardinality)
                    || resource.hasProperty(OWL2.minQualifiedCardinality)) {
                imageKey = CorePluginImages.IMG_OWL_MIN_CARDINALITY;
            }
            else if (resource.hasProperty(OWL.maxCardinality)
                    || resource.hasProperty(OWL2.maxQualifiedCardinality)) {
                imageKey = CorePluginImages.IMG_OWL_MAX_CARDINALITY;
            }
            else if (resource.hasProperty(OWL.cardinality)
                    || resource.hasProperty(OWL2.qualifiedCardinality)) {
                imageKey = CorePluginImages.IMG_OWL_CARDINALITY;
            }
            else {
                imageKey = CorePluginImages.IMG_OWL_RESTRICTION;
            }
        }
        else if (isInstanceOf(resource, OWL.Class)) {
            if (resource.hasProperty(OWL.equivalentClass)
                    || isObjectTo(resource, OWL.equivalentClass)) {
                imageKey = CorePluginImages.IMG_OWL_EQUIVALENT_CLASS;
            }
            else if (resource.hasProperty(OWL.intersectionOf)) {
                imageKey = CorePluginImages.IMG_OWL_INTERSECTION_OF;
            }
            else if (resource.hasProperty(OWL.unionOf)) {
                imageKey = CorePluginImages.IMG_OWL_UNION_OF;
            }
            else if (resource.hasProperty(OWL.complementOf)
                    || isObjectTo(resource, OWL.complementOf)) {
                imageKey = CorePluginImages.IMG_OWL_COMPLEMENT_OF;
            }
            else {
                imageKey = CorePluginImages.IMG_OWL_CLASS;
            }
        }
        else if (isInstanceOf(resource, RDFS.Class)) {
            imageKey = CorePluginImages.IMG_RDFS_CLASS;
        }
        return imageKey;
    }

    public boolean isObjectTo(Resource resource, Property property) {
        Var varSubject = Var.alloc("subject");
        QueryBuilder qb = QueryBuilder.createAsk();
        qb.addTriplePattern(varSubject, property, resource);
        return qb.execAsk(model);
    }

    public static enum InspectOrder {
        CLASS_INDIVIDUAL_PROPERTY, INDIVIDUAL_CLASS_PROPERTY, PROPERTY_INDIVIDUAL_CLASS, CLASS_PROPERTY_INDIVIDUAL;
    }

    /**
     * 
     * @param resource
     * @return
     */
    protected String getResourceImageKey(Resource resource, InspectOrder order) {
        resource = resource.inModel(model);
        if (!resourceImageKeys.containsKey(resource)) {
            String imageKey = null;
            try {
                // Check direct resource URIs
                if (RDF.nil.equals(resource)) {
                    imageKey = CorePluginImages.IMG_RDF_NIL;
                }
                // Check the types of the resource
                // The inspection can be customized using order; this prevents
                // an unnessary check if if the type is known beforehand
                switch (order) {
                case CLASS_INDIVIDUAL_PROPERTY:
                    if (imageKey == null) {
                        imageKey = getClassImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getIndividualImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getPropertyImageKey(resource);
                    }
                    break;
                case PROPERTY_INDIVIDUAL_CLASS:
                    if (imageKey == null) {
                        imageKey = getPropertyImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getIndividualImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getClassImageKey(resource);
                    }
                    break;
                case CLASS_PROPERTY_INDIVIDUAL:
                    if (imageKey == null) {
                        imageKey = getClassImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getPropertyImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getIndividualImageKey(resource);
                    }
                    break;
                case INDIVIDUAL_CLASS_PROPERTY:
                default:
                    if (imageKey == null) {
                        imageKey = getIndividualImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getClassImageKey(resource);
                    }
                    if (imageKey == null) {
                        imageKey = getPropertyImageKey(resource);
                    }
                    break;
                }

                if (imageKey == null) {
                    if (isInstanceOf(resource, OWL.Ontology)) {
                        imageKey = CorePluginImages.IMG_OWL_ONTOLOGY;
                    }
                    else if (isRDFList(resource)) {
                        imageKey = CorePluginImages.IMG_RDF_LIST;
                    }
                    else if (isInstanceOf(resource, RDF.Statement)) {
                        imageKey = CorePluginImages.IMG_RDF_STATEMENT;
                    }
                }
                // Fall-back
                if (imageKey == null) {
                    imageKey = CorePluginImages.IMG_RDF_RESOURCE;
                }
            }
            catch (Exception ex) {
                logger.error(String.format("Exception during getTypeImageKey on resource: %s",
                        ex.getMessage()));
                ex.printStackTrace();
            }
            resourceImageKeys.put(resource, imageKey);
            return imageKey;
        }
        return resourceImageKeys.get(resource);
    }

    /**
     * Returns the imageKey of an instance of the specified type(s).
     * 
     * @param types
     * @return
     */
    public String getInstanceImageKey(Resource type) {
        type = type.inModel(model);

        if (model == null) {
            return null;
        }
        if (!instanceImageKeys.containsKey(type)) {
            String imageKey = null;

            if (isSubClassOf(type, OWL.Restriction)) {
                imageKey = CorePluginImages.IMG_OWL_RESTRICTION;
            }
            else if (isSubClassOf(type, OWL.Class)) {
                imageKey = CorePluginImages.IMG_OWL_CLASS;
            }
            else if (isSubClassOf(type, RDFS.Class)) {
                imageKey = CorePluginImages.IMG_RDFS_CLASS;
            }
            else if (isSubClassOf(type, OWL.Thing)) {
                imageKey = CorePluginImages.IMG_OWL_INDIVIDUAL;
            }
            else if (isSubClassOf(type, RDF.List)) {
                imageKey = CorePluginImages.IMG_RDF_LIST;
            }
            else if (isSubClassOf(type, RDF.Property)) {
                imageKey = CorePluginImages.IMG_RDF_PROPERTY;
            }
            else if (isSubClassOf(type, SEMM.Role)) {
                imageKey = CorePluginImages.IMG_SEMM_ROLE;
            }
            else if (isSubClassOf(type, OWL.Ontology)) {
                imageKey = CorePluginImages.IMG_OWL_ONTOLOGY;
            }
            else if (isSubClassOf(type, RDFS.Literal)) {
                imageKey = CorePluginImages.IMG_XSD;
            }

            if (imageKey == null) {
                imageKey = CorePluginImages.IMG_RDF_RESOURCE;
            }
            instanceImageKeys.put(type, imageKey);
        }
        return instanceImageKeys.get(type);
    }

    protected String getResourceTextFromSPARQL(Resource resource) {
        resource = resource.inModel(model);

        if (!resourceLabels.containsKey(resource)) {
            QueryExecution execution = QueryExecutionFactory.create(resourceLabelQuery, model);
            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("resource", resource);
            execution.setInitialBinding(bindings);
            ResultSet result = execution.execSelect();
            String text = null;
            if (result.hasNext()) {
                QuerySolution solution = result.next();
                text = QuerySolutions.getLexicalForm(solution, "text");
            }
            if (text != null) {
                resourceLabels.put(resource, text);
            }
        }
        return resourceLabels.get(resource);
    }

    protected static Query resourceLabelQuery = createLabelQuery();

    protected static Query createLabelQuery() {
        Query query = QueryFactory.create();
        query.setQuerySelectType();
        ElementTriplesBlock pattern = new ElementTriplesBlock();
        pattern.addTriple(new Triple(Var.alloc("resource"), LabelProviderPropertyFunction.asNode(),
                Var.alloc("?x")));
        pattern.addTriple(new Triple(Var.alloc("?x"), RDF.first.asNode(), Var.alloc("text")));
        pattern.addTriple(new Triple(Var.alloc("?x"), RDF.rest.asNode(), Var.alloc("?y")));
        pattern.addTriple(new Triple(Var.alloc("?y"), RDF.first.asNode(), Var.alloc("image")));
        pattern.addTriple(new Triple(Var.alloc("?y"), RDF.rest.asNode(), RDF.nil.asNode()));

        query.setQueryPattern(pattern);
        return query;
    }

    protected String getNamespaceTextFromSPARQL(RDFNode node) {
        node = node.inModel(model);

        if (!namespaceLabels.containsKey(node)) {
            QueryExecution execution = QueryExecutionFactory.create(namespaceLabelQuery, model);
            QuerySolutionMap bindings = new QuerySolutionMap();
            bindings.add("node", node);
            execution.setInitialBinding(bindings);
            ResultSet result = execution.execSelect();
            String text = null;
            if (result.hasNext()) {
                QuerySolution solution = result.next();
                text = QuerySolutions.getLexicalForm(solution, "text");
            }
            if (text != null) {
                namespaceLabels.put(node, text);
            }
        }
        return namespaceLabels.get(node);
    }

    protected static Query namespaceLabelQuery = createNamespaceLabelQuery();

    protected static Query createNamespaceLabelQuery() {
        Query query = QueryFactory.create();
        query.setQuerySelectType();
        ElementTriplesBlock pattern = new ElementTriplesBlock();
        pattern.addTriple(new Triple(Var.alloc("node"), NamespaceLabelProviderPropertyFunction
                .asNode(), Var.alloc("?x")));
        pattern.addTriple(new Triple(Var.alloc("?x"), RDF.first.asNode(), Var.alloc("text")));
        pattern.addTriple(new Triple(Var.alloc("?x"), RDF.rest.asNode(), Var.alloc("?y")));
        pattern.addTriple(new Triple(Var.alloc("?y"), RDF.first.asNode(), Var.alloc("image")));
        pattern.addTriple(new Triple(Var.alloc("?y"), RDF.rest.asNode(), RDF.nil.asNode()));

        query.setQueryPattern(pattern);
        return query;
    }

}