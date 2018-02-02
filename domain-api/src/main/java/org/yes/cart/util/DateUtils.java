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

    private static final DateTimeFormatter NUMBER_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final DateTimeFormatter EXPORT_FILE_TIMESTAMP =
            NUMBER_TIMESTAMP;

    private static final DateTimeFormatter IMPEX_FILE_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");

    private static final DateTimeFormatter AUTO_IMPORT_TIMESTAMP =
            DateTimeFormatter.ofPattern("_yyyy-MM-dd_HHmmss_SSS");

    private static final DateTimeFormatter YEAR =
            DateTimeFormatter.ofPattern("yyyy");

    private static final Map<String, DateTimeFormatter> CUSTOM = new ConcurrentHashMap<>();
    static {
        CUSTOM.put("yyyy-MM-dd HH:mm:ss", STANDARD_DATETIME);
        CUSTOM.put("yyyy-MM-dd", STANDARD_DATE);
        CUSTOM.put("yyyyMMddHHmmss", NUMBER_TIMESTAMP);
        CUSTOM.put("_yyyy-MM-dd_HHmmss_SSS", AUTO_IMPORT_TIMESTAMP);
        CUSTOM.put("dd/MM/yyyy", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private static final Map<Locale, Map<String, DateTimeFormatter>> CUSTOM_LOCAL = new ConcurrentHashMap<>();

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
        return exportFileTimestamp(ZonedDateTime.now(zone()));
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param date date time
     *
     * @return timestamp
     */
    public static String exportFileTimestamp(final Date date) {
        return format(date, EXPORT_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param instant date time
     *
     * @return timestamp
     */
    public static String exportFileTimestamp(final Instant instant) {
        return format(instant, EXPORT_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param localDateTime local date time
     *
     * @return timestamp
     */
    public static String exportFileTimestamp(final LocalDateTime localDateTime) {
        return format(localDateTime, EXPORT_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param zonedDateTime zoned date time
     *
     * @return timestamp
     */
    public static String exportFileTimestamp(final ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, EXPORT_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param dateTime date time
     *
     * @return timestamp
     */
    public static String numberTimestamp(final Date dateTime) {
        return format(dateTime, NUMBER_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param instant date time
     *
     * @return timestamp
     */
    public static String numberTimestamp(final Instant instant) {
        return format(instant, NUMBER_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param localDateTime date time
     *
     * @return timestamp
     */
    public static String numberTimestamp(final LocalDateTime localDateTime) {
        return format(localDateTime, NUMBER_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyyMMddHHmmss format
     *
     * @param zonedDateTime date time
     *
     * @return timestamp
     */
    public static String numberTimestamp(final ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, NUMBER_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @return timestamp
     */
    public static String impexFileTimestamp() {
        return impexFileTimestamp(ZonedDateTime.now(zone()));
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @param date date time
     *
     * @return timestamp
     */
    public static String impexFileTimestamp(final Date date) {
        return format(date, IMPEX_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @param instant date time
     *
     * @return timestamp
     */
    public static String impexFileTimestamp(final Instant instant) {
        return format(instant, IMPEX_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @param localDateTime date time
     *
     * @return timestamp
     */
    public static String impexFileTimestamp(final LocalDateTime localDateTime) {
        return format(localDateTime, IMPEX_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  yyyy-MM-dd_HHmmss format
     *
     * @param zonedDateTime date time
     *
     * @return timestamp
     */
    public static String impexFileTimestamp(final ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, IMPEX_FILE_TIMESTAMP);
    }

    /**
     * Return timestamp for "now" in  _yyyy-MM-dd_HHmmss_SSS format
     *
     * @return timestamp
     */
    public static String autoImportTimestamp() {
        return autoImportTimestamp(ZonedDateTime.now(zone()));
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
     * @param localDate date
     *
     * @return date
     */
    public static Date from(final LocalDate localDate) {
        return localDate != null ? Date.from(localDate.atStartOfDay(zone()).toInstant()) : null;
    }

    /**
     * Convert to old Date
     *
     * @param localDateTime date time
     *
     * @return date
     */
    public static Date from(final LocalDateTime localDateTime) {
        return localDateTime != null ? Date.from(localDateTime.atZone(zone()).toInstant()) : null;
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
     * Get millis.
     *
     * @param localDate date
     *
     * @return millis
     */
    public static long millis(final LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay(zone()).toInstant().toEpochMilli() : 0L;
    }

    /**
     * Get millis.
     *
     * @param localDateTime date
     *
     * @return millis
     */
    public static long millis(final LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(zone()).toInstant().toEpochMilli() : 0L;
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

    private static DateTimeFormatter lazyLoad(final String pattern) {
        DateTimeFormatter dtf = CUSTOM.get(pattern);
        if (dtf == null) {
            LOG.info("Add new custom date time formatter using pattern {}", pattern);
            dtf = DateTimeFormatter.ofPattern(pattern);
        }
        return dtf;
    }

    private static DateTimeFormatter lazyLoad(final String pattern, final Locale locale) {
        Map<String, DateTimeFormatter> dtfLocal = CUSTOM_LOCAL.get(locale);
        if (dtfLocal == null) {
            dtfLocal = new ConcurrentHashMap<>();
            CUSTOM_LOCAL.put(locale, dtfLocal);
        }
        DateTimeFormatter dtf = dtfLocal.get(pattern);
        if (dtf == null) {
            LOG.info("Add new custom date time formatter using pattern {} for {}", pattern, locale);
            dtf = DateTimeFormatter.ofPattern(pattern);
        }
        return dtf;
    }

    /**
     * Parse date or date time using pattern
     *
     * @param datetime date or datetime
     * @param pattern pattern
     *
     * @return date object
     */
    public static Date dParse(final String datetime, final String pattern) {
        return dParse(datetime, lazyLoad(pattern));
    }

    private static Date dParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            try {

                TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    return Date.from(((LocalDateTime) temporalAccessor).atZone(zone()).toInstant());
                }
                return Date.from(((LocalDate) temporalAccessor).atStartOfDay(zone()).toInstant());
            } catch (Exception exp) {
                LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }


    /**
     * Parse date or date time using pattern
     *
     * @param datetime date or datetime
     * @param pattern pattern
     *
     * @return date object
     */
    public static Instant iParse(final String datetime, final String pattern) {
        return iParse(datetime, lazyLoad(pattern));
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
                        return ((LocalDateTime) temporalAccessor).atZone(zone()).toInstant();
                    }
                    return ((LocalDate) temporalAccessor).atStartOfDay(zone()).toInstant();
                } catch (Exception exp) {
                    LOG.error("Unable to parse date {} using formatter {}", datetime, formatter);
                    LOG.error(exp.getMessage(), exp);
                }
            }
        }
        return null;

    }


    /**
     * Parse date or date time using pattern
     *
     * @param datetime date or datetime
     * @param pattern pattern
     *
     * @return date object
     */
    public static LocalDate ldParse(final String datetime, final String pattern) {
        return ldParse(datetime, lazyLoad(pattern));
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


    /**
     * Parse date or date time using pattern
     *
     * @param datetime date or datetime
     * @param pattern pattern
     *
     * @return date object
     */
    public static LocalDateTime ldtParse(final String datetime, final String pattern) {
        return ldtParse(datetime, lazyLoad(pattern));
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


    /**
     * Parse date or date time using pattern
     *
     * @param datetime date or datetime
     * @param pattern pattern
     *
     * @return date object
     */
    public static ZonedDateTime zdtParse(final String datetime, final String pattern) {
        return zdtParse(datetime, lazyLoad(pattern));
    }


    private static ZonedDateTime zdtParse(final String datetime, final DateTimeFormatter formatter) {

        if (StringUtils.isNotBlank(datetime)) {
            try {

                TemporalAccessor temporalAccessor = formatter.parseBest(datetime, LocalDateTime::from, LocalDate::from);
                if (temporalAccessor instanceof LocalDateTime) {
                    return ((LocalDateTime) temporalAccessor).atZone(zone());
                }
                return ((LocalDate) temporalAccessor).atStartOfDay(zone());
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
                return ZonedDateTime.ofInstant(datetime.toInstant(), zone()).format(formatter);
            } catch (Exception exp) {
                LOG.error("Unable to format date {} using formatter {}", datetime, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }

    private static String format(final Instant instant, final DateTimeFormatter formatter) {

        if (instant != null) {
            try {
                return ZonedDateTime.ofInstant(instant, zone()).format(formatter);
            } catch (Exception exp) {
                LOG.error("Unable to format date {} using formatter {}", instant, formatter);
                LOG.error(exp.getMessage(), exp);
            }
        }
        return null;

    }

    private static String format(final LocalDate datetime, final DateTimeFormatter formatter) {

        if (datetime != null) {
            try {
                return datetime.atStartOfDay(zone()).format(formatter);
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
                return datetime.atZone(zone()).format(formatter);
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

        return format(ZonedDateTime.now(zone()), STANDARD_DATETIME);

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
                + ZonedDateTime.ofInstant(instant, zone());

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

        return format(ZonedDateTime.now(zone()), YEAR);

    }



    /**
     * Format date in locale specific way in the following formats:
     * dd MMMMM yyyy
     *
     * @param instant date time
     * @param locale locale
     *
     * @return year now
     */
    public static String formatCustomer(final Instant instant, final Locale locale) {

        return format(instant, lazyLoad("dd MMMM yyyy", locale));

    }

    /**
     * Format date in locale specific way in the following formats:
     * dd MMMMM yyyy
     *
     * @param date date time
     * @param locale locale
     *
     * @return year now
     */
    public static String formatCustomer(final Date date, final Locale locale) {

        return format(date, lazyLoad("dd MMMM yyyy", locale));

    }

    /**
     * Format date in locale specific way in the following formats:
     * dd MMMMM yyyy
     *
     * @param date date time
     * @param locale locale
     *
     * @return year now
     */
    public static String formatCustomer(final LocalDate date, final Locale locale) {

        return format(date, lazyLoad("dd MMMM yyyy", locale));

    }

    /**
     * Format date in locale specific way in the following formats:
     * dd MMMMM yyyy
     *
     * @param date date time
     * @param locale locale
     *
     * @return year now
     */
    public static String formatCustomer(final LocalDateTime date, final Locale locale) {

        return format(date, lazyLoad("dd MMMM yyyy", locale));

    }

    /**
     * Format date in locale specific way in the following formats:
     * dd MMMMM yyyy
     *
     * @param date date time
     * @param locale locale
     *
     * @return year now
     */
    public static String formatCustomer(final ZonedDateTime date, final Locale locale) {

        return format(date, lazyLoad("dd MMMM yyyy", locale));

    }



}
