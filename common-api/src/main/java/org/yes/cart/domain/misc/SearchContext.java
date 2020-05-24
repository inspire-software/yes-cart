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

package org.yes.cart.domain.misc;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * User: denispavlov
 * Date: 27/10/2019
 * Time: 10:56
 */
public class SearchContext {

    public enum JoinMode {

        OR("or"), AND("and");

        private final String mode;

        JoinMode(final String mode) {
            this.mode = mode;
        }

        /**
         * Get internal name for mode.
         *
         * @return mode
         */
        public String mode() {
            return this.mode;
        }

        /**
         * Convenience method to match mode in a polymorphic way.
         *
         * @param mode mode enum or String.
         *
         * @return true if matches
         */
        public boolean matches(final Object mode) {
            return mode == this || (mode instanceof String && ((String) mode).equalsIgnoreCase(this.mode));
        }

        /**
         * Convenience method to set mode in filter object.
         *
         * @param filter filter to set mode in
         */
        public void setMode(final Map<String, List> filter) {
            filter.put(JOIN, Collections.singletonList(this));
        }
    }

    public enum MatchMode {

        LT("<"), LE("<="), NE("="), EQ("="), GE(">="), GT(">"), NULL("null"), NOTNULL("not null"), EMPTY("empty"), NOTEMPTY("not empty"), ANY("*");

        private final String mode;

        MatchMode(final String mode) {
            this.mode = mode;
        }

        /**
         * Get internal name for mode.
         *
         * @return mode
         */
        public String mode() {
            return this.mode;
        }

        /**
         * Convenience method to match mode in a polymorphic way.
         *
         * @param mode mode enum or String.
         *
         * @return true if matches
         */
        public boolean matches(final Object mode) {
            return mode == this ||  mode instanceof String && ((String) mode).equalsIgnoreCase(this.mode);
        }

        /**
         * Convenience method to wrap values with match mode.
         *
         * @param value value to wrap.
         *
         * @return mode and value
         */
        public Pair<SearchContext.MatchMode, Object> toParam(final Object value) {
            return new Pair<>(this, value);
        }

    }

    public static final String JOIN = "#join#";

    private final Map<String, List> parameters;
    private final int start;
    private final int size;
    private final String sortBy;
    private final boolean sortDesc;

    public SearchContext(final Map<String, List> parameters,
                         final int start,
                         final int size,
                         final String sortBy,
                         final boolean sortDesc,
                         final String ... allowed) {
        this.parameters = collectParameters(parameters, allowed);
        this.start = start;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDesc = sortDesc;
    }

    /**
     * All parameters.
     *
     * @return parameters
     */
    public Map<String, List> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Reduce the parameters map to restricted set only.
     *
     * @param allowed   allowed keys
     *
     * @return reduced map
     */
    public Map<String, List> reduceParameters(final String ... allowed) {
        final Map<String, List> reduced = new LinkedHashMap<>();
        if (allowed != null) {
            for (final String allow : allowed) {
                final List existing = parameters.get(allow);
                if (existing != null) {
                    reduced.put(allow, existing);
                }
            }
        }
        return reduced;
    }

    /**
     * Expand one parameter into an OR'ed filter.
     *
     * @param key       key to expand
     * @param expand    keys to expand to
     *
     * @return expanded map
     */
    public Map<String, List> expandParameter(final String key, final String ... expand) {
        final Map<String, List> expanded = new LinkedHashMap<>();
        final List existing = parameters.get(key);
        if (existing != null && expand != null) {
            for (final String item : expand) {
                expanded.put(item, existing);
            }
            JoinMode.OR.setMode(expanded);
        }
        return expanded;
    }

    /**
     * Start page index, starting from 0.
     *
     * @return start page
     */
    public int getStart() {
        return start;
    }

    /**
     * Page size.
     *
     * @return page size
     */
    public int getSize() {
        return size;
    }

    /**
     * Sort field.
     *
     * @return sort field
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * If sort field specified, defines if it is in descending order.
     *
     * @return descending order of sorting
     */
    public boolean isSortDesc() {
        return sortDesc;
    }

    private Map<String, List> collectParameters(final Map<String, List> parameters, final String ... allowed) {
        final Map<String, List> collected = new LinkedHashMap<>();
        if (allowed != null && parameters != null) {
            for (final String key : allowed) {
                final List val = parameters.get(key);
                if (val != null && !val.isEmpty()) {
                    final List copyNonBlank = new ArrayList();
                    for (final Object valItem : val) {
                        if (valItem != null) {
                            if (!(valItem instanceof String) || ((String) valItem).length() > 0) {
                                copyNonBlank.add(valItem);
                            }
                        }
                    }
                    if (!copyNonBlank.isEmpty()) {
                        collected.put(key, copyNonBlank);
                    }
                }
            }
        }
        return collected;
    }

}
