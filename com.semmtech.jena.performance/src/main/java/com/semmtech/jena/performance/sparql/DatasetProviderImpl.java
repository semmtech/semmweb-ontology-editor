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


import com.hp.hpl.jena.query.Dataset;


public class DatasetProviderImpl implements DatasetProvider {

    private Dataset dataset;
    private String name;

    public DatasetProviderImpl(Dataset dataset, String name) {
        this.dataset = dataset;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Dataset getDataset() {
        return dataset;
    }

    @Override
    public long getLastLoadTime() {
        return -1;
    }

    @Override
    public QueryExecutionTester getTester() {
        return null;
    }
}
