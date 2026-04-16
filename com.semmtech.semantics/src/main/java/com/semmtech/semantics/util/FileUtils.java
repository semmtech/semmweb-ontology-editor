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

package com.semmtech.semantics.util;


import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public final class FileUtils {

    public static final Map<String, String> FORMATS_NAMES = createFormatsMap();

    private FileUtils() {
    }

    private static Map<String, String> createFormatsMap() {
        Map<String, String> result = Maps.newLinkedHashMap();
        result.put("Turtle (*.ttl)", "ttl");
        result.put("RDF/XML-ABBREV (*.owl)", "owl");
        result.put("RDF/XML (*.rdf)", "rdf");
        result.put("N-Triples (*.nt)", "nt");
        result.put("Notation3 (*.n3)", "n3");
        return result;
    }

    /**
     * Returns a bidimensional array that contains the file formats and the name
     * of the extension. This method is useful to add filter to SWT FileDialog
     * 
     * <pre>
     *  formats[0] -> {"*.rdf", "*.ttl", ecc..}
     *  formats[1] -> {"RDF/XML (*.rdf)", "Turtle (*.ttl)", ecc..}
     * </pre>
     * 
     * @param allSemantic
     *            add at the first position the composition of all semantic
     *            formats ("*.rdf, *.ttl, ecc..")
     * @param allFiles
     *            add in the end the "All File" filter ("*.*")
     * @return
     */
    public static String[][] getFileDialogFormats(boolean allSemantic, boolean allFiles) {
        List<String> names = Lists.newArrayList();
        List<String> formats = Lists.newArrayList();

        if (allSemantic) {
            names.add("All Semantic Files");
            String format = "";

            for (String ext : FORMATS_NAMES.values()) {
                format += "*." + ext + ";";
            }
            // remove the last useless comma
            format = format.replaceAll(";$", "");

            formats.add(format);
        }

        for (String key : FORMATS_NAMES.keySet()) {
            names.add(key);
            formats.add("*." + FORMATS_NAMES.get(key));
        }

        if (allFiles) {
            names.add("All Files");
            formats.add("*.*");
        }

        String[][] result = new String[][] { formats.toArray(new String[formats.size()]),
                names.toArray(new String[names.size()]) };

        return result;
    }
}
