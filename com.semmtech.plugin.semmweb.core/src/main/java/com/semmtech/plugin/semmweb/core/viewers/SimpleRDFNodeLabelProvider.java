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


import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;


public class SimpleRDFNodeLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof Resource) {
            return getResourceText((Resource) element);
        }
        else if (element instanceof Literal) {
            return getLiteralText((Literal) element);
        }
        return null;
    }

    protected String getResourceText(Resource resource) {
        if (resource.isAnon()) {
            return String.format("<%s>", resource.getId());
        }
        return getQNameFor(resource);
    }

    protected static String getLiteralText(Literal literal) {
        String value = literal.getString();
        String language = literal.getLanguage();

        if (language != null && language.length() > 0) {
            return String.format("%s {@%s}", value, language);
        }
        return String.format("%s", value);
    }

    /**
     * 
     * @param resource
     * @return
     */
    protected String getQNameFor(Resource resource) {
        if (!resource.isAnon()) {
            String namespacePrefix = null;
            if (resource.getNameSpace() != null) {
                namespacePrefix = resource.getModel().getNsURIPrefix(resource.getNameSpace());
            }
            if (namespacePrefix != null && namespacePrefix.length() > 0) {
                return String.format("%s:%s", namespacePrefix, resource.getLocalName());
            }
            else if (resource.getLocalName() != null && resource.getLocalName().length() > 0) {
                return String.format("%s", resource.getLocalName());
            }
            else {
                return String.format("<%s>", resource.getURI());
            }
        }
        return null;
    }
}
