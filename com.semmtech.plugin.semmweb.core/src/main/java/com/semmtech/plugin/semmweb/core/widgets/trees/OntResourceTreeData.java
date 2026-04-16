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


import com.hp.hpl.jena.ontology.AllDifferent;
import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DataRange;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


public class OntResourceTreeData extends ResourceTreeData implements OntResource {
    private final OntResource resource;

    public OntResourceTreeData(OntResource resource) {
        super(resource);
        this.resource = resource;
    }

    @Override
    public void addComment(Literal l) {
        resource.addComment(l);
    }

    @Override
    public void addComment(String v, String l) {
        resource.addComment(v, l);
    }

    @Override
    public void addDifferentFrom(Resource r) {
        resource.addDifferentFrom(r);
    }

    @Override
    public void addIsDefinedBy(Resource r) {
        resource.addIsDefinedBy(r);
    }

    @Override
    public void addLabel(Literal l) {
        resource.addLabel(l);
    }

    @Override
    public void addLabel(String s, String l) {
        resource.addLabel(s, l);
    }

    @Override
    public void addRDFType(Resource t) {
        resource.addRDFType(t);
    }

    @Override
    public void addSameAs(Resource r) {
        resource.addSameAs(r);
    }

    @Override
    public void addSeeAlso(Resource r) {
        resource.addSeeAlso(r);
    }

    @Override
    public void addVersionInfo(String t) {
        resource.addVersionInfo(t);
    }

    @Override
    public AllDifferent asAllDifferent() {
        return resource.asAllDifferent();
    }

    @Override
    public AnnotationProperty asAnnotationProperty() {
        return resource.asAnnotationProperty();
    }

    @Override
    public OntClass asClass() {
        return resource.asClass();
    }

    @Override
    public DataRange asDataRange() {
        return resource.asDataRange();
    }

    @Override
    public DatatypeProperty asDatatypeProperty() {
        return resource.asDatatypeProperty();
    }

    @Override
    public Individual asIndividual() {
        return resource.asIndividual();
    }

    @Override
    public ObjectProperty asObjectProperty() {
        return resource.asObjectProperty();
    }

    @Override
    public Ontology asOntology() {
        return resource.asOntology();
    }

    @Override
    public OntProperty asProperty() {
        return resource.asProperty();
    }

    @Override
    public int getCardinality(Property prop) {
        return resource.getCardinality(prop);
    }

    @Override
    public String getComment(String c) {
        return resource.getComment(c);
    }

    @Override
    public OntResource getDifferentFrom() {
        return resource.getDifferentFrom();
    }

    @Override
    public Resource getIsDefinedBy() {
        return resource.getIsDefinedBy();
    }

    @Override
    public String getLabel(String t) {
        return resource.getLabel(t);
    }

    @Override
    public OntModel getOntModel() {
        return resource.getOntModel();
    }

    @Override
    public Profile getProfile() {
        return resource.getProfile();
    }

    @Override
    public RDFNode getPropertyValue(Property arg0) {
        return resource.getPropertyValue(arg0);
    }

    @Override
    public Resource getRDFType() {
        return resource.getRDFType();
    }

    @Override
    public Resource getRDFType(boolean direct) {
        return resource.getRDFType(direct);
    }

    @Override
    public OntResource getSameAs() {
        return resource.getSameAs();
    }

    @Override
    public Resource getSeeAlso() {
        return resource.getSeeAlso();
    }

    @Override
    public String getVersionInfo() {
        return resource.getVersionInfo();
    }

    @Override
    public boolean hasComment(Literal comment) {
        return resource.hasComment(comment);
    }

    @Override
    public boolean hasComment(String comment, String lang) {
        return resource.hasComment(comment, lang);
    }

    @Override
    public boolean hasLabel(Literal label) {
        return resource.hasLabel(label);
    }

    @Override
    public boolean hasLabel(String label, String lang) {
        return resource.hasLabel(label, lang);
    }

    @Override
    public boolean hasRDFType(Resource ontClass) {
        return resource.hasRDFType(ontClass);
    }

    @Override
    public boolean hasRDFType(String uri) {
        return resource.hasRDFType(uri);
    }

    @Override
    public boolean hasRDFType(Resource ontClass, boolean direct) {
        return resource.hasRDFType(ontClass, direct);
    }

    @Override
    public boolean hasSeeAlso(Resource res) {
        return resource.hasSeeAlso(res);
    }

    @Override
    public boolean hasVersionInfo(String info) {
        return resource.hasVersionInfo(info);
    }

