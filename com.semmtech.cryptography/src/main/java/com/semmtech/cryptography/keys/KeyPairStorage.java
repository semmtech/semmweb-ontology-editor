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

package com.semmtech.cryptography.keys;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.io.IOUtils;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class KeyPairStorage {

    public static final String DEFAULT_PRIVATE_KEY_NAME = "private.key";
    public static final String DEFAULT_PUBLIC_KEY_NAME = "public.key";

    private static KeyPairStorage instance;

    private KeyPairStorage() {
    }

    public void dumpKeyPair(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        System.out.println("Public Key: " + getHexString(publicKey.getEncoded()));

        PrivateKey privateKey = keyPair.getPrivate();
        System.out.println("Private Key: " + getHexString(privateKey.getEncoded()));
    }

    private String getHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public void storeKeyPair(String path, KeyPair keyPair) throws IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());

        try (FileOutputStream fos = new FileOutputStream(path + File.separator
                + DEFAULT_PUBLIC_KEY_NAME)) {
            fos.write(x509EncodedKeySpec.getEncoded());
        }

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());

        try (FileOutputStream fos = new FileOutputStream(path + File.separator
                + DEFAULT_PRIVATE_KEY_NAME)) {
            fos.write(pkcs8EncodedKeySpec.getEncoded());
        }
    }

    // public KeyPair retrieveKeyPair(String path, String algorithm) throws
    // IOException,
    // NoSuchAlgorithmException, InvalidKeySpecException {
    //
    // PublicKey publicKey = retrievePublicKey(path + File.separator +
    // DEFAULT_PUBLIC_KEY_NAME,
    // algorithm);
    //
    // PrivateKey privateKey = retrievePrivateKey(
    // path + File.separator + DEFAULT_PRIVATE_KEY_NAME, algorithm);
    //
    // return new KeyPair(publicKey, privateKey);
    // }

    public PrivateKey retrievePrivateKey(InputStream stream) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encodedPrivateKey = IOUtils.toByteArray(stream);

        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return privateKey;
    }

    public PublicKey retrievePublicKey(InputStream stream) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] encodedPublicKey = IOUtils.toByteArray(stream);
        return getPublicKey(encodedPublicKey, "DSA");
    }

    public PublicKey getPublicKey(byte[] encoded, String algorithm) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encoded);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    public static KeyPairStorage getInstance() {
        if (instance == null) {
            instance = new KeyPairStorage();
        }
        return instance;
    }
}
