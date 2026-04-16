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

package com.semmtech.jena.performance.sparql;


import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.rdf.model.Model;


public class DatasetProviderFactory {

    public static DatasetProvider create(Model model, String name) {
        Dataset dataset = DatasetFactory.create(model);
        return new DatasetProviderImpl(dataset, name);
    }

    public static List<DatasetProvider> fromModels(List<Model> models,
            Map<Model, String> nameMapping) {

        List<DatasetProvider> providers = Lists.newArrayList();

        for (Model m : models) {
            String name = nameMapping.get(m);

            if (name == null) {
                name = m.getClass().getSimpleName();
            }

            providers.add(create(m, name));
        }

        return providers;
    }

}
