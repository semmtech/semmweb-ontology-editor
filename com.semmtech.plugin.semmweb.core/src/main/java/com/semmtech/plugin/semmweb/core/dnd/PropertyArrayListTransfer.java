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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.ModelProviderRegistry;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.PropertyArrayList;


public class PropertyArrayListTransfer extends ByteArrayTransfer {
    private static Logger logger = Logger.getLogger(PropertyArrayListTransfer.class);

    private static final PropertyArrayListTransfer instance = new PropertyArrayListTransfer();

    // Create a unique ID to make sure that different Eclipse
    // applications use different "types" of
    // <code>PropertyArrayListTransfer</code>
    private static final String TYPE_NAME = "property-array-list-transfer-format:"
            + System.currentTimeMillis() + ":" + instance.hashCode();
    private static final int TYPE_ID = registerType(TYPE_NAME);

    private PropertyArrayListTransfer() {
    }

    public static PropertyArrayListTransfer getInstance() {
        return instance;
    }

    /**
     * Converts plain text represented by a java String to a platform specific
     * representation.
     */
    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (!checkPropertyArrayList(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(output)) {

            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            String modelUri = provider.getModelURI();
            PropertyArrayList list = (PropertyArrayList) object;

            String data = "";
            for (Property property : list) {
                data += String.format("%s;%s;", property.getURI(), modelUri);
            }

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
        PropertyArrayList list = new PropertyArrayList();
        try {
            String data = input.readUTF();
            logger.debug(String.format("nativeToJava -> data = '%s'", data));

            String[] values = data.split(";");

            IModelProvider provider = null;

            for (int i = 0; i < values.length; i += 2) {
                String propertyUri = values[i];
                String modelUri = values[i + 1];
                logger.debug(String.format("nativeToJava -> propertyUri = '%s'; modelUri = '%s';",
                        propertyUri, modelUri));
                if (provider == null)
                    provider = ModelProviderRegistry.getProvider(modelUri);
                if (provider != null) {
                    Model model = provider.getOntModel();
                    if (model != null)
                        list.add(model.getProperty(propertyUri));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            logger.error("Caught an IOException in PropertyArrayListTransfer");
        }
        return list;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    boolean checkPropertyArrayList(Object object) {
        return (object != null && object instanceof PropertyArrayList);
    }

    protected boolean validate(Object object) {
        return checkPropertyArrayList(object);
    }

    public static PropertyArrayList nativeToList(TransferData transferData) {
        return (PropertyArrayList) PropertyArrayListTransfer.getInstance().nativeToJava(
                transferData);
    }
}