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


import static com.semmtech.cryptography.license.LicenseConstants.DEFAULT_DATE_FORMAT;
import static com.semmtech.cryptography.license.LicenseConstants.PROP_EXPIRATION;
import static com.semmtech.cryptography.license.LicenseConstants.PROP_ID;
import static com.semmtech.cryptography.license.LicenseConstants.PROP_NAME;
import static com.semmtech.cryptography.license.LicenseConstants.PROP_SIGNATURE;
import static com.semmtech.cryptography.license.LicenseConstants.PROP_VERSION;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import com.google.common.base.Strings;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class LicenseReader {

    public static final SimpleDateFormat expirationFormat = new SimpleDateFormat(
            DEFAULT_DATE_FORMAT);

    /**
     * Create a License Object from the given InputStream. If one of the fields
     * id, name, expiration, signature are missing or one of the features has a
     * wrong key null is returned
     * <p>
     * NB: The license signatureHex isn't validated, use the
     * {@code LicenseValidator} for this purpose
     * 
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static License readLicense(InputStream input) throws IOException, ParseException {
        Properties properties = new Properties();
        properties.load(input);

        List<String> mandatoryFields = Arrays.asList(PROP_ID, PROP_NAME, PROP_VERSION,
                PROP_EXPIRATION, PROP_SIGNATURE);

        String id = properties.getProperty(PROP_ID);
        String name = properties.getProperty(PROP_NAME);
        String version = properties.getProperty(PROP_VERSION);
        String expiration = properties.getProperty(PROP_EXPIRATION);
        String signatureHex = properties.getProperty(PROP_SIGNATURE);

        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(name)
                || Strings.isNullOrEmpty(version) || Strings.isNullOrEmpty(expiration)
                || Strings.isNullOrEmpty(signatureHex)) {

            return null;
        }

        UUID uuid = UUID.fromString(id);
        expirationFormat.setTimeZone(TimeZone.getTimeZone(LicenseConstants.DEFAULT_TIME_ZONE));

        Calendar expirationCal = new GregorianCalendar(
                TimeZone.getTimeZone(LicenseConstants.DEFAULT_TIME_ZONE));
        expirationCal.setTimeInMillis(expirationFormat.parse(expiration).getTime());

        License license = new License(uuid, name, version, expirationCal);

        for (Object key : properties.keySet()) {
            String keyStr = key.toString();
            if (!mandatoryFields.contains(keyStr)) {
                if (keyStr.startsWith(License.FEATURE_PREFIX)) {
                    license.addFeature(keyStr, Boolean.parseBoolean(properties.getProperty(keyStr)));
                }
                else {
                    return null;
                }
            }
        }

        return license;
    }

    public static String readSignatureHex(InputStream input) throws IOException {
        Properties properties = new Properties();
        properties.load(input);
        return properties.getProperty(PROP_SIGNATURE);
    }
}
