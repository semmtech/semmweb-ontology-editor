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

package com.semmtech.plugin.semmweb.core.dnd;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.preferences.PreferencesUtil;


public class LiteralTransfer extends ByteArrayTransfer {
    private static Logger logger = Logger.getLogger(LiteralTransfer.class);

    private static final LiteralTransfer instance = new LiteralTransfer();

    // Create a unique ID to make sure that different Eclipse
    // applications use different "types" of <code>ResourceTransfer</code>
    private static final String TYPE_NAME = "literal-transfer-format:" + System.currentTimeMillis()
            + ":" + instance.hashCode();
    private static final int TYPE_ID = registerType(TYPE_NAME);

    private String delimiter = "|";
    private String delimiterCode = "@@__delimiter";

    private LiteralTransfer() {
    }

    public static LiteralTransfer getInstance() {
        return instance;
    }

    /**
     * Converts plain text represented by a java String to a platform specific
     * representation.
     */
    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (!checkLiteral(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(output)) {

            Literal literal = (Literal) object;

            String datatypeURI = literal.getDatatypeURI();
            String language = literal.getLanguage();
            String lexicalForm = literal.getLexicalForm();

            if (lexicalForm.contains(delimiter)) {
                lexicalForm = lexicalForm.replace(delimiter, delimiterCode);
            }

            String data = PreferencesUtil.encode(new String[] { lexicalForm,
                    (language == null) ? "" : language, (datatypeURI == null) ? "" : datatypeURI },
                    delimiter);

            logger.debug(String.format("javaToNative -> data = '%s'", data));
            dataOutput.writeUTF(data);

            byte[] bytes = output.toByteArray();
            super.javaToNative(bytes, transferData);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a platform specific representation of plain text to a java
     * String. TODO: Check if public accessiblity is allowed!
     */
    @Override
    public Object nativeToJava(TransferData transferData) {
        // / The property serialization format is: (String) URI of OntProperty
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        if (bytes == null)
            return null;

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
        Literal literal = null;
        try {
            String data = input.readUTF();
            logger.debug(String.format("nativeToJava -> data = '%s'", data));

            String[] values = PreferencesUtil.decode(data, delimiter);
            String datatypeURI = values[2];
            String language = values[1];
            String lexicalForm = values[0];

            if (lexicalForm.contains(delimiterCode)) {
                lexicalForm = lexicalForm.replace(delimiterCode, delimiter);
            }

            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            if (provider != null) {
                Model model = provider.getOntModel();
                if (datatypeURI.length() == 0 && language.length() == 0) {
                    literal = model.createLiteral(lexicalForm);
                }
                else if (datatypeURI.length() == 0) {
                    literal = model.createLiteral(lexicalForm, language);
                }
                else if (datatypeURI.length() > 0) {
                    literal = model.createTypedLiteral(lexicalForm, datatypeURI);
                }
            }
        }
        catch (IOException e) {
            logger.error("Caught an IOException in LiteralTransfer");
        }
        return literal;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    boolean checkLiteral(Object object) {
        return (object != null && object instanceof Literal);
    }

    protected boolean validate(Object object) {
        return checkLiteral(object);
    }
}
