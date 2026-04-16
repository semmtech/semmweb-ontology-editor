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


import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ViewerFilter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel;
import com.semmtech.plugin.semmweb.core.sparql.LabelProviderPropertyFunction;
import com.semmtech.semantics.sparql.PathUtil;
import com.semmtech.semantics.sparql.QueryBuilder;
import com.semmtech.semantics.sparql.Triples;
import com.semmtech.semantics.util.JenaUtil;


/**
 * 
 * @author Simone Rondelli
 */
public class RestrictionsViewModel extends AbstractResourceViewModel {

    public static class RestrictionsVocabulary extends Vocabulary {
        public static final Property isInheritedFrom = property("isInherited");
    }

    private OntClass root;
    private final ListMultimap<OntResource, OntResource> cache;

    private Comparator<OntResource> comparator;
    private boolean needSort;
    private boolean cacheIsComplete;

    public RestrictionsViewModel(OntModel currentModel, OntClass root) {
        super(ModelFactory.createDefaultModel(), currentModel);
        this.root = root;
        cache = ArrayListMultimap.create();
        cacheIsComplete = false;
    }

    @Override
    public int getRootsCount() {
        if (cacheIsComplete) {
            List<OntResource> roots = cache.get(null);
            return (roots == null) ? 0 : roots.size();
        }
        return super.getRootsCount();
    }

    @Override
    public int getChildCount(OntResource clazz) {
        clazz = JenaUtil.asOntResource(clazz.inModel(currentModel), currentModel);
        if (cacheIsComplete) {
            List<OntResource> children = cache.get(clazz);
            return (children == null) ? 0 : children.size();
        }
        return super.getChildCount(clazz);
    }

    /**
     * Return the child at the given index under the given root
     */
    public List<OntResource> getChildren(OntResource root) {
        root = JenaUtil.asOntResource(root.inModel(currentModel), currentModel);
        if (cache.containsKey(root)) {
            return cache.get(root);
        }
        List<OntResource> children = Lists.newArrayList();
        if (!cacheIsComplete) {
            if (root != null) {
                Var varChildClass = Var.alloc("child");

                QueryBuilder qb = QueryBuilder.createSelect(false);
                qb.addTriplePattern(varChildClass, Vocabulary.isChildOf, root);
                qb.addResultVar(varChildClass);

                ResultSet iter = qb.execSelect(viewModel);
                while (iter.hasNext()) {
                    QuerySolution qs = iter.next();
                    Resource resource = qs.getResource(varChildClass.getName());
                    OntResource childClass = JenaUtil.asOntResource(resource, currentModel);
                    children.add(childClass);
                }
            }
            if (!cache.containsKey(root)) {
                cache.putAll(root, children);
            }
        }
        return children;
    }

    @Override
    public OntResource getChild(OntResource clazz, int index) {
        List<OntResource> children = getChildren(clazz);
        if (needSort && comparator != null) {
            Collections.sort(children, comparator);
        }
        return children.get(index);
    }

    @Override
    public List<OntResource> getRoots() {
        if (!cache.containsKey(null)) {
            if (cacheIsComplete) {
                return Lists.newArrayList();
            }
            cache.putAll(null, super.getRoots());
        }

        if (needSort && comparator != null) {
            Collections.sort(cache.get(null), comparator);
        }

        return cache.get(null);
    }

