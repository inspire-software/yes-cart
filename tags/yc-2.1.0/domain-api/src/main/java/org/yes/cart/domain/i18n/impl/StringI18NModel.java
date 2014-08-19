/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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

package org.yes.cart.domain.i18n.impl;

import org.yes.cart.domain.i18n.I18NModel;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 12-08-08
 * Time: 8:26 PM
 */
public class StringI18NModel implements I18NModel {

    public static final String SEPARATOR = "#~#";
    private static final Pattern PATTERN = Pattern.compile(SEPARATOR);

    private final Map<String, String> values = new HashMap<String, String>();

    public StringI18NModel(final Map<String, String> values) {
        if (values != null && !values.isEmpty()) {
            this.values.putAll(values);
        }
    }

    public StringI18NModel(final String raw) {
        if (raw != null && raw.length() > 0) {
            final Matcher matcher = PATTERN.matcher(raw);
            int lastPosition = 0;
            int beforeSep;
            while (matcher.find()) {
                beforeSep = matcher.start();
                final String key = raw.substring(lastPosition, beforeSep);
                lastPosition = matcher.end();
                if (matcher.find()) {
                    beforeSep = matcher.start();
                    final String value = raw.substring(lastPosition, beforeSep);
                    lastPosition = matcher.end();
                    if (value != null && value.length() > 0) {
                        values.put(key, value);
                    }
                } else if (raw.length() - 1 != lastPosition) {
                    final String value = raw.substring(lastPosition);
                    if (value != null && value.length() > 0) {
                        values.put(key, value);
                    }
                }
            }
        }
    }

    /** {@inheritDoc} */
    public String getValue(final String locale) {
        return values.get(locale);
    }

    /** {@inheritDoc} */
    public void putValue(final String locale, final String value) {
        values.put(locale, value);
    }

    /** {@inheritDoc} */
    public Map<String, String> getAllValues() {
        return values;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        for (final Map.Entry<String, String> entry : values.entrySet()) {
            out.append(entry.getKey()).append(SEPARATOR).append(entry.getValue()).append(SEPARATOR);
        }
        return out.toString();
    }
}
