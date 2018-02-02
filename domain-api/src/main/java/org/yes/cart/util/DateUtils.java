/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: denispavlov
 * Date: 31/01/2018
 * Time: 20:56
 */
public final class DateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

    private static final DateTimeFormatter STANDARD_DATETIME_PARSE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd[ HH:mm:ss]");

    private static final DateTimeFormatter STANDARD_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter STANDARD_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter EXPORT_FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final DateTimeFormatter IMPEX_FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

    private static final DateTimeFormatter AUTO_IMPORT_TIMESTAMP =
            DateTimeFormatter.ofPattern("_yyyy-MM-dd_HHmmss_SSS");

    private static final DateTimeFormatter YEAR =
            DateTimeFormatter.ofPattern("yyyy");

    private static final Map<Locale, DateTimeFormatter> CUSTOMER_DATE = new ConcurrentHashMap<>();

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private static final String INSTANT_COMMENT = " //";

    private DateUtils() {
        // no instance
    }

    /**
     * Zone ID.
     *
     * @return Zone ID
     */
    public static ZoneId zone() {
        return ZONE;
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @return timestamp
     */
    public static String exportFileTimestamp() {
        return exportFileTimestamp(ZonedDateTime.now(ZONE));
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @return timestamp
     */
    public static String exportFileTimestamp(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(EXPORT_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @return timestamp
     */
    public static String impexFileTimestamp() {
        return impexFileTimestamp(ZonedDateTime.now(ZONE));
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @return timestamp
     */
    public static String impexFileTimestamp(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(IMPEX_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  _yyyy-MM-dd_HHmmss_SSS format
     *
     * @return timestamp
     */
    public static String autoImportTimestamp() {
        return autoImportTimestamp(ZonedDateTime.now(ZONE));
    }

    /**
     * Return timestamp for "now" in  _yyyy-MM-dd_HHmmss_SSS format
     *
     * @return timestamp
     */
    public static String autoImportTimestamp(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(AUTO_IMPORT_TIMESTAMP);
    }

    /**
     * Convert to old Date
     *
     * @param instant date time
     *
     * @return date
     */
    public static Date from(final Instant instant) {
        return instant != null ? Date.from(instant) : null;
    }

    /**
     * Convert to old Date
     *
     * @param zonedDateTime date time
     *
     * @return date
     */
    public static Date from(final ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? Date.from(zonedDateTime.toInstant()) : null;
    }

    /**
     * Convert to old Date
     *
     * @param localDateTime date time
     *
     * @return date
     */
    public static Date from(final LocalDateTime localDateTime) {
        return localDateTime != null ? Date.from(localDateTime.atZone(ZONE).toInstant()) : null;
    }

    /**
     * Convert to old Date
     *
     * @param localDate date
     *
     * @return date
     */
    public static Date from(final LocalDate localDate) {
        return localDate != null ? Date.from(localDate.atStartOfDay(ZONE).toInstant()) : null;
    }

    /**
     * Get millis.
     *
     * @param localDate date
     *
     * @return millis
     */
    public static long millis(final LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay(ZONE).toInstant().toEpochMilli() : 0L;
    }

    /**
     * Get millis.
     *
     * @param localDateTime date
     *
     * @return millis
     */
    public static long millis(final LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZONE).toInstant().toEpochMilli() : 0L;
    }

    /**
     * Get millis.
     *
     * @param zonedDateTime date
     *
     * @return millis
     */
    public static long millis(final ZonedDateTime zonedDateTime) {
        return zonedDateTime != null ? zonedDateTime.toInstant().toEpochMilli() : 0L;
    }

    private static Date dParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            try {

                TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    return Date.from(((LocalDateTime) temporalAccessor).atZone(ZONE).toInstant());
                }
                return Date.from(((LocalDate) temporalAccessor).atStartOfDay(ZONE).toInstant());
            } catch (Exception exp) {
                LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }

    private static Instant iParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            final String[] instant = StringUtils.splitByWholeSeparator(datetime, INSTANT_COMMENT);
            final long time = NumberUtils.toLong(instant[0]);
            if (time > 0L) {
                return Instant.ofEpochMilli(time);
            } else {
                LOG.warn("Using date time for Instant conversion");
                try {
                    TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                    if (temporalAccessor instanceof LocalDateTime) {
                        return ((LocalDateTime) temporalAccessor).atZone(ZONE).toInstant();
                    }
                    return ((LocalDate) temporalAccessor).atStartOfDay(ZONE).toInstant();
                } catch (Exception exp) {
                    LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                    LOG.error(exp.getMessage(), exp);
                }
            }
        }
        return null;

    }

    private static LocalDate ldParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            try {

                TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    return ((LocalDateTime) temporalAccessor).toLocalDate();
                }
                return (LocalDate) temporalAccessor;
            } catch (Exception exp) {
                LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }


    private static LocalDateTime ldtParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            try {

                TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    return (LocalDateTime) temporalAccessor;
                }
                return ((LocalDate) temporalAccessor).atStartOfDay();
            } catch (Exception exp) {
                LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }


    private static ZonedDateTime zdtParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            try {

                TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    return ((LocalDateTime) temporalAccessor).atZone(ZONE);
                }
                return ((LocalDate) temporalAccessor).atStartOfDay(ZONE);
            } catch (Exception exp) {
                LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }


    /**
     * Parse date or date time in the following formats:
     * yyyy-MM-dd
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date object
     */
    public static Date dParseSDT(final String datetime) {

        return dParse(datetime, STANDARD_DATETIME_PARSE);

    }

    /**
     * Parse date or date time in the following formats:
     * yyyy-MM-dd
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date object
     */
    public static Instant iParseSDT(final String datetime) {

        return iParse(datetime, STANDARD_DATETIME_PARSE);

    }

    /**
     * Parse date or date time in the following formats:
     * yyyy-MM-dd
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date object
     */
    public static LocalDate ldParseSDT(final String datetime) {

        return ldParse(datetime, STANDARD_DATETIME_PARSE);

    }

    /**
     * Parse date or date time in the following formats:
     * yyyy-MM-dd
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date object
     */
    public static LocalDateTime ldtParseSDT(final String datetime) {

        return ldtParse(datetime, STANDARD_DATETIME_PARSE);

    }

    /**
     * Parse date or date time in the following formats:
     * yyyy-MM-dd
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date object
     */
    public static ZonedDateTime zdtParseSDT(final String datetime) {

        return zdtParse(datetime, STANDARD_DATETIME_PARSE);

    }

    private static String format(final Date datetime, final DateTimeFormatter formatter) {

        if (datetime != null) {
            try {
                if (datetime instanceof java.sql.Date) { // java.sql.Date.toInstant() is not supported operation
                    return format(((java.sql.Date) datetime).toLocalDate(), formatter);
                }
                return ZonedDateTime.ofInstant(datetime.toInstant(), ZONE).format(formatter);
            } catch (Exception exp) {
                LOG.error("Unable to format date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }

    private static String format(final LocalDateTime datetime, final DateTimeFormatter formatter) {

        if (datetime != null) {
            try {
                return datetime.atZone(ZONE).format(formatter);
            } catch (Exception exp) {
                LOG.error("Unable to format date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }

    private static String format(final LocalDate datetime, final DateTimeFormatter formatter) {

        if (datetime != null) {
            try {
                return datetime.atStartOfDay(ZONE).format(formatter);
            } catch (Exception exp) {
                LOG.error("Unable to format date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }

    private static String format(final ZonedDateTime datetime, final DateTimeFormatter formatter) {

        if (datetime != null) {
            try {
                return datetime.format(formatter);
            } catch (Exception exp) {
                LOG.error("Unable to format date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }



    /**
     * Format date now in the following formats:
     * yyyy-MM-dd HH:mm:ss
     *
     * @return date now
     */
    public static String formatSDT() {

        return format(ZonedDateTime.now(ZONE), STANDARD_DATETIME);

    }


    /**
     * Format date or date time in the following formats:
     * SSSSS // ISO-ZONED-DATE
     *
     * @param instant date
     *
     * @return date
     */
    public static String formatSDT(final Instant instant) {

        return instant.toEpochMilli()
                + INSTANT_COMMENT
                + ZonedDateTime.ofInstant(instant, ZONE);

    }


    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date
     */
    public static String formatSDT(final Date datetime) {

        return format(datetime, STANDARD_DATETIME);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date
     */
    public static String formatSDT(final LocalDate datetime) {

        return format(datetime, STANDARD_DATETIME);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date
     */
    public static String formatSDT(final LocalDateTime datetime) {

        return format(datetime, STANDARD_DATETIME);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime date or datetime
     *
     * @return date
     */
    public static String formatSDT(final ZonedDateTime datetime) {

        return format(datetime, STANDARD_DATETIME);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd
     *
     * @param date date
     *
     * @return date
     */
    public static String formatSD(final Date date) {

        return format(date, STANDARD_DATE);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd
     *
     * @param date date
     *
     * @return date
     */
    public static String formatSD(final LocalDate date) {

        return format(date, STANDARD_DATE);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd
     *
     * @param date date
     *
     * @return date
     */
    public static String formatSD(final LocalDateTime date) {

        return format(date, STANDARD_DATE);

    }

    /**
     * Format date or date time in the following formats:
     * yyyy-MM-dd
     *
     * @param date date
     *
     * @return date
     */
    public static String formatSD(final ZonedDateTime date) {

        return format(date, STANDARD_DATE);

    }



    /**
     * Format date now in the following formats:
     * yyyy
     *
     * @return year now
     */
    public static String formatYear() {

        return format(ZonedDateTime.now(ZONE), YEAR);

    }



    /**
     * Format date in locale specific way in the following formats:
     * dd MMMMM yyyy
     *
     * @return year now
     */
    public static String formatCustomer(final Date date, final Locale locale) {

        DateTimeFormatter dtf = CUSTOMER_DATE.get(locale);
        if (dtf == null) {
            dtf = DateTimeFormatter.ofPattern("dd MMMM yyyy");
            CUSTOMER_DATE.put(locale, dtf);
        }

        return format(date, dtf);


    }



}
