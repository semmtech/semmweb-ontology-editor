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

package com.semmtech.plugin.semmweb.core.markers;


import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Resource;


/**
 * 
 * @author Sander Stolk
 */
public class SemanticProblem extends AbstractProblem {
    public final static String TYPE = "com.semmtech.plugin.semmweb.core.markers.semanticProblem";

    public static final String ATTRIBUTE_RDF_RESOURCE = "RDF resource";
    public static final String ATTRIBUTE_RDF_PATH = "Property path";
    public static final String ATTRIBUTE_SEMANTIC_SOURCE = "Semantic source";

    protected String rdfResource;
    protected String propertyPath;
    protected String semanticSource;

    public SemanticProblem(IResource file) {
        super(file);
    }

    /**
     * RdfResource is the full URI (or id, in case of anonymous nodes) of the
     * node that has the problem.
     */
    public String getRdfResource() {
        return rdfResource;
    }

    /**
     * RdfResource is the full URI (or id, in case of anonymous nodes) of the
     * node that has the problem.
     */
    public void setRdfResource(String rdfResource) {
        this.rdfResource = rdfResource;
    }

    /**
     * PropertyPath is the full URI of the property to which the problem is
     * related.
     */
    public String getPropertyPath() {
        return propertyPath;
    }

    /**
     * PropertyPath is the full URI of the property to which the problem is
     * related.
     */
    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    /**
     * SemanticSource is the full URI of the resource that detected and
     * generated the problem.
     */
    public String getSemanticSource() {
        return semanticSource;
    }

    /**
     * SemanticSource is the full URI of the resource that detected and
     * generated the problem.
     */
    public void setSemanticSource(String source) {
        this.semanticSource = source;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SemanticProblem) || !super.equals(other)) {
            return false;
        }

        SemanticProblem problem = (SemanticProblem) other;

        if (((rdfResource == null) != (problem.rdfResource == null))
                || (rdfResource != null && !rdfResource.equals(problem.rdfResource))) {
            return false;
        }
        if (((propertyPath == null) != (problem.propertyPath == null))
                || (propertyPath != null && !propertyPath.equals(problem.propertyPath))) {
            return false;
        }
        if (((semanticSource == null) != (problem.semanticSource == null))
                || (semanticSource != null && !semanticSource.equals(problem.semanticSource))) {
            return false;
        }

        return true;
    }

    @Override
    public IMarker generateMarker() {
        return generateMarker(MODE_OVERWRITE_EXISTING_DUPLICATE);
    }

    @Override
    public IMarker generateMarker(int mode) {
        IMarker marker = generateMarker(TYPE, mode);
        if (marker == null) {
            return null;
        }

        try {
            // set RDF resource
            String rdfResource = getRdfResource();
            if (!Strings.isNullOrEmpty(rdfResource)) {
                marker.setAttribute(ATTRIBUTE_RDF_RESOURCE, rdfResource);
            }

            // set RDF path
            String rdfPath = getPropertyPath();
            if (!Strings.isNullOrEmpty(rdfPath)) {
                marker.setAttribute(ATTRIBUTE_RDF_PATH, rdfPath);
            }

            // set source
            String source = getSemanticSource();
            if (!Strings.isNullOrEmpty(source)) {
                marker.setAttribute(ATTRIBUTE_SEMANTIC_SOURCE, source);
            }

            return marker;
        }
        catch (CoreException e) {
            e.printStackTrace();
            try {
                marker.delete();
            }
            catch (CoreException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected IMarker[] findIdenticalMarkers_internal() {
        return findIdenticalMarkers(this);
    }

    protected static IMarker[] findIdenticalMarkers(SemanticProblem problem) {
        if (problem == null) {
            return new IMarker[] {};
        }

        List<IMarker> identicalMarkers = Lists.newArrayList();
        IMarker semanticMarkers[] = Markers.find(problem.getFile(), TYPE);
        for (IMarker otherMarker : semanticMarkers) {
            SemanticProblem otherProblem = getSemanticProblem(otherMarker);
            if (otherProblem != null && problem.equals(otherProblem)) {
                identicalMarkers.add(otherMarker);
            }
        }

        return identicalMarkers.toArray(new IMarker[identicalMarkers.size()]);
    }

    public static SemanticProblem getSemanticProblem(IMarker marker) {
        if (marker == null) {
            return null;
        }

        try {
            if (!marker.getType().equals(TYPE)) {
                return null;
            }

            // retrieve file
            SemanticProblem result = new SemanticProblem(marker.getResource());

            getAttributesAbstractProblem(marker, result);

            // retrieve RDF resource
            String rdfResource = (String) marker.getAttribute(ATTRIBUTE_RDF_RESOURCE);
            if (!Strings.isNullOrEmpty(rdfResource)) {
                result.setRdfResource(rdfResource);
            }

            // retrieve RDF path
            String rdfPath = (String) marker.getAttribute(ATTRIBUTE_RDF_PATH);
            if (!Strings.isNullOrEmpty(rdfPath)) {
                result.setPropertyPath(rdfPath);
            }

            // retrieve source
            String source = (String) marker.getAttribute(ATTRIBUTE_SEMANTIC_SOURCE);
            if (!Strings.isNullOrEmpty(source)) {
                result.setSemanticSource(source);
            }

            return result;
        }
        catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<SemanticProblem> getSemanticProblems(IMarker[] markers) {
        List<SemanticProblem> result = Lists.newArrayList();
        for (IMarker marker : markers) {
            SemanticProblem problem = getSemanticProblem(marker);
            if (problem != null) {
                result.add(problem);
            }
        }
        return result;
    }

    public static List<SemanticProblem> find(IResource file) {
        return getSemanticProblems(findMarkers(file));
    }

    public static IMarker[] findMarkers(IResource file) {
        return Markers.find(file, TYPE);
    }

    public static List<SemanticProblem> find(IResource file, Resource resource) {
        return getSemanticProblems(findMarkers(file, resource));
    }

    public static IMarker[] findMarkers(IResource file, Resource resource) {
        String resourceId = getResourceIdentifier(resource);
        IMarker semanticMarkers[] = findMarkers(file);

        List<IMarker> result = Lists.newArrayList();
        for (IMarker semanticMarker : semanticMarkers) {
            SemanticProblem problem = getSemanticProblem(semanticMarker);
            if (((resourceId == null) == (problem.getRdfResource() == null))
                    && (resourceId == null || resourceId.equals(problem.getRdfResource()))) {
                result.add(semanticMarker);
            }
        }

        return result.toArray(new IMarker[result.size()]);
    }

    protected static String getResourceIdentifier(Resource resource) {
        if (resource == null) {
            return null;
        }
        if (resource.isURIResource()) {
            return resource.getURI();
        }
        return resource.getId().toString();
    }
}
