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

package com.semmtech.plugin.semmweb.core.model;


import java.util.List;

import com.hp.hpl.jena.ontology.OntResource;


/**
 * 
 * @author Sander Stolk
 */
public interface OpenResourceEventListener {

    /**
     * This method is called when a resource has been opened. Note that if the
     * resource was already open in the first place (regardless of whether it
     * had been the active open resource), this method will not be called. Only
     * the <code>resourceActivated</code> method will be called in such cases.
     * As the opening of a resource tends to activate that resource as well
     * (i.e. showing it to the user), a call to <code>resourceActivated</code>
     * usually occurs directly after.
     * 
     * @see #resourceActivated(OntResource)
     */
    public void resourceOpened(OntResource resource);

    /**
     * This method is called when an open resource is shown to the user. When a
     * resource is no longer shown to the user, this method is called with null
     * as <code>resource</code>.
     * 
     * @param resource
     *            The activated (or shown) resource, or null if no resource is
     *            shown any more.
     */
    public void resourceActivated(OntResource resource);

    /**
     * This method is called when an open resource has been closed. Note that
     * this does not necessarily entail that this resource was also the active
     * open resource (i.e. the one that was displayed to the user at the moment
     * of closing). If the resource was the active open resource,
     * <code>resourceActivated</code> will be called.
     * 
     * @see #resourceActivated(OntResource)
     */
    public void resourceClosed(OntResource resource);

    /**
     * This method is called when one or more open resources have been closed.
     * In addition, <code>resourceClosed()</code> will be called separately for
     * each of the closed resources.
     * 
     * @see #resourceClosed(OntResource)
     */
    public void resourcesClosed(List<OntResource> resources);

}
