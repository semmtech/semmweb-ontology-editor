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

package com.semmtech.semantics;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.semantics.annotations.NamespacePrefix;
import com.semmtech.semantics.annotations.NamespacePrefixMappings;
import com.semmtech.semantics.annotations.PlainLiteral;
import com.semmtech.semantics.annotations.PropertyValue;
import com.semmtech.semantics.annotations.RdfResource;
import com.semmtech.semantics.annotations.RdfType;
import com.semmtech.semantics.annotations.RdfsComment;
import com.semmtech.semantics.annotations.RdfsLabel;
import com.semmtech.semantics.annotations.RdfsSubClassOf;
import com.semmtech.semantics.annotations.RdfsSubPropertyOf;


public abstract class VocabularyWithAnnotations extends Vocabulary {
    private static final Logger logger = Logger.getLogger(VocabularyWithAnnotations.class);

    /**
     * 
     * @param model
     */
    protected static void updateModel(Model model, Class<?> clazz) {
        try {
            for (Annotation a : clazz.getAnnotations()) {
                if (a instanceof NamespacePrefixMappings) {
                    NamespacePrefixMappings annotation = ((NamespacePrefixMappings) a);
                    for (NamespacePrefix mapping : annotation.mappings()) {
                        String prefix = mapping.prefix();
                        if (model.getNsPrefixURI(prefix) != null) {
                            String previous = model.getNsPrefixURI(prefix);
                            logger.warn(String
                                    .format("The prefix \"%s\" had already been mapped to \"%s\", overwritten!",
                                            prefix, previous));
                        }
                        model.setNsPrefix(mapping.prefix(), mapping.namespace());
                    }
                }
            }
            for (Field field : clazz.getFields()) {
                Resource resource = null;
                if (field.getType().equals(Resource.class)) {
                    resource = (Resource) field.get(null);
                }
                else if (field.getType().equals(Property.class)) {
                    resource = (Property) field.get(null);
                }
                if (resource != null) {
                    resource = model.createResource(resource.getURI());
                    applyAnnotations(model, resource, field);
                }
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Applies any of the Annotations to the given resource within the model.
     * 
     * @param model
     * @param resource
     * @param field
     */
    protected static void applyAnnotations(Model model, Resource resource, Field field) {
        boolean ignore = false;
        for (Annotation a : field.getAnnotations()) {
            if (a instanceof RdfResource) {
                RdfResource annotation = ((RdfResource) a);
                ignore = annotation.ignore();
                break;
            }
        }
        if (ignore) {
            return;
        }
        for (Annotation a : field.getAnnotations()) {
            if (a instanceof RdfType) {
                RdfType annotation = ((RdfType) a);
                String typeUri = model.expandPrefix(annotation.uri());
                if (!Strings.isNullOrEmpty(typeUri)) {
                    resource.addProperty(RDF.type, ResourceFactory.createResource(typeUri));
                }
                for (String uri : annotation.uris()) {
                    typeUri = model.expandPrefix(uri);
                    if (!Strings.isNullOrEmpty(typeUri)) {
                        resource.addProperty(RDF.type, ResourceFactory.createResource(typeUri));
                    }
                }
            }
            else if (a instanceof RdfsSubClassOf) {
                RdfsSubClassOf annotation = ((RdfsSubClassOf) a);
                String superClassUri = model.expandPrefix(annotation.uri());
                if (!Strings.isNullOrEmpty(superClassUri)) {
                    resource.addProperty(RDFS.subClassOf,
                            ResourceFactory.createResource(superClassUri));
                }
                for (String uri : annotation.uris()) {
                    superClassUri = model.expandPrefix(uri);
                    if (!Strings.isNullOrEmpty(superClassUri)) {
                        resource.addProperty(RDFS.subClassOf,
                                ResourceFactory.createResource(superClassUri));
                    }
                }
            }
            else if (a instanceof RdfsSubPropertyOf) {
                RdfsSubPropertyOf annotation = ((RdfsSubPropertyOf) a);
                String superPropertyUri = model.expandPrefix(annotation.propertyUri());
                if (!Strings.isNullOrEmpty(superPropertyUri)) {
                    resource.addProperty(RDFS.subPropertyOf,
                            ResourceFactory.createResource(superPropertyUri));
                }
                for (String uri : annotation.propertyUris()) {
                    superPropertyUri = model.expandPrefix(uri);
                    if (!Strings.isNullOrEmpty(superPropertyUri)) {
                        resource.addProperty(RDFS.subClassOf,
                                ResourceFactory.createResource(superPropertyUri));
                    }
                }
            }
            else if (a instanceof RdfsLabel) {
                RdfsLabel annotation = ((RdfsLabel) a);
                for (PlainLiteral literal : annotation.values()) {
                    String label = literal.value();
                    String lang = literal.lang();
                    if (!Strings.isNullOrEmpty(label) && !Strings.isNullOrEmpty(lang)) {
                        resource.addProperty(RDFS.label, label, lang);
                    }
                    else if (!Strings.isNullOrEmpty(label)) {
                        resource.addProperty(RDFS.label, label);
                    }
                }
            }
            else if (a instanceof RdfsComment) {
                RdfsComment annotation = ((RdfsComment) a);
                for (PlainLiteral literal : annotation.values()) {
                    String label = literal.value();
                    String lang = literal.lang();
                    if (!Strings.isNullOrEmpty(label) && !Strings.isNullOrEmpty(lang)) {
                        resource.addProperty(RDFS.comment, label, lang);
                    }
                    else if (!Strings.isNullOrEmpty(label)) {
                        resource.addProperty(RDFS.comment, label);
                    }
                }
            }
            if (a instanceof RdfResource) {
                RdfResource annotation = ((RdfResource) a);
                // String typeUri = model.expandPrefix(annotation.typeUri());
                // if (!Strings.isNullOrEmpty(typeUri)) {
                // resource.addProperty(RDF.type,
                // ResourceFactory.createResource(typeUri));
                // }

                // for (PlainLiteral literal : annotation.comments()) {
                // String label = literal.value();
                // String lang = literal.lang();
                // if (!Strings.isNullOrEmpty(label) &&
                // !Strings.isNullOrEmpty(lang)) {
                // resource.addProperty(RDFS.comment, label, lang);
                // }
                // else if (!Strings.isNullOrEmpty(label)) {
                // resource.addProperty(RDFS.comment, label);
                // }
                // }
                for (PropertyValue property : annotation.properties()) {
                    String predicateUri = model.expandPrefix(property.predicateUri());
                    String objectUri = model.expandPrefix(property.objectUri());
                    if (!Strings.isNullOrEmpty(predicateUri)
                            && !Strings.isNullOrEmpty(predicateUri)) {
                        resource.addProperty(ResourceFactory.createProperty(predicateUri),
                                ResourceFactory.createResource(objectUri));
                    }
                }
            }
        }
    }
}
