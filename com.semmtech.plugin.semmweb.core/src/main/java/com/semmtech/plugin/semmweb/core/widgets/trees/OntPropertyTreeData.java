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
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.FunctionalProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.InverseFunctionalProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.ontology.SymmetricProperty;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
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
public class OntPropertyTreeData extends OntResourceTreeData implements OntProperty {
    private final OntProperty property;

    public OntPropertyTreeData(OntProperty property) {
        super(property);
        this.property = property;
    }

    public Node asNode() {
        return property.asNode();
    }

    public OntModel getOntModel() {
        return property.getOntModel();
    }

    public boolean isAnon() {
        return property.isAnon();
    }

    public void setSuperProperty(Property prop) {
        property.setSuperProperty(prop);
    }

    public int getOrdinal() {
        return property.getOrdinal();
    }

    public boolean isLiteral() {
        return property.isLiteral();
    }

    public Profile getProfile() {
        return property.getProfile();
    }

    public boolean isURIResource() {
        return property.isURIResource();
    }

    public void addSuperProperty(Property prop) {
        property.addSuperProperty(prop);
    }

    public boolean isResource() {
        return property.isResource();
    }

    public boolean isOntLanguageTerm() {
        return property.isOntLanguageTerm();
    }

    public <T extends RDFNode> T as(Class<T> view) {
        return property.as(view);
    }

    public OntProperty getSuperProperty() {
        return property.getSuperProperty();
    }

    public AnonId getId() {
        return property.getId();
    }

    public void setSameAs(Resource res) {
        property.setSameAs(res);
    }

    public ExtendedIterator<? extends OntProperty> listSuperProperties() {
        return property.listSuperProperties();
    }

    public <T extends RDFNode> boolean canAs(Class<T> view) {
        return property.canAs(view);
    }

    public Property inModel(Model m) {
        return property.inModel(m);
    }

    public void addSameAs(Resource res) {
        property.addSameAs(res);
    }

    public boolean hasURI(String uri) {
        return property.hasURI(uri);
    }

    public ExtendedIterator<? extends OntProperty> listSuperProperties(boolean direct) {
        return property.listSuperProperties(direct);
    }

    public String getURI() {
        return property.getURI();
    }

    public Model getModel() {
        return property.getModel();
    }

    public OntResource getSameAs() {
        return property.getSameAs();
    }

    public String getNameSpace() {
        return property.getNameSpace();
    }

    public String getLocalName() {
        return property.getLocalName();
    }

    public ExtendedIterator<? extends Resource> listSameAs() {
        return property.listSameAs();
    }

    public String toString() {
        return property.toString();
    }

    public Object visitWith(RDFVisitor rv) {
        return property.visitWith(rv);
    }

    public boolean hasSuperProperty(Property prop, boolean direct) {
        return property.hasSuperProperty(prop, direct);
    }

    public boolean equals(Object o) {
        return property.equals(o);
    }

    public Resource asResource() {
        return property.asResource();
    }

    public boolean isSameAs(Resource res) {
        return property.isSameAs(res);
    }

    public Literal asLiteral() {
        return property.asLiteral();
    }

    public void removeSuperProperty(Property prop) {
        property.removeSuperProperty(prop);
    }

    public void removeSameAs(Resource res) {
        property.removeSameAs(res);
    }

    public Statement getRequiredProperty(Property p) {
        return property.getRequiredProperty(p);
    }

    public void setSubProperty(Property prop) {
        property.setSubProperty(prop);
    }

    public void setDifferentFrom(Resource res) {
        property.setDifferentFrom(res);
    }

    public Statement getProperty(Property p) {
        return property.getProperty(p);
    }

    public void addSubProperty(Property prop) {
        property.addSubProperty(prop);
    }

    public void addDifferentFrom(Resource res) {
        property.addDifferentFrom(res);
    }

    public OntProperty getSubProperty() {
        return property.getSubProperty();
    }

    public StmtIterator listProperties(Property p) {
        return property.listProperties(p);
    }

    public OntResource getDifferentFrom() {
        return property.getDifferentFrom();
    }

    public ExtendedIterator<? extends OntProperty> listSubProperties() {
        return property.listSubProperties();
    }

    public StmtIterator listProperties() {
        return property.listProperties();
    }

    public ExtendedIterator<? extends Resource> listDifferentFrom() {
        return property.listDifferentFrom();
    }

