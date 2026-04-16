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

package com.semmtech.plugin.semmweb.core.widgets.trees;


import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.AllDifferent;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.ComplementClass;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.EnumeratedClass;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.IntersectionClass;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.UnionClass;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


/**
 * 
 * @author Sander Stolk
 */
public class OntClassTreeData extends OntResourceTreeData implements OntClass {
    private final OntClass clazz;

    public OntClassTreeData(OntClass clazz) {
        super(clazz.as(OntResource.class));
        this.clazz = clazz;
    }

    public Node asNode() {
        return clazz.asNode();
    }

    public void setSuperClass(Resource cls) {
        clazz.setSuperClass(cls);
    }

    public OntModel getOntModel() {
        return clazz.getOntModel();
    }

    public boolean isAnon() {
        return clazz.isAnon();
    }

    public boolean isLiteral() {
        return clazz.isLiteral();
    }

    public Profile getProfile() {
        return clazz.getProfile();
    }

    public void addSuperClass(Resource cls) {
        clazz.addSuperClass(cls);
    }

    public boolean isURIResource() {
        return clazz.isURIResource();
    }

    public boolean isResource() {
        return clazz.isResource();
    }

    public OntClass getSuperClass() {
        return clazz.getSuperClass();
    }

    public boolean isOntLanguageTerm() {
        return clazz.isOntLanguageTerm();
    }

    public <T extends RDFNode> T as(Class<T> view) {
        return clazz.as(view);
    }

    public ExtendedIterator<OntClass> listSuperClasses() {
        return clazz.listSuperClasses();
    }

    public AnonId getId() {
        return clazz.getId();
    }

    public void setSameAs(Resource res) {
        clazz.setSameAs(res);
    }

    public <T extends RDFNode> boolean canAs(Class<T> view) {
        return clazz.canAs(view);
    }

    public ExtendedIterator<OntClass> listSuperClasses(boolean direct) {
        return clazz.listSuperClasses(direct);
    }

    public Resource inModel(Model m) {
        return clazz.inModel(m);
    }

    public void addSameAs(Resource res) {
        clazz.addSameAs(res);
    }

    public boolean hasURI(String uri) {
        return clazz.hasURI(uri);
    }

    public String getURI() {
        return clazz.getURI();
    }

    public Model getModel() {
        return clazz.getModel();
    }

    public OntResource getSameAs() {
        return clazz.getSameAs();
    }

    public String getNameSpace() {
        return clazz.getNameSpace();
    }

    public String getLocalName() {
        return clazz.getLocalName();
    }

    public boolean hasSuperClass(Resource cls) {
        return clazz.hasSuperClass(cls);
    }

    public ExtendedIterator<? extends Resource> listSameAs() {
        return clazz.listSameAs();
    }

    public String toString() {
        return clazz.toString();
    }

    public Object visitWith(RDFVisitor rv) {
        return clazz.visitWith(rv);
    }

    public boolean hasSuperClass() {
        return clazz.hasSuperClass();
    }

    public boolean equals(Object o) {
        return clazz.equals(o);
    }

    public Resource asResource() {
        return clazz.asResource();
    }

    public boolean isSameAs(Resource res) {
        return clazz.isSameAs(res);
    }

    public Literal asLiteral() {
        return clazz.asLiteral();
    }

    public boolean hasSuperClass(Resource cls, boolean direct) {
        return clazz.hasSuperClass(cls, direct);
    }

    public void removeSameAs(Resource res) {
        clazz.removeSameAs(res);
    }

    public Statement getRequiredProperty(Property p) {
        return clazz.getRequiredProperty(p);
    }

    public void setDifferentFrom(Resource res) {
        clazz.setDifferentFrom(res);
    }

    public void removeSuperClass(Resource cls) {
        clazz.removeSuperClass(cls);
    }

    public Statement getProperty(Property p) {
        return clazz.getProperty(p);
    }

    public void addDifferentFrom(Resource res) {
        clazz.addDifferentFrom(res);
    }

    public void setSubClass(Resource cls) {
        clazz.setSubClass(cls);
    }

    public StmtIterator listProperties(Property p) {
        return clazz.listProperties(p);
    }

    public OntResource getDifferentFrom() {
        return clazz.getDifferentFrom();
    }

    public void addSubClass(Resource cls) {
        clazz.addSubClass(cls);
    }

    public StmtIterator listProperties() {
        return clazz.listProperties();
    }

    public ExtendedIterator<? extends Resource> listDifferentFrom() {
        return clazz.listDifferentFrom();
    }

