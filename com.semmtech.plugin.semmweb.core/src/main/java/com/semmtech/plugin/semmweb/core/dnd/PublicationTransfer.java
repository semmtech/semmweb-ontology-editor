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

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.semmtech.plugin.semmweb.core.extensionpoint.IPublisher.PublicationInfo;


/**
 * 
 * @author Sander Stolk
 */
public class PublicationTransfer extends ByteArrayTransfer {
    private static Logger logger = Logger.getLogger(PublicationTransfer.class);

    private static final PublicationTransfer instance = new PublicationTransfer();

    // Create a unique ID to make sure that different Eclipse
    // applications use different "types" of <code>ResourceTransfer</code>
    private static final String TYPE_NAME = "publication-transfer-format:"
            + System.currentTimeMillis() + ":" + instance.hashCode();
    private static final int TYPE_ID = registerType(TYPE_NAME);

    private PublicationTransfer() {
    }

    public static PublicationTransfer getInstance() {
        return instance;
    }

    /**
     * Converts a Java Object to plain text
     */
    @Override
    public void javaToNative(Object object, TransferData transferData) {
        if (!checkPublication(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        byte[] bytes = javaToByteArray(object);
        if (bytes != null) {
            super.javaToNative(bytes, transferData);
        }
    }

    public byte[] javaToByteArray(Object object) {
        if (!checkPublication(object)) {
            return null;
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(output)) {

            PublicationInfo publication = (PublicationInfo) object;
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(publication);
            logger.debug(String.format("javaToNative -> data = '%s'", data));
            dataOutput.writeUTF(data);

            byte[] bytes = output.toByteArray();
            return bytes;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Converts a plain text to a Java Object.
     */
    @Override
    public Object nativeToJava(TransferData transferData) {
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        return byteArrayToJava(bytes);
    }

    public PublicationInfo byteArrayToJava(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
        PublicationInfo publication = null;
        try {
            String data = input.readUTF();
            logger.debug(String.format("nativeToJava -> data = '%s'", data));

            ObjectMapper om = new ObjectMapper();
            publication = om.readValue(data, PublicationInfo.class);
        }
        catch (Exception e) {
            logger.error("Caught an IOException in PublicationTransfer");
        }
        return publication;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    boolean checkPublication(Object object) {
        return (object != null && object instanceof PublicationInfo);
    }

    protected boolean validate(Object object) {
        return checkPublication(object);
    }
}
