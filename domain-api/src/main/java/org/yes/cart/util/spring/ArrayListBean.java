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
public class ArrayListBean<E> extends ArrayList<E> {

    private final ArrayListBean<E> parent;

    public ArrayListBean(final Collection<E> base) {
        super(base);
        parent = base instanceof ArrayListBean ? (ArrayListBean<E>) base : null;
    }

    /**
     * Extension for this list.
     *
     * @param extension extension
     */
    public void setExtensionList(List<E> extension) {
        this.addAll(extension);
        if (this.parent != null) {
            this.parent.addAll(extension);
        }
    }

}