    @Override
    public boolean isAllDifferent() {
        return resource.isAllDifferent();
    }

    @Override
    public boolean isAnnotationProperty() {
        return resource.isAnnotationProperty();
    }

    @Override
    public boolean isClass() {
        return resource.isClass();
    }

    @Override
    public boolean isDataRange() {
        return resource.isDataRange();
    }

    @Override
    public boolean isDatatypeProperty() {
        return resource.isDatatypeProperty();
    }

    @Override
    public boolean isDefinedBy(Resource res) {
        return resource.isDefinedBy(res);
    }

    @Override
    public boolean isDifferentFrom(Resource res) {
        return resource.isDifferentFrom(res);
    }

    @Override
    public boolean isIndividual() {
        return resource.isIndividual();
    }

    @Override
    public boolean isObjectProperty() {
        return resource.isObjectProperty();
    }

    @Override
    public boolean isOntLanguageTerm() {
        return resource.isOntLanguageTerm();
    }

    @Override
    public boolean isOntology() {
        return resource.isOntology();
    }

    @Override
    public boolean isProperty() {
        return resource.isProperty();
    }

    @Override
    public boolean isSameAs(Resource res) {
        return resource.isSameAs(res);
    }

    @Override
    public ExtendedIterator<RDFNode> listComments(String lang) {
        return resource.listComments(lang);
    }

    @Override
    public ExtendedIterator<? extends Resource> listDifferentFrom() {
        return resource.listDifferentFrom();
    }

    @Override
    public ExtendedIterator<RDFNode> listIsDefinedBy() {
        return resource.listIsDefinedBy();
    }

    @Override
    public ExtendedIterator<RDFNode> listLabels(String lang) {
        return resource.listLabels(lang);
    }

    @Override
    public NodeIterator listPropertyValues(Property prop) {
        return resource.listPropertyValues(prop);
    }

    @Override
    public ExtendedIterator<Resource> listRDFTypes(boolean direct) {
        return resource.listRDFTypes(direct);
    }

    @Override
    public ExtendedIterator<? extends Resource> listSameAs() {
        return resource.listSameAs();
    }

    @Override
    public ExtendedIterator<RDFNode> listSeeAlso() {
        return resource.listSeeAlso();
    }

    @Override
    public ExtendedIterator<String> listVersionInfo() {
        return resource.listVersionInfo();
    }

    @Override
    public void remove() {
        resource.remove();
    }

    @Override
    public void removeComment(Literal comment) {
        resource.removeComment(comment);
    }

    @Override
    public void removeComment(String comment, String lang) {
        resource.removeComment(comment, lang);
    }

    @Override
    public void removeDefinedBy(Resource res) {
        resource.removeDefinedBy(res);
    }

    @Override
    public void removeDifferentFrom(Resource res) {
        resource.removeDifferentFrom(res);
    }

    @Override
    public void removeLabel(Literal label) {
        resource.removeLabel(label);
    }

    @Override
    public void removeLabel(String label, String lang) {
        resource.removeLabel(label, lang);
    }

    @Override
    public void removeProperty(Property prop, RDFNode value) {
        resource.removeProperty(prop, value);
    }

    @Override
    public void removeRDFType(Resource cls) {
        resource.removeRDFType(cls);
    }

    @Override
    public void removeSameAs(Resource res) {
        resource.removeSameAs(res);
    }

    @Override
    public void removeSeeAlso(Resource res) {
        resource.removeSeeAlso(res);
    }

    @Override
    public void removeVersionInfo(String info) {
        resource.removeVersionInfo(info);
    }

    @Override
    public void setComment(String comment, String lang) {
        resource.setComment(comment, lang);
    }

    @Override
    public void setDifferentFrom(Resource res) {
        resource.setDifferentFrom(res);
    }

    @Override
    public void setIsDefinedBy(Resource res) {
        resource.setIsDefinedBy(res);
    }

    @Override
    public void setLabel(String label, String lang) {
        resource.setLabel(label, lang);
    }

    @Override
    public void setPropertyValue(Property prop, RDFNode value) {
        resource.setPropertyValue(prop, value);
    }

    @Override
    public void setRDFType(Resource cls) {
        resource.setRDFType(cls);
    }

    @Override
    public void setSameAs(Resource res) {
        resource.setSameAs(res);
    }

    @Override
    public void setSeeAlso(Resource res) {
        resource.setSeeAlso(res);
    }

    @Override
    public void setVersionInfo(String info) {
        resource.setVersionInfo(info);
    }
}