    public OntClass getSubClass() {
        return clazz.getSubClass();
    }

    public Resource addLiteral(Property p, boolean o) {
        return clazz.addLiteral(p, o);
    }

    public boolean isDifferentFrom(Resource res) {
        return clazz.isDifferentFrom(res);
    }

    public ExtendedIterator<OntClass> listSubClasses() {
        return clazz.listSubClasses();
    }

    public Resource addLiteral(Property p, long o) {
        return clazz.addLiteral(p, o);
    }

    public void removeDifferentFrom(Resource res) {
        clazz.removeDifferentFrom(res);
    }

    public ExtendedIterator<OntClass> listSubClasses(boolean direct) {
        return clazz.listSubClasses(direct);
    }

    public Resource addLiteral(Property p, char o) {
        return clazz.addLiteral(p, o);
    }

    public void setSeeAlso(Resource res) {
        clazz.setSeeAlso(res);
    }

    public Resource addLiteral(Property value, double d) {
        return clazz.addLiteral(value, d);
    }

    public void addSeeAlso(Resource res) {
        clazz.addSeeAlso(res);
    }

    public Resource addLiteral(Property value, float d) {
        return clazz.addLiteral(value, d);
    }

    public Resource getSeeAlso() {
        return clazz.getSeeAlso();
    }

    public Resource addLiteral(Property p, Object o) {
        return clazz.addLiteral(p, o);
    }

    public ExtendedIterator<RDFNode> listSeeAlso() {
        return clazz.listSeeAlso();
    }

    public Resource addLiteral(Property p, Literal o) {
        return clazz.addLiteral(p, o);
    }

    public boolean hasSeeAlso(Resource res) {
        return clazz.hasSeeAlso(res);
    }

    public Resource addProperty(Property p, String o) {
        return clazz.addProperty(p, o);
    }

    public void removeSeeAlso(Resource res) {
        clazz.removeSeeAlso(res);
    }

    public Resource addProperty(Property p, String o, String l) {
        return clazz.addProperty(p, o, l);
    }

    public void setIsDefinedBy(Resource res) {
        clazz.setIsDefinedBy(res);
    }

    public Resource addProperty(Property p, String lexicalForm, RDFDatatype datatype) {
        return clazz.addProperty(p, lexicalForm, datatype);
    }

    public boolean hasSubClass(Resource cls) {
        return clazz.hasSubClass(cls);
    }

    public void addIsDefinedBy(Resource res) {
        clazz.addIsDefinedBy(res);
    }

    public Resource getIsDefinedBy() {
        return clazz.getIsDefinedBy();
    }

    public boolean hasSubClass() {
        return clazz.hasSubClass();
    }

    public Resource addProperty(Property p, RDFNode o) {
        return clazz.addProperty(p, o);
    }

    public boolean hasSubClass(Resource cls, boolean direct) {
        return clazz.hasSubClass(cls, direct);
    }

    public ExtendedIterator<RDFNode> listIsDefinedBy() {
        return clazz.listIsDefinedBy();
    }

    public boolean hasProperty(Property p) {
        return clazz.hasProperty(p);
    }

    public boolean hasLiteral(Property p, boolean o) {
        return clazz.hasLiteral(p, o);
    }

    public boolean isDefinedBy(Resource res) {
        return clazz.isDefinedBy(res);
    }

    public boolean hasLiteral(Property p, long o) {
        return clazz.hasLiteral(p, o);
    }

    public void removeSubClass(Resource cls) {
        clazz.removeSubClass(cls);
    }

    public void removeDefinedBy(Resource res) {
        clazz.removeDefinedBy(res);
    }

    public boolean hasLiteral(Property p, char o) {
        return clazz.hasLiteral(p, o);
    }

    public void setVersionInfo(String info) {
        clazz.setVersionInfo(info);
    }

    public void setEquivalentClass(Resource cls) {
        clazz.setEquivalentClass(cls);
    }

    public boolean hasLiteral(Property p, double o) {
        return clazz.hasLiteral(p, o);
    }

    public boolean hasLiteral(Property p, float o) {
        return clazz.hasLiteral(p, o);
    }

    public void addVersionInfo(String info) {
        clazz.addVersionInfo(info);
    }

    public void addEquivalentClass(Resource cls) {
        clazz.addEquivalentClass(cls);
    }

    public boolean hasLiteral(Property p, Object o) {
        return clazz.hasLiteral(p, o);
    }

    public String getVersionInfo() {
        return clazz.getVersionInfo();
    }

    public OntClass getEquivalentClass() {
        return clazz.getEquivalentClass();
    }

