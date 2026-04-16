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

package com.semmtech.plugin.semmweb.core.io;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.semmtech.io.StringOutputStream;


public final class ModelIOUtils {

    /**
     * Creates if file does not exist; otherwise writes the model to the file.
     * 
     * @throws CoreException
     * @throws IOException
     */
    public static void writeModel(Model model, IFile file, String lang, String base,
            String charsetName, IProgressMonitor monitor) throws CoreException, IOException {

        try (OutputStream stream = new StringOutputStream(Charsets.UTF_8.name())) {

            RDFWriter writer = model.getWriter(lang);
            // We have to set the XML base for XML writers ourselves.
            if (!Strings.isNullOrEmpty(base)) {
                writer.setProperty("xmlbase", base);
            }
            writer.write(model, stream, base);

            stream.flush();
            String content = stream.toString();

            // The base URI should be set by the writer already. So the code
            // below
            // appears to be redundant.
            /*
             * if (base != null && lang != null &&
             * (lang.equals(FileUtils.langTurtle) ||
             * lang.equals(FileUtils.langN3))) { content =
             * String.format("@base <%s> .\n%s", base, content); }
             */

            byte[] bytes = content.getBytes(charsetName);
            InputStream input = new ByteArrayInputStream(bytes);
            if (!file.exists()) {
                file.create(input, true, monitor);
                file.setCharset(charsetName, monitor);
            }
            else {
                file.setContents(input, IResource.FORCE, monitor);
            }
        }
    }

    public static void writeModel(Model model, IFileStore file, String lang, String base,
            String charsetName, IProgressMonitor monitor) throws CoreException, IOException {

        try (OutputStream stream = new StringOutputStream(Charsets.UTF_8.name())) {

            RDFWriter writer = model.getWriter(lang);
            // We have to set the XML base for XML writers ourselves.
            if (!Strings.isNullOrEmpty(base)) {
                writer.setProperty("xmlbase", base);
            }
            writer.write(model, stream, base);

            stream.flush();
            String content = stream.toString();

            try (InputStream in = IOUtils.toInputStream(content, charsetName);
                    OutputStream out = file.openOutputStream(0, monitor)) {
                IOUtils.copy(in, out);
            }
        }
    }

    public static Model readModel(IFile file, String base, String lang) throws CoreException,
            IOException {
        Model model = ModelFactory.createDefaultModel();
        String encoding = file.getCharset();
        byte[] bytes = IOUtils.toByteArray(file.getContents());
        String content = new String(bytes, encoding);

        try (InputStream stream = IOUtils.toInputStream(content, Charsets.UTF_8.name())) {
            model.read(stream, base, lang);
            return model;
        }
    }
}
