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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * 
 * @author Simone Rondelli
 */
public class CryptoStream {

    private static final String CIPHER_ALGORITHM = "Blowfish";
    private static final String KEY_ALGORITHM = "Blowfish";

    private final int MAX_FILE_BUF = 1024;

    private final String mPassword;

    /**
     * create an object with just the passphrase from the user. Don't do
     * anything else yet
     * 
     * @param password
     */
    public CryptoStream(String password) {
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        if (password.length() == 16) {
            this.mPassword = password;
        }
        else if (password.length() > 16) {
            this.mPassword = password.substring(0, 16);
        }
        else {
            char[] bytes = password.toCharArray();

            String newPass = password;

            int i = 0;

            do {
                newPass += bytes[i];

                if (++i > bytes.length - 1) {
                    i = 0;
                }
            } while (newPass.length() % 16 != 0);

            this.mPassword = newPass;
        }
    }

    public CipherInputStream wrapInputStream(InputStream is) throws CryptoException {
        Cipher cipher = buildCipher(mPassword.toCharArray(), Cipher.DECRYPT_MODE);
        return new CipherInputStream(is, cipher);
    }

    public CipherOutputStream wrapOutputStream(OutputStream os) throws CryptoException {
        Cipher cipher = buildCipher(mPassword.toCharArray(), Cipher.ENCRYPT_MODE);
        return new CipherOutputStream(os, cipher);
    }

    /**
     * This is where we write out the actual encrypted data to disk. Pass two
     * file objects representing the actual input (cleartext) and output file to
     * be encrypted.
     * 
     * there may be a way to write a cleartext header to the encrypted file
     * containing the salt, but I ran into uncertain problems with that.
     * 
     * @param input
     *            - the cleartext file to be encrypted
     * @param output
     *            - the encrypted data file
     * @throws IOException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws CryptoException
     */
    public void writeEncryptedFile(File input, File output) throws IOException, CryptoException {
        int nread = 0;
        byte[] inbuf = new byte[MAX_FILE_BUF];

        try (FileOutputStream fout = new FileOutputStream(output);
                FileInputStream fin = new FileInputStream(input);
                CipherOutputStream out = wrapOutputStream(fout)) {

            while ((nread = fin.read(inbuf)) > 0) {
                // create a buffer to write with the exact number of bytes read.
                // Otherwise a short read fills inbuf with 0x0
                // and results in full blocks of MAX_FILE_BUF being written.
                byte[] trimbuf = new byte[nread];
                for (int i = 0; i < nread; i++)
                    trimbuf[i] = inbuf[i];

                out.write(trimbuf);
            }
            out.flush();
        }
        catch (Exception e) {
            throw new CryptoException("Error while writing the encrypted file: " + e.getMessage(),
                    e);
        }
    }

    /**
     * Read from the encrypted file (input) and turn the cipher back into
     * cleartext. Write the cleartext buffer back out to disk as (output) File.
     * 
     * I left CipherInputStream in here as a test to see if I could mix it with
     * the update() and final() methods of encrypting and still have a correctly
     * decrypted file in the end. Seems to work so left it in.
     * 
     * @param input
     *            - File object representing encrypted data on disk
     * @param output
     *            - File object of cleartext data to write out after decrypting
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     * @throws CryptoException
     */
    public void readEncryptedFile(File input, File output) throws IOException, CryptoException {
        int nread = 0;
        byte[] inbuf = new byte[MAX_FILE_BUF];

        // creating a decoding stream from the FileInputStream above using
        // the cipher created from setupDecrypt()
        try (FileOutputStream fout = new FileOutputStream(output);
                FileInputStream fin = new FileInputStream(input);
                CipherInputStream cin = wrapInputStream(fin)) {

            while ((nread = cin.read(inbuf)) > 0) {
                // create a buffer to write with the exact number of bytes read.
                // Otherwise a short read fills inbuf with 0x0
                byte[] trimbuf = new byte[nread];
                for (int i = 0; i < nread; i++)
                    trimbuf[i] = inbuf[i];

                // write out the size-adjusted buffer
                fout.write(trimbuf);
            }
            fout.flush();
        }
    }

    private Cipher buildCipher(char[] password, int mode) throws CryptoException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            Key key = buildKey(password);
            cipher.init(mode, key);
            return cipher;
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
                | InvalidKeyException e) {
            throw new CryptoException("Error while creating Cipher: " + e.getMessage(), e);
        }
    }

    private Key buildKey(char[] password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        byte[] key = String.valueOf(password).getBytes("UTF-8");
        SecretKeySpec spec = new SecretKeySpec(key, KEY_ALGORITHM);
        return spec;
    }

}