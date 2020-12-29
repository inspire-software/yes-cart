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

package org.yes.cart.utils.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.yes.cart.utils.RuntimeConstants;

import java.io.IOException;
import java.util.*;

/**
 * User: denispavlov
 * Date: 02/12/2015
 * Time: 21:42
 */
public class RuntimeConstantsImpl implements RuntimeConstants {

    private final Map<String, String> constants = new TreeMap<>();

    public RuntimeConstantsImpl() {

    }

    /**
     * Set constants from constants map.
     *
     * @param constants constants
     */
    public void setConstantsMap(final Map<String, String> constants) {

        this.constants.putAll(constants);

    }

    /**
     * Set constants from resource config file.
     *
     * @param configs resource file
     *
     * @throws IOException if resource is not readable
     */
    public void setConstantsResources(final Resource ... configs) throws IOException {

        final Properties properties = new Properties();
        for (final Resource config : configs) {
            properties.load(config.getInputStream());
        }

        setConstantsMap(new TreeMap(properties));

    }

    /** {@inheritDoc} */
    @Override
    public boolean hasValue(final String key) {
        return StringUtils.isNotBlank(constants.get(key));
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(constants.keySet());
    }

    /** {@inheritDoc} */
    @Override
    public String getConstant(final String key) {
        return constants.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public String getConstantOrDefault(final String key, final String def) {
        final String value = constants.get(key);
        return value != null ? value : def;
    }

    /** {@inheritDoc} */
    @Override
    public String getConstantNonBlankOrDefault(final String key, final String def) {
        final String value = constants.get(key);
        return StringUtils.isNotBlank(value) ? value : def;
    }
}
