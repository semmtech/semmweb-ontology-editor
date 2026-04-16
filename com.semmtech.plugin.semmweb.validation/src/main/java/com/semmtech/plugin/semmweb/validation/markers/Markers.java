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

package com.semmtech.plugin.semmweb.validation.markers;


import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.topbraid.spin.constraints.ConstraintViolation;
import org.topbraid.spin.constraints.SimplePropertyPath;

import com.google.common.base.Strings;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.markers.SemanticProblem;


/**
 * 
 * @author Sander Stolk
 */
public class Markers {

    public static void generate(IResource file, ValidityReport validityReport) {
        if (file == null || validityReport == null || !file.exists() || validityReport.isClean()) {
            return;
        }

        for (Iterator<Report> i = validityReport.getReports(); i.hasNext();) {
            Report report = i.next();
            createMarker(file, report);
        }
    }

    public static IMarker createMarker(IResource file, Report report) {
        if (file == null || report == null) {
            return null;
        }

        SemanticProblem problem = createSemanticProblem(file, report);
        return problem.generateMarker();
    }

    protected static SemanticProblem createSemanticProblem(IResource file, Report report) {
        SemanticProblem problem = new SemanticProblem(file);

        String message = null;
        String type = report.getType();
        if (!Strings.isNullOrEmpty(type)) {
            type = StringUtils.capitalize(StringUtils.strip(type, "\" \r\n"));
            message = type;
        }
        String description = report.getDescription();
        if (!Strings.isNullOrEmpty(description)) {
            // Report description typically holds more than one line. The
            // first line is the description message. The remaining lines
            // state the culprit or implicated node. We're only interested
            // in obtaining the first line for our own marker message.
            int newlineIndex = StringUtils.indexOfAny(description, "\r\n");
            if (newlineIndex > 0) {
                description = StringUtils.substring(description, 0, newlineIndex);
            }
            description = StringUtils.capitalize(StringUtils.strip(description, "\" \r\n"));
            if (!Strings.isNullOrEmpty(message)) {
                message = String.format("%s. %s", message, description);
            }
            else {
                message = description;
            }
        }

        if (!Strings.isNullOrEmpty(message)) {
            problem.setMessage(message);
        }

        int severity = (report.isError()) ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING;
        problem.setSeverity(severity);

        String locationValue = null;
        Object extension = report.getExtension();
        if (extension instanceof Resource) {
            Resource resource = (Resource) extension;
            String value = getResourceIdentifier(resource);
            problem.setRdfResource(value);
            locationValue = String.format("<%s>", value);
        }

        if (locationValue != null) {
            problem.setLocation(locationValue);
        }

        return problem;
    }

    public static void generate(IResource file, List<ConstraintViolation> violations) {
        if (file == null || violations == null || !file.exists()) {
            return;
        }

        for (ConstraintViolation violation : violations) {
            createMarker(file, violation);
        }
    }

    public static IMarker createMarker(IResource file, ConstraintViolation violation) {
        if (file == null || violation == null) {
            return null;
        }
        SemanticProblem problem = createSemanticProblem(file, violation);
        return problem.generateMarker();
    }

    protected static SemanticProblem createSemanticProblem(IResource file,
            ConstraintViolation violation) {
        SemanticProblem problem = new SemanticProblem(file);

        Resource constraintSource = violation.getSource();
        if (constraintSource != null && constraintSource.isURIResource()) {
            problem.setSemanticSource(getResourceIdentifier(constraintSource));
        }

        String description = violation.getMessage();
        if (!Strings.isNullOrEmpty(description)) {
            problem.setMessage(description);
        }
        else if (constraintSource != null) {
            String value = getConstraintDescription(constraintSource);
            if (!Strings.isNullOrEmpty(value)) {
                problem.setMessage(value);
            }
        }

        problem.setSeverity(IMarker.SEVERITY_WARNING);

        String locationValue = null;
        Resource resource = violation.getRoot();
        if (resource != null) {
            String value = getResourceIdentifier(resource);
            problem.setRdfResource(value);
            locationValue = value;
        }

        if (violation.getPaths() != null && !violation.getPaths().isEmpty()) {
            String value = new String();
            for (SimplePropertyPath path : violation.getPaths()) {
                String predicateId = getResourceIdentifier(path.getPredicate());
                if (!Strings.isNullOrEmpty(predicateId)) {
                    value = String.format("%s", predicateId);
                    break;
                }
            }
            problem.setPropertyPath(value);
            if (!Strings.isNullOrEmpty(locationValue)) {
                locationValue = String.format("<%s>  at predicate  <%s>", locationValue, value);
            }
        }

        if (!Strings.isNullOrEmpty(locationValue)) {
            problem.setLocation(locationValue);
        }

        return problem;
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

    protected static String getConstraintDescription(Resource resource) {
        if (resource == null) {
            return null;
        }

        String result = getResourceDescription(resource);
        if (result != null && !result.isEmpty()) {
            return result;
        }

        for (Statement typeStatement : resource.listProperties(RDF.type).toList()) {
            RDFNode type = typeStatement.getObject();
            if (type.isResource()) {
                result = getResourceDescription(type.asResource());
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
        }
        return null;
    }

    protected static String getResourceDescription(Resource resource) {
        if (resource == null) {
            return null;
        }

        // RDFS.label
        String result = null;
        Statement labelStatement = resource.getProperty(RDFS.label);
        if (labelStatement != null && labelStatement.getObject().isLiteral()) {
            String value = labelStatement.getObject().asLiteral().getLexicalForm();
            result = value;
        }
        // // RDFS.comment
        // Statement commentStatement = resource.getProperty(RDFS.comment);
        // if (commentStatement != null &&
        // commentStatement.getObject().isLiteral()) {
        // String value =
        // commentStatement.getObject().asLiteral().getLexicalForm();
        // ;
        // if (result == null) {
        // result = value;
        // }
        // else {
        // result = String.format("%s. %s", result, value);
        // }
        // }

        return result;
    }
}
