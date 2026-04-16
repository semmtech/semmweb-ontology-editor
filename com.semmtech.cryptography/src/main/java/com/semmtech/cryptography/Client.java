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

import com.semmtech.cryptography.license.LicenseValidator;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class Client {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String filename = "expired-2014-12-31.license";
        boolean valid = validateLicense(filename);
        System.out.println(String.format("isValid(\"%s\") = %s", filename, valid));
    }

    public static boolean validateLicense(String filename) {
        LicenseValidator validator = LicenseValidator.getInstance();
        try (FileInputStream fis = new FileInputStream(new File(filename))) {
            return validator.validateLicense(fis);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
