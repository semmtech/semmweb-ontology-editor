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

package com.semmtech.plugin.semmweb.core.viewers;


import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.widgets.RestrictionsLabelProvider;


/**
 * @author Simone Rondelli
 */
public class RestrictionToolTipContent extends ResourceToolTipContent {

    private final Resource ownerClass;
    private final Restriction restriction;

    public RestrictionToolTipContent(Composite parent, IModelProvider modelProvider,
            Restriction restriction, Resource ownerClass, ILabelProvider restrictionsLabelProvider) {
        super(parent, modelProvider, restriction, SWT.NONE);
        super.labelProvider = restrictionsLabelProvider;
        this.ownerClass = ownerClass;
        this.restriction = restriction;
        createContent();
    }

    private void createContent() {

        if (ownerClass != null) {
            addCustomSection("Owner Class:", ownerClass.getLocalName());
        }

        if (labelProvider instanceof RestrictionsLabelProvider) {
            RestrictionsLabelProvider resLabelProvider = (RestrictionsLabelProvider) labelProvider;
            String hierarchyTooltip = resLabelProvider.getTextTaxonomy(restriction);

            if (!Strings.isNullOrEmpty(hierarchyTooltip)) {
                addCustomSection("Inherited via class hierarchy:", hierarchyTooltip);
            }
        }
    }

}
