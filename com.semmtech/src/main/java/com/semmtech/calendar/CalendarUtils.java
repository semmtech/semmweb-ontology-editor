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

package com.semmtech.calendar;


import java.util.Calendar;


/**
 * 
 * @author Mike Henrichs
 * @author Simone Rondelli
 */
public class CalendarUtils {

    /**
     * http://stackoverflow.com/questions/3299972/difference-in-days-between-two
     * -dates-in-java
     * 
     * This is not quick but if only doing a few days backwards/forwards then it
     * is very accurate.
     * 
     * @param startDate
     *            from
     * @param endDate
     *            to
     * @return day count between the two dates, this can be negative if
     *         startDate is after endDate
     */
    public static long daysBetween(final Calendar startDate, final Calendar endDate) {

        // Forwards or backwards?
        final boolean forward = startDate.before(endDate);
        // Which direction are we going
        final int multiplier = forward ? 1 : -1;

        // The date we are going to move.
        final Calendar date = (Calendar) startDate.clone();

        // Result
        long daysBetween = 0;

        // Start at millis (then bump up until we go back a day)
        int fieldAccuracy = 4;
        int field;
        int dayBefore, dayAfter;
        while (forward && date.before(endDate) || !forward && endDate.before(date)) {
            // We start moving slowly if no change then we decrease accuracy.
            switch (fieldAccuracy) {
            case 4:
                field = Calendar.MILLISECOND;
                break;
            case 3:
                field = Calendar.SECOND;
                break;
            case 2:
                field = Calendar.MINUTE;
                break;
            case 1:
                field = Calendar.HOUR_OF_DAY;
                break;
            default:
            case 0:
                field = Calendar.DAY_OF_MONTH;
                break;
            }
            // Get the day before we move the time, Change, then get the day
            // after.
            dayBefore = date.get(Calendar.DAY_OF_MONTH);
            date.add(field, multiplier);
            dayAfter = date.get(Calendar.DAY_OF_MONTH);

            // This shifts lining up the dates, one field at a time.
            if (dayBefore == dayAfter && date.get(field) == endDate.get(field))
                fieldAccuracy--;
            // If day has changed after moving at any accuracy level we bump the
            // day counter.
            if (dayBefore != dayAfter) {
                daysBetween += multiplier;
            }
        }
        return daysBetween;
    }

    /**
     * Return a new instance of Calendar without the information about hour
     * minutes and seconds:
     * <ul>
     * <li>2014/08/02 23:59:59 --> 2014/08/02 00:00:00</li>
     * <li>2014/08/02 12:59:59 --> 2014/08/02 00:00:00</li>
     * <li>2014/08/02 01:00:00 --> 2014/08/02 00:00:00</li>
     * </ul>
     */
    public static Calendar normalizeToDay(Calendar calendar) {
        Calendar normalized = Calendar.getInstance(calendar.getTimeZone());
        normalized.setTimeInMillis(calendar.getTimeInMillis());
        normalized.set(Calendar.HOUR_OF_DAY, 0); // set hour to midnight
        normalized.set(Calendar.MINUTE, 0); // set minute in hour
        normalized.set(Calendar.SECOND, 0); // set second in minute
        normalized.set(Calendar.MILLISECOND, 0);
        // normalized.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        // normalized.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        // normalized.set(Calendar.DAY_OF_MONTH,
        // calendar.get(Calendar.DAY_OF_MONTH));
        return normalized;
    }
}
