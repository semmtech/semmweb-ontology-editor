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

package com.semmtech.plugin.semmweb.laces.ldp.wizards;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.hp.hpl.jena.ontology.OntModel;
import com.semmtech.plugin.semmweb.core.model.OntModelUtils;
import com.semmtech.plugin.semmweb.core.wizards.RewriteNamespacesWizardPage;
import com.semmtech.semantics.util.NamespaceMapping;
import com.semmtech.semantics.util.NamespaceRewriteRule;


/**
 * 
 * @author Sander Stolk
 * @author Mike Henrichs
 */
public class ShareOntologyWizardPage extends RewriteNamespacesWizardPage {
    protected String publicationLocation;

    public ShareOntologyWizardPage(OntModel ontologyModel) {
        super(ontologyModel);
    }

    @Override
    protected void createControlHeading(Composite parent) {
        super.createControlHeading(parent);

        String text = "By default, the namespace associated with the ontology will be set to be rewritten to that of the publication location. "
                + "Bear in mind that any rewritten namespaces will show up as such on the publication platform only; the model stored locally will not be affected.";
        Label label = new Label(parent, SWT.WRAP);
        label.setText(text);
        GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1);
        layoutData.widthHint = 400;
        label.setLayoutData(layoutData);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setPageComplete(true);
        }
        super.setVisible(visible);
    }

    /** Expects a location that ends in a hash or a forward slash. */
    public void setPublicationLocation(String location) {
        String oldPublicationLocation = publicationLocation;
        publicationLocation = Strings.nullToEmpty(location);

        if (!Strings.isNullOrEmpty(oldPublicationLocation)) {
            if (!oldPublicationLocation.equals(publicationLocation)) {
                // change existing rewrite rules that started with the old
                // publication location to point to the new publication location

                List<NamespaceRewriteRule> newRules = Lists.newArrayList();
                for (NamespaceRewriteRule oldRule : rules) {
                    String to = oldRule.getTo();
                    if (to != null && to.startsWith(oldPublicationLocation)) {
                        if (!Strings.isNullOrEmpty(publicationLocation)) {
                            to = publicationLocation
                                    + to.substring(oldPublicationLocation.length());
                            NamespaceRewriteRule newRule = new NamespaceRewriteRule(
                                    oldRule.getFrom(), to, oldRule.isUpdatePrefixMap());
                            newRules.add(newRule);
                        }
                    }
                    else {
                        newRules.add(oldRule);
                    }
                }
                rules.clear();
                rules.addAll(newRules);
            }
        }
        else {
            // default rewrite rules:
            // namespaces that start with ontologyModelURI ->
            // start with publish location

            String ontologyModelURI = OntModelUtils.getURI(ontModel);
            if (!Strings.isNullOrEmpty(ontologyModelURI)
                    && !ontologyModelURI.equals(publicationLocation)) {
                for (NamespaceMapping namespace : namespaces) {
                    String namespaceURI = namespace.getURI();
                    if (namespaceURI.startsWith(ontologyModelURI)) {
                        String to = publicationLocation
                                + namespaceURI.substring(ontologyModelURI.length());
                        NamespaceRewriteRule newRule = new NamespaceRewriteRule(namespaceURI, to,
                                true);
                        rules.add(newRule);
                    }
                }
            }
        }

        setPageComplete(false);
        refreshViewer();
    }

}
