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

package org.yes.cart.bulkimport.xml.impl;

import org.yes.cart.bulkimport.xml.XmlImportTuple;

/**
 * User: denispavlov
 * Date: 26/11/2015
 * Time: 12:07
 */
public class XmlImportTupleImpl implements XmlImportTuple {

    private final String sourceId;
    private final Object data;

    public XmlImportTupleImpl(final String sourceId, final Object data) {
        this.sourceId = sourceId;
        this.data = data;
    }

    /** {@inheritDoc} */
    @Override
    public String getSourceId() {
        return sourceId;
    }

    /** {@inheritDoc} */
    @Override
    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        if (data != null) {
            return "XmlImportTupleImpl{data="
                    + data.getClass().getSimpleName() + ":"
                    + sourceId
                    + "}";

        }
        return "XmlImportTupleImpl{data=NULL}";
    }
}
