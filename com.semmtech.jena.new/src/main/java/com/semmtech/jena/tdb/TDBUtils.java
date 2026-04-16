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

package com.semmtech.jena.tdb;


import java.io.OutputStream;
import java.util.Iterator;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;


public class TDBUtils {

    public static void exportTDBDataset(String dir, OutputStream out, Lang lang) {
        Dataset dataset = TDBFactory.createDataset(dir);
        RDFDataMgr.write(out, dataset, lang);
    }

    public static Dataset createTDBBacked(String uri, Lang lang, String dir) {
        Dataset source = RDFDataMgr.loadDataset(uri, lang);
        return createTDBBacked(source, dir);
    }

    public static Dataset createTDBBacked(String uri, Lang lang, Location loc) {
        Dataset source = RDFDataMgr.loadDataset(uri, lang);
        return createTDBBacked(source, loc);
    }

    public static Dataset createTDBBacked(String uri, String dir) {
        Dataset source = RDFDataMgr.loadDataset(uri);
        return createTDBBacked(source, dir);
    }

    public static Dataset createTDBBacked(Dataset source, String dir) {
        Dataset tdb = TDBFactory.createDataset(dir);
        copySourceToTDB(source, tdb);
        return tdb;
    }

    private static void copySourceToTDB(Dataset source, Dataset tdb) {
        tdb.begin(ReadWrite.WRITE);
        for (Iterator<String> names = tdb.listNames(); names.hasNext();) {
            tdb.removeNamedModel(names.next());
        }
        Model defaultModel = source.getDefaultModel();
        tdb.getDefaultModel().removeAll();
        tdb.getDefaultModel().add(defaultModel);
        for (Iterator<String> names = source.listNames(); names.hasNext();) {
            String name = names.next();
            Model model = source.getNamedModel(name);
            tdb.addNamedModel(name, model);
        }
        tdb.commit();
        tdb.end();
    }

    public static Dataset createTDBBacked(Dataset source, Location loc) {
        Dataset tdb = TDBFactory.createDataset(loc);
        copySourceToTDB(source, tdb);
        return tdb;
    }
}
