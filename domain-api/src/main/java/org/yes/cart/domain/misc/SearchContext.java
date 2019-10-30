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

package org.yes.cart.domain.misc;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 27/10/2019
 * Time: 10:56
 */
public class SearchContext {

    private final Map<String, List> parameters;
    private final int start;
    private final int size;
    private final String sortBy;
    private final boolean sortDesc;

    public SearchContext(final Map<String, List> parameters,
                         final int start,
                         final int size,
                         final String sortBy,
                         final boolean sortDesc) {
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        this.start = start;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDesc = sortDesc;
    }

    public Map<String, List> getParameters() {
        return parameters;
    }

    public int getStart() {
        return start;
    }

    public int getSize() {
        return size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public boolean isSortDesc() {
        return sortDesc;
    }

    public Map<String, Object> collectSingleValueParameters(final String ... allowed) {
        final Map<String, Object> collected = new HashMap<>();
        if (allowed != null) {
            for (final String key : allowed) {
                final List val = parameters.get(key);
                if (val != null && !val.isEmpty()) {
                    final Object val0 = val.get(0);
                    if (val0 != null) {
                        if (val0 instanceof String) {
                            if (StringUtils.isNotBlank((String) val0)) {
                                collected.put(key, val0);
                            }
                        } else {
                            collected.put(key, val.get(0));
                        }
                    }
                }
            }
        }
        return collected;
    }

}
