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

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Resource;
import com.semmtech.plugin.semmweb.core.CorePlugin;
import com.semmtech.plugin.semmweb.core.internal.dnd.CoreTransferUtil;
import com.semmtech.plugin.semmweb.core.model.IModelProvider;


public class OntClassTransfer extends ByteArrayTransfer {
    private static Logger logger = Logger.getLogger(OntClassTransfer.class);

    private static final OntClassTransfer instance = new OntClassTransfer();
    private static final String TYPE_NAME = "ont-class-transfer-format:"
            + System.currentTimeMillis() + ":" + instance.hashCode();
    private static final int TYPE_ID = registerType(TYPE_NAME);

    private OntClassTransfer() {
    }

    public static OntClassTransfer getInstance() {
        return instance;
    }

    /**
     * Converts plain text represented by a java String to a platform specific
     * representation.
     */
    @Override
    protected void javaToNative(Object object, TransferData transferData) {
        if (!checkOntClass(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
                DataOutputStream dataOutput = new DataOutputStream(output)) {

            OntClass clazz = (OntClass) object;
            IModelProvider provider = CorePlugin.getDefault().getActiveModelProvider();
            String modelUri = provider.getModelURI();
            String data = CoreTransferUtil.convertOntClassToString(clazz, modelUri);
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
        byte[] bytes = (byte[]) super.nativeToJava(transferData);
        if (bytes == null)
            return null;
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
        OntClass clazz = null;
        try {
            clazz = CoreTransferUtil.convertStringToOntClass(input.readUTF());
        }
        catch (IOException e) {
            logger.error("Caught an IOException in OntClassTransfer");
        }
        return clazz;
    }

    @Override
    protected int[] getTypeIds() {
        return new int[] { TYPE_ID };
    }

    @Override
    protected String[] getTypeNames() {
        return new String[] { TYPE_NAME };
    }

    boolean checkOntClass(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof Resource))
            return false;
        if (!(object instanceof OntClass))
            return false;
        return true;
    }

    protected boolean validate(Object object) {
        return checkOntClass(object);
    }

    public int containsSupportedTypeAtIndex(TransferData[] transferData) {
        for (int i = 0; i < transferData.length; i++) {
            if (isSupportedType(transferData[i])) {
                return i;
            }
        }
        return -1;
    }

    public boolean containsSupportedType(TransferData[] transferData) {
        return (containsSupportedTypeAtIndex(transferData) != -1);
    }
}