    @Override
    protected Model buildViewModel(OntModel currentModel) {
        Model inverseTaxonomyModel = buildInverseTaxonomy(root);
        Model viewModel = ModelFactory.createDefaultModel();

        RestrictionSubsumptionSPARQL subsumption = new RestrictionSubsumptionSPARQL(currentModel);
        Map<Resource, List<Restriction>> unsubsumedSuperRestrictionsAtClass = Maps.newHashMap();

        while (true) {
            Var varSuperClass = Var.alloc("superClass");
            Var varClass = Var.alloc("class");

            QueryBuilder hierarchyQuery = QueryBuilder.createSelect(true);
            hierarchyQuery.addResultVars(varClass, varSuperClass);
            hierarchyQuery.addTriplePattern(varSuperClass, Vocabulary.isChildOf, varClass);
            hierarchyQuery.addFilterPattern(
                    Triples.create(Var.alloc("any"), Vocabulary.isChildOf, varSuperClass), true);

            QueryExecution exec = QueryExecutionFactory.create(hierarchyQuery.getQuery(),
                    inverseTaxonomyModel);
            ResultSet rs = exec.execSelect();

            List<QuerySolution> solutions = ResultSetFormatter.toList(rs);

            if (solutions.isEmpty()) {
                break;
            }

            for (QuerySolution qs : solutions) {
                Resource superClazz = qs.get(varSuperClass.getName()).asResource();
                Resource clazz = qs.get(varClass.getName()).asResource();

                inverseTaxonomyModel.remove(superClazz.asResource(), Vocabulary.isChildOf, clazz);

                List<Restriction> directSuperRestrictions = listDirectRestrictions(superClazz);
                List<Restriction> localRestrictions = listDirectRestrictions(clazz);

                // First mark all restrictions as Root. Clean up later.
                // Set the inheritedFrom for all required restrictions.
                for (Restriction sRes : directSuperRestrictions) {
                    viewModel.add(sRes, RDF.type, Vocabulary.Root);
                    if (!root.equals(superClazz)) {
                        viewModel.add(sRes, RestrictionsVocabulary.isInheritedFrom, superClazz);
                    }
                }
                for (Restriction lRes : localRestrictions) {
                    viewModel.add(lRes, RDF.type, Vocabulary.Root);
                    if (!root.equals(clazz)) {
                        viewModel.add(lRes, RestrictionsVocabulary.isInheritedFrom, clazz);
                    }
                }

                List<Restriction> toSubsumeRestrictions = Lists.newArrayList();
                List<Restriction> indirectUnsubsumedSuperRestrictions = unsubsumedSuperRestrictionsAtClass
                        .get(superClazz);
                if (indirectUnsubsumedSuperRestrictions != null) {
                    toSubsumeRestrictions.addAll(indirectUnsubsumedSuperRestrictions);
                }
                toSubsumeRestrictions.addAll(directSuperRestrictions);

                List<Restriction> unsubsumed = Lists.newArrayList();
                if (localRestrictions.isEmpty()) {
                    unsubsumed.addAll(toSubsumeRestrictions);
                }
                else {
                    for (Restriction sRes : toSubsumeRestrictions) {
                        boolean subsumedByClazz = false;
                        for (Restriction lRes : localRestrictions) {
                            if (subsumption.isSubsumed(sRes, lRes)) {
                                viewModel.add(sRes, Vocabulary.isChildOf, lRes);
                                subsumedByClazz = true;
                            }
                        }

                        if (!subsumedByClazz) {
                            unsubsumed.add(sRes);
                        }
                    }
                }

                List<Restriction> values = unsubsumedSuperRestrictionsAtClass.get(clazz);
                if (values == null) {
                    values = Lists.newArrayList();
                }
                values.addAll(unsubsumed);
                unsubsumedSuperRestrictionsAtClass.put(clazz, values);
            }
        }

        // Clean up. Remove Root-type for restrictions that aren't roots.
        for (Resource notRootRes : viewModel.listSubjectsWithProperty(Vocabulary.isChildOf)
                .toList()) {
            viewModel.removeAll(notRootRes, RDF.type, Vocabulary.Root);
        }

        // retrieving text and image. Even though they aren't used they are
        // necessary to the query executed by the viewModel
        Var varRestriction = Var.alloc("restriction");
        Var varText = Var.alloc("text");
        Var varImage = Var.alloc("image");

        QueryBuilder labelQuery = QueryBuilder.createConstruct();
        labelQuery.addTriplePattern(varRestriction, Node.ANY, Node.ANY);

        labelQuery.addTriplePatterns(Triples.create(varRestriction,
                LabelProviderPropertyFunction.asNode(),
                Lists.newArrayList(varText.asNode(), varImage.asNode())));

        BasicPattern bgp = new BasicPattern();
        bgp.add(Triples.create(varRestriction, Vocabulary.text, varText));
        bgp.add(Triples.create(varRestriction, Vocabulary.image, varImage));

        Template constructTemplate = new Template(bgp);
        labelQuery.setConstructTemplate(constructTemplate);

        Model labelModel = labelQuery.execConstruct(viewModel);
        viewModel.add(labelModel);

        return viewModel;
    }

    /**
     * Build the Taxonomy of the element from the element itself to the top of
     * the hierarchy
     * 
     * @param resource
     * @return ViewModel with the hierarchy elements in relation with the
     *         property isChildOf
     */
    public Model buildInverseTaxonomy(Resource resource) {
        Var varSuperClass = Var.alloc("superClass");
        Var varClass = Var.alloc("class");

        Triple classTriple = Triples.create(root, PathUtil.subClassOfAny, varClass);
        Triple superClassTriple = Triples.create(varClass, RDFS.subClassOf, varSuperClass);

        ElementGroup eg = new ElementGroup();
        eg.addTriplePattern(classTriple);
        eg.addTriplePattern(superClassTriple);

        BasicPattern bgp = new BasicPattern();

        bgp.add(Triples.create(root, RDF.type, Vocabulary.Root.asNode()));
        bgp.add(Triples.create(varSuperClass, Vocabulary.isChildOf, varClass));

        QueryBuilder taxonomyQuery = QueryBuilder.createConstruct();

        Template constructTemplate = new Template(bgp);
        taxonomyQuery.setConstructTemplate(constructTemplate);
        taxonomyQuery.addPattern(eg);

        return taxonomyQuery.execConstruct(currentModel);
    }