    public boolean hasProperty(Property p, String o) {
        return clazz.hasProperty(p, o);
    }

    public ExtendedIterator<String> listVersionInfo() {
        return clazz.listVersionInfo();
    }

    public boolean hasProperty(Property p, String o, String l) {
        return clazz.hasProperty(p, o, l);
    }

    public ExtendedIterator<OntClass> listEquivalentClasses() {
        return clazz.listEquivalentClasses();
    }

    public boolean hasVersionInfo(String info) {
        return clazz.hasVersionInfo(info);
    }

    public boolean hasProperty(Property p, RDFNode o) {
        return clazz.hasProperty(p, o);
    }

    public void removeVersionInfo(String info) {
        clazz.removeVersionInfo(info);
    }

    public Resource removeProperties() {
        return clazz.removeProperties();
    }

    public void setLabel(String label, String lang) {
        clazz.setLabel(label, lang);
    }

    public Resource removeAll(Property p) {
        return clazz.removeAll(p);
    }

    public boolean hasEquivalentClass(Resource cls) {
        return clazz.hasEquivalentClass(cls);
    }

    public Resource begin() {
        return clazz.begin();
    }

    public Resource abort() {
        return clazz.abort();
    }

    public void addLabel(String label, String lang) {
        clazz.addLabel(label, lang);
    }

    public void removeEquivalentClass(Resource cls) {
        clazz.removeEquivalentClass(cls);
    }

    public Resource commit() {
        return clazz.commit();
    }

    public Resource getPropertyResourceValue(Property p) {
        return clazz.getPropertyResourceValue(p);
    }

    public void addLabel(Literal label) {
        clazz.addLabel(label);
    }

    public void setDisjointWith(Resource cls) {
        clazz.setDisjointWith(cls);
    }

    public String getLabel(String lang) {
        return clazz.getLabel(lang);
    }

    public void addDisjointWith(Resource cls) {
        clazz.addDisjointWith(cls);
    }

    public OntClass getDisjointWith() {
        return clazz.getDisjointWith();
    }

    public ExtendedIterator<RDFNode> listLabels(String lang) {
        return clazz.listLabels(lang);
    }

    public ExtendedIterator<OntClass> listDisjointWith() {
        return clazz.listDisjointWith();
    }

    public boolean hasLabel(String label, String lang) {
        return clazz.hasLabel(label, lang);
    }

    public boolean hasLabel(Literal label) {
        return clazz.hasLabel(label);
    }

    public boolean isDisjointWith(Resource cls) {
        return clazz.isDisjointWith(cls);
    }

    public void removeLabel(String label, String lang) {
        clazz.removeLabel(label, lang);
    }

    public void removeDisjointWith(Resource cls) {
        clazz.removeDisjointWith(cls);
    }

    public void removeLabel(Literal label) {
        clazz.removeLabel(label);
    }

    public void setComment(String comment, String lang) {
        clazz.setComment(comment, lang);
    }

    public ExtendedIterator<OntProperty> listDeclaredProperties() {
        return clazz.listDeclaredProperties();
    }

    public ExtendedIterator<OntProperty> listDeclaredProperties(boolean direct) {
        return clazz.listDeclaredProperties(direct);
    }

    public void addComment(String comment, String lang) {
        clazz.addComment(comment, lang);
    }

    public void addComment(Literal comment) {
        clazz.addComment(comment);
    }

    public String getComment(String lang) {
        return clazz.getComment(lang);
    }

    public boolean hasDeclaredProperty(Property p, boolean direct) {
        return clazz.hasDeclaredProperty(p, direct);
    }

    public ExtendedIterator<RDFNode> listComments(String lang) {
        return clazz.listComments(lang);
    }

    public boolean hasComment(String comment, String lang) {
        return clazz.hasComment(comment, lang);
    }

    public ExtendedIterator<? extends OntResource> listInstances() {
        return clazz.listInstances();
    }

    public boolean hasComment(Literal comment) {
        return clazz.hasComment(comment);
    }

    public void removeComment(String comment, String lang) {
        clazz.removeComment(comment, lang);
    }

    public ExtendedIterator<? extends OntResource> listInstances(boolean direct) {
        return clazz.listInstances(direct);
    }

    public void removeComment(Literal comment) {
        clazz.removeComment(comment);
    }

    public void setRDFType(Resource cls) {
        clazz.setRDFType(cls);
    }

    public Individual createIndividual() {
        return clazz.createIndividual();
    }

    public Individual createIndividual(String uri) {
        return clazz.createIndividual(uri);
    }