    public Resource addLiteral(Property p, boolean o) {
        return property.addLiteral(p, o);
    }

    public ExtendedIterator<? extends OntProperty> listSubProperties(boolean direct) {
        return property.listSubProperties(direct);
    }

    public boolean isDifferentFrom(Resource res) {
        return property.isDifferentFrom(res);
    }

    public Resource addLiteral(Property p, long o) {
        return property.addLiteral(p, o);
    }

    public void removeDifferentFrom(Resource res) {
        property.removeDifferentFrom(res);
    }

    public Resource addLiteral(Property p, char o) {
        return property.addLiteral(p, o);
    }

    public boolean hasSubProperty(Property prop, boolean direct) {
        return property.hasSubProperty(prop, direct);
    }

    public void setSeeAlso(Resource res) {
        property.setSeeAlso(res);
    }

    public Resource addLiteral(Property value, double d) {
        return property.addLiteral(value, d);
    }

    public void addSeeAlso(Resource res) {
        property.addSeeAlso(res);
    }

    public void removeSubProperty(Property prop) {
        property.removeSubProperty(prop);
    }

    public Resource addLiteral(Property value, float d) {
        return property.addLiteral(value, d);
    }

    public Resource getSeeAlso() {
        return property.getSeeAlso();
    }

    public void setDomain(Resource res) {
        property.setDomain(res);
    }

    public Resource addLiteral(Property p, Object o) {
        return property.addLiteral(p, o);
    }

    public ExtendedIterator<RDFNode> listSeeAlso() {
        return property.listSeeAlso();
    }

    public Resource addLiteral(Property p, Literal o) {
        return property.addLiteral(p, o);
    }

    public void addDomain(Resource res) {
        property.addDomain(res);
    }

    public boolean hasSeeAlso(Resource res) {
        return property.hasSeeAlso(res);
    }

    public OntResource getDomain() {
        return property.getDomain();
    }

    public Resource addProperty(Property p, String o) {
        return property.addProperty(p, o);
    }

    public void removeSeeAlso(Resource res) {
        property.removeSeeAlso(res);
    }

    public ExtendedIterator<? extends OntResource> listDomain() {
        return property.listDomain();
    }

    public Resource addProperty(Property p, String o, String l) {
        return property.addProperty(p, o, l);
    }

    public void setIsDefinedBy(Resource res) {
        property.setIsDefinedBy(res);
    }

    public boolean hasDomain(Resource res) {
        return property.hasDomain(res);
    }

    public Resource addProperty(Property p, String lexicalForm, RDFDatatype datatype) {
        return property.addProperty(p, lexicalForm, datatype);
    }

    public void addIsDefinedBy(Resource res) {
        property.addIsDefinedBy(res);
    }

    public void removeDomain(Resource cls) {
        property.removeDomain(cls);
    }

    public Resource getIsDefinedBy() {
        return property.getIsDefinedBy();
    }

    public Resource addProperty(Property p, RDFNode o) {
        return property.addProperty(p, o);
    }

    public void setRange(Resource res) {
        property.setRange(res);
    }

    public ExtendedIterator<RDFNode> listIsDefinedBy() {
        return property.listIsDefinedBy();
    }

    public boolean hasProperty(Property p) {
        return property.hasProperty(p);
    }

    public boolean hasLiteral(Property p, boolean o) {
        return property.hasLiteral(p, o);
    }

    public boolean isDefinedBy(Resource res) {
        return property.isDefinedBy(res);
    }

    public boolean hasLiteral(Property p, long o) {
        return property.hasLiteral(p, o);
    }

    public void removeDefinedBy(Resource res) {
        property.removeDefinedBy(res);
    }

    public void addRange(Resource res) {
        property.addRange(res);
    }

    public boolean hasLiteral(Property p, char o) {
        return property.hasLiteral(p, o);
    }

    public void setVersionInfo(String info) {
        property.setVersionInfo(info);
    }

    public boolean hasLiteral(Property p, double o) {
        return property.hasLiteral(p, o);
    }

    public boolean hasLiteral(Property p, float o) {
        return property.hasLiteral(p, o);
    }

    public void addVersionInfo(String info) {
        property.addVersionInfo(info);
    }

    public OntResource getRange() {
        return property.getRange();
    }

    public boolean hasLiteral(Property p, Object o) {
        return property.hasLiteral(p, o);
    }

    public String getVersionInfo() {
        return property.getVersionInfo();
    }

