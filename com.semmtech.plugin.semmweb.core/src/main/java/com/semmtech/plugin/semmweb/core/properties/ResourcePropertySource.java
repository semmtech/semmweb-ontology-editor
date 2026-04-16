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

package com.semmtech.plugin.semmweb.core.properties;


import java.util.List;
import java.util.Map;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;


public class ResourcePropertySource extends PropertySourceAdapter {
    private static final String PROPERTY_ANONYMOUS = "resource.isAnonymous";
    private static final String PROPERTY_URI = "resource.uri";
    private static final String PROPERTY_ID = "resource.id";

    private final Resource resource;
    private IPropertyDescriptor[] descriptors;

    private Map<String, Property> properties;

    public ResourcePropertySource(Resource resource) {
        this.resource = resource;
        initialize();
    }

    private void initialize() {
        properties = Maps.newLinkedHashMap();
        for (Statement stmt : resource.listProperties().toList()) {
            Property property = stmt.getPredicate();
            properties.put(property.getURI(), property);
        }
    }

    @Override
    public Object getPropertyValue(Object id) {
        if (id.equals(PROPERTY_ANONYMOUS))
            return new Boolean(resource.isAnon());
        else if (id.equals(PROPERTY_ID) && resource.isAnon())
            return resource.getId().toString();
        else if (id.equals(PROPERTY_URI) && !resource.isAnon())
            return resource.getURI();
        else if (properties.containsKey(id)) {
            Property property = properties.get(id);
            Statement stmt = resource.getProperty(property);
            if (stmt == null)
                return null;
            // return stmt.getObject();
            if (stmt.getObject().isResource() && !stmt.getObject().isAnon())
                return stmt.getResource().getURI();
            else if (stmt.getObject().isResource() && stmt.getObject().isAnon())
                return stmt.getResource().getId().toString();
            else
                return stmt.getLiteral().toString();
        }
        return null;
    }

    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (descriptors == null) {
            List<IPropertyDescriptor> descriptorsList = Lists.newArrayList();

            PropertyDescriptor anonymousDescriptor = new PropertyDescriptor(PROPERTY_ANONYMOUS,
                    "Anonymous");
            anonymousDescriptor.setCategory("General");
            descriptorsList.add(anonymousDescriptor);

            PropertyDescriptor uriDescriptor = new PropertyDescriptor(PROPERTY_URI, "URI");
            uriDescriptor.setCategory("General");
            descriptorsList.add(uriDescriptor);

            PropertyDescriptor idDescriptor = new PropertyDescriptor(PROPERTY_ID, "Id");
            idDescriptor.setCategory("General");
            descriptorsList.add(idDescriptor);

            for (String propertyUri : properties.keySet()) {
                PropertyDescriptor descriptor = new PropertyDescriptor(propertyUri, propertyUri);
                descriptor.setCategory("Model");
                // descriptor.setLabelProvider(provider);
                descriptorsList.add(descriptor);

            }

            descriptors = new IPropertyDescriptor[descriptorsList.size()];
            descriptorsList.toArray(descriptors);
        }
        return descriptors;

    }

}
