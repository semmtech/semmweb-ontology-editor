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

package com.semmtech.plugin.semmweb.core.fieldassist;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.semantics.util.NamespaceMapping;
import com.semmtech.semantics.util.NamespaceUtil;


public class PrefixContentProposalProvider implements IContentProposalProvider {

    private boolean ignoreFirstCharacter = true;
    private final List<PrefixProposal> allProposals;

    public PrefixContentProposalProvider(Model model) {

        allProposals = Lists.newArrayList();
        for (NamespaceMapping mapping : NamespaceUtil.getNamespaceMappings(model, false)) {
            String prefix = mapping.getPrefix();
            String namespaceUri = mapping.getURI();
            allProposals.add(new PrefixProposal(prefix, namespaceUri));
        }
        Collections.sort(allProposals, new Comparator<IContentProposal>() {

            @Override
            public int compare(IContentProposal o1, IContentProposal o2) {
                if (o1.getLabel().equals(":")) {
                    return -1;
                }
                else if (o2.getLabel().equals(":")) {
                    return 1;
                }
                return o1.getLabel().compareToIgnoreCase(o2.getLabel());
            }
        });
    }

    @Override
    public IContentProposal[] getProposals(String contents, int position) {
        List<IContentProposal> proposals = Lists.newArrayList();
        for (PrefixProposal proposal : allProposals) {
            if ((ignoreFirstCharacter && contents.length() <= 1)
                    || proposal.getContent().startsWith(contents)) {
                proposals.add(proposal);
            }
        }
        IContentProposal[] result = new IContentProposal[proposals.size()];
        proposals.toArray(result);
        return result;
    }
}
