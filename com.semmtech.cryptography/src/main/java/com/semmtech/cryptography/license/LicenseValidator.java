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

package com.semmtech.cryptography.license;


import static com.semmtech.cryptography.license.LicenseConstants.DEFAULT_ENCODING;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.Security;
import java.text.ParseException;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import com.semmtech.cryptography.CryptoUtils;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class LicenseValidator {
    private static LicenseValidator instance;

    private PublicKey publicKey;

    private LicenseValidator() {
        Security.addProvider(new BouncyCastleProvider());
    }

    public void init(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public boolean validateLicense(InputStream input) throws IOException, ParseException,
            GeneralSecurityException {
        if (publicKey == null) {
            throw new IllegalStateException("Public key has not been initialized!");
        }

        boolean verified = false;

        String licenseContent = IOUtils.toString(input, DEFAULT_ENCODING);

        License license = LicenseReader.readLicense(IOUtils.toInputStream(licenseContent));

        if (license == null) {
            throw new ParseException("The provided license doesn't adhere to the expected format.",
                    0);
        }

        String signatureHex = LicenseReader.readSignatureHex(IOUtils.toInputStream(licenseContent));

        byte[] data = license.encode();
        byte[] signature = Hex.decode(signatureHex);

        verified = CryptoUtils.verifySignature(data, publicKey, signature);

        return verified;
    }

    public static LicenseValidator getInstance() {
        if (instance == null) {
            instance = new LicenseValidator();
        }
        return instance;
    }
}
