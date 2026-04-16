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


/**
 * 
 * @author Mike Henrichs
 */
public class PropertyTransfer extends ByteArrayTransfer {
    private static Logger logger = Logger.getLogger(PropertyTransfer.class);

    private static final PropertyTransfer instance = new PropertyTransfer();

    // Create a unique ID to make sure that different Eclipse
    // applications use different "types" of <code>ResourceTransfer</code>
    private static final String TYPE_NAME = "property-transfer-format:"
            + System.currentTimeMillis() + ":" + instance.hashCode();
    private static final int TYPE_ID = registerType(TYPE_NAME);

    private PropertyTransfer() {
    }

    public static PropertyTransfer getInstance() {
        return instance;
    }

    /**
     * Converts plain text represented by a java String to a platform specific
     * representation.
     */
    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (!checkProperty(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(output)) {

            Property property = (Property) object;
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            String modelUri = provider.getModelURI();
            logger.debug(String.format("javaToNative -> propertyUri = '%s'; modelUri = '%s'",
                    property.getURI(), modelUri));

            String data = String.format("%s;%s", property.getURI(), modelUri);
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
        if (bytes == null) {
            return null;
        }

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
        Property property = null;
        try {
            String data = input.readUTF();
            logger.debug(String.format("nativeToJava -> data = '%s'", data));

            String[] values = data.split(";");
            String propertyUri = values[0];
            String modelUri = values[1];
            logger.debug(String.format("nativeToJava -> propertyUri = '%s'; modelUri = '%s'",
                    propertyUri, modelUri));

            IModelProvider provider = ModelProviderRegistry.getProvider(modelUri);

            if (provider != null) {
                Model model = provider.getOntModel();
                if (model != null) {
                    property = model.getProperty(propertyUri);
                }
            }
        }
        catch (IOException e) {
            logger.error("Caught an IOException in PropertyTransfer");
        }
        return property;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    boolean checkProperty(Object object) {
        return (object != null && object instanceof Property);
    }

    protected boolean validate(Object object) {
        return checkProperty(object);
    }
}
