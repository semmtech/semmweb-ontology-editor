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
 * Creative Commons Rights Expression Language. http://creativecommons.org/ns#
 * 
 * @author Sander Stolk
 */
public class CC {
    private static Model model = ModelFactory.createDefaultModel();

    public static final String NS = "http://creativecommons.org/ns#";

    public static final Resource NAMESPACE = model.createResource(NS);

    protected static final Resource resource(String local) {
        return ResourceFactory.createResource(NS + local);
    }

    protected static final Property property(String local) {
        return ResourceFactory.createProperty(NS + local);
    }

    // Classes
    public static final Resource Jurisdiction = resource("Jurisdiction");
    public static final Resource License = resource("License");
    public static final Resource Permission = resource("Permission");
    public static final Resource Prohibition = resource("Prohibition");
    public static final Resource Requirement = resource("Requirement");
    public static final Resource Work = resource("Work");

    // Properties
    public static final Property attributionName = property("attributionName");
    public static final Property attributionURL = property("attributionURL");
    public static final Property deprecatedOn = property("deprecatedOn");
    public static final Property jurisdiction = property("jurisdiction");
    public static final Property legalcode = property("legalcode");
    public static final Property license = property("license");
    public static final Property morePermissions = property("morePermissions");
    public static final Property permits = property("permits");
    public static final Property prohibits = property("prohibits");
    public static final Property requires = property("requires");
    public static final Property useGuidelines = property("useGuidelines");

    // Instances of Permission
    public static final Resource DerivativeWorks = resource("DerivativeWorks");
    public static final Resource Distribution = resource("Distribution");
    public static final Resource Reproduction = resource("Reproduction");
    public static final Resource Sharing = resource("Sharing");

    // Instances of Prohibition
    public static final Resource CommercialUse = resource("CommercialUse");
    public static final Resource HighIncomeNationUse = resource("HighIncomeNationUse");

    // Instances of Requirement
    public static final Resource Attribution = resource("Attribution");
    public static final Resource Copyleft = resource("Copyleft");
    public static final Resource LesserCopyleft = resource("LesserCopyleft");
    public static final Resource Notice = resource("Notice");
    public static final Resource ShareAlike = resource("ShareAlike");
    public static final Resource SourceCode = resource("SourceCode");

}
