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

import com.google.common.collect.Lists;


/**
 * 
 * @author Sander Stolk
 */
public class ImportProblem extends AbstractProblem {
    public final static String TYPE = "com.semmtech.plugin.semmweb.core.markers.importProblem";

    public static final String ATTRIBUTE_WORKING_COPY = "Working copy";

    protected boolean isWorkingCopy;

    public ImportProblem(IResource file, boolean isWorkingCopy) {
        super(file);
        this.isWorkingCopy = isWorkingCopy;
    }

    protected ImportProblem(IResource file) {
        this(file, false);
    }

    public boolean isWorkingCopy() {
        return isWorkingCopy;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            ImportProblem otherProblem = (ImportProblem) other;
            if (isWorkingCopy() == otherProblem.isWorkingCopy()) {
                return true;
            }
        }
        return false;
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
            // add isWorkingCopy
            marker.setAttribute(ATTRIBUTE_WORKING_COPY, isWorkingCopy);
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

    protected static IMarker[] findIdenticalMarkers(ImportProblem problem) {
        if (problem == null) {
            return new IMarker[] {};
        }

        List<IMarker> identicalMarkers = Lists.newArrayList();
        IMarker importMarkers[] = Markers.find(problem.getFile(), TYPE);
        for (IMarker otherMarker : importMarkers) {
            ImportProblem otherProblem = getImportProblem(otherMarker);
            if (otherProblem != null && problem.equals(otherProblem)) {
                identicalMarkers.add(otherMarker);
            }
        }

        return identicalMarkers.toArray(new IMarker[identicalMarkers.size()]);
    }

    public static ImportProblem getImportProblem(IMarker marker) {
        if (marker == null) {
            return null;
        }

        try {
            if (!marker.getType().equals(TYPE)) {
                return null;
            }

            // retrieve file
            ImportProblem result = new ImportProblem(marker.getResource());

            getAttributesAbstractProblem(marker, result);

            // retrieve isWorkingCopy
            result.isWorkingCopy = marker.getAttribute(ATTRIBUTE_WORKING_COPY, false);

            return result;
        }
        catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ImportProblem> getImportProblems(IMarker[] markers) {
        List<ImportProblem> result = Lists.newArrayList();
        for (IMarker marker : markers) {
            ImportProblem problem = getImportProblem(marker);
            if (problem != null) {
                result.add(problem);
            }
        }
        return result;
    }

    public static List<ImportProblem> find(IResource file, boolean isWorkingCopy) {
        return getImportProblems(findMarkers(file, isWorkingCopy));
    }

    public static IMarker[] findMarkers(IResource file, boolean isWorkingCopy) {
        List<IMarker> result = Lists.newArrayList();
        IMarker[] markers = Markers.find(file, TYPE);
        for (IMarker marker : markers) {
            ImportProblem problem = getImportProblem(marker);
            if (problem.isWorkingCopy() == isWorkingCopy) {
                result.add(marker);
            }
        }
        return result.toArray(new IMarker[] {});
    }
}
