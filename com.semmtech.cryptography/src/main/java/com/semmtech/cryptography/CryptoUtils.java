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

package com.semmtech.cryptography;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.apache.commons.codec.binary.Base64;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class CryptoUtils {

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static byte[] signData(byte[] data, PrivateKey key) throws GeneralSecurityException {
        Signature signer = Signature.getInstance("SHA1withDSA");
        signer.initSign(key);
        signer.update(data);
        return (signer.sign());
    }

    public static boolean verifySignature(byte[] data, PublicKey key, byte[] signature)
            throws GeneralSecurityException {
        Signature signer = Signature.getInstance("SHA1withDSA");
        signer.initVerify(key);
        signer.update(data);
        return (signer.verify(signature));
    }

    public static String base64encode(String text) {
        try {
            return new String(Base64.encodeBase64(text.getBytes(DEFAULT_ENCODING)));
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Error while encode " + text, e);
        }
    }

    public static String base64decode(String text) {
        try {
            return new String(Base64.decodeBase64(text.getBytes(DEFAULT_ENCODING)));
        }
        catch (IOException e) {
            throw new IllegalStateException("Error while decode " + text, e);
        }

    }

    public static void main(String[] args) {
        String txt = "some text to be encrypted";
        String key = "some text to be encrypted";
        System.out.println(txt + " XOR-ed to: " + (txt = xorMessage(txt, key)));
        String encoded = base64encode(txt);
        System.out.println(" is encoded to: " + encoded + " and that is decoding to: "
                + (txt = base64decode(encoded)));
        System.out.print("XOR-ing back to original: " + xorMessage(txt, key));

    }

    public static String xorMessage(String message, String key) {
        char[] keys = key.toCharArray();
        char[] mesg = message.toCharArray();

        int ml = mesg.length;
        int kl = keys.length;
        char[] newmsg = new char[ml];

        for (int i = 0; i < ml; i++) {
            newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
        }// for i
        mesg = null;
        keys = null;
        return new String(newmsg);
    }

    public static String simpleEncrypt(String message, String key) {
        if (message.isEmpty() || key.isEmpty()) {
            throw new IllegalArgumentException("Message or Key must not be empty");
        }

        while (message.length() > key.length()) {
            key += key;
        }

        return base64encode(xorMessage(message, key));
    }

    public static String simpleDecrypt(String message, String key) {
        if (message.isEmpty() || key.isEmpty()) {
            throw new IllegalArgumentException("Message or Key must not be empty");
        }

        while (message.length() > key.length()) {
            key += key;
        }

        return xorMessage(base64decode(message), key);
    }
}
