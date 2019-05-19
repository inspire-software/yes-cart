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

package org.yes.cart.utils.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Basic array list bean, which can be extended in XML.
 *
 * User: denispavlov
 * Date: 25/03/2018
 * Time: 15:36
 */
public class ArrayListBean<E> extends ArrayList<E> implements BeanNameAware, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger("CONFIG");

    private String name;
    private final ArrayListBean<E> parent;
    private List<E> extension;

    public ArrayListBean(final Collection<E> base) {
        super(base);
        parent = base instanceof ArrayListBean ? (ArrayListBean<E>) base : null;
    }

    /**
     * Extension for this list.
     *
     * @param extension extension
     */
    public void setExtension(final List<E> extension) {
        if (this.parent == null) {
            throw new UnsupportedOperationException("Unable to extend list without parent ArrayListBean");
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
            this.addAll(extension);
        }
        if (this.parent != null) {
            logList(extension, true);
            this.parent.addAll(extension);
        } else { // this is root
            logList(this, false);
        }
    }

    private void logList(final List<E> list, final boolean extending) {
        for (final E item : list) {
            if (extending) {
                LOG.debug("{}:{} loading list extension {}", this.parent.name, this.name, item);
            } else {
                LOG.debug("{} loading extendable list item {}", this.name, item);
            }
        }
    }
}
