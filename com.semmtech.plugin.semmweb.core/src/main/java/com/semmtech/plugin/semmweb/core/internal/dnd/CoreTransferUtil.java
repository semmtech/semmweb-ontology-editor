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

package com.semmtech.plugin.semmweb.core.internal.dnd;


import com.google.common.base.Strings;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.ModelProviderRegistry;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;


public final class CoreTransferUtil {

    private CoreTransferUtil() {
    }

    public static String convertResourceToString(Resource resource, String modelUri) {
        if (resource == null)
            return null;
        if (Strings.isNullOrEmpty(modelUri))
            return null;
        String uri = "";
        String id = "";
        if (resource.isAnon())
            id = resource.getId().toString();
        else
            uri = resource.getURI();
        return String.format("%s;%s;%s", uri, id, modelUri);
    }

    public static String convertOntClassToString(OntClass clazz, String modelUri) {
        if (clazz == null)
            return null;
        if (Strings.isNullOrEmpty(modelUri))
            return null;
        String uri = "";
        String id = "";
        if (clazz.isAnon())
            id = clazz.getId().toString();
        else
            uri = clazz.getURI();
        return String.format("%s;%s;%s", uri, id, modelUri);
    }

    public static String convertResourceListToString(ResourceArrayList list, String modelUri) {
        if (Strings.isNullOrEmpty(modelUri))
            return null;
        String data = "";
        for (Resource resource : list) {
            data += CoreTransferUtil.convertResourceToString(resource, modelUri) + ";";
        }
        return data;
    }

    public static Resource convertStringToResource(String data) {
        if (Strings.isNullOrEmpty(data)) {
            return null;
        }

        String[] values = data.split(";");
        String uri = values[0];
        String id = values[1];
        String modelUri = values[2];
        IModelProvider provider = ModelProviderRegistry.getProvider(modelUri);
        if (provider == null) {
            return null;
        }

        Model model = provider.getOntModel();
        if (model == null) {
            return null;
        }
        if (!Strings.isNullOrEmpty(uri)) {
            return model.getResource(uri);
        }
        else if (!Strings.isNullOrEmpty(id)) {
            return model.createResource(new AnonId(id));
        }
        return null;
    }

    public static OntClass convertStringToOntClass(String data) {
        if (Strings.isNullOrEmpty(data)) {
            return null;
        }

        String[] values = data.split(";");
        String uri = values[0];
        String id = values[1];
        String modelUri = values[2];
        IModelProvider provider = ModelProviderRegistry.getProvider(modelUri);
        if (provider == null) {
            return null;
        }

        Model model = provider.getOntModel();
        if (model == null) {
            return null;
        }
        if (!Strings.isNullOrEmpty(uri)) {
            return model.getResource(uri).as(OntClass.class);
        }
        else if (!Strings.isNullOrEmpty(id)) {
            return model.createResource(new AnonId(id)).as(OntClass.class);
        }
        return null;
    }

    public static ResourceArrayList convertStringToResourceList(String data) {
        ResourceArrayList list = new ResourceArrayList();
        if (Strings.isNullOrEmpty(data)) {
            return list;
        }
        IModelProvider provider = null;
        Model model = null;
        String[] values = data.split(";");
        for (int i = 0; i < values.length; i += 3) {
            String uri = values[i];
            String id = values[i + 1];
            String modelUri = values[i + 2];
            if (provider == null) {
                provider = ModelProviderRegistry.getProvider(modelUri);
            }
            if (model == null) {
                model = provider.getOntModel();
            }
            if (model != null) {
                if (!Strings.isNullOrEmpty(uri))
                    list.add(model.getResource(uri));
                else if (!Strings.isNullOrEmpty(id))
                    list.add(model.createResource(new AnonId(id)));
            }
        }
        return list;
    }
}