    public boolean hasProperty(Property p, String o) {
        return property.hasProperty(p, o);
    }

    public ExtendedIterator<? extends OntResource> listRange() {
        return property.listRange();
    }

    public ExtendedIterator<String> listVersionInfo() {
        return property.listVersionInfo();
    }

    public boolean hasProperty(Property p, String o, String l) {
        return property.hasProperty(p, o, l);
    }

    public boolean hasRange(Resource res) {
        return property.hasRange(res);
    }

    public boolean hasVersionInfo(String info) {
        return property.hasVersionInfo(info);
    }

    public boolean hasProperty(Property p, RDFNode o) {
        return property.hasProperty(p, o);
    }

    public void removeVersionInfo(String info) {
        property.removeVersionInfo(info);
    }

    public void removeRange(Resource cls) {
        property.removeRange(cls);
    }

    public Resource removeProperties() {
        return property.removeProperties();
    }

    public void setLabel(String label, String lang) {
        property.setLabel(label, lang);
    }

    public Resource removeAll(Property p) {
        return property.removeAll(p);
    }

    public void setEquivalentProperty(Property prop) {
        property.setEquivalentProperty(prop);
    }

    public Resource begin() {
        return property.begin();
    }

    public Resource abort() {
        return property.abort();
    }

    public void addLabel(String label, String lang) {
        property.addLabel(label, lang);
    }

    public Resource commit() {
        return property.commit();
    }

    public void addEquivalentProperty(Property prop) {
        property.addEquivalentProperty(prop);
    }

    public Resource getPropertyResourceValue(Property p) {
        return property.getPropertyResourceValue(p);
    }

    public void addLabel(Literal label) {
        property.addLabel(label);
    }

    public OntProperty getEquivalentProperty() {
        return property.getEquivalentProperty();
    }

    public String getLabel(String lang) {
        return property.getLabel(lang);
    }

    public ExtendedIterator<? extends OntProperty> listEquivalentProperties() {
        return property.listEquivalentProperties();
    }

    public ExtendedIterator<RDFNode> listLabels(String lang) {
        return property.listLabels(lang);
    }

    public boolean hasEquivalentProperty(Property prop) {
        return property.hasEquivalentProperty(prop);
    }

    public boolean hasLabel(String label, String lang) {
        return property.hasLabel(label, lang);
    }

    public void removeEquivalentProperty(Property prop) {
        property.removeEquivalentProperty(prop);
    }

    public boolean hasLabel(Literal label) {
        return property.hasLabel(label);
    }

    public void removeLabel(String label, String lang) {
        property.removeLabel(label, lang);
    }

    public void setInverseOf(Property prop) {
        property.setInverseOf(prop);
    }

    public void removeLabel(Literal label) {
        property.removeLabel(label);
    }

    public void addInverseOf(Property prop) {
        property.addInverseOf(prop);
    }

    public void setComment(String comment, String lang) {
        property.setComment(comment, lang);
    }

    public OntProperty getInverseOf() {
        return property.getInverseOf();
    }

    public void addComment(String comment, String lang) {
        property.addComment(comment, lang);
    }

    public ExtendedIterator<? extends OntProperty> listInverseOf() {
        return property.listInverseOf();
    }

    public void addComment(Literal comment) {
        property.addComment(comment);
    }

    public String getComment(String lang) {
        return property.getComment(lang);
    }

    public boolean isInverseOf(Property prop) {
        return property.isInverseOf(prop);
    }

    public void removeInverseProperty(Property prop) {
        property.removeInverseProperty(prop);
    }

    public ExtendedIterator<RDFNode> listComments(String lang) {
        return property.listComments(lang);
    }

    public FunctionalProperty asFunctionalProperty() {
        return property.asFunctionalProperty();
    }

    public boolean hasComment(String comment, String lang) {
        return property.hasComment(comment, lang);
    }

    public DatatypeProperty asDatatypeProperty() {
        return property.asDatatypeProperty();
    }

    public boolean hasComment(Literal comment) {
        return property.hasComment(comment);
    }

    public void removeComment(String comment, String lang) {
        property.removeComment(comment, lang);
    }

    public ObjectProperty asObjectProperty() {
        return property.asObjectProperty();
    }

    public void removeComment(Literal comment) {
        property.removeComment(comment);
    }

    public TransitiveProperty asTransitiveProperty() {
        return property.asTransitiveProperty();
    }

    public void setRDFType(Resource cls) {
        property.setRDFType(cls);
    }

