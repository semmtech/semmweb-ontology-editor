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

package com.semmtech.semantics.vocabulary;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 * This is a basic RDF vocabulary that provides the Semantic Web community with
 * a namespace for representing lat(itude), long(itude) and other information
 * about spatially-located things, using WGS84 as a reference datum.
 * 
 * @author Mike Henrichs
 * 
 */
public class WGS84 {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://www.w3.org/2003/01/geo/wgs84_pos#";

    public static final Resource NAMESPACE = model.createResource(NS);

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(NS + local);
    }

    /**
     * Anything with temporal extent, i.e. duration. e.g. the taking of a
     * photograph, a scheduled meeting, a GPS timestamped trackpoint.
     */
    public static final Resource TemporalThing = resource("TemporalThing");
    /**
     * Anything with spatial extent, i.e. size, shape, or position. e.g. people,
     * places, bowling balls, as well as abstract areas like cubes.
     */
    // public static final Resource SpatialThing = resource("SpatialThing");
    /**
     * An event, with negligible duration but unique date-time. Examples include
     * a GPS timestamped trackpoint, the taking of a photograph, and the sending
     * of a message.
     */
    // public static final Resource Event = resource("Event");
    /**
     * A point, typically described using a coordinate system relative to Earth,
     * such as WGS84.
     */
    public static final Resource Point = resource("Point");

    /**
     * The WGS84 latitude of a SpatialThing (decimal degrees).
     */
    public static final Property latitude = property("lat");
    /**
     * The date time of an event, including fully qualified date, and Z or
     * offset from UTC. XML Schema standard date-time format.
     */
    // public static final Property time = property("time");
    /**
     * The relation between something and the point, or other geometrical thing
     * in space, where it is. For example, the realtionship between a radio
     * tower and a Point with a given lat and long. Or a relationship between a
     * park and its outline as a closed arc of points, or a road and its
     * location as a arc (a sequence of points). Clearly in practice there will
     * be limit to the accuracy of any such statement, but one would expect an
     * accuracy appropriate for the size of the object and uses such as mapping.
     */
    public static final Property location = property("location");
    /**
     * The WGS84 longitude of a SpatialThing (decimal degrees).
     */
    public static final Property longitude = property("long");
    /**
     * The WGS84 altitude of a SpatialThing (decimal meters above the local
     * reference ellipsoid).
     */
    public static final Property alt = property("alt");
    /**
     * A comma-separated representation of a latitude, longitude coordinate.
     */
    public static final Property lat_long = property("lat_long");

}
