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
public class ArrayListBean<E> extends ArrayList<E> implements BeanNameAware {

    private static final Logger LOG = LoggerFactory.getLogger("CONFIG");

    private String name;
    private final ArrayListBean<E> parent;

    public ArrayListBean(final Collection<E> base) {
        super(base);
        if (base instanceof ArrayListBean) {
            parent = (ArrayListBean<E>) base;
        } else {
            parent = null;
            logList(this, false);
        }
    }

    /**
     * Extension for this list.
     *
     * @param extension extension
     */
    public void setExtension(final List<E> extension) {
        this.addAll(extension);
        if (this.parent != null) {
            logList(extension, true);
            this.parent.addAll(extension);
        }
    }

    private void logList(final List<E> list, final boolean extending) {
        for (final E item : list) {
            if (extending) {
                LOG.debug("{} loading list extension {}", this.name, item);
            } else {
                LOG.debug("{} loading extendable list {}", this.name, item);
            }
        }
    }

    @Override
    public void setBeanName(final String name) {
         this.name = name;
    }
}