    public InverseFunctionalProperty asInverseFunctionalProperty() {
        return property.asInverseFunctionalProperty();
    }

    public void addRDFType(Resource cls) {
        property.addRDFType(cls);
    }

    public SymmetricProperty asSymmetricProperty() {
        return property.asSymmetricProperty();
    }

    public Resource getRDFType() {
        return property.getRDFType();
    }

    public FunctionalProperty convertToFunctionalProperty() {
        return property.convertToFunctionalProperty();
    }

    public DatatypeProperty convertToDatatypeProperty() {
        return property.convertToDatatypeProperty();
    }

    public Resource getRDFType(boolean direct) {
        return property.getRDFType(direct);
    }

    public ObjectProperty convertToObjectProperty() {
        return property.convertToObjectProperty();
    }

    public TransitiveProperty convertToTransitiveProperty() {
        return property.convertToTransitiveProperty();
    }

    public ExtendedIterator<Resource> listRDFTypes(boolean direct) {
        return property.listRDFTypes(direct);
    }

    public InverseFunctionalProperty convertToInverseFunctionalProperty() {
        return property.convertToInverseFunctionalProperty();
    }

    public SymmetricProperty convertToSymmetricProperty() {
        return property.convertToSymmetricProperty();
    }

    public boolean hasRDFType(Resource ontClass, boolean direct) {
        return property.hasRDFType(ontClass, direct);
    }

    public boolean isFunctionalProperty() {
        return property.isFunctionalProperty();
    }

    public boolean isDatatypeProperty() {
        return property.isDatatypeProperty();
    }

    public boolean hasRDFType(Resource ontClass) {
        return property.hasRDFType(ontClass);
    }

    public boolean isObjectProperty() {
        return property.isObjectProperty();
    }

    public boolean isTransitiveProperty() {
        return property.isTransitiveProperty();
    }

    public boolean isInverseFunctionalProperty() {
        return property.isInverseFunctionalProperty();
    }

    public void removeRDFType(Resource cls) {
        property.removeRDFType(cls);
    }

    public boolean isSymmetricProperty() {
        return property.isSymmetricProperty();
    }

    public boolean hasRDFType(String uri) {
        return property.hasRDFType(uri);
    }

    public OntProperty getInverse() {
        return property.getInverse();
    }

    public int getCardinality(Property p) {
        return property.getCardinality(p);
    }

    public void setPropertyValue(Property property, RDFNode value) {
        this.property.setPropertyValue(property, value);
    }

    public ExtendedIterator<? extends OntProperty> listInverse() {
        return property.listInverse();
    }

    public RDFNode getPropertyValue(Property property) {
        return this.property.getPropertyValue(property);
    }

    public boolean hasInverse() {
        return property.hasInverse();
    }

    public ExtendedIterator<? extends OntClass> listDeclaringClasses() {
        return property.listDeclaringClasses();
    }

    public NodeIterator listPropertyValues(Property property) {
        return this.property.listPropertyValues(property);
    }

    public ExtendedIterator<? extends OntClass> listDeclaringClasses(boolean direct) {
        return property.listDeclaringClasses(direct);
    }

    public void removeProperty(Property property, RDFNode value) {
        this.property.removeProperty(property, value);
    }

    public void remove() {
        property.remove();
    }

    public ExtendedIterator<Restriction> listReferringRestrictions() {
        return property.listReferringRestrictions();
    }

    public OntProperty asProperty() {
        return property.asProperty();
    }

    public AnnotationProperty asAnnotationProperty() {
        return property.asAnnotationProperty();
    }

    public Individual asIndividual() {
        return property.asIndividual();
    }

    public OntClass asClass() {
        return property.asClass();
    }

    public Ontology asOntology() {
        return property.asOntology();
    }

    public DataRange asDataRange() {
        return property.asDataRange();
    }

    public AllDifferent asAllDifferent() {
        return property.asAllDifferent();
    }

    public boolean isProperty() {
        return property.isProperty();
    }

    public boolean isAnnotationProperty() {
        return property.isAnnotationProperty();
    }

    public boolean isIndividual() {
        return property.isIndividual();
    }

    public boolean isClass() {
        return property.isClass();
    }

    public boolean isOntology() {
        return property.isOntology();
    }

    public boolean isDataRange() {
        return property.isDataRange();
    }

    public boolean isAllDifferent() {
        return property.isAllDifferent();
    }

}
