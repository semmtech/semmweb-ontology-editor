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


import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Strings;


/**
 * 
 * @author Sander Stolk
 */
public abstract class AbstractProblem {
    public static final int MODE_ADD = 0;
    public static final int MODE_OVERWRITE_EXISTING_DUPLICATE = 1;
    public static final int MODE_LEAVE_EXISTING_DUPLICATE = 2;

    protected final IResource file;
    protected int severity;
    protected String message;
    protected String location;

    public AbstractProblem(IResource file) {
        this.file = file;
        this.severity = IMarker.SEVERITY_INFO;
    }

    public IResource getFile() {
        return file;
    }

    /**
     * Severity can be IMarker.SEVERITY_INFO, IMarker.SEVERITY_WARNING, or
     * IMarker.SEVERITY_ERROR. Default value is IMarker.SEVERITY_INFO.
     */
    public int getSeverity() {
        return severity;
    }

    /**
     * Severity can be IMarker.SEVERITY_INFO, IMarker.SEVERITY_WARNING, or
     * IMarker.SEVERITY_ERROR. Default value is IMarker.SEVERITY_INFO.
     */
    public void setSeverity(int severity) {
        this.severity = severity;
    }

    /** Message is a human-readable description of the problem. */
    public String getMessage() {
        return message;
    }

    /** Message is a human-readable description of the problem. */
    public void setMessage(String message) {
        this.message = message;
    }

    /** Location is a human-readable message on the whereabouts of the problem. */
    public String getLocation() {
        return location;
    }

    /** Location is a human-readable message on the whereabouts of the problem. */
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AbstractProblem)) {
            return false;
        }

        AbstractProblem problem = (AbstractProblem) other;

        if (!file.equals(problem.file)) {
            return false;
        }
        if (severity != problem.severity) {
            return false;
        }
        if (((message == null) != (problem.message == null))
                || (message != null && !message.equals(problem.message))) {
            return false;
        }
        if (((location == null) != (problem.location == null))
                || (location != null && !location.equals(problem.location))) {
            return false;
        }

        return true;
    }

    abstract public IMarker generateMarker();

    abstract public IMarker generateMarker(int mode);

    protected IMarker generateMarker(String markerType, int mode) {
        if (Strings.isNullOrEmpty(markerType) || getFile() == null) {
            return null;
        }

        if (mode != MODE_ADD) {
            IMarker identicalMarkers[] = findIdenticalMarkers_internal();
            if (mode == MODE_LEAVE_EXISTING_DUPLICATE && identicalMarkers.length > 0) {
                return identicalMarkers[0];
            }
            if (mode == MODE_OVERWRITE_EXISTING_DUPLICATE) {
                for (IMarker identicalMarker : identicalMarkers) {
                    try {
                        identicalMarker.delete();
                    }
                    catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        IMarker marker = null;
        try {
            IResource file = getFile();
            marker = file.createMarker(markerType);

            // set severity
            marker.setAttribute(IMarker.SEVERITY, getSeverity());

            // set message
            String message = getMessage();
            if (!Strings.isNullOrEmpty(message)) {
                marker.setAttribute(IMarker.MESSAGE, message);
            }

            // set location
            String location = getLocation();
            if (!Strings.isNullOrEmpty(location)) {
                marker.setAttribute(IMarker.LOCATION, location);
            }

            return marker;
        }
        catch (CoreException e) {
            e.printStackTrace();
            if (marker != null) {
                try {
                    marker.delete();
                }
                catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return null;
    }

    abstract protected IMarker[] findIdenticalMarkers_internal();

    public static boolean getAttributesAbstractProblem(IMarker marker, AbstractProblem problem)
            throws CoreException {
        // retrieve severity
        Integer severity = (Integer) marker.getAttribute(IMarker.SEVERITY);
        if (severity != null) {
            problem.setSeverity(severity.intValue());
        }

        // retrieve message
        String message = (String) marker.getAttribute(IMarker.MESSAGE);
        if (!Strings.isNullOrEmpty(message)) {
            problem.setMessage(message);
        }

        // retrieve location
        String location = (String) marker.getAttribute(IMarker.LOCATION);
        if (!Strings.isNullOrEmpty(location)) {
            problem.setLocation(location);
        }

        return true;
    }

}