    public void addRDFType(Resource cls) {
        clazz.addRDFType(cls);
    }

    public void dropIndividual(Resource individual) {
        clazz.dropIndividual(individual);
    }

    public Resource getRDFType() {
        return clazz.getRDFType();
    }

    public boolean isHierarchyRoot() {
        return clazz.isHierarchyRoot();
    }

    public Resource getRDFType(boolean direct) {
        return clazz.getRDFType(direct);
    }

    public EnumeratedClass asEnumeratedClass() {
        return clazz.asEnumeratedClass();
    }

    public UnionClass asUnionClass() {
        return clazz.asUnionClass();
    }

    public ExtendedIterator<Resource> listRDFTypes(boolean direct) {
        return clazz.listRDFTypes(direct);
    }

    public IntersectionClass asIntersectionClass() {
        return clazz.asIntersectionClass();
    }

    public ComplementClass asComplementClass() {
        return clazz.asComplementClass();
    }

    public boolean hasRDFType(Resource ontClass, boolean direct) {
        return clazz.hasRDFType(ontClass, direct);
    }

    public Restriction asRestriction() {
        return clazz.asRestriction();
    }

    public boolean hasRDFType(Resource ontClass) {
        return clazz.hasRDFType(ontClass);
    }

    public boolean isEnumeratedClass() {
        return clazz.isEnumeratedClass();
    }

    public boolean isUnionClass() {
        return clazz.isUnionClass();
    }

    public boolean isIntersectionClass() {
        return clazz.isIntersectionClass();
    }

    public void removeRDFType(Resource cls) {
        clazz.removeRDFType(cls);
    }

    public boolean isComplementClass() {
        return clazz.isComplementClass();
    }

    public boolean isRestriction() {
        return clazz.isRestriction();
    }

    public boolean hasRDFType(String uri) {
        return clazz.hasRDFType(uri);
    }

    public EnumeratedClass convertToEnumeratedClass(RDFList individuals) {
        return clazz.convertToEnumeratedClass(individuals);
    }

    public int getCardinality(Property p) {
        return clazz.getCardinality(p);
    }

    public IntersectionClass convertToIntersectionClass(RDFList classes) {
        return clazz.convertToIntersectionClass(classes);
    }

    public void setPropertyValue(Property property, RDFNode value) {
        clazz.setPropertyValue(property, value);
    }

    public UnionClass convertToUnionClass(RDFList classes) {
        return clazz.convertToUnionClass(classes);
    }

    public ComplementClass convertToComplementClass(Resource cls) {
        return clazz.convertToComplementClass(cls);
    }

    public RDFNode getPropertyValue(Property property) {
        return clazz.getPropertyValue(property);
    }

    public Restriction convertToRestriction(Property prop) {
        return clazz.convertToRestriction(prop);
    }

    public NodeIterator listPropertyValues(Property property) {
        return clazz.listPropertyValues(property);
    }

    public void removeProperty(Property property, RDFNode value) {
        clazz.removeProperty(property, value);
    }

    public void remove() {
        clazz.remove();
    }

    public OntProperty asProperty() {
        return clazz.asProperty();
    }

    public AnnotationProperty asAnnotationProperty() {
        return clazz.asAnnotationProperty();
    }

    public ObjectProperty asObjectProperty() {
        return clazz.asObjectProperty();
    }

    public DatatypeProperty asDatatypeProperty() {
        return clazz.asDatatypeProperty();
    }

    public Individual asIndividual() {
        return clazz.asIndividual();
    }

    public OntClass asClass() {
        return clazz.asClass();
    }

    public Ontology asOntology() {
        return clazz.asOntology();
    }

    public DataRange asDataRange() {
        return clazz.asDataRange();
    }

    public AllDifferent asAllDifferent() {
        return clazz.asAllDifferent();
    }

    public boolean isProperty() {
        return clazz.isProperty();
    }

    public boolean isAnnotationProperty() {
        return clazz.isAnnotationProperty();
    }

    public boolean isObjectProperty() {
        return clazz.isObjectProperty();
    }

    public boolean isDatatypeProperty() {
        return clazz.isDatatypeProperty();
    }

    public boolean isIndividual() {
        return clazz.isIndividual();
    }

    public boolean isClass() {
        return clazz.isClass();
    }

    public boolean isOntology() {
        return clazz.isOntology();
    }

    public boolean isDataRange() {
        return clazz.isDataRange();
    }

    public boolean isAllDifferent() {
        return clazz.isAllDifferent();
    }
}
