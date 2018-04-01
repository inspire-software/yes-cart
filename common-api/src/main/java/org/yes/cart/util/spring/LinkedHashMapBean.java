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

package org.yes.cart.util.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic hash map bean, which can be extended in XML.
 *
 * User: denispavlov
 * Date: 31/03/2018
 * Time: 16:14
 */
public class LinkedHashMapBean<K, V> extends LinkedHashMap<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger("CONFIG");

    private final LinkedHashMapBean<K, V> parent;

    public LinkedHashMapBean(final Map<K, V> base) {
        super(base);
        if (base instanceof LinkedHashMapBean) {
            parent = (LinkedHashMapBean<K, V>) base;
        } else {
            parent = null;
            logMap(this, false);
        }
    }

    /**
     * Extension for this map.
     *
     * @param extension extension
     */
    public void setExtensionList(final Map<K, V> extension) {
        this.putAll(extension);
        if (this.parent != null) {
            logMap(extension, true);
            this.parent.putAll(extension);
        }
    }

    private void logMap(final Map<K, V> map, final boolean extending) {
        for (final Map.Entry<K, V> item : map.entrySet()) {
            if (extending) {
                if (this.parent.containsKey(item.getKey())) {
                    LOG.warn("loading map extension (override) {}", item);
                } else {
                    LOG.debug("loading map extension {}", item);
                }
            } else {
                LOG.debug("loading extendable map {}", item);
            }
        }
    }

}
