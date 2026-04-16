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


import java.util.List;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;


/**
 * Using this tree data wrapper class, ensures that nesting of the same resource
 * does not cause endless loops during retrieval or other operations performed
 * on the tree. Each ResourceTreeData object is uniquely identified by the path
 * from the root to this object. For instance if the resource ex:A is nested
 * within the same resource ex:A, the prior of these two ResourceTreeData
 * objects is identified by "[ex:A]", whereas the latter has as its identifier
 * the value "[ex:A] > [ex:A]".
 * 
 * Also since this class implements the Jena Resource interface this TreeData
 * object can be used as any other Resource.
 * 
 * @author Mike Henrichs
 * 
 */
public class ResourceTreeData extends TreeData implements Resource {

    public ResourceTreeData(Resource resource) {
        super((resource.isAnon()) ? resource.getId().toString() : resource.getURI(), resource);
    }

    public Resource getResource() {
        return (Resource) getData();
    }

    @Override
    public <T extends RDFNode> T as(Class<T> clazz) {
        return getResource().as(clazz);
    }

    @Override
    public Literal asLiteral() {
        return getResource().asLiteral();
    }

    @Override
    public Resource asResource() {
        return getResource().asResource();
    }

    @Override
    public <T extends RDFNode> boolean canAs(Class<T> clazz) {
        return getResource().canAs(clazz);
    }

    @Override
    public Model getModel() {
        return getResource().getModel();
    }

    @Override
    public boolean isAnon() {
        return getResource().isAnon();
    }

    @Override
    public boolean isLiteral() {
        return getResource().isLiteral();
    }

    @Override
    public boolean isResource() {
        return getResource().isResource();
    }

    @Override
    public boolean isURIResource() {
        return getResource().isURIResource();
    }

    @Override
    public Object visitWith(RDFVisitor visitor) {
        return getResource().visitWith(visitor);
    }

    @Override
    public Node asNode() {
        return getResource().asNode();
    }

    @Override
    public Resource abort() {
        return getResource().abort();
    }

    @Override
    public Resource addLiteral(Property prop, boolean o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addLiteral(Property prop, long o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addLiteral(Property prop, char o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addLiteral(Property prop, double o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addLiteral(Property prop, float o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addLiteral(Property prop, Object o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addLiteral(Property prop, Literal o) {
        return getResource().addLiteral(prop, o);
    }

    @Override
    public Resource addProperty(Property prop, String o) {
        return getResource().addProperty(prop, o);
    }

    @Override
    public Resource addProperty(Property prop, RDFNode o) {
        return getResource().addProperty(prop, o);
    }

    @Override
    public Resource addProperty(Property prop, String o, String l) {
        return getResource().addProperty(prop, o, l);
    }

    @Override
    public Resource addProperty(Property prop, String o, RDFDatatype t) {
        return getResource().addProperty(prop, o, t);
    }

    @Override
    public Resource begin() {
        return getResource().begin();
    }

    @Override
    public Resource commit() {
        return getResource().commit();
    }

    @Override
    public AnonId getId() {
        return getResource().getId();
    }

    @Override
    public String getLocalName() {
        return getResource().getLocalName();
    }

    @Override
    public String getNameSpace() {
        return getResource().getNameSpace();
    }

    @Override
    public Statement getProperty(Property prop) {
        return getResource().getProperty(prop);
    }

    @Override
    public Resource getPropertyResourceValue(Property prop) {
        return getResource().getPropertyResourceValue(prop);
    }

    @Override
    public Statement getRequiredProperty(Property prop) {
        return getResource().getRequiredProperty(prop);
    }

    @Override
    public String getURI() {
        return getResource().getURI();
    }

    @Override
    public boolean hasLiteral(Property prop, boolean o) {
        return getResource().hasLiteral(prop, o);
    }

    @Override
    public boolean hasLiteral(Property prop, long o) {
        return getResource().hasLiteral(prop, o);
    }

    @Override
    public boolean hasLiteral(Property prop, char o) {
        return getResource().hasLiteral(prop, o);
    }

    @Override
    public boolean hasLiteral(Property prop, double o) {
        return getResource().hasLiteral(prop, o);
    }

    @Override
    public boolean hasLiteral(Property prop, float o) {
        return getResource().hasLiteral(prop, o);
    }

    @Override
    public boolean hasLiteral(Property prop, Object o) {
        return getResource().hasLiteral(prop, o);
    }

    @Override
    public boolean hasProperty(Property prop) {
        return getResource().hasProperty(prop);
    }

    @Override
    public boolean hasProperty(Property prop, String o) {
        return getResource().hasProperty(prop, o);
    }

    @Override
    public boolean hasProperty(Property prop, RDFNode o) {
        return getResource().hasProperty(prop, o);
    }

    @Override
    public boolean hasProperty(Property prop, String o, String l) {
        return getResource().hasProperty(prop, o, l);
    }

    @Override
    public boolean hasURI(String uri) {
        return getResource().hasURI(uri);
    }

    @Override
    public Resource inModel(Model model) {
        return getResource().inModel(model);
    }

    @Override
    public StmtIterator listProperties() {
        return getResource().listProperties();
    }

    @Override
    public StmtIterator listProperties(Property prop) {
        return getResource().listProperties(prop);
    }

    @Override
    public Resource removeAll(Property prop) {
        return getResource().removeAll(prop);
    }

    @Override
    public Resource removeProperties() {
        return getResource().removeProperties();
    }

    public static ResourceTreeData createItemFromPath(List<Resource> path) {
        if (path == null) {
            return null;
        }
        if (path.size() == 0) {
            return null;
        }
        ResourceTreeData current = null;
        for (Resource resource : path) {
            ResourceTreeData parent = current;
            current = new ResourceTreeData(resource);
            current.setParent(parent);
        }
        return current;
    }

    // private String getHashable() {
    // return getPath().toString();
    // }
    //
    // @Override
    // public int hashCode() {
    // return getHashable().hashCode();
    // }
    //
    // @Override
    // public boolean equals(Object obj) {
    // if (obj == null)
    // return false;
    // if (!(obj instanceof ResourceTreeData))
    // return false;
    // return equals((ResourceTreeData)obj);
    // }
    //
    // private boolean equals(ResourceTreeData other) {
    // return getPath().equals(other.getPath());
    // }
}
