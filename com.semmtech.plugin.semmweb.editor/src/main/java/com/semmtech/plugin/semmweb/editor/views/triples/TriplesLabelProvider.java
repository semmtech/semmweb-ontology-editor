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

package com.semmtech.plugin.semmweb.editor.views.triples;


import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.semmtech.semantics.util.JenaUtil;


public class TriplesLabelProvider extends StyledCellLabelProvider {
    private LabelProvider labelProvider;
    private Multimap<Statement, Statement> reifiedStatements;
    private List<Property> reifiedPredicates;
    private OntModel ontModel;

    private final Styler descendantStyler = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
        }
    };

    public TriplesLabelProvider() {

    }

    public void setLabelProvider(LabelProvider labelProvider) {
        this.labelProvider = labelProvider;
    }

    public void setReifiedStatements(Multimap<Statement, Statement> reifiedStatements) {
        this.reifiedStatements = reifiedStatements;
    }

    public void setReifiedPredicates(List<Property> reifiedPredicates) {
        this.reifiedPredicates = reifiedPredicates;
    }

    public void setModel(OntModel model) {
        this.ontModel = model;
    }

    @Override
    public void update(ViewerCell cell) {
        int columnIndex = cell.getColumnIndex();
        StyledString styledString = getElementText(cell.getElement(), columnIndex);
        if (styledString == null) {
            cell.setText(null);
        }
        else {
            cell.setText(styledString.getString());
            cell.setStyleRanges(styledString.getStyleRanges());
        }
        cell.setImage(getElementImage(cell.getElement(), columnIndex));
    }

    public StyledString getElementText(Object element, int columnIndex) {
        Statement statement = (Statement) element;
        if (labelProvider == null) {
            return null;
        }
        if (columnIndex == 0) {
            return new StyledString(labelProvider.getText(statement.getSubject()));
        }
        else if (columnIndex == 1) {
            return new StyledString(labelProvider.getText(statement.getPredicate()));
        }
        else if (columnIndex == 2) {
            RDFNode object = statement.getObject();
            if (object.isResource()) {
                return new StyledString(labelProvider.getText(object.asResource()));
            }
            else if (object.isLiteral()) {
                return new StyledString(labelProvider.getText(object.asLiteral()));
            }
        }
        else {
            int index = columnIndex - 3;
            if ((reifiedStatements == null) || (reifiedPredicates == null)
                    || (reifiedPredicates.size() < index)) {
                return null;
            }
            if (reifiedStatements.containsKey(statement)) {
                Statement statementWithSubProperty = null;
                List<Resource> reifiedResources = getReifiedResourcesFromStatements(reifiedStatements
                        .get(statement));
                Property predicate = reifiedPredicates.get(index);
                for (Statement sub : reifiedStatements.get(statement)) {
                    if (reifiedResources.contains(sub.getSubject())) {
                        Property subPredicate = sub.getPredicate();
                        if (subPredicate.equals(predicate)) {
                            return new StyledString(labelProvider.getText(sub.getObject()));
                        }
                        if ((ontModel != null)
                                && JenaUtil.asOntProperty(subPredicate, ontModel).hasSuperProperty(
                                        predicate, false)) {
                            statementWithSubProperty = sub;
                        }
                    }
                }
                if (statementWithSubProperty != null) {
                    return new StyledString(labelProvider.getText(statementWithSubProperty
                            .getObject()), descendantStyler);
                }
            }
        }
        return null;
    }

    public Image getElementImage(Object element, int columnIndex) {
        Statement statement = (Statement) element;
        if (labelProvider == null) {
            return null;
        }
        if (columnIndex == 0) {
            return labelProvider.getImage(statement.getSubject());
        }
        else if (columnIndex == 1) {
            return labelProvider.getImage(statement.getPredicate());
        }
        else if (columnIndex == 2) {
            RDFNode object = statement.getObject();
            if (object.isResource()) {
                return labelProvider.getImage(object.asResource());
            }
            else if (object.isLiteral()) {
                return labelProvider.getImage(object.asLiteral());
            }
        }
        else {
            int index = columnIndex - 3;
            if ((reifiedStatements == null) || (reifiedPredicates == null)
                    || (reifiedPredicates.size() < index)) {
                return null;
            }
            if (reifiedStatements.containsKey(statement)) {
                Statement statementWithSubProperty = null;
                List<Resource> reifiedResources = getReifiedResourcesFromStatements(reifiedStatements
                        .get(statement));
                Property predicate = reifiedPredicates.get(index);
                for (Statement sub : reifiedStatements.get(statement)) {
                    if (reifiedResources.contains(sub.getSubject())) {
                        Property subPredicate = sub.getPredicate();
                        if (sub.getPredicate().equals(predicate)) {
                            return labelProvider.getImage(sub.getObject());
                        }
                        if ((ontModel != null)
                                && JenaUtil.asOntProperty(subPredicate, ontModel).hasSuperProperty(
                                        predicate, false)) {
                            statementWithSubProperty = sub;
                        }
                    }
                }
                if (statementWithSubProperty != null) {
                    return labelProvider.getImage(statementWithSubProperty.getObject());
                }
            }
        }
        return null;
    }

    private List<Resource> getReifiedResourcesFromStatements(Collection<Statement> statements) {
        List<Resource> result = Lists.newArrayList();
        for (Statement statement : statements) {
            if (statement.getPredicate().equals(RDF.type)) {
                result.add(statement.getSubject());
            }
        }
        return result;
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object) {
        return 40;
    }

    @Override
    public int getToolTipTimeDisplayed(Object object) {
        return 25000;
    }

    @Override
    public Point getToolTipShift(Object object) {
        return new Point(8, 10);
    }

    @Override
    public String getToolTipText(Object element) {
        if (element instanceof Statement) {
            return ((Statement) element).toString();
        }
        return null;
    }
}
