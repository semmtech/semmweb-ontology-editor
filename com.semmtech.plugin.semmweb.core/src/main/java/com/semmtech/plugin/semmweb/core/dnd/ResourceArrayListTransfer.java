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

import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.internal.dnd.CoreTransferUtil;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;
import com.semmtech.plugin.semmweb.core.model.ResourceArrayList;


public class ResourceArrayListTransfer extends ByteArrayTransfer {
    private static Logger logger = Logger.getLogger(ResourceArrayListTransfer.class);

    private static final ResourceArrayListTransfer instance = new ResourceArrayListTransfer();

    // Create a unique ID to make sure that different Eclipse
    // applications use different "types" of
    // <code>ResourceArrayListTransfer</code>
    private static final String TYPE_NAME = "resource-array-list-transfer-format:"
            + System.currentTimeMillis() + ":" + instance.hashCode();
    private static final int TYPE_ID = registerType(TYPE_NAME);

    private ResourceArrayListTransfer() {
    }

    public static ResourceArrayListTransfer getInstance() {
        return instance;
    }

    /**
     * Converts plain text represented by a java String to a platform specific
     * representation.
     */
    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (!checkResourceArrayList(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(output)) {

            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            String modelUri = provider.getModelURI();
            ResourceArrayList list = (ResourceArrayList) object;
            String data = CoreTransferUtil.convertResourceListToString(list, modelUri);

            dataOutput.writeUTF(data);
            dataOutput.close();
            output.close();
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
        ResourceArrayList list = new ResourceArrayList();
        try {
            list = CoreTransferUtil.convertStringToResourceList(input.readUTF());
        }
        catch (IOException e) {
            logger.error("Caught an IOException in ResourceArrayListTransfer");
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

    boolean checkResourceArrayList(Object object) {
        return (object != null && object instanceof ResourceArrayList);
    }

    protected boolean validate(Object object) {
        return checkResourceArrayList(object);
    }
}