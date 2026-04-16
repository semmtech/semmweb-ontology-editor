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
import static com.semmtech.cryptography.license.LicenseConstants.DEFAULT_ENCODING;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.semmtech.calendar.CalendarUtils;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class License {

    /**
     * For the deserialization purpose we assume that each feature key starts
     * with 'license.feature.', in this way we can distinguish the features from
     * the others values.
     */
    public static final String FEATURE_PREFIX = "license.feature.";

    private final UUID id;
    private final String name;
    private final String version;
    private final Calendar expirationDate;

    private Map<String, Boolean> features;

    public License(UUID id, String name, String version, Calendar expiration) {
        if (id == null) {
            throw new IllegalArgumentException("The id must not be null!");
        }
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("The name must not be null or empty!");
        }
        if (Strings.isNullOrEmpty(version)) {
            throw new IllegalArgumentException("The version must not be null or empty!");
        }
        if (expiration == null) {
            throw new IllegalArgumentException("The expiration date must not be null!");
        }

        // normalize the calendar

        this.id = id;
        this.name = name;
        this.version = version;
        this.expirationDate = CalendarUtils.normalizeToDay(expiration);
        features = Maps.newHashMap();
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public Calendar getExpiration() {
        return expirationDate;
    }

    /**
     * The returned Map contains all the features of this plugin and is
     * unmodifiable. For adding features use {@code  addFeature(key, state)}
     * <p>
     * NB: The returned features key contains the prefix
     */
    public Map<String, Boolean> getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    /**
     * The returned Map contains all the features of this plugin and is
     * unmodifiable. For adding features use {@code  addFeature(key, state)}
     * <p>
     * NB: The returned features key doesn't contains the prefix
     * 
     * <pre>
     * public Map<String, Boolean> getFeaturesWithoutPrefix() {
     *     Map<String, Boolean> featuresWithoutPrefix = Maps.newHashMap();
     * 
     *     for (String featureId : features.keySet()) {
     *         String newFeatureId = featureId.replace(FEATURE_PREFIX, "");
     *         featuresWithoutPrefix.put(newFeatureId, features.get(featureId));
     *     }
     * 
     *     return Collections.unmodifiableMap(featuresWithoutPrefix);
     * }
     */

    public void addFeature(String featureId, Boolean state) {
        checkFeatureKey(featureId);

        if (state == null) {
            throw new IllegalStateException("The features state must not be null. Feature id: "
                    + featureId);
        }

        features.put(featureId, state);
    }

    public boolean isFeatureEnabled(String featureId, Calendar date) throws LicenseException {
        checkFeatureKey(featureId);

        if (!features.containsKey(featureId)) {
            throw new LicenseException("The feature " + featureId + " is not part of the license");
        }

        return features.get(featureId) && !isExpired(date);
    }

    public boolean isFeatureEnabled(String featureId) throws LicenseException {
        checkFeatureKey(featureId);

        if (!features.containsKey(featureId)) {
            throw new LicenseException("The feature " + featureId + " is not part of license");
        }

        return features.get(featureId);
    }

    public boolean isVersionValid(String version) {
        return this.version.equals(version);
    }

    public boolean containsFeature(String featureId) {
        return features.containsKey(featureId);
    }

    public boolean isExpired(Calendar date) {
        return getRemainingDaysFrom(date) < 0;
    }

    public long getRemainingDaysFrom(Calendar date) {
        Calendar other = CalendarUtils.normalizeToDay(date);
        return CalendarUtils.daysBetween(other, expirationDate);
    }

    /**
     * Return the encoded representation of this license
     */
    public byte[] encode() {
        SimpleDateFormat expirationFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        expirationFormat.setTimeZone(TimeZone.getTimeZone(LicenseConstants.DEFAULT_TIME_ZONE));

        String id = this.id.toString();
        String expiration = expirationFormat.format(this.expirationDate.getTime());
        String content = String.format("%s;%s;%s;%s;", id, this.name, this.version, expiration);

        // The order in which the key is encoded is relevant
        List<String> orderedFeaturesKey = Lists.newArrayList(this.features.keySet());
        Collections.sort(orderedFeaturesKey);

        for (String featureId : this.features.keySet()) {
            content += featureId.replace(FEATURE_PREFIX, "") + ":" + this.features.get(featureId)
                    + ";";
        }

        byte[] data = content.getBytes(Charset.forName(DEFAULT_ENCODING));
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof License) {
            License other = (License) obj;
            return Arrays.equals(encode(), other.encode());
        }
        return false;
    }

    /**
     * Add simply the prefix to the passed featureId
     */
    public static String generatyFeatureKey(String featureId) {
        return FEATURE_PREFIX + featureId;
    }

    private void checkFeatureKey(String featureId) {
        if (featureId == null) {
            throw new IllegalArgumentException("The features key must not be null. Feature id: "
                    + featureId);
        }

        if (!featureId.startsWith(FEATURE_PREFIX)) {
            throw new IllegalArgumentException("The features key must start with: "
                    + FEATURE_PREFIX + ". Feature id: " + featureId);
        }
    }
}
