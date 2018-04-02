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
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic hash map bean, which can be extended in XML.
 *
 * User: denispavlov
 * Date: 31/03/2018
 * Time: 16:14
 */
public class LinkedHashMapBean<K, V> extends LinkedHashMap<K, V> implements BeanNameAware, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger("CONFIG");

    private String name;
    private final LinkedHashMapBean<K, V> parent;
    private Map<K, V> extension;

    public LinkedHashMapBean(final Map<K, V> base) {
        super(base);
        parent = base instanceof LinkedHashMapBean ? (LinkedHashMapBean<K, V>) base : null;
    }

    /**
     * Extension for this map.
     *
     * @param extension extension
     */
    public void setExtension(final Map<K, V> extension) {
        if (this.parent == null) {
            throw new UnsupportedOperationException("Unable to extend map without parent LinkedHashMapBean");
        }
        this.extension = extension;
    }

    @Override
    public void setBeanName(final String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (extension != null) {
            this.putAll(extension);
        }
        if (this.parent != null) {
            logMap(extension, true);
            this.parent.putAll(extension);
        } else { // this is root
            logMap(this, false);
        }
    }

    private void logMap(final Map<K, V> map, final boolean extending) {
        for (final Map.Entry<K, V> item : map.entrySet()) {
            if (extending) {
                if (this.parent.containsKey(item.getKey())) {
                    LOG.warn("{}:{} loading map extension (override) {}", this.parent.name, this.name, item);
                } else {
                    LOG.debug("{}:{} loading map extension {}", this.parent.name, this.name, item);
                }
            } else {
                LOG.debug("{} loading extendable map {}", this.name, item);
            }
        }
    }
}
