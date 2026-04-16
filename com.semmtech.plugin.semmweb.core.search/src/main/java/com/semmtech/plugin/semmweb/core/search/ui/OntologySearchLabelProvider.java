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

package com.semmtech.plugin.semmweb.core.search.ui;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.CorePluginImages;
import com.semmtech.plugin.semmweb.core.search.SearchPlugin;
import com.semmtech.plugin.semmweb.core.search.SearchPluginImages;
import com.semmtech.plugin.semmweb.core.search.elements.LiteralElement;
import com.semmtech.plugin.semmweb.core.search.elements.ResourceElement;


final class OntologySearchLabelProvider extends StyledCellLabelProvider {

    enum Modality {
        TABLE_MODE, TREE_MODE
    }

    Modality modality;

    OntologySearchLabelProvider(Modality modality) {
        this.modality = modality;
    }

    public void setModality(Modality modality) {
        this.modality = modality;
    }

    private final Styler predicateStyler = new Styler() {
        @Override
        public void applyStyles(TextStyle textStyle) {
            textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
        }
    };

    @Override
    public void update(ViewerCell cell) {
        Object cellElement = cell.getElement();

        if (cellElement instanceof LiteralElement) {
            LiteralElement element = (LiteralElement) cellElement;
            String property;

            property = element.getPropertyLabel();
            cell.setImage(SearchPlugin.getDefault().getImage(SearchPluginImages.IMG_RESULT_MARKER));

            StyledString styled = new StyledString();
            styled.append(property);
            styled.append(": ");
            styled.setStyle(0, property.length() + 2, predicateStyler);
            styled.append(element.getLiteralLabel());

            cell.setText(styled.getString());
            cell.setStyleRanges(styled.getStyleRanges());

            cell.setImage(SearchPlugin.getDefault().getImage(SearchPluginImages.IMG_RESULT_MARKER));
        }
        else if (cellElement instanceof ResourceElement) {
            ResourceElement element = (ResourceElement) cellElement;
            Resource resource = element.getResource();
            Statement stmt = resource.getProperty(RDF.type);
            Resource type = null;

            if (stmt != null && stmt.getObject() != null) {
                type = stmt.getObject().asResource();
            }

            if (modality == Modality.TREE_MODE) {
                cell.setText(element.getLabel());

                String imageKey = CorePluginImages.IMG_RDF_RESOURCE;
                if (type == null) {
                    imageKey = CorePluginImages.IMG_RDF_RESOURCE;
                }
                else if (type.equals(RDFS.Class)) {
                    imageKey = CorePluginImages.IMG_RDFS_CLASS;
                }
                else if (type.equals(OWL.Class)) {
                    imageKey = CorePluginImages.IMG_OWL_CLASS;
                }
                else if (type.equals(RDF.Property)) {
                    imageKey = CorePluginImages.IMG_RDF_PROPERTY;
                }
                else if (type.equals(OWL.Ontology)) {
                    imageKey = CorePluginImages.IMG_OWL_ONTOLOGY;
                }

                cell.setImage(CorePlugin.getDefault().getImage(imageKey));
            }
            else if (modality == Modality.TABLE_MODE) {
                String text = element.getFile().getName() + " - " + element.getLabel();
                int literalCount = element.getLiteralElements().size();
                StyledString styled = new StyledString(text);

                if (literalCount > 0) {
                    String matches;
                    if (literalCount == 1) {
                        matches = String.format("(%s match)", literalCount);
                    }
                    else {
                        matches = String.format("(%s matches)", literalCount);
                    }

                    styled.append(" " + matches);
                    styled.setStyle(text.length() + 1, matches.length(), predicateStyler);
                }

                cell.setText(styled.getString());
                cell.setStyleRanges(styled.getStyleRanges());

                cell.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMANTIC_FILE));
            }
        }
        else if (cellElement instanceof IResource) {
            IResource resource = (IResource) cellElement;
            cell.setText(resource.getName());

            if (resource instanceof IProject) {
                cell.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMM_PROJECT));
            }
            else if (resource instanceof IFolder) {
                cell.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_FOLDER));
            }
            else if (resource instanceof IFile) {
                cell.setImage(CorePlugin.getDefault().getImage(CorePluginImages.IMG_SEMANTIC_FILE));
            }
        }
        super.update(cell);
    }
}