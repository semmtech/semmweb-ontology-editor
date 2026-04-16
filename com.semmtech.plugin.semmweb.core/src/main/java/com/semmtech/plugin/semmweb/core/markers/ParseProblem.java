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
public class ParseProblem extends AbstractProblem {
    public final static String TYPE = "com.semmtech.plugin.semmweb.core.markers.parseProblem";

    protected int line;

    public ParseProblem(IResource file) {
        super(file);
        line = -1;
    }

    /** Location is a human-readable message on the whereabouts of the problem. */
    public void setLocation(int line, int column) {
        if (line <= 0) {
            setLocation(null);
        }
        else {
            this.line = line;
            this.location = "Line " + line;
            if (column >= 0) {
                this.location = this.location + ", Column " + column;
            }
        }
    }

    @Override
    public void setLocation(String location) {
        super.setLocation(location);
        this.line = -1;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ParseProblem) || !super.equals(other)) {
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
            // add line number
            if (line >= 0) {
                marker.setAttribute(IMarker.LINE_NUMBER, line);
            }
            // column number can't be added, unfortunately

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

    protected static IMarker[] findIdenticalMarkers(ParseProblem problem) {
        if (problem == null) {
            return new IMarker[] {};
        }

        List<IMarker> identicalMarkers = Lists.newArrayList();
        IMarker semanticMarkers[] = Markers.find(problem.getFile(), TYPE);
        for (IMarker otherMarker : semanticMarkers) {
            ParseProblem otherProblem = getParseProblem(otherMarker);
            if (otherProblem != null && problem.equals(otherProblem)) {
                identicalMarkers.add(otherMarker);
            }
        }

        return identicalMarkers.toArray(new IMarker[identicalMarkers.size()]);
    }

    public static ParseProblem getParseProblem(IMarker marker) {
        if (marker == null) {
            return null;
        }

        try {
            if (!marker.getType().equals(TYPE)) {
                return null;
            }

            // retrieve file
            ParseProblem result = new ParseProblem(marker.getResource());

            getAttributesAbstractProblem(marker, result);

            // retrieve line
            Integer line = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);
            if (line != null) {
                result.line = line.intValue();
            }

            return result;
        }
        catch (CoreException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<ParseProblem> getParseProblems(IMarker[] markers) {
        List<ParseProblem> result = Lists.newArrayList();
        for (IMarker marker : markers) {
            ParseProblem problem = getParseProblem(marker);
            if (problem != null) {
                result.add(problem);
            }
        }
        return result;
    }

    public static List<ParseProblem> find(IResource file) {
        return getParseProblems(findMarkers(file));
    }

    public static IMarker[] findMarkers(IResource file) {
        return Markers.find(file, TYPE);
    }
}
