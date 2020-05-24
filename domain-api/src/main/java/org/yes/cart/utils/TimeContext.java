/*
 * Copyright 2009 Inspire-Software.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.utils;

import java.time.*;

/**
 * User: denispavlov
 * Date: 17/10/2017
 * Time: 18:31
 */
public final class TimeContext {

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final ThreadLocal<Instant> NOW = new ThreadLocal<>();
    private static final ThreadLocal<ZoneId> ZONE = ThreadLocal.withInitial(() -> DEFAULT_ZONE);

    private TimeContext() {
        // no instance
    }

    /**
     * Get zone ID for current time context.
     *
     * @return current zone
     */
    public static ZoneId getZone() {
        return ZONE.get();
    }

    /**
     * Time frame set for this thread local.
     * If time frame is not set then new Date() is returned.
     *
     * @return date now
     */
    public static long getMillis() {
        final Instant instant = NOW.get();
        if (instant == null) {
            return System.currentTimeMillis();
        }
        return instant.toEpochMilli();
    }


    /**
     * Time frame set for this thread local.
     * If time frame is not set then new Date() is returned.
     *
     * @return date now
     */
    public static Instant getTime() {
        final Instant instant = NOW.get();
        if (instant == null) {
            return Instant.now();
        }
        return instant;
    }


    /**
     * Time frame set for this thread local.
     *
     * If time frame is not set then current system time is returned.
     *
     * @return date now in given zone
     */
    public static LocalDate getLocalDate() {
        return getLocalDateTime().toLocalDate();
    }


    /**
     * Time frame set for this thread local.
     *
     * If time frame is not set then current system time is returned.
     *
     * @return date time now in given zone
     */
    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.ofInstant(getTime(), getZone());
    }


    /**
     * Time frame set for this thread local.
     *
     * If time frame is not set then current system time is returned.
     *
     * @return date time now in given zone
     */
    public static ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.ofInstant(getTime(), getZone());
    }


    /**
     * Time frame to use for this thread local.
     *
     * Setting null results in {@link #getTime()} producing new instant on each invocation.
     * Setting this to an Instant "freezes" time to this instant until {@link #clear()} is called.
     *
     * @param time time
     */
    public static void setTime(final Instant time) {
        NOW.set(time);
    }

    /**
     * Set time frame to now (Instant.now())
     */
    public static void setNow() {
        setTime(Instant.now());
    }

    /**
     * Set zone ID.
     *
     * Zone ID to use for localized date/datetime conversions.
     * Setting this value to null results in resetting zone id to {@link #DEFAULT_ZONE}
     *
     * @param zoneId zone ID
     */
    public static void setZone(final ZoneId zoneId) {
        ZONE.set(zoneId != null ? zoneId : DEFAULT_ZONE);
    }



    /**
     * Remove thread local setting
     */
    public static void clear() {
        setTime(null);
        setZone(null);
    }

    /**
     * Explicitly remove thread locals to prevent memory leaks.
     */
    public static  void destroy() {
        NOW.remove();
        ZONE.remove();
    }


}
