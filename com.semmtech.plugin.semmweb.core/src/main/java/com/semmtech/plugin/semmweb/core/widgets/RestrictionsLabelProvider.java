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

package com.semmtech.plugin.semmweb.core.widgets;


import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.OWL2;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.resourceviewer.AbstractResourceViewModel.Vocabulary;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceLabelProvider;
import com.semmtech.plugin.semmweb.core.widgets.ModelResourceRestrictionsContentPart.RestrictionColumnName;
import com.semmtech.semantics.util.JenaUtil;
import com.semmtech.ui.plugin.decorators.OverlayImageIcon;


/**
 * 
 * @author Simone Rondelli
 */
public class RestrictionsLabelProvider extends ResourceLabelProvider {

    private final List<RestrictionColumnName> viewerColumnName;

    public RestrictionsLabelProvider(LabelProvider defaultLabelProvider,
            List<RestrictionColumnName> viewerColumnName) {
        super(defaultLabelProvider);
        this.viewerColumnName = viewerColumnName;
    }

    private final Styler greyStyler = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
        }
    };

    public void update(ViewerCell cell) {
        super.update(cell);

        Object element = cell.getElement();

        if (!(element instanceof OntResource)) {
            return;
        }

        Restriction restriction = JenaUtil.asRestriction((OntResource) element);

        boolean isInherited = getViewModel().isInherited(restriction);
        boolean isInBaseModel = getViewModel().getCurrentModel().getBaseModel()
                .contains(null, RDFS.subClassOf, restriction);

        String text = cell.getText();

        if (Strings.isNullOrEmpty(text)) {
            return;
        }

        if ((isInherited || !isInBaseModel)) {
            StyledString styledText = new StyledString(text, greyStyler);
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
        }
    }

    /**
     * This implementation of the getImage return the icon of the restriction
     * with an overlay that indicate that the restriction has been inherited
     * from a super class. In this way this method can be used in other way than
     * just visualizing the icon on the table (eg. tooltip icon)
     */
    @Override
    public Image getImage(Object element) {
        if (!(element instanceof OntResource)) {
            return null;
        }

        Restriction restriction = JenaUtil.asRestriction((OntResource) element);
        boolean isInherited = getViewModel().isInherited(restriction);
        Image baseImage = defaultLabelProvider.getImage(restriction);

        if (baseImage != null && isInherited) {
            OverlayImageIcon icon = new OverlayImageIcon(baseImage, CorePlugin.getDefault());
            icon.addImageDecoration(CorePluginImages.IMG_OVERLAY_INHERITED,
                    OverlayImageIcon.BOTTOM_LEFT);
            return icon.createImage();
        }
        return baseImage;
    }

    public Image getColumnImage(Object element, int columnIndex) {
        if (!(element instanceof OntResource)) {
            return null;
        }

        Restriction restriction = JenaUtil.asRestriction((OntResource) element);
        Property p = restriction.getOnProperty();

        if (viewerColumnName.get(columnIndex) == RestrictionColumnName.ON_PROPERTY) {
            return defaultLabelProvider.getImage(p);
        }
        else if (viewerColumnName.get(columnIndex) == RestrictionColumnName.CARDINALITY) {
            return getImage(element);
        }
        else if (viewerColumnName.get(columnIndex) == RestrictionColumnName.RESOURCE) {
            if (restriction.hasProperty(OWL.someValuesFrom)) {
                return defaultLabelProvider.getImage(restriction
                        .getPropertyResourceValue(OWL.someValuesFrom));
            }
            if (restriction.hasProperty(OWL.allValuesFrom)) {
                return defaultLabelProvider.getImage(restriction
                        .getPropertyResourceValue(OWL.allValuesFrom));
            }
            if (restriction.hasProperty(OWL.hasValue)) {
                return defaultLabelProvider.getImage(restriction.getProperty(OWL.hasValue)
                        .getObject());
            }
            if (restriction.hasProperty(OWL2.onClass)) {
                return defaultLabelProvider.getImage(restriction
                        .getPropertyResourceValue(OWL2.onClass));
            }
        }
        return null;
    }

    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof OntResource)) {
            return null;
        }

        Restriction restriction = JenaUtil.asRestriction((OntResource) element);

        if (viewerColumnName.get(columnIndex) == RestrictionColumnName.CARDINALITY) {
            if (restriction.hasProperty(OWL.minCardinality)) {
                return "min "
                        + restriction.getPropertyValue(OWL.minCardinality).asLiteral()
                                .getLexicalForm();
            }
            else if (restriction.hasProperty(OWL2.minQualifiedCardinality)) {
                return "min "
                        + restriction.getPropertyValue(OWL2.minQualifiedCardinality).asLiteral()
                                .getLexicalForm();
            }
            else if (restriction.hasProperty(OWL.maxCardinality)) {
                return "max "
                        + restriction.getPropertyValue(OWL.maxCardinality).asLiteral()
                                .getLexicalForm();
            }
            else if (restriction.hasProperty(OWL2.maxQualifiedCardinality)) {
                return "max "
                        + restriction.getPropertyValue(OWL2.maxQualifiedCardinality).asLiteral()
                                .getLexicalForm();
            }
            else if (restriction.hasProperty(OWL2.qualifiedCardinality)) {
                return "exactly "
                        + restriction.getPropertyValue(OWL2.qualifiedCardinality).asLiteral()
                                .getLexicalForm();
            }
            else if (restriction.hasProperty(OWL.cardinality)) {
                return "exactly "
                        + restriction.getPropertyValue(OWL.cardinality).asLiteral()
                                .getLexicalForm();
            }
            else if (restriction.hasProperty(OWL.someValuesFrom)) {
                return "some";
            }
            else if (restriction.hasProperty(OWL.allValuesFrom)) {
                return "only";
            }
            if (restriction.hasProperty(OWL.hasValue)) {
                return "value";
            }
        }
        else if (viewerColumnName.get(columnIndex) == RestrictionColumnName.ON_PROPERTY) {
            return defaultLabelProvider.getText(restriction.getOnProperty());
        }
        else if (viewerColumnName.get(columnIndex) == RestrictionColumnName.RESOURCE) {
            return getOnResource(restriction);
        }
        return null;
    }

    @Override
    public int getToolTipDisplayDelayTime(Object object) {
        return 80;
    }

    @Override
    public int getToolTipTimeDisplayed(Object object) {
        return 10000;
    }

    @Override
    public Point getToolTipShift(Object object) {
        return new Point(8, 10);
    }

    @Override
    public Color getToolTipBackgroundColor(Object object) {
        return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
    }

    @Override
    public String getToolTipText(Object element) {
        return new String(); // required to trigger custom tooltip
    }

    @Override
    public RestrictionsViewModel getViewModel() {
        return (RestrictionsViewModel) super.getViewModel();
    }

    public String getOnResource(Restriction restriction) {
        if (restriction.hasProperty(OWL.someValuesFrom)) {
            return defaultLabelProvider.getText(restriction
                    .getPropertyResourceValue(OWL.someValuesFrom));
        }
        if (restriction.hasProperty(OWL.allValuesFrom)) {
            return defaultLabelProvider.getText(restriction
                    .getPropertyResourceValue(OWL.allValuesFrom));
        }
        if (restriction.hasProperty(OWL.hasValue)) {
            return defaultLabelProvider.getText(restriction.getProperty(OWL.hasValue).getObject());
        }
        if (restriction.hasProperty(OWL2.onClass)) {
            return defaultLabelProvider.getText(restriction.getPropertyResourceValue(OWL2.onClass));
        }
        return null;
    }

    @SuppressWarnings("unused")
    private String getTextRestrictionType(Restriction restriction) {
        if (restriction.hasProperty(OWL.someValuesFrom)) {
            return "some";
        }
        if (restriction.hasProperty(OWL.allValuesFrom)) {
            return "only";
        }
        if (restriction.hasProperty(OWL.hasValue)) {
            return "value";
        }
        if (restriction.hasProperty(OWL2.onClass)) {
            return "qualified";
        }
        return null;
    }

    /**
     * Returns the string representation of the taxonomy from the passed
     * restriction owner class to the root element owner class.
     * <p>
     * Let say that the passed restriction is owned by the class SuperClass and
     * the root element is SubClass, the following will be the output:<br>
     * 
     * <pre>
     * SuperClass &gt; Class &gt; SubClass
     * </pre>
     */
    public String getTextTaxonomy(Restriction restriction) {
        String result = "";
        if (viewModel instanceof RestrictionsViewModel) {
            RestrictionsViewModel resViewModel = (RestrictionsViewModel) viewModel;
            Model taxonomy = resViewModel.getInverseTaxonomyModel();
            Resource ownerClass = resViewModel.getInheritedFrom(restriction);

            if (ownerClass == null) {
                return result;
            }

            List<Resource> hierarchy = Lists.newArrayList();
            hierarchy.add(ownerClass);

            while (ownerClass != null) {
                List<RDFNode> children = taxonomy.listObjectsOfProperty(ownerClass,
                        Vocabulary.isChildOf).toList();
                if (!children.isEmpty()) {
                    ownerClass = children.get(0).asResource();
                    hierarchy.add(ownerClass);
                }
                else {
                    ownerClass = null;
                }
            }

            Iterator<Resource> it = hierarchy.iterator();
            while (it.hasNext()) {
                Resource clazz = it.next();
                result += clazz.getLocalName();

                if (it.hasNext()) {
                    result += " > ";
                }
            }
        }
        return result;
    }
}