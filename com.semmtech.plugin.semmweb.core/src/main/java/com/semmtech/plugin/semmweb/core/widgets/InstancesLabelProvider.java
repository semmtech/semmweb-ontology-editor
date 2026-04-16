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


import org.eclipse.jface.viewers.LabelProvider;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.semmtech.plugin.semmweb.core.resourceviewer.ResourceLabelProvider;
import com.semmtech.plugin.semmweb.core.widgets.trees.OntResourceTreeData;


public class InstancesLabelProvider extends ResourceLabelProvider {

    public InstancesLabelProvider(LabelProvider labelProvider) {
        super(labelProvider);
    }

    protected String getColumnText(Object element, int columnIndex) {
        String text = super.getColumnText(element, columnIndex);

        if ((element instanceof OntResourceTreeData) && (text != null)) {
            OntResourceTreeData resource = (OntResourceTreeData) element;
            if (resource.getResource() instanceof OntClass) {
                int directInstanceCount = viewModel.getChildCount((OntResource) resource
                        .getResource());
                text += String.format(" (%s)", directInstanceCount);
            }
        }
        return text;
    }
}