    public Model getInverseTaxonomyModel() {
        return buildInverseTaxonomy(root);
    }

    private List<Restriction> listDirectRestrictions(Resource res) {
        Var varRestriction = Var.alloc("r");

        QueryBuilder qb = QueryBuilder.createSelect(true);
        qb.addResultVar(varRestriction);

        qb.addTriplePattern(res, RDFS.subClassOf, varRestriction);
        qb.addTriplePattern(varRestriction, PathUtil.isInstanceOf, OWL.Restriction);

        List<Restriction> restrictions = Lists.newArrayList();

        for (ResultSet iter = qb.execSelect(currentModel); iter.hasNext();) {
            QuerySolution qs = iter.next();

            Resource rResource = qs.getResource(varRestriction.getName());
            Restriction restriction = JenaUtil.asRestriction(rResource, currentModel);
            restrictions.add(restriction);
        }
        return restrictions;
    }

    public boolean isInherited(Resource r) {
        return viewModel.contains(r, RestrictionsVocabulary.isInheritedFrom, (RDFNode) null);
    }

    public Resource getInheritedFrom(Resource r) {
        for (RDFNode node : viewModel.listObjectsOfProperty(r,
                RestrictionsVocabulary.isInheritedFrom).toList()) {
            return node.asResource();
        }
        return null;
    }

    @Override
    public Query buildQuery() {
        // TODO build query should be removed and buildViewModel should always
        // used instead
        return null;
    }

    public void setComparator(Comparator<OntResource> comparator) {
        this.comparator = comparator;
        setNeedsSort(true);
    }

    public void setNeedsSort(boolean needsSort) {
        this.needSort = needsSort;
    }

    public void applyFilter(ViewerFilter filter) {
        if (filter == null && !cacheIsComplete) {
            return;
        }

        cacheIsComplete = false;
        cache.clear();
        if (filter == null) {
            return;
        }

        // will need to retrieve all, as filtering should also show any parents
        // of enabled children.
        cacheAll();

        Set<OntResource> allRestrictions = Sets.newHashSet();
        allRestrictions.addAll(cache.keySet());
        for (OntResource parentRestriction : cache.keySet()) {
            allRestrictions.addAll(cache.get(parentRestriction));
        }
        allRestrictions.remove(null);

        Set<OntResource> allSelectedRestrictions = Sets.newHashSet();
        for (OntResource restriction : allRestrictions) {
            if (filter.select(null, null, restriction)) {
                allSelectedRestrictions.add(restriction);
            }
        }

        Set<OntResource> removeRestrictions = Sets.newHashSet();
        for (OntResource restriction : allRestrictions) {
            if (!allSelectedRestrictions.contains(restriction)
                    && !hasDescendantIn(restriction, allSelectedRestrictions)) {
                removeRestrictions.add(restriction);
            }
        }

        for (OntResource removeRestriction : removeRestrictions) {
            cache.removeAll(removeRestriction);
        }
        for (OntResource parentRestriction : cache.keySet()) {
            for (OntResource removeRestriction : removeRestrictions) {
                cache.remove(parentRestriction, removeRestriction);
            }
        }

        cacheIsComplete = true;
    }

    private boolean hasDescendantIn(OntResource restriction, Collection<OntResource> collection) {
        if (!cache.containsKey(restriction)) {
            return false;
        }
        List<OntResource> descendants = Lists.newArrayList();

        if (restriction != null && viewModel != null && currentModel != null) {
            String queryString = "SELECT DISTINCT ?child WHERE { ?child <"
                    + Vocabulary.isChildOf.getURI() + ">+ ?parent }";
            QuerySolutionMap initialBinding = new QuerySolutionMap();
            initialBinding.add("parent", restriction);
            QueryExecution qExec = QueryExecutionFactory.create(queryString, viewModel,
                    initialBinding);
            ResultSet iter = qExec.execSelect();
            while (iter.hasNext()) {
                Resource childRestriction = iter.next().getResource("child");
                OntResource descendant = JenaUtil.asOntResource(childRestriction, currentModel);
                descendants.add(descendant);
            }
        }

        for (OntResource descendant : descendants) {
            if (collection != null && collection.contains(descendant)) {
                return true;
            }
        }
        return false;
    }

    private void cacheAll() {
        List<OntResource> roots = getRoots(); // caches roots
        for (OntResource root : roots) {
            cacheAllChildren(root);
        }
    }

    private void cacheAllChildren(OntResource parent) {
        if (!cache.containsKey(parent)) {
            List<OntResource> children = getChildren(parent); // caches children
            for (OntResource child : children) {
                cacheAllChildren(child);
            }
        }
    }

    @Override
    public void close() {
        cache.clear();
        comparator = null;
        cacheIsComplete = false;
        super.close();
    }
}
