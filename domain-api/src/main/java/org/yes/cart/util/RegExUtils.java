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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 17/05/2018
 * Time: 07:23
 */
public final class RegExUtils {

    private static final Map<String, RegEx> CACHE = new ConcurrentHashMap<>();

    private RegExUtils() {
        // no instance
    }

    /**
     * Cache and get instance of pattern.
     *
     * @param pattern pattern
     *
     * @return regex
     */
    public static RegEx getInstance(final String pattern) {
        return CACHE.computeIfAbsent(pattern, RegEx::new);
    }


    public static class RegEx {

        private final String patternString;
        private final Pattern pattern;

        public RegEx(final String patternString) {
            this.patternString = patternString;
            this.pattern = Pattern.compile(patternString);
        }

        /**
         * @return compiled pattern
         */
        public Pattern getPattern() {
            return pattern;
        }

        /**
         * @return original pattern string
         */
        public String getPatternString() {
            return patternString;
        }

        /**
         * @param input input
         *
         * @return quick match method
         */
        public boolean matches(final String input) {
            return pattern.matcher(input).matches();
        }
    }

}
