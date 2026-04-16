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

package com.semmtech.semantics.util;


import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDFS;


public class ClassHierarchyProvider {
    private Model model;

    private Map<Resource, List<Resource>> superClassOfMap;
    private Map<Resource, List<Resource>> subClassOfMap;

    public ClassHierarchyProvider(Model model) {
        setOntModel(model);
    }

    public void setOntModel(Model model) {
        this.model = model;
        this.superClassOfMap = Maps.newHashMap();
        this.subClassOfMap = Maps.newHashMap();
    }

    public List<Resource> getSubClasses(Resource root, boolean includeRoot) {
        if (!subClassOfMap.containsKey(root)) {
            List<Resource> subClasses = Lists.newArrayList();
            for (Statement stmt : model.listStatements(
                    new SimpleSelector(null, RDFS.subClassOf, root)).toList()) {
                Resource resource = stmt.getSubject();
                if (resource.getURI().equals(root.getURI()))
                    continue;
                if (!subClasses.contains(resource))
                    subClasses.add(resource);
            }
            subClassOfMap.put(root, subClasses);
        }
        List<Resource> descendants = Lists.newArrayList();
        if (includeRoot)
            descendants.add(root);
        for (Resource subClass : subClassOfMap.get(root))
            descendants.addAll(getSubClasses(subClass, true));

        return descendants;
    }

    public boolean isSubClassOf(Resource clazz, Resource superClass) {
        return isSubClassOf(clazz, superClass, false);
    }

    public boolean isSubClassOf(Resource clazz, Resource superClass, boolean directOnly) {
        if (clazz.getURI().equals(superClass.getURI()))
            return true;
        if (!superClassOfMap.containsKey(clazz)) {
            List<Resource> superClasses = Lists.newArrayList();
            for (Statement stmt : model.listStatements(
                    new SimpleSelector(clazz, RDFS.subClassOf, (RDFNode) null)).toList()) {
                if (!stmt.getObject().isResource())
                    continue;
                Resource resource = stmt.getObject().asResource();
                if (resource.getURI().equals(clazz.getURI()))
                    continue;
                if (!superClasses.contains(resource))
                    superClasses.add(resource);
            }
            superClassOfMap.put(clazz, superClasses);
        }
        if (directOnly) {
            return superClassOfMap.get(clazz).contains(superClass);
        }
        for (Resource s : superClassOfMap.get(clazz)) {
            if (s.getURI().equals(superClass.getURI()))
                return true;
            else if (isSubClassOf(s, superClass, false))
                return true;
        }
        return false;
    }

    public List<Resource> getSuperClasses(Resource root, boolean includeRoot) {
        if (!superClassOfMap.containsKey(root)) {
            List<Resource> superClasses = Lists.newArrayList();
            for (Statement stmt : model.listStatements(
                    new SimpleSelector(root, RDFS.subClassOf, (RDFNode) null)).toList()) {
                if (!stmt.getObject().isResource())
                    continue;
                Resource resource = stmt.getObject().asResource();
                if (resource.getURI().equals(root.getURI()))
                    continue;
                if (!superClasses.contains(resource))
                    superClasses.add(resource);
            }
            superClassOfMap.put(root, superClasses);
        }
        List<Resource> ancestors = Lists.newArrayList();
        if (includeRoot)
            ancestors.add(root);
        for (Resource superClass : superClassOfMap.get(root))
            ancestors.addAll(getSuperClasses(superClass, true));
        return ancestors;
    }

    public boolean isSuperClassOf(Resource clazz, Resource subClass) {
        return isSuperClassOf(clazz, subClass, false);
    }

    public boolean isSuperClassOf(Resource clazz, Resource subClass, boolean directOnly) {
        if (clazz.getURI().equals(subClass.getURI()))
            return true;
        if (!subClassOfMap.containsKey(clazz)) {
            List<Resource> subClasses = Lists.newArrayList();
            for (Statement stmt : model.listStatements(
                    new SimpleSelector(null, RDFS.subClassOf, clazz)).toList()) {
                Resource resource = stmt.getSubject();
                if (resource.getURI().equals(clazz.getURI()))
                    continue;
                if (!subClasses.contains(resource))
                    subClasses.add(resource);
            }
            subClassOfMap.put(clazz, subClasses);
        }
        if (directOnly) {
            return subClassOfMap.get(clazz).contains(subClass);
        }
        for (Resource s : subClassOfMap.get(clazz)) {
            if (s.getURI().equals(subClass.getURI()))
                return true;
            else if (isSuperClassOf(s, subClass, false))
                return true;
        }
        return false;
    }

    // public List<Resource> getPath(Resource from, Resource to) {
    // List<Resource> path = null;
    // if (isSubClassOf(from, to, false)) {
    // path = Lists.newArrayList();
    // }
    // else if (isSubClassOf(to, from, false)) {
    // path = Lists.newArrayList();
    //
    // }
    // return path;
    // }
